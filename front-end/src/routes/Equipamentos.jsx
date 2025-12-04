import { useEffect, useState } from "react";
import { ContainerListagem } from "../components/Containers/ContainerListagem";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { api } from "../api.js";
import { useNavigate } from "react-router-dom";

export function Equipamentos() {
  const navigate = useNavigate();

  const [data, setData] = useState([]); // array de objetos Equipamento
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState("");

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

  const handleDelete = async (id) => {
    const ok = window.confirm(
      "Tem certeza que deseja excluir este equipamento?"
    );
    if (!ok) return;
    try {
      const resposta = await api.delete(`/equipamento/${id}`, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
      if (resposta.status === 200 || resposta.status === 204) {
        setData((prev) => prev.filter((item) => item.id !== id));
      } else {
        await refresh();
      }
    } catch (err) {
      console.error(err);
      alert("Não foi possível excluir. Verifique console.");
    }
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
        {filtered.length != 0 ? (
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
    </>
  );
}
