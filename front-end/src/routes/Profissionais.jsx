import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { api } from "../api";
import { ContainerProfissional } from "../components/Containers/ContainerProfissional";
import { Modal } from "../components/Modal/Modal.jsx";
import { ModalFormulario } from "../components/Modal/ModalFormulario.jsx";
import { CadastroProfissionais } from "./CadastroProfissionais.jsx";
import { Paginacao } from "../components/Paginacao.jsx";

const ITENS_POR_PAGINA = 5;

export function Profissionais() {
  const navigate = useNavigate();
  const [profissionais, setProfissionais] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

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

  async function getProfissionais(pagina = 0, termo = search) {
    setLoading(true);
    try {
      const request = await api.get("/profissional/paginado", {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
        params: { page: pagina, size: ITENS_POR_PAGINA, busca: termo },
      });
      if (request.status === 200) {
        const paginaData = request.data;
        setProfissionais(paginaData.content);
        setTotalPaginas(paginaData.totalPages);
        setTotalElementos(paginaData.totalElements);
        setPaginaAtual(paginaData.number);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }

  // Debounce: busca no backend 400ms após parar de digitar
  useEffect(() => {
    const timer = setTimeout(() => {
      getProfissionais(0, search);
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    getProfissionais(0, "");
  }, []);

  function mudarPagina(novaPagina) {
    if (novaPagina < 0 || novaPagina >= totalPaginas) return;
    getProfissionais(novaPagina);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  const deletar = (id) => {
    setModalTitulo("Confirmar exclusão");
    setModalDescricao("Tem certeza que deseja excluir este profissional?");
    setModalActions(
      <>
        <button
          className="bg-red-500 text-white px-4 py-2 rounded mr-3"
          onClick={async () => {
            try {
              const resposta = await api.delete(`/profissional/${id}`, {
                headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
              });
              if (resposta.status === 200 || resposta.status === 204) {
                const novaPagina = profissionais.length === 1 && paginaAtual > 0
                  ? paginaAtual - 1
                  : paginaAtual;
                setModalOpen(false);
                getProfissionais(novaPagina);
              }
            } catch (err) {
              console.error(err);
              setModalTitulo("Erro");
              setModalDescricao("Não foi possível excluir. Tente novamente.");
              setModalActions(
                <button className="bg-gray-300 px-4 py-2 rounded" onClick={() => setModalOpen(false)}>
                  Fechar
                </button>
              );
              setModalOpen(true);
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

  // Filtro local dentro da página atual
  const profissionaisFiltrados = profissionais.filter((p) => {
    if (!search.trim()) return true;
    const termo = search.toLowerCase();
    const campos = [p.nome, p.disponibilidade, p.contato, p.funcao];
    return campos.filter(Boolean).some((c) => c.toLowerCase().includes(termo));
  });

  return (
    <>
      <div className="flex justify-between">
        <h1>Profissionais</h1>
        <BotaoPrimario
          titulo="+ Novo profissional"
          onClick={() => setModalCadastroAberta(true)}
        />
      </div>

      <div className="w-full pr-5">
        <InputBordaLabel
          type="text"
          titulo="Buscar"
          placeholder="Nome, disponibilidade, contato..."
          value={search}
          onInput={(e) => setSearch(e.target.value)}
          className=""
        />
      </div>

      {loading && <p className="text-xl">Carregando profissionais...</p>}

      {!loading && (
        <>
          {totalElementos > 0 && (
            <p className="text-slate-400 text-[1.1rem] mb-4">
              {totalElementos} profissional{totalElementos !== 1 ? "is" : ""} cadastrado{totalElementos !== 1 ? "s" : ""}
            </p>
          )}

          <section>
            {profissionais.length !== 0 ? (
              profissionais.map((p) => (
                <ContainerProfissional
                  key={p.id}
                  dados={p}
                  onClickDel={() => deletar(p.id)}
                  onClickEdit={() => navigate(`/editar/profissional/${p.id}`, { state: p })}
                />
              ))
            ) : (
              <p className="text-xl">Nenhum profissional encontrado{search ? ` para "${search}"` : ""}.</p>
            )}
          </section>

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

      {/* Modal de cadastro de profissional */}
      {modalCadastroAberta && (
        <ModalFormulario
          titulo="Novo Profissional"
          onFechar={() => setModalCadastroAberta(false)}
        >
          <CadastroProfissionais
            onSucesso={() => {
              setModalCadastroAberta(false);
              getProfissionais(paginaAtual);
            }}
            onCancelar={() => setModalCadastroAberta(false)}
          />
        </ModalFormulario>
      )}
    </>
  );
}