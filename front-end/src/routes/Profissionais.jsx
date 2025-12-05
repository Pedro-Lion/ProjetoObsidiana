import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api";
import { ContainerProfissional } from "../components/Containers/ContainerProfissional";

export function Profissionais() {
  const navigate = useNavigate();
  const [profissionais, setProfissionais] = useState([]);

  useEffect(() => {
    async function getServicos() {
      const request = await api.get("/profissional", {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if ((request.status = 200)) {
        setProfissionais(request.data);
      }
    }
    getServicos();
  }, []);

  async function deletar(id) {
    const ok = window.confirm("Tem certeza que deseja excluir este profissional?");
    if (!ok) return;
    try {
      const resposta = await api.delete(`/profissional/${id}`, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
      if (resposta.status === 200 || resposta.status === 204) {
        setProfissionais(profissionais.filter((p) => p.id != id));
      }
    } catch (err) {
      console.error(err);
      alert("Não foi possível excluir.");
    }
  }

  return (
    <>
      <div className="flex justify-between">
        <h1>Profissionais</h1>
        <BotaoPrimario
          titulo="+ Novo profissional"
          onClick={() => navigate("/cadastro/profissionais")}
        />
      </div>

      <section>
        {profissionais.length != 0 ? (
          profissionais.map((p) => (
            <ContainerProfissional
              key={p.id}
              dados={p}
              onClickEdit={() =>
                navigate(`/editar/profissional/${p.id}`, { state: p })
              }
              onClickDel={() => deletar(p.id)}
            />
          ))
        ) : (
          <p className="text-xl">Nenhum profissional cadastrado.</p>
        )}
      </section>
    </>
  );
}
