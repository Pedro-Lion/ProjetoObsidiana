import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api";
import { CardOrcamento } from "../components/Cards/CardOrcamento";

export function Orcamentos() {
  const navigate = useNavigate();
  const [orcamentos, setOrcamentos] = useState([]);

  useEffect(() => {
    async function getOrcamentos() {
      const request = await api.get("/orcamento", {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if ((request.status = 200)) {
        setOrcamentos(request.data);
      }
    }
    getOrcamentos();
  }, []);

  async function deletar(id) {
    const ok = window.confirm("Tem certeza que deseja excluir este orçamento?");
    if (!ok) return;
    try {
      const resposta = await api.delete(`/orcamento/${id}`, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
      if (resposta.status === 200 || resposta.status === 204) {
        setOrcamentos(orcamentos.filter((o) => o.id != id));
      }
    } catch (err) {
      console.error(err);
      alert("Não foi possível excluir.");
    }
  }

  return (
    <>
      <div className="flex justify-between">
        <h1>Orçamentos</h1>
        <BotaoPrimario
          titulo="+ Novo orçamento"
          onClick={() => navigate("/cadastro/orcamento")}
        />
      </div>

      <section className="flex flex-wrap gap-5">
        {orcamentos.length != 0 ? (
          orcamentos.map((o) => (
            <CardOrcamento
              key={o.id}
              dados={o}
              onClickDel={() => deletar(o.id)}
            />
          ))
        ) : (
          <p className="text-xl">Nenhum orçamento cadastrado.</p>
        )}
      </section>
    </>
  );
}
