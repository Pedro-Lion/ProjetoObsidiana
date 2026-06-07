import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { api } from "../api";
import { useState, useEffect, useRef } from "react";
import { Modal } from "../components/Modal/Modal.jsx";
import { notificar } from "../features/notificar.jsx";
import { toast } from "react-toastify";

export function CadastroEquipamentos({ onSucesso, onCancelar }) {
  const navigate = useNavigate();
  const { id } = useParams();
  const state = useLocation().state;

  const [arquivoImagem, setArquivoImagem] = useState(null);
  const [previewImagem, setPreviewImagem] = useState(null);
  const previewRef = useRef(null);
  // Fallback alinhado com o do api.js — sem isso o fetch ia para o Vite (5173) e voltava index.html
  const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

  // Só pré-popula a partir do state quando estamos em modo edição (id presente em useParams).
  // No modo cadastro (modal ou rota /cadastro/equipamentos), o state pode conter resíduos
  // de navegação (ex.: {highlightId, pagina}) deixados pelo irParaLista — usar isso como
  // dados de equipamento causava PUT /equipamento/undefined e formulário pré-populado com lixo.
  const [equipamento, setEquipamento] = useState(
    id
      ? (state ?? {})
      : {
          nome: "",
          categoria: "",
          marca: "",
          quantidadeTotal: 0,
          modelo: "",
          numeroSerie: "",
          valorPorHora: null,
        }
  );
  const [valorHora, setValorHora] = useState(
    equipamento.valorPorHora
      ? Number(equipamento.valorPorHora).toFixed(2)
      : "0.00"
  );

  // Erros de validação
  const [erros, setErros] = useState({});

  const onChangeTexto = (campo) => (e) =>
    setEquipamento((prev) => ({ ...prev, [campo]: e.target.value }));

  const onChangeNumero = (campo) => (e) => {
    const n = e.target.value === "" ? 0 : Number(e.target.value);
    setEquipamento((prev) => ({ ...prev, [campo]: n }));
  };

  const onInputValorHora = (e) => {
    let v = (e.target.value || "").replace(/\D/g, "");
    const numero = (Number(v) / 100).toFixed(2);
    setValorHora(numero);
    setEquipamento((prev) => ({ ...prev, valorPorHora: Number(numero) }));
  };

  // Carrega preview da imagem existente (modo edição via rota)
  useEffect(() => {
    async function carregarPreviewExistente() {
      if (!id) return;
      try {
        const token = sessionStorage.getItem("token");
        // cache: no-store + timestamp evita devolução de 304 do browser ao reabrir o cadastro
        const url = `${API_BASE}/equipamento/${id}/imagem?v=${Date.now()}`;
        const resp = await fetch(url, {
          method: "GET",
          cache: "no-store",
          headers: { Authorization: token ? "Bearer " + token : "" },
        });
        if (!resp.ok) return;
        const ctype = resp.headers.get("content-type") || "";
        if (!ctype.startsWith("image/")) return;
        const blob = await resp.blob();
        const objectUrl = URL.createObjectURL(blob);
        if (previewRef.current) {
          try { URL.revokeObjectURL(previewRef.current); } catch (e) { /* ignore */ }
        }
        previewRef.current = objectUrl;
        setPreviewImagem(objectUrl);
      } catch (err) {
        console.log("Sem imagem existente ou erro ao buscar:", err);
      }
    }
    carregarPreviewExistente();

    return () => {
      if (previewRef.current) {
        try { URL.revokeObjectURL(previewRef.current); } catch (e) { /* ignore */ }
        previewRef.current = null;
      }
    };
  }, [id]);

  // novoId: id do item recém criado ou editado, para destacá-lo na listagem
  function irParaLista(novoId) {
    if (onSucesso) {
      onSucesso(novoId);
    } else {
      // Edição via rota: state.paginaOrigem guarda a página de onde o usuário veio
      navigate("/equipamentos", {
        state: { highlightId: novoId, pagina: state?.paginaOrigem ?? 0 },
      });
    }
  }

  function cancelar() {
    if (onCancelar) {
      onCancelar();
    } else {
      navigate("/equipamentos");
    }
  }

  // Validação antes de salvar
  function validar() {
    const novosErros = {};

    if (!equipamento.nome || equipamento.nome.trim() === "") {
      novosErros.nome = "Nome é obrigatório.";
    }
    if (!equipamento.quantidadeTotal || Number(equipamento.quantidadeTotal) <= 0) {
      novosErros.quantidadeTotal = "Quantidade deve ser maior que 0.";
    }
    if (!equipamento.valorPorHora || Number(equipamento.valorPorHora) <= 0) {
      novosErros.valorPorHora = "Valor por hora não pode ser vazio ou zero.";
    }

    setErros(novosErros);
    return Object.keys(novosErros).length === 0;
  }

  async function uploadImagem(equipamentoId) {
    if (!arquivoImagem) return null;
    const formData = new FormData();
    formData.append("arquivo", arquivoImagem);
    try {
      const res = await api.post(`/equipamento/${equipamentoId}/imagem`, formData, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
      return res.data;
    } catch (err) {
      // Loga e relança para que cadastrar/editar consigam tratar a falha
      // (antes o erro era engolido e o usuário via "sucesso" mesmo sem imagem anexada)
      console.error("Erro ao enviar imagem:", err);
      throw err;
    }
  }

  // Fix: captura o arquivo diretamente do evento para usar no upload
  function handleInputFotoChange(e) {
    const file = e.target.files?.[0] ?? null;
    setArquivoImagem(file);
  }

  function cadastrar() {
    if (!validar()) return;

    notificar(
      api.post("/equipamento", equipamento, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      }),
      async (req) => {
        if (req.status == 201) {
          const criado = req.data;
          // Fix upload via modal: usa o arquivo do state, não depende de ref perdida
          if (arquivoImagem && criado?.id) {
            try {
              await uploadImagem(criado.id);
            } catch (err) {
              // Equipamento foi criado, mas a imagem falhou — avisa o usuário em vez de seguir como se tudo tivesse dado certo
              toast.error("Equipamento criado, mas a imagem não pôde ser anexada. Tente reenviar pelo modo edição.");
            }
          }
          irParaLista(criado?.id);
        }
      },
      {
        pending: "Cadastrando equipamento...",
        success: [
          "Equipamento cadastrado com sucesso!",
          "Retornando à página de serviços"
        ],
        error: "Ocorreu um erro ao cadastrar o equipamento"
      }
    )
  }

  async function editar() {
    if (!validar()) return;

    notificar(
      api.put(`/equipamento/${id ?? equipamento.id}`, equipamento, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      }),
      async (req) => {
        if (req.status == 200) {
          if (arquivoImagem && id) {
            try {
              await uploadImagem(id);
            } catch (err) {
              // Alterações textuais foram salvas, mas a nova imagem não — avisa o usuário
              toast.error("Alterações salvas, mas a nova imagem não pôde ser enviada. Tente reenviar a foto.");
            }
          }
          // id vem do useParams (modo edição via rota)
          irParaLista(id ? Number(id) : undefined);
        }
      },
      {
        pending: "Salvando alterações...",
        success: [
          "Alterações do equipamento salvas com sucesso!",
          "Retornando à página de equipamentos"
        ],
        error: "Ocorreu um erro durante o registro das alterações"
      }
    )
  }

  // Componente auxiliar para exibir mensagem de erro abaixo do campo
  const ErroMsg = ({ campo }) =>
    erros[campo] ? (
      <span className="text-red-500 text-[1rem] mt-1">{erros[campo]}</span>
    ) : null;

  return (
    <>
      {!onCancelar && (
        <h1 className="mb-16 text-4xl font-bold">Cadastrar Equipamento</h1>
      )}

      <div className="w-full">
        <div className="self-start">
          <InputFoto onChange={handleInputFotoChange}
          initialPreview={previewImagem}
          inputId="equipamento-foto-upload"
          />
        </div>

        <div className="mt-10 flex justify-between items-start gap-6 flex-wrap">
          {/* Coluna esquerda */}
          <div className="flex flex-col gap-4 flex-1 min-w-48">
            <div className="flex flex-col">
              <InputBordaLabel
                titulo="Nome"
                placeholder="Ex: Memória SD 128gb"
                onInput={onChangeTexto("nome")}
                value={equipamento.nome}
              />
              <ErroMsg campo="nome" />
            </div>

            <InputBordaLabel
              titulo="Categoria"
              placeholder="Ex: Armazenamento"
              onInput={onChangeTexto("categoria")}
              value={equipamento.categoria}
            />
            <InputBordaLabel
              titulo="Marca"
              placeholder="Ex: SanDisk"
              onInput={onChangeTexto("marca")}
              value={equipamento.marca}
            />
            <div className="flex flex-col">
              <InputBordaLabel
                titulo="Quantidade"
                type="number"
                placeholder="Ex: 10"
                onInput={onChangeNumero("quantidadeTotal")}
                value={equipamento.quantidadeTotal}
              />
              <ErroMsg campo="quantidadeTotal" />
            </div>
          </div>

          {/* Coluna direita */}
          <div className="flex flex-col gap-4 flex-1 min-w-48">
            <InputBordaLabel
              titulo="Modelo"
              type="text"
              placeholder="Ex: SD128GB"
              onInput={onChangeTexto("modelo")}
              value={equipamento.modelo}
            />
            <InputBordaLabel
              titulo="Número de Série"
              type="text"
              placeholder="Ex: 123456789"
              onInput={onChangeTexto("numeroSerie")}
              value={equipamento.numeroSerie}
            />
            <div className="flex flex-col">
              <InputBordaLabel
                titulo="Valor por Hora"
                type="text"
                placeholder="Ex: 150.00"
                onInput={onInputValorHora}
                value={valorHora}
              />
              <ErroMsg campo="valorPorHora" />
            </div>
          </div>
        </div>

        <div className="flex gap-3 mt-10">
          {/* Discrimina cadastro vs edição pelo id de useParams, não pelo state — ver comentário no useState acima */}
          {!id ? (
            <BotaoPrimario titulo="Cadastrar" className="mb-0 mt-0" onClick={cadastrar} />
          ) : (
            <BotaoPrimario titulo="Salvar alterações" className="mb-0 mt-0" onClick={editar} />
          )}
          <BotaoSecundario titulo="Cancelar" className="mb-0 mt-0" onClick={cancelar} />
        </div>
      </div>
    </>
  );
}