import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { useNavigate } from "react-router-dom";
import { api } from "../api";
import { CardServico } from "../components/Cards/CardServico";

export function Servicos() {
  const navigate = useNavigate();
  const [servicos, setServicos] = useState([]);

  useEffect(() => {
    async function getServicos() {
      const request = await api.get("/servico", {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status = 200) {
        setServicos(request.data);
      }
    }
    getServicos();
  }, []);

  async function deletar(id) {
    const ok = window.confirm("Tem certeza que deseja excluir este serviço?");
    if (!ok) return;
    try {
      const resposta = await api.delete(`/servico/${id}`, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
      if (resposta.status === 200 || resposta.status === 204) {
        setServicos(servicos.filter((s) => s.id != id));
      }
    } catch (err) {
      console.error(err);
      alert("Não foi possível excluir.");
    }
  }

  return (
    <>
      <div className="flex justify-between">
        <h1>Serviços</h1>
        <BotaoPrimario
          titulo="+ Novo serviço"
          onClick={() => navigate("/cadastro/servicos")}
        />
      </div>

      <section>
        {servicos.length != 0 ? (
          servicos.map((s) => (
            <CardServico
              key={s.id}
              dados={s}
              onClickDel={() => deletar(s.id)}
            />
          ))
        ) : (
          <p className="text-xl">Nenhum serviço cadastrado.</p>
        )}
      </section>
    </>
  );
}
