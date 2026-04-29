import { useNavigate, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
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

  // Paginação
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);

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
        params: { page: pagina, size: ITENS_POR_PAGINA, busca: termo },
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

  // Debounce: busca no backend 400ms após parar de digitar
  useEffect(() => {
    const timer = setTimeout(() => {
      buscarOrcamentos(0, search);
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    // Usa o filtro inicial (vindo da KPI) ou vazio caso acesso direto
    buscarOrcamentos(0, statusInicial);
  }, []);

  function mudarPagina(novaPagina) {
    if (novaPagina < 0 || novaPagina >= totalPaginas) return;
    buscarOrcamentos(novaPagina);
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  // Filtro local dentro da página atual.
  // Aplicado sobre os itens já retornados pelo backend, para consistência
  // enquanto o debounce aguarda a resposta da API.
  // Os campos cobertos espelham os campos da query findByBusca no backend.
  const orcamentosFiltrados = orcamentos.filter((o) => {
    if (!search.trim()) return true;
    const termo = search.toLowerCase();
    const camposOrcamento = [
      o.localEvento,
      o.descricao,
      o.status,
      o.valorTotal?.toString(),
    ];
    const camposServicos = (o.servicos || []).flatMap((s) => [
      s.nome, s.descricao,
    ]);
    const camposEquipamentos = (o.equipamentos || []).flatMap((e) => [
      e.nome, e.categoria, e.marca,
    ]);
    return [...camposOrcamento, ...camposServicos, ...camposEquipamentos]
      .filter(Boolean)
      .some((c) => c.toLowerCase().includes(termo));
  });

  async function deletar(orcamento) {
    setModalTitulo("Confirmar exclusão");
    setModalDescricao(
      `Tem certeza que deseja excluir o orçamento "${orcamento.localEvento || "este orçamento"}"?`
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
            onClick={() => navigate("/cadastro/orcamentos")}
            className="w-full md:w-fit"
          />
        </div>
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