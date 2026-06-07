import { useEffect, useState, useRef } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { SelectBordaLabel } from "../components/Inputs/SelectBordaLabel";
import { useNavigate, useLocation } from "react-router-dom";
import { api } from "../api";
import { CardServico } from "../components/Cards/CardServico";
import { Modal } from "../components/Modal/Modal.jsx";
import { Paginacao } from "../components/Paginacao/Paginacao.jsx";

const ITENS_POR_PAGINA = 6;

export function Servicos() {
  const navigate = useNavigate();
  const location = useLocation();
  const [servicos, setServicos] = useState([]);

  // Highlight: ID do item recém cadastrado/editado para scroll após retorno
  const [highlightId, setHighlightId] = useState(null);
  const initialNavStateRef = useRef(location.state);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  // Ordenação local: campo e direção
  const [ordenarPor, setOrdenarPor] = useState("nome");
  const [direcaoOrdem, setDirecaoOrdem] = useState("asc");

  // Paginação
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);

  // Estados do modal
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  async function buscarServicos(pagina = 0, termo = search) {
    setLoading(true);
    try {
      const request = await api.get("/servico/paginado", {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
        params: {
          page: pagina,
          size: ITENS_POR_PAGINA,
          busca: termo,
          // Ordenação server-side: garante que itens de outras páginas migrem corretamente
          // ao trocar campo/direção, em vez de só reordenar a página atual no client.
          ordenarPor,
          direcao: direcaoOrdem,
        },
      });
      if (request.status === 200) {
        const paginaData = request.data;
        setServicos(paginaData.content);
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
  // Na primeira execução, lê highlightId/pagina vindos do formulário de edição via rota.
  useEffect(() => {
    const timer = setTimeout(() => {
      const initState = initialNavStateRef.current;
      if (initState?.highlightId) {
        setHighlightId(initState.highlightId);
        buscarServicos(initState.pagina ?? 0, search);
        initialNavStateRef.current = null;
      } else {
        buscarServicos(0, search);
      }
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  // Refaz a busca imediatamente quando a ordenação muda (sem debounce — ação intencional).
  // useRef impede que esse efeito dispare também no mount, onde o useEffect de busca já roda.
  const pularPrimeiraOrdenacao = useRef(true);
  useEffect(() => {
    if (pularPrimeiraOrdenacao.current) {
      pularPrimeiraOrdenacao.current = false;
      return;
    }
    buscarServicos(0, search);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ordenarPor, direcaoOrdem]);

  // Limpa o highlight após a animação/scroll (~2.2s)
  useEffect(() => {
    if (!highlightId) return;
    const timer = setTimeout(() => setHighlightId(null), 2200);
    return () => clearTimeout(timer);
  }, [highlightId]);

  function mudarPagina(novaPagina) {
    if (novaPagina < 0 || novaPagina >= totalPaginas) return;
    buscarServicos(novaPagina);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  const deletar = (id) => {
    setModalTitulo("Confirmar exclusão");
    setModalDescricao("Tem certeza que deseja excluir este serviço?");
    setModalActions(
      <>
        <button
          className="bg-red-500 text-white px-4 py-2 rounded mr-3"
          onClick={async () => {
            try {
              const resposta = await api.delete(`/servico/${id}`, {
                headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
              });
              if (resposta.status === 200 || resposta.status === 204) {
                const novaPagina = servicos.length === 1 && paginaAtual > 0
                  ? paginaAtual - 1
                  : paginaAtual;
                setModalOpen(false);
                buscarServicos(novaPagina);
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
  const servicosFiltrados = servicos
    .filter((s) => {
      if (!search.trim()) return true;
      const termo = search.toLowerCase();
      const camposServico = [s.nome, s.descricao, s.valorPorHora?.toString(), s.horas?.toString()];
      const camposEquipamentos = (s.equipamentos || []).flatMap((e) => [
        e.nome, e.categoria, e.valorPorHora?.toString(),
      ]);
      return [...camposServico, ...camposEquipamentos]
        .filter(Boolean)
        .some((campo) => campo.toLowerCase().includes(termo));
    });
  // Ordenação é aplicada no back-end (ORDER BY no SQL antes da paginação),
  // então não há mais .sort() local — o servidor já devolve os itens na ordem correta.

  return (
    <>
      <h1>Serviços</h1>

      <div className="flex flex-col-reverse justify-evenly items-start md:flex-row md:justify-between md:items-baseline-last">
        <div className="w-full md:w-3/4">
          <InputBordaLabel
            type="text"
            titulo="Buscar"
            placeholder="Nome, descrição, equipamento..."
            value={search}
            onInput={(e) => setSearch(e.target.value)}
            className=""
          />
        </div>
        <div className="block md:hidden border-b border-slate-300 w-full mt-3.5 mb-2"/>
        <BotaoPrimario
          titulo="+ Novo serviço"
          onClick={() => navigate("/cadastro/servicos", { state: { totalElementos, itensPorPagina: ITENS_POR_PAGINA } })}
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
          options={[
            { value: "nome", label: "Nome" },
            { value: "valorPorHora", label: "Valor por hora" },
            { value: "horas", label: "Duração" },
          ]}
        />
        <SelectBordaLabel
          titulo="Direção"
          className="w-auto"
          value={direcaoOrdem}
          onChange={(e) => setDirecaoOrdem(e.target.value)}
          options={[
            { value: "asc", label: "Crescente (A→Z)" },
            { value: "desc", label: "Decrescente (Z→A)" },
          ]}
        />
      </div>

      {loading && <p className="text-xl">Carregando serviços...</p>}

      {!loading && (
        <>
          {totalElementos > 0 && (
            <p className="text-slate-400 text-[1.1rem] mb-4">
              {totalElementos} serviço{totalElementos !== 1 ? "s" : ""} encontrado{totalElementos !== 1 ? "s" : ""}
            </p>
          )}

          <section className="flex flex-wrap gap-5">
            {servicosFiltrados.length !== 0 ? (
              servicosFiltrados.map((s) => (
                <CardServico
                  key={s.id}
                  dados={s}
                  onClickDel={() => deletar(s.id)}
                  onClickEdit={() => navigate(`/editar/servico/${s.id}`, { state: { ...s, paginaOrigem: paginaAtual } })}
                  highlight={highlightId !== null && s.id === highlightId}
                />
              ))
            ) : (
              <p className="text-xl">Nenhum serviço encontrado{search ? ` para "${search}"` : ""}.</p>
            )}
          </section>

          <Paginacao
            paginaAtual={paginaAtual}
            totalPaginas={totalPaginas}
            onMudarPagina={mudarPagina}
          />
        </>
      )}

      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}