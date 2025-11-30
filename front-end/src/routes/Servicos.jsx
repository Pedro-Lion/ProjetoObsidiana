import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { useNavigate } from "react-router-dom";
import { api } from "../api";
import { CardServico } from "../components/Cards/CardServico";

export function Servicos() {
  const navigate = useNavigate();
  const [servicos, setServicos] = useState("Buscando serviços...");

  useEffect(() => {
    async function getServicos() {
      const request = await api.get("/servico", {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        }
      });

      if (request.status = 200) {
        const dados = request.data;
        setServicos(
          dados.map((s) => (
            <CardServico key={s.id} dados={s} />
          ))
        );
      }

    }
    getServicos();
  }, []);

  return (
    <>
      <div className="flex justify-between">
        <h1>Serviços</h1>
        <BotaoPrimario
          titulo="+ Novo serviço"
          onClick={() => navigate("/cadastro/servico")}
        />
      </div>

      <section>
        {servicos}
      </section>
    </>
  );
}
