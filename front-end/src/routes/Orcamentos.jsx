import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";

export function Orcamentos() {
  const navigate = useNavigate();
  const [orcamentos, setoOrcamentos] = useState("Buscando orçamentos...");

  return (
    <>
      <div className="flex justify-between">
        <h1>Orçamentos</h1>
        <BotaoPrimario
          titulo="+ Novo orçamento"
          onClick={() => navigate("/cadastro/orcamento")}
        />
      </div>

      <section>{orcamentos}</section>
    </>
  );
}
