import { useEffect, useState, useRef } from "react";
import { ContainerListagem } from "../components/Containers/ContainerListagem";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { api } from "../api.js";
import { useNavigate } from "react-router-dom";
import { Modal } from "../components/Modal/Modal.jsx";
import { ModalFormulario } from "../components/Modal/ModalFormulario.jsx";
import { CadastroEquipamentos } from "./CadastroEquipamento.jsx";

export function Equipamentos() {
  const navigate = useNavigate();
  const previewsRef = useRef([]);

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState("");

  // Modal de confirmação/exclusão
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  // Modal de cadastro
  const [modalCadastroAberta, setModalCadastroAberta] = useState(false);

  const API_BASE = import.meta.env.VITE_API_BASE_URL || "";

  /* ── Carregamento da lista ── */
  function recarregarLista() {
    // Remonta o useEffect relançando a busca
    setLoading(true);
    buscarEquipamentos();
  }

  async function carregarPreviews(equipamentos) {
    if (previewsRef.current?.length) {
      previewsRef.current.forEach((u) => { try { URL.revokeObjectURL(u); } catch (e) { } });
      previewsRef.current = [];
    }

    const token = sessionStorage.getItem("token");
    const promessas = equipamentos.map(async (eq) => {
      if (!eq.nomeArquivoImagem) return { ...eq, preview: null };
      try {
        const url = `${API_BASE}/equipamento/${eq.id}/imagem`;
        const resp = await fetch(url, {
          method: "GET",
          headers: { Authorization: token ? "Bearer " + token : "" },
        });
        if (!resp.ok) return { ...eq, preview: null };
        const ctype = resp.headers.get("content-type") || "";
        if (!ctype.startsWith("image/")) return { ...eq, preview: null };
        const blob = await resp.blob();
        const objectUrl = URL.createObjectURL(blob);
        previewsRef.current.push(objectUrl);
        return { ...eq, preview: objectUrl };
      } catch {
        return { ...eq, preview: null };
      }
    });
    return Promise.all(promessas);
  }

  async function buscarEquipamentos() {
    try {
      const resposta = await api.get("/equipamento", {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
      if (resposta.status === 200 && Array.isArray(resposta.data)) {
        const withPreviews = await carregarPreviews(resposta.data);
        setData(withPreviews);
      } else {
        setData([]);
      }
    } catch (err) {
      console.error(err);
      setError("Erro ao carregar equipamentos.");
      setData([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    let mounted = true;
    setLoading(true);
    setError(null);

    buscarEquipamentos().then(() => {
      if (!mounted) return;
    });

    return () => {
      mounted = false;
      if (previewsRef.current?.length) {
        previewsRef.current.forEach((u) => { try { URL.revokeObjectURL(u); } catch (e) { } });
        previewsRef.current = [];
      }
    };
  }, []);

  /* ── Deletar ── */
  const deletar = (equipamento) => {
    setModalTitulo("Confirmar exclusão");
    setModalDescricao(`Tem certeza que deseja excluir "${equipamento.nome}"?`);
    setModalActions(
      <>
        <button
          className="bg-red-500 text-white px-4 py-2 rounded mr-3"
          onClick={async () => {
            try {
              await api.delete(`/equipamento/${equipamento.id}`, {
                headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
              });
              setData((prev) => prev.filter((e) => e.id !== equipamento.id));
              setModalOpen(false);
            } catch (err) {
              console.error(err);
              setModalTitulo("Erro");
              setModalDescricao("Não foi possível excluir. Tente novamente.");
              setModalActions(
                <button
                  className="bg-gray-300 px-4 py-2 rounded"
                  onClick={() => setModalOpen(false)}
                >
                  Fechar
                </button>
              );
            }
          }}
        >
          Excluir
        </button>
        <button
          className="bg-gray-300 px-4 py-2 rounded"
          onClick={() => setModalOpen(false)}
        >
          Cancelar
        </button>
      </>
    );
    setModalOpen(true);
  };

  const filtrado = data.filter((e) =>
    e.nome?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <>
      <div className="flex justify-between items-center mb-6">
        <h1>Equipamentos</h1>
        <BotaoPrimario
          titulo="+ Novo equipamento"
          onClick={() => setModalCadastroAberta(true)}
        />
      </div>

      <InputBordaLabel
        titulo="Buscar"
        placeholder="Buscar por nome..."
        value={search}
        onInput={(e) => setSearch(e.target.value)}
        className="mb-6 max-w-sm"
      />

      {loading && <p className="text-xl">Carregando equipamentos...</p>}
      {error && <p className="text-red-500">{error}</p>}

      {!loading && !error && (
        <section className="flex flex-wrap gap-5">
          {filtrado.length !== 0 ? (
            filtrado.map((e) => (
              <ContainerListagem
                key={e.id}
                dados={e}
                preview={e.preview}
                onClickDel={() => deletar(e)}
                onClickEdit={() => navigate(`/editar/equipamento/${e.id}`, { state: e })}
              />
            ))
          ) : (
            <p className="text-xl">Nenhum equipamento encontrado.</p>
          )}
        </section>
      )}

      {/* Modal de confirmação/exclusão */}
      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}

      {/* Modal de cadastro de equipamento */}
      {modalCadastroAberta && (
        <ModalFormulario
          titulo="Novo Equipamento"
          onFechar={() => setModalCadastroAberta(false)}
        >
          <CadastroEquipamentos
            onSucesso={() => {
              setModalCadastroAberta(false);
              recarregarLista();
            }}
            onCancelar={() => setModalCadastroAberta(false)}
          />
        </ModalFormulario>
      )}
    </>
  );
}