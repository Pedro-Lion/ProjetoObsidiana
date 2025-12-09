import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { api } from "../api";
import { useState, useEffect, useRef } from "react";
import { Modal } from "../components/Modal/Modal.jsx";

export function CadastroEquipamentos() {
  const navigate = useNavigate();
  const { id } = useParams();
  const state = useLocation().state;
  const [arquivoImagem, setArquivoImagem] = useState(null); // arquivo selecionado
  const [previewImagem, setPreviewImagem] = useState(null); // url para preview
  // referência para guardar objectURL criado e poder revogar corretamente
  const previewRef = useRef(null);
  // base do Vite: deve apontar para o backend (localhost:8080)
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
  // Estados do modal
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  // Funções utilitárias para inputs
  const onChangeTexto = (campo) => (e) => {
    setEquipamento((prev) => ({ ...prev, [campo]: e.target.value }));
  };
  const onChangeNumero = (campo) => (e) => {
    const n = e.target.value === "" ? 0 : Number(e.target.value);
    setEquipamento((prev) => ({ ...prev, [campo]: n }));
  };
  const onInputValorHora = (e) => {
    let v = e.target.value || "";
    v = v.replace(/\D/g, "");
    const numero = (Number(v) / 100).toFixed(2);
    setValorHora(numero);
    setEquipamento((prev) => ({ ...prev, valorPorHora: Number(numero) }));
  };

  /* Carrega a imagem atual do equipamento e faz o set da preview */
  useEffect(() => {
    async function carregarPreviewExistente() {
      if (!id) return; // só age se tiver id (edição)
      try {
        const token = sessionStorage.getItem("token");
        const url = `${API_BASE}/equipamento/${id}/imagem`;
        const resp = await fetch(url, {
          method: "GET",
          headers: { Authorization: token ? ("Bearer " + token) : "" },
        });
        if (!resp.ok) return; // equipamento sem imagem ou não autorizado
        const ctype = resp.headers.get("content-type") || "";
        if (!ctype.startsWith("image/")) {
          console.warn(`Preview não é imagem (content-type=${ctype}) para equipamento ${id}`);
          return;
        }
        const blob = await resp.blob();
        const objectUrl = URL.createObjectURL(blob);

        // revogar objectURL anterior se existir
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

    // cleanup: liberar objectURL quando componente desmontar
    return () => {
      if (previewRef.current) {
        try { URL.revokeObjectURL(previewRef.current); } catch (e) { /* ignore */ }
        previewRef.current = null;
      }
    };
  }, [id]); // roda quando id mudar


  /* Métodos de cadastro e edição de equipamentos */
  async function cadastrar() {
    try {
      const request = await api.post("/equipamento", equipamento, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status == 201) {
        const criado = request.data;
        // Se tiver arquivo para subir, chamar a função de upload:
        if (arquivoImagem && criado && criado.id) {
          await uploadImagem(criado.id);
        }
        setModalTitulo("Sucesso!");
        setModalDescricao(
          "Cadastrado com sucesso! Quer retornar à lista de equipamentos?"
        );
        setModalActions(
          <>
            <button
              className="bg-blue-500 text-white px-4 py-2 rounded mr-3"
              onClick={() => navigate("/equipamentos")}
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
      setModalDescricao(
        "Equipamento não pôde ser cadastrado. Tente novamente."
      );
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

  async function editar() {
    try {
      const request = await api.put(`/equipamento/${id}`, equipamento, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status == 200) {
        // Se a imagem foi alterada, chamar a função de upload novamente
        if (arquivoImagem && id) {
          await uploadImagem(id);
        }
        setModalTitulo("Sucesso!");
        setModalDescricao(
          "Editado com sucesso! Retornando à lista de equipamentos."
        );
        setModalActions(
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded"
            onClick={() => navigate("/equipamentos")}
          >
            Ok
          </button>
        );
        setModalOpen(true);
      } else {
        setModalTitulo("Erro");
        setModalDescricao("Equipamento não pôde ser editado. Tente novamente.");
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
    } catch (error) {
      console.log(error);
      setModalTitulo("Erro");
      setModalDescricao("Erro ao editar equipamento.");
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

  /* Funções para upload de imagem */
  async function uploadImagem(equipamentoId) {
    if (!arquivoImagem) return null;

    const formData = new FormData();
    formData.append("arquivo", arquivoImagem);

    try {
      const res = await api.post(`/equipamento/${equipamentoId}/imagem`, formData, {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
          // NÃO setar Content-Type — axios configura o boundary automaticamente
        },
      });
      return res.data;
    } catch (err) {
      // ADICIONAR A MENSAGEM DE ERRO DA MODAL!!
      console.error("Erro ao enviar imagem:", err);
      return null;
    }
  }

  function handleInputFotoChange(e) {
    /* O componente InputFoto já mostra o preview local via imageURL. Aqui, só guardamos o arquivo para envio posterior */
    const file = e.target.files?.[0] ?? null;
    setArquivoImagem(file);
  }

  return (
    <>
      <h1 className="mb-16 text-4xl font-bold">Cadastrar Equipamento</h1>
      <div className="w-180">
        <div className="self-start">
          <InputFoto onChange={handleInputFotoChange} initialPreview={previewImagem} />
        </div>

        <div className="h-95 mt-10 flex justify-between items-center">
          <div className="flex flex-col justify-between h-full">
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
          <div className="flex flex-col justify-between h-full">
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
              titulo="Valor"
              type="text"
              placeholder="Ex: 150.00"
              onInput={onInputValorHora}
              value={valorHora}
            />

            {!state ? (
              <BotaoPrimario
                titulo="Cadastrar"
                className="w-full mb-0 mt-10"
                onClick={cadastrar}
              />
            ) : (
              <BotaoPrimario
                titulo="Salvar alterações"
                className="w-full mb-0 mt-7"
                onClick={editar}
              />
            )}
          </div>
        </div>
      </div>

      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}
