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

  return (
    <>
      <div className="flex justify-between">
        <h1>Orçamentos</h1>
        <BotaoPrimario
          titulo="+ Novo orçamento"
          onClick={() => navigate("/cadastro/orcamento")}
        />
      </div>

      <section>
        {orcamentos.length != 0 ? (
          orcamentos.map((o) => (
            <CardOrcamento
              key={o.id}
              dados={o}
              onClickDel={() => deletar(o.id)}
            />
          ))
        ) : (
          <p className="text-xl">Nenhum serviço cadastrado.</p>
        )}
      </section>
    </>
  );
}
