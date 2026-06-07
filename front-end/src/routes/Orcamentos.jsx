import { useNavigate, useLocation } from "react-router-dom";
import { useEffect, useState, useRef } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { SelectBordaLabel } from "../components/Inputs/SelectBordaLabel";
import { api } from "../api";
import { CardOrcamento } from "../components/Cards/CardOrcamento";
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";
import { Modal } from "../components/Modal/Modal.jsx";
import { Paginacao } from "../components/Paginacao/Paginacao.jsx";
import { gerarRelatorioExcel } from "../features/orcamento/utils/gerarRelatorioExcel.js";

import { getAccessToken } from "../../utils/getAccessToken";

const ITENS_POR_PAGINA = 6;

export function Orcamentos() {
  const navigate = useNavigate();
  const location = useLocation();
  const { instance } = useMsal();
  const account = instance.getActiveAccount();

  // Lê o filtro de status enviado pela KPI do dashboard (ex: "em análise", "confirmado", "cancelado").
  // Se o usuário acessar a página diretamente (sem vir de uma KPI), o valor é vazio.
  const statusInicial = location.state?.statusFilter || "";

  const [orcamentos, setOrcamentos] = useState([]);
  const [search, setSearch] = useState(statusInicial);
  const [loading, setLoading] = useState(true);

  // Ordenação local: campo e direção
  const [ordenarPor, setOrdenarPor] = useState("titulo");
  const [direcaoOrdem, setDirecaoOrdem] = useState("asc");

  // Filtro por intervalo de datas (formato YYYY-MM-DD, nativo do input type="date")
  const [filtroDataDe, setFiltroDataDe] = useState("");
  const [filtroDataAte, setFiltroDataAte] = useState("");

  // Paginação
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);

  // Highlight: ID do item recém cadastrado/editado para scroll após retorno
  const [highlightId, setHighlightId] = useState(null);
  const initialNavStateRef = useRef(location.state);

  // Estados do modal
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  // ── Exportar relatório anual em Excel ─────────────────────────────────────
  // Busca TODOS os orçamentos (sem paginação) e gera o .xlsx para o ano atual.
  const [exportando, setExportando] = useState(false);

  async function exportarRelatorio() {
    setExportando(true);
    try {
      const resposta = await api.get("/orcamento", {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
      if (resposta.status === 200) {
        const anoAtual = new Date().getFullYear();
        gerarRelatorioExcel(resposta.data, anoAtual);
      }
    } catch (err) {
      console.error("Erro ao exportar relatório:", err);
    } finally {
      setExportando(false);
    }
  }

  async function buscarOrcamentos(pagina = 0, termo = search) {
    setLoading(true);
    try {
      const request = await api.get("/orcamento/paginado", {
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
        setOrcamentos(paginaData.content);
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
        buscarOrcamentos(initState.pagina ?? 0, search);
        initialNavStateRef.current = null;
      } else {
        buscarOrcamentos(0, search);
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
    buscarOrcamentos(0, search);
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
    buscarOrcamentos(novaPagina);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  // Filtro local dentro da página atual.
  // Aplicado sobre os itens já retornados pelo backend, para consistência
  // enquanto o debounce aguarda a resposta da API.
  // Os campos cobertos espelham os campos da query findByBusca no backend.
  const orcamentosFiltrados = orcamentos
    .filter((o) => {
      // Filtro por texto (busca livre)
      if (search.trim()) {
        const termo = search.toLowerCase();
        const camposOrcamento = [
          o.titulo,
          o.localEvento,
          o.observacoes,
          o.status,
          o.valorTotal?.toString(),
        ];
        const camposServicos = (o.servicos || []).flatMap((s) => [
          s.nome, s.descricao,
        ]);
        const camposEquipamentos = (o.equipamentos || []).flatMap((e) => [
          e.nome, e.categoria, e.marca,
        ]);
        const bateu = [...camposOrcamento, ...camposServicos, ...camposEquipamentos]
          .filter(Boolean)
          .some((c) => c.toLowerCase().includes(termo));
        if (!bateu) return false;
      }

      // Filtro por intervalo de datas — usa o campo dataInicio do orçamento
      if (filtroDataDe || filtroDataAte) {
        if (!o.dataInicio) return false;
        const dataOrcamento = new Date(o.dataInicio);
        if (filtroDataDe) {
          // Inclui orçamentos a partir do início do dia selecionado
          const de = new Date(filtroDataDe + "T00:00:00");
          if (dataOrcamento < de) return false;
        }
        if (filtroDataAte) {
          // Inclui orçamentos até o final do dia selecionado
          const ate = new Date(filtroDataAte + "T23:59:59");
          if (dataOrcamento > ate) return false;
        }
      }

      return true;
    });
  // Ordenação é aplicada no back-end (ORDER BY no SQL antes da paginação),
  // então não há mais .sort() local — o servidor já devolve os itens na ordem correta.

  async function deletar(orcamento) {
    setModalTitulo("Confirmar exclusão");
    setModalDescricao(
      `Tem certeza que deseja excluir o orçamento "${orcamento.titulo || orcamento.localEvento || "este orçamento"}"?`
    );
    setModalActions(
      <>
        <button
          className="bg-red-500 text-white px-4 py-2 rounded mr-3"
          onClick={async () => {
            // Deleta evento no Outlook, se houver
            if (orcamento.idCalendar && account) {
              try {
                const response = await instance.acquireTokenSilent({
                  ...loginRequest,
                  account: account,
                });
                await fetch(
                  `https://graph.microsoft.com/v1.0/me/calendar/events/${orcamento.idCalendar}`,
                  {
                    method: "DELETE",
                    headers: {
                      Authorization: `Bearer ${response.accessToken}`,
                      "Content-Type": "application/json",
                    },
                  }
                );
              } catch (error) {
                console.error("Erro ao excluir evento do calendário:", error);
              }
            }

            try {
              const resposta = await api.delete(`/orcamento/${orcamento.id}`, {
                headers: {
                  Authorization: "Bearer " + sessionStorage.getItem("token"),
                },
              });
              if (resposta.status === 200 || resposta.status === 204) {
                const novaPagina =
                  orcamentos.length === 1 && paginaAtual > 0
                    ? paginaAtual - 1
                    : paginaAtual;
                setModalOpen(false);
                buscarOrcamentos(novaPagina, search);
              }
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
  }

  return (
    <>
      <h1>Orçamentos</h1>

      <div className="flex flex-col-reverse justify-evenly items-start md:flex-row md:justify-between md:items-baseline-last">
        <div className="w-full md:w-3/4">
          <InputBordaLabel
            type="text"
            titulo="Buscar"
            placeholder="Local, descrição, status, serviço, equipamento..."
            value={search}
            onInput={(e) => setSearch(e.target.value)}
            className=""
          />
        </div>
        <div className="block md:hidden border-b border-slate-300 w-full mt-3.5 mb-2"/>
        <div className="flex gap-2 w-full md:w-fit">
          {/* Exporta todos os orçamentos do ano atual para um arquivo .xlsx */}
          <BotaoSecundario
            titulo={exportando ? "Exportando..." : "Exportar anual"}
            icone="bi bi-download"
            onClick={exportarRelatorio}
            className="w-full md:w-fit"
          />
          <BotaoPrimario
            titulo="+ Novo orçamento"
            onClick={() => navigate("/cadastro/orcamentos", { state: { totalElementos, itensPorPagina: ITENS_POR_PAGINA } })}
            className="w-full md:w-fit"
          />
        </div>
      </div>

      {/* Controles de ordenação e filtro por período */}
      <div className="flex flex-wrap gap-3 mt-2 items-end">
        <SelectBordaLabel
          titulo="Ordenar por"
          className="w-48"
          value={ordenarPor}
          onChange={(e) => setOrdenarPor(e.target.value)}
          options={[
            { value: "titulo", label: "Título" },
            { value: "localEvento", label: "Local" },
            { value: "dataInicio", label: "Data" },
            { value: "valorTotal", label: "Valor total" },
            { value: "status", label: "Status" },
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
        {/* Filtro de período: exibe somente orçamentos entre as duas datas */}
        <InputBordaLabel
          type="date"
          titulo="De"
          className="w-auto"
          value={filtroDataDe}
          onInput={(e) => setFiltroDataDe(e.target.value)}
        />
        <InputBordaLabel
          type="date"
          titulo="Até"
          className="w-auto"
          value={filtroDataAte}
          onInput={(e) => setFiltroDataAte(e.target.value)}
        />
        {/* Botão para limpar o filtro de datas quando ambos estiverem preenchidos */}
        {(filtroDataDe || filtroDataAte) && (
          <button
            className="text-indigo-500 text-[1rem] underline self-center mt-1"
            onClick={() => { setFiltroDataDe(""); setFiltroDataAte(""); }}
          >
            Limpar datas
          </button>
        )}
      </div>

      {loading && <p className="text-xl">Carregando orçamentos...</p>}

      {!loading && (
        <>
          {totalElementos > 0 && (
            <p className="text-slate-400 text-[1.1rem] mb-4">
              {totalElementos} orçamento{totalElementos !== 1 ? "s" : ""}{" "}
              encontrado{totalElementos !== 1 ? "s" : ""}
            </p>
          )}

          <section className="flex flex-wrap gap-5">
            {orcamentosFiltrados.length !== 0 ? (
              orcamentosFiltrados.map((o) => (
                <CardOrcamento
                  key={o.id}
                  dados={o}
                  onClickDel={() => deletar(o)}
                  onClickEdit={() => navigate(`/editar/orcamento/${o.id}`, { state: { ...o, paginaOrigem: paginaAtual } })}
                  highlight={highlightId !== null && o.id === highlightId}
                />
              ))
            ) : (
              <p className="text-xl">
                Nenhum orçamento encontrado
                {search ? ` para "${search}"` : ""}.
              </p>
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