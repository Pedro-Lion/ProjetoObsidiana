import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api";
import { CardOrcamento } from "../components/Cards/CardOrcamento";
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";

export function Orcamentos() {
  const navigate = useNavigate();
  const { instance } = useMsal();
  const account = instance.getActiveAccount();

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

  async function deletar(orcamento) {
    const ok = window.confirm("Tem certeza que deseja excluir este orçamento?");
    if (!ok) return;

    if (orcamento.idCalendar && account) {
      try {
        const response = await instance.acquireTokenSilent({
          ...loginRequest,
          account: account,
        });

        const accessToken = response.accessToken;

        await fetch(
          `https://graph.microsoft.com/v1.0/me/calendar/events/${orcamento.idCalendar}`,
          {
            method: "DELETE",
            headers: {
              Authorization: `Bearer ${accessToken}`,
              "Content-Type": "application/json",
            },
          }
        );
      } catch (error) {
        alert("Não foi possível excluir o evento associado ao orçamento.");
        return console.log(error);
      }
    }

    try {
      const resposta = await api.delete(`/orcamento/${orcamento.id}`, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
      if (resposta.status === 200 || resposta.status === 204) {
        setOrcamentos(orcamentos.filter((o) => o.id != orcamento.id));
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
            <CardOrcamento key={o.id} dados={o} onClickDel={() => deletar(o)} />
          ))
        ) : (
          <p className="text-xl">Nenhum orçamento cadastrado.</p>
        )}
      </section>
    </>
  );
}
