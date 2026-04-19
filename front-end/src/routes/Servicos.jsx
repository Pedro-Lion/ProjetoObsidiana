import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { useNavigate } from "react-router-dom";
import { api } from "../api";
import { CardServico } from "../components/Cards/CardServico";
import { Modal } from "../components/Modal/Modal.jsx";
import { Paginacao } from "../components/Paginacao.jsx";

const ITENS_POR_PAGINA = 6;

export function Servicos() {
  const navigate = useNavigate();
  const [servicos, setServicos] = useState([]);
  const [search, setSearch] = useState("");
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

  async function buscarServicos(pagina = 0, termo = search) {
    setLoading(true);
    try {
      const request = await api.get("/servico/paginado", {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
        params: { page: pagina, size: ITENS_POR_PAGINA, busca: termo },
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

  // Debounce: busca no backend 400ms após parar de digitar
  useEffect(() => {
    const timer = setTimeout(() => {
      buscarServicos(0, search);
    }, 400);
    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    buscarServicos(0, "");
  }, []);

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
  const servicosFiltrados = servicos.filter((s) => {
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

  return (
    <>
      <div className="flex justify-between">
        <h1>Serviços</h1>
        <BotaoPrimario
          titulo="+ Novo serviço"
          onClick={() => navigate("/cadastro/servicos")}
        />
      </div>

      <div className="w-full pr-5">
        <InputBordaLabel
          type="text"
          titulo="Buscar"
          placeholder="Nome, descrição, equipamento..."
          value={search}
          onInput={(e) => setSearch(e.target.value)}
          className=""
        />
      </div>

      {loading && <p className="text-xl">Carregando serviços...</p>}

      {!loading && (
        <>
          {totalElementos > 0 && (
            <p className="text-slate-400 text-[1.1rem] mb-4">
              {totalElementos} serviço{totalElementos !== 1 ? "s" : ""} cadastrado{totalElementos !== 1 ? "s" : ""}
            </p>
          )}

          <section className="flex flex-wrap gap-5">
            {servicos.length !== 0 ? (
              servicos.map((s) => (
                <CardServico key={s.id} dados={s} onClickDel={() => deletar(s.id)} />
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