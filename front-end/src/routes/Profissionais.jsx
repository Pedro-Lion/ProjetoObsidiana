import { useNavigate, useLocation } from "react-router-dom";
import { useEffect, useState, useRef } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { SelectBordaLabel } from "../components/Inputs/SelectBordaLabel";
import { api } from "../api";
import { ContainerProfissional } from "../components/Containers/ContainerProfissional";
import { Modal } from "../components/Modal/Modal.jsx";
import { ModalFormulario } from "../components/Modal/ModalFormulario.jsx";
import { CadastroProfissionais } from "./CadastroProfissionais.jsx";
import { Paginacao } from "../components/Paginacao/Paginacao.jsx";

const ITENS_POR_PAGINA = 5;

export function Profissionais() {
  const navigate = useNavigate();
  const location = useLocation();
  const [profissionais, setProfissionais] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  // Ordenação local: campo e direção
  const [ordenarPor, setOrdenarPor] = useState("");
  const [direcaoOrdem, setDirecaoOrdem] = useState("asc");

  // Paginação
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);

  // Highlight: ID do item recém cadastrado/editado para animar a borda
  const [highlightId, setHighlightId] = useState(null);

  // Guarda o state de navegação no mount para ler highlightId/pagina vindos do formulário de edição
  const initialNavStateRef = useRef(location.state);

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

  // Debounce: busca no backend 400ms após parar de digitar.
  // Na primeira execução (search ainda vazio), verifica se há highlightId vindo do formulário
  // de edição via rota e carrega a página correta em vez de sempre começar na página 0.
  useEffect(() => {
    const timer = setTimeout(() => {
      const initState = initialNavStateRef.current;
      if (initState?.highlightId) {
        setHighlightId(initState.highlightId);
        getProfissionais(initState.pagina ?? 0, search);
        initialNavStateRef.current = null; // consome o state uma única vez
      } else {
        getProfissionais(0, search);
      }
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  // Limpa o highlight após a animação terminar (~2.2s)
  useEffect(() => {
    if (!highlightId) return;
    const timer = setTimeout(() => setHighlightId(null), 2200);
    return () => clearTimeout(timer);
  }, [highlightId]);

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

  // Filtro local dentro da página atual (inclui categoria)
  const profissionaisFiltrados = profissionais
    .filter((p) => {
      if (!search.trim()) return true;
      const termo = search.toLowerCase();
      const campos = [p.nome, p.disponibilidade, p.contato, p.categoria, p.funcao];
      return campos.filter(Boolean).some((c) => c.toLowerCase().includes(termo));
    })
    // Ordenação local aplicada sobre os resultados filtrados
    .sort((a, b) => {
      if (!ordenarPor) return 0;
      let va, vb;
      if (ordenarPor === "nome") {
        va = a.nome?.toLowerCase() ?? "";
        vb = b.nome?.toLowerCase() ?? "";
      } else if (ordenarPor === "categoria") {
        va = a.categoria?.toLowerCase() ?? "";
        vb = b.categoria?.toLowerCase() ?? "";
      } else {
        return 0;
      }
      if (direcaoOrdem === "asc") return va > vb ? 1 : va < vb ? -1 : 0;
      return va < vb ? 1 : va > vb ? -1 : 0;
    });

  return (
    <>
      <h1>Profissionais</h1>

      <div className="flex flex-col-reverse justify-evenly items-start md:flex-row md:justify-between md:items-baseline-last">
        <div className="w-full md:w-3/4">
          <InputBordaLabel
            type="text"
            titulo="Buscar"
            placeholder="Nome, disponibilidade, contato, categoria..."
            value={search}
            onInput={(e) => setSearch(e.target.value)}
            className=""
          />
        </div>
        <div className="block md:hidden border-b border-slate-300 w-full mt-3.5 mb-2"/>
        <BotaoPrimario
          titulo="+ Novo profissional"
          onClick={() => setModalCadastroAberta(true)}
          className="w-full md:w-fit"
        />
      </div>

      {/* Controles de ordenação */}
      <div className="flex flex-wrap gap-3 mt-2 items-end">
        <SelectBordaLabel
          titulo="Ordenar por"
          className="w-48"
          value={ordenarPor}
          onChange={(e) => setOrdenarPor(e.target.value)}
          placeholder="Padrão"
          options={[
            { value: "nome", label: "Nome" },
            { value: "categoria", label: "Categoria" },
          ]}
        />
        <SelectBordaLabel
          titulo="Direção"
          className="w-40"
          value={direcaoOrdem}
          onChange={(e) => setDirecaoOrdem(e.target.value)}
          options={[
            { value: "asc", label: "Crescente (A→Z)" },
            { value: "desc", label: "Decrescente (Z→A)" },
          ]}
        />
      </div>

      {loading && <p className="text-xl">Carregando profissionais...</p>}

      {!loading && (
        <>
          {totalElementos > 0 && (
            <p className="text-slate-400 text-[1.1rem] mb-4">
              {totalElementos} profissional{totalElementos !== 1 ? "is" : ""} encontrado{totalElementos !== 1 ? "s" : ""}
            </p>
          )}

          <section>
            {profissionaisFiltrados.length !== 0 ? (
              profissionaisFiltrados.map((p) => (
                <ContainerProfissional
                  key={p.id}
                  dados={p}
                  onClickDel={() => deletar(p.id)}
                  onClickEdit={() => navigate(`/editar/profissional/${p.id}`, { state: { ...p, paginaOrigem: paginaAtual } })}
                  highlight={highlightId !== null && p.id === highlightId}
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
            onSucesso={(novoId) => {
              setModalCadastroAberta(false);
              // Novo item vai para a última página; +1 porque o total ainda não foi atualizado
              const ultimaPagina = Math.max(0, Math.ceil((totalElementos + 1) / ITENS_POR_PAGINA) - 1);
              setHighlightId(novoId);
              getProfissionais(ultimaPagina);
            }}
            onCancelar={() => setModalCadastroAberta(false)}
          />
        </ModalFormulario>
      )}
    </>
  );
}