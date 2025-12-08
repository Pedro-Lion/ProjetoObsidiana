import { useEffect, useState } from "react";
import { ContainerListagem } from "../components/Containers/ContainerListagem";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { api } from "../api.js";
import { useNavigate } from "react-router-dom";
import { Modal } from "../components/Modal/Modal.jsx";

export function Equipamentos() {
  const navigate = useNavigate();

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
          setData(resposta.data);
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
    };
  }, []);

  const refresh = async () => {
    setLoading(true);
    try {
      const resposta = await api.get("/equipamento", {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
      if (resposta.status === 200 && Array.isArray(resposta.data)) {
        setData(resposta.data);
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
                setData((prev) => prev.filter((item) => item.id !== id));
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

        <div className="flex gap-3 items-end">
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
              <InputCheckbox className="mr-3" />
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
