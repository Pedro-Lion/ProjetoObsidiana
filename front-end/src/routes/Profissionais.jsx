import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api";

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
        console.log(request.data)
        setProfissionais(request.data);
      }
    }
    getServicos();
  }, []);

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
          profissionais.map((p) => <p key={p.id}>{p.nome}</p>)
        ) : (
          <p className="text-xl">Nenhum profissional cadastrado.</p>
        )}
      </section>
    </>
  );
}
