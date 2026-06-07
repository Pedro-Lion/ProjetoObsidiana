import { useEffect, useState, useRef } from "react";
import { ContainerListagem } from "../components/Containers/ContainerListagem";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { SelectBordaLabel } from "../components/Inputs/SelectBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api.js";
import { useNavigate, useLocation } from "react-router-dom";
import { Modal } from "../components/Modal/Modal.jsx";
import { ModalFormulario } from "../components/Modal/ModalFormulario.jsx";
import { CadastroEquipamentos } from "./CadastroEquipamento.jsx";
import { Paginacao } from "../components/Paginacao/Paginacao.jsx";

const ITENS_POR_PAGINA = 5;

export function Equipamentos() {
  const navigate = useNavigate();
  const location = useLocation();
  const previewsRef = useRef([]);

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState("");

  // Ordenação local: campo e direção
  const [ordenarPor, setOrdenarPor] = useState("nome");
  const [direcaoOrdem, setDirecaoOrdem] = useState("asc");

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

  // Highlight: ID do item recém cadastrado/editado para animar a borda
  const [highlightId, setHighlightId] = useState(null);

  // Guarda o state de navegação no mount para ler highlightId/pagina vindos do formulário de edição
  const initialNavStateRef = useRef(location.state);

  // Fallback alinhado com o do api.js — sem isso, quando VITE_API_BASE_URL não está
  // definida o fetch ia para o próprio Vite (5173) e voltava o index.html (200 text/html),
  // o que travava o preview da imagem com !ctype.startsWith("image/").
  const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

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
        // Cache-buster com o nome do arquivo: garante que, ao trocar a imagem,
        // a URL muda e o browser não devolve uma resposta cacheada (304 vinha
        // sendo capturado como !resp.ok, zerando o preview).
        const url = `${API_BASE}/equipamento/${eq.id}/imagem?v=${encodeURIComponent(eq.nomeArquivoImagem)}`;
        const resp = await fetch(url, {
          method: "GET",
          cache: "no-store",
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

  // Debounce: busca no backend 400ms após parar de digitar.
  // Na primeira execução (search ainda vazio), verifica se há highlightId vindo do formulário
  // de edição via rota e carrega a página correta em vez de sempre começar na página 0.
  useEffect(() => {
    if (!search) return;
    const timer = setTimeout(() => {
      buscarEquipamentos(0, search);
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    const initState = initialNavStateRef.current;
    if (initState?.highlightId) {
      setHighlightId(initState.highlightId);
      buscarEquipamentos(initState.pagina ?? 0, "");
    } else {
      buscarEquipamentos(0, "");
    }
    return () => {
      if (previewsRef.current?.length) {
        previewsRef.current.forEach((u) => { try { URL.revokeObjectURL(u); } catch (e) { } });
        previewsRef.current = [];
      }
    };
  }, []);

  // Limpa o highlight após a animação terminar (~2.2s)
  useEffect(() => {
    if (!highlightId) return;
    const timer = setTimeout(() => setHighlightId(null), 2200);
    return () => clearTimeout(timer);
  }, [highlightId]);

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

  /* ── Filtro local (dentro da página atual) ──
   * Aplicado sobre os itens já retornados pelo backend para a página atual.
   * Garante consistência enquanto o debounce aguarda a resposta da API.
   * Os campos cobertos aqui espelham os campos da query findByBusca no backend. */
  const filtrado = data
    .filter((e) => {
      if (!search.trim()) return true;
      const termo = search.toLowerCase();
      const campos = [
        e.nome,
        e.categoria,
        e.marca,
        e.modelo,
        e.numeroSerie,
        e.valorPorHora?.toString(),
        e.quantidadeTotal?.toString(),
        e.quantidadeDisponivel?.toString(),
      ];
      return campos.filter(Boolean).some((c) => c.toLowerCase().includes(termo));
    })
    // Ordenação local aplicada sobre os resultados filtrados
    .sort((a, b) => {
      let va, vb;
      if (ordenarPor === "nome") {
        va = a.nome?.toLowerCase() ?? "";
        vb = b.nome?.toLowerCase() ?? "";
      } else if (ordenarPor === "valorPorHora") {
        va = a.valorPorHora ?? 0;
        vb = b.valorPorHora ?? 0;
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
      <h1>Equipamentos</h1>

      <div className="flex flex-col-reverse justify-evenly items-start md:flex-row md:justify-between md:items-baseline-last">
        <div className="w-full md:w-3/4">
          <InputBordaLabel
            type="text"
            titulo="Buscar"
            placeholder="Nome, categoria ou marca"
            value={search}
            onInput={(e) => setSearch(e.target.value)}
            className=""
          />
        </div>
        <div className="block md:hidden border-b border-slate-300 w-full mt-3.5 mb-2"/>
        <BotaoPrimario
          titulo="+ Novo equipamento"
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
          options={[
            { value: "nome", label: "Nome" },
            { value: "valorPorHora", label: "Valor por hora" },
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

      {loading && <p className="text-xl">Carregando equipamentos...</p>}
      {error && <p className="text-red-500">{error}</p>}

      {!loading && !error && (
        <>
          {totalElementos > 0 && (
            <p className="text-slate-400 text-[1.1rem] mb-4">
              {totalElementos} equipamento{totalElementos !== 1 ? "s" : ""} encontrado{totalElementos !== 1 ? "s" : ""}
            </p>
          )}

          <section className="flex flex-wrap gap-5">
            {filtrado.length !== 0 ? (
              filtrado.map((e) => (
                <ContainerListagem
                  key={e.id}
                  dados={e}
                  preview={e.preview}
                  onClickDel={() => deletar(e)}
                  onClickEdit={() => navigate(`/editar/equipamento/${e.id}`, { state: { ...e, paginaOrigem: paginaAtual } })}
                  highlight={highlightId !== null && e.id === highlightId}
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
            onSucesso={(novoId) => {
              setModalCadastroAberta(false);
              // Novo item vai para a última página; +1 porque o total ainda não foi atualizado
              const ultimaPagina = Math.max(0, Math.ceil((totalElementos + 1) / ITENS_POR_PAGINA) - 1);
              setHighlightId(novoId);
              buscarEquipamentos(ultimaPagina);
            }}
            onCancelar={() => setModalCadastroAberta(false)}
          />
        </ModalFormulario>
      )}
    </>
  );
}