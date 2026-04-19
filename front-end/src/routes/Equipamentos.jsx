import { useEffect, useState, useRef } from "react";
import { ContainerListagem } from "../components/Containers/ContainerListagem";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api.js";
import { useNavigate } from "react-router-dom";
import { Modal } from "../components/Modal/Modal.jsx";
import { ModalFormulario } from "../components/Modal/ModalFormulario.jsx";
import { CadastroEquipamentos } from "./CadastroEquipamento.jsx";
import { Paginacao } from "../components/Paginacao/Paginacao.jsx";

const ITENS_POR_PAGINA = 5;

export function Equipamentos() {
  const navigate = useNavigate();
  const previewsRef = useRef([]);

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState("");

  // Paginação
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);

  // Modal de confirmação/exclusão
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  // Modal de cadastro
  const [modalCadastroAberta, setModalCadastroAberta] = useState(false);

  const API_BASE = import.meta.env.VITE_API_BASE_URL || "";

  /* ── Carrega previews de imagem ── */
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

  /* ── Busca paginada ── */
  async function buscarEquipamentos(pagina = 0, termo = search) {
    setLoading(true);
    setError(null);
    try {
      const resposta = await api.get("/equipamento/paginado", {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
        params: { page: pagina, size: ITENS_POR_PAGINA, busca: termo },
      });

      if (resposta.status === 200) {
        const paginaData = resposta.data;
        const withPreviews = await carregarPreviews(paginaData.content);
        setData(withPreviews);
        setTotalPaginas(paginaData.totalPages);
        setTotalElementos(paginaData.totalElements);
        setPaginaAtual(paginaData.number);
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

  // Debounce: busca no backend 400ms após parar de digitar
  useEffect(() => {
    const timer = setTimeout(() => {
      buscarEquipamentos(0, search);
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    buscarEquipamentos(0, "");
    return () => {
      if (previewsRef.current?.length) {
        previewsRef.current.forEach((u) => { try { URL.revokeObjectURL(u); } catch (e) { } });
        previewsRef.current = [];
      }
    };
  }, []);

  /* ── Troca de página ── */
  function mudarPagina(novaPagina) {
    if (novaPagina < 0 || novaPagina >= totalPaginas) return;
    buscarEquipamentos(novaPagina);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

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
              // Volta para a página anterior se a atual ficou vazia
              const novaPagina = data.length === 1 && paginaAtual > 0
                ? paginaAtual - 1
                : paginaAtual;
              setModalOpen(false);
              buscarEquipamentos(novaPagina);
            } catch (err) {
              console.error(err);
              setModalTitulo("Erro");
              setModalDescricao("Não foi possível excluir. Tente novamente.");
              setModalActions(
                <button className="bg-gray-300 px-4 py-2 rounded" onClick={() => setModalOpen(false)}>
                  Fechar
                </button>
              );
            }
          }}
        >
          Excluir
        </button>
        <button className="bg-gray-300 px-4 py-2 rounded" onClick={() => setModalOpen(false)}>
          Cancelar
        </button>
      </>
    );
    setModalOpen(true);
  };

  /* ── Filtro local (dentro da página atual) ── */
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

      <div className="w-full pr-5">
        <InputBordaLabel
          type="text"
          titulo="Buscar"
          placeholder="Nome, categoria ou marca"
          value={search}
          onInput={(e) => setSearch(e.target.value)}
          className=""
        />
      </div>

      {loading && <p className="text-xl">Carregando equipamentos...</p>}
      {error && <p className="text-red-500">{error}</p>}

      {!loading && !error && (
        <>
          <section className="flex flex-wrap gap-5">
            {data.length !== 0 ? (
              data.map((e) => (
                <ContainerListagem
                  key={e.id}
                  dados={e}
                  preview={e.preview}
                  onClickDel={() => deletar(e)}
                  onClickEdit={() => navigate(`/editar/equipamento/${e.id}`, { state: e })}
                />
              ))
            ) : (
              <p className="text-xl">Nenhum equipamento encontrado{search ? ` para "${search}"` : ""}.</p>
            )}
          </section>

          {/* Paginação */}
          <Paginacao
            paginaAtual={paginaAtual}
            totalPaginas={totalPaginas}
            onMudarPagina={mudarPagina}
          />
        </>
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
              buscarEquipamentos(paginaAtual);
            }}
            onCancelar={() => setModalCadastroAberta(false)}
          />
        </ModalFormulario>
      )}
    </>
  );
}