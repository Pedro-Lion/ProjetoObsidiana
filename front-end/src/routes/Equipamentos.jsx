import { useEffect, useState, useRef } from "react";
import { ContainerListagem } from "../components/Containers/ContainerListagem";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { api } from "../api.js";
import { useNavigate } from "react-router-dom";
import { Modal } from "../components/Modal/Modal.jsx";

export function Equipamentos() {
  const navigate = useNavigate();
  // ref para guardar urls criadas (objectURLs) e limpar quando desmontar o componente
  const previewsRef = useRef([]);

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState("");
  // Estados do modal
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  useEffect(() => {
    let mounted = true;
    async function buscar() {
      setLoading(true);
      setError(null);
      try {
        const resposta = await api.get("/equipamento", {
          headers: {
            Authorization: "Bearer " + sessionStorage.getItem("token"),
          },
        });
        if (!mounted) return;

        if (resposta.status === 200 && Array.isArray(resposta.data)) {
          // carrega previews (faz fetch dos blobs e cria objectURLs)
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
        if (mounted) setLoading(false);
      }
    }
    buscar();

    return () => {
      mounted = false;
      // limpa object URLs quando desmontar
      if (previewsRef.current && previewsRef.current.length) {
        previewsRef.current.forEach((u) => {
          try {
            URL.revokeObjectURL(u);
          } catch (e) { }
        });
        previewsRef.current = [];
      }
    };
  }, []);

  const API_BASE = import.meta.env.VITE_API_BASE_URL || "";

  async function carregarPreviews(equipamentos) {
    // limpa previews antigos
    if (previewsRef.current && previewsRef.current.length) {
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
          headers: { Authorization: token ? ("Bearer " + token) : "" },
        });

        if (!resp.ok) {
          console.warn(`Preview não disponível para equipamento ${eq.id}: ${resp.status}`);
          return { ...eq, preview: null };
        }

        // checando o content-type para garantir que veio imagem
        const ctype = resp.headers.get("content-type") || "";
        if (!ctype.startsWith("image/")) {
          console.warn(`Resposta não é imagem para equipamento ${eq.id}, content-type=${ctype}`);
          return { ...eq, preview: null };
        }

        const blob = await resp.blob();
        const objectUrl = URL.createObjectURL(blob);
        previewsRef.current.push(objectUrl);
        return { ...eq, preview: objectUrl };
      } catch (err) {
        console.error("Erro ao buscar preview do equipamento", eq.id, err);
        return { ...eq, preview: null };
      }
    });

    return Promise.all(promessas);
  }



  const refresh = async () => {
    setLoading(true);
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
      setError("Erro ao atualizar lista.");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = (id) => {
    setModalTitulo("Confirmar exclusão");
    setModalDescricao("Tem certeza que deseja excluir este equipamento?");
    setModalActions(
      <>
        <button
          className="bg-red-500 text-white px-4 py-2 rounded mr-3"
          onClick={async () => {
            try {
              const resposta = await api.delete(`/equipamento/${id}`, {
                headers: {
                  Authorization: "Bearer " + sessionStorage.getItem("token"),
                },
              });
              if (resposta.status === 200 || resposta.status === 204) {
                // remover do state local e limpar a preview do item removido
                setData((prev) => {
                  const removed = prev.find((p) => p.id === id);
                  if (removed && removed.preview) {
                    try {
                      URL.revokeObjectURL(removed.preview);
                      // também remove da lista de previewsRef caso ainda esteja lá
                      previewsRef.current = previewsRef.current.filter((u) => u !== removed.preview);
                    } catch (e) { }
                  }
                  return prev.filter((item) => item.id !== id);
                });
              } else {
                await refresh();
              }
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
              setModalOpen(true);
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

  const filtered = data.filter((e) => {
    if (!search) return true;
    const q = search.toLowerCase();
    return (
      (e.nome || "").toLowerCase().includes(q) ||
      (e.categoria || "").toLowerCase().includes(q) ||
      (e.marca || "").toLowerCase().includes(q)
    );
  });

  if (loading) return <p>Carregando equipamentos...</p>;
  if (error) return <p className="text-red-500">{error}</p>;
  if (data.length === 0)
    return (
      <>
        <div className="flex items-center justify-between">
          <h1 className="text-4xl font-medium">Equipamentos</h1>

          <BotaoPrimario
            titulo="+ Novo equipamento"
            className="mb-0 mt-0"
            onClick={() => navigate("/cadastro/equipamentos")}
          />
        </div>

        <p className="mt-4">Nenhum equipamento cadastrado.</p>
      </>
    );

  return (
    <>
      <div className="flex items-center justify-between">
        <h1 className="text-4xl font-medium">Equipamentos</h1>

        <div className="flex gap-3 content-between">
          <InputBordaLabel
            type="text"
            titulo="Buscar"
            placeholder="Nome, categoria ou marca"
            value={search}
            onInput={(e) => setSearch(e.target.value)}
            className="w-72"
          />
          <BotaoPrimario
            titulo="+ Novo equipamento"
            className="mb-0 mt-0"
            onClick={() => navigate("/cadastro/equipamentos")}
          />
        </div>
      </div>

      <section className="h-full mt-5 space-y-3">
        {filtered.length !== 0 ? (
          filtered.map((e) => (
            <div className="pr-5 flex items-center" key={e.id}>

              <ContainerListagem
                dados={e}
                onClickEdit={() =>
                  navigate(`/editar/equipamento/${e.id}`, { state: e })
                }
                onClickDel={() => handleDelete(e.id)}
              />
            </div>
          ))
        ) : (
          <p className="text-xl italic text-gray-700">
            Nenhum equipamento corresponde à sua busca.
          </p>
        )}
      </section>

      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}