import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api";
import { useNavigate } from "react-router-dom";

export function CadastroProfissionais() {
  const navigate = useNavigate();

  const profissional = {};

  async function cadastrar() {
    try {
      const request = await api.post("/profissional", profissional, {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status == 201) {
        const confirmacao = confirm(
          "Cadastrado com sucesso! Quer retornar à lista de profissionais?"
        );

        if (confirmacao) {
          navigate("/profissionais");
        }
        return;
      }
    } catch (error) {
      console.log(error);
      alert("Profissional não pôde ser cadastrado. Tente novamente.");
    }
  }

  return (
    <>
      <h1 className="mb-16 text-4xl font-bold">Cadastrar Profissional</h1>

      <section>
        <InputFoto />

        <div className="mt-5 flex flex-col gap-6">
          <InputBordaLabel
            titulo="Nome"
            placeholder="Ex: Fulano de Tal"
            onInput={(e) => (profissional.nome = e.target.value)}
          />

          <InputBordaLabel
            titulo="Disponibilidde"
            placeholder="Ex: Das terças às quintas às 14h"
            onInput={(e) => (profissional.disponibilidade = e.target.value)}
          />

          <InputBordaLabel
            titulo="Contato"
            placeholder="Ex: (11) 91234-1234 ou fulano@email.com"
            onInput={(e) => (profissional.contato = e.target.value)}
          />
        </div>

        <BotaoPrimario
          className="mb-0 mt-10"
          titulo="Cadastrar"
          onClick={cadastrar}
        />
      </section>
    </>
  );
}
