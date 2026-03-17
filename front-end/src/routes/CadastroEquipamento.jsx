import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { api } from "../api";
import { useState, useEffect, useRef } from "react";
import { Modal } from "../components/Modal/Modal.jsx";

// Props opcionais para uso em modal:
//   onSucesso  → chamado após cadastro/edição bem-sucedidos (fecha modal e recarrega lista)
//   onCancelar → chamado ao clicar em Cancelar (fecha modal)
// Quando não passadas, o componente se comporta normalmente via navigate (rota direta).
export function CadastroEquipamentos({ onSucesso, onCancelar }) {
  // useNavigate/useParams/useLocation lançam erro fora de Router, mas como o componente
  // sempre é renderizado dentro do App que já tem o Router, isso é seguro.
  const navigate = useNavigate();
  const { id } = useParams();
  const state = useLocation().state;

  const [arquivoImagem, setArquivoImagem] = useState(null);
  const [previewImagem, setPreviewImagem] = useState(null);
  const previewRef = useRef(null);
  const API_BASE = import.meta.env.VITE_API_BASE_URL || "";

  const [equipamento, setEquipamento] = useState(
    state ?? {
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

  // Estados do modal interno (confirmações e erros)
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  // Funções utilitárias para inputs
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

  // Carrega preview da imagem existente (apenas no modo edição via rota)
  useEffect(() => {
    async function carregarPreviewExistente() {
      if (!id) return;
      try {
        const token = sessionStorage.getItem("token");
        const url = `${API_BASE}/equipamento/${id}/imagem`;
        const resp = await fetch(url, {
          method: "GET",
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

  /* ── Helpers de navegação/fechamento ── */
  function irParaLista() {
    if (onSucesso) {
      onSucesso(); // fecha a modal e recarrega a lista
    } else {
      navigate("/equipamentos");
    }
  }

  function cancelar() {
    if (onCancelar) {
      onCancelar();
    } else {
      navigate("/equipamentos");
    }
  }

  /* ── Upload de imagem ── */
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
      console.error("Erro ao enviar imagem:", err);
      return null;
    }
  }

  function handleInputFotoChange(e) {
    const file = e.target.files?.[0] ?? null;
    setArquivoImagem(file);
  }

  /* ── Cadastrar ── */
  async function cadastrar() {
    try {
      const request = await api.post("/equipamento", equipamento, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status == 201) {
        const criado = request.data;
        if (arquivoImagem && criado?.id) {
          await uploadImagem(criado.id);
        }

        setModalTitulo("Sucesso!");
        setModalDescricao("Cadastrado com sucesso! Quer retornar à lista de equipamentos?");
        setModalActions(
          <>
            <button
              className="bg-blue-500 text-white px-4 py-2 rounded mr-3"
              onClick={irParaLista}
            >
              Ir para lista
            </button>
            <button
              className="bg-gray-300 px-4 py-2 rounded"
              onClick={() => setModalOpen(false)}
            >
              Continuar
            </button>
          </>
        );
        setModalOpen(true);
      }
    } catch (error) {
      console.log(error);
      setModalTitulo("Erro");
      setModalDescricao("Equipamento não pôde ser cadastrado. Tente novamente.");
      setModalActions(
        <button
          className="bg-gray-300 px-4 py-2 rounded"
          onClick={() => setModalOpen(false)}
        >
          Fechar
        </button>
      );
      setModalOpen(true);
    }
  }

  /* ── Editar ── */
  async function editar() {
    try {
      const request = await api.put(`/equipamento/${id}`, equipamento, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status == 200) {
        if (arquivoImagem && id) {
          await uploadImagem(id);
        }
        setModalTitulo("Sucesso!");
        setModalDescricao("Editado com sucesso! Retornando à lista de equipamentos.");
        setModalActions(
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded"
            onClick={irParaLista}
          >
            Ok
          </button>
        );
        setModalOpen(true);
      } else {
        setModalTitulo("Erro");
        setModalDescricao("Equipamento não pôde ser editado. Tente novamente.");
        setModalActions(
          <button className="bg-gray-300 px-4 py-2 rounded" onClick={() => setModalOpen(false)}>
            Fechar
          </button>
        );
        setModalOpen(true);
      }
    } catch (error) {
      console.log(error);
      setModalTitulo("Erro");
      setModalDescricao("Erro ao editar equipamento.");
      setModalActions(
        <button className="bg-gray-300 px-4 py-2 rounded" onClick={() => setModalOpen(false)}>
          Fechar
        </button>
      );
      setModalOpen(true);
    }
  }

  return (
    <>
      {/* Título só aparece quando usado como página (sem onCancelar) */}
      {!onCancelar && (
        <h1 className="mb-16 text-4xl font-bold">Cadastrar Equipamento</h1>
      )}

      <div className="w-full">
        <div className="self-start">
          <InputFoto onChange={handleInputFotoChange} initialPreview={previewImagem} />
        </div>

        <div className="mt-10 flex justify-between items-start gap-6 flex-wrap">
          {/* Coluna esquerda */}
          <div className="flex flex-col gap-4 flex-1 min-w-48">
            <InputBordaLabel
              titulo="Nome"
              placeholder="Ex: Memória SD 128gb"
              onInput={onChangeTexto("nome")}
              value={equipamento.nome}
            />
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
            <InputBordaLabel
              titulo="Quantidade"
              type="number"
              placeholder="Ex: 10"
              onInput={onChangeNumero("quantidadeTotal")}
              value={equipamento.quantidadeTotal}
            />
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
            <InputBordaLabel
              titulo="Valor por Hora"
              type="text"
              placeholder="Ex: 150.00"
              onInput={onInputValorHora}
              value={valorHora}
            />
          </div>
        </div>

        {/* Botões de ação */}
        <div className="flex gap-3 mt-10">
          {!state ? (
            <BotaoPrimario titulo="Cadastrar" className="mb-0 mt-0" onClick={cadastrar} />
          ) : (
            <BotaoPrimario titulo="Salvar alterações" className="mb-0 mt-0" onClick={editar} />
          )}
          <BotaoSecundario titulo="Cancelar" className="mb-0 mt-0" onClick={cancelar} />
        </div>
      </div>

      {/* Modal de confirmação/erro interno */}
      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}