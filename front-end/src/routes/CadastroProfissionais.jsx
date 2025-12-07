import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useState } from "react";

export function CadastroProfissionais() {
  const navigate = useNavigate();

  const { id } = useParams();
  const state = useLocation().state;
  const [profissional, setProfissional] = useState(state ?? {});

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

  async function editar() {
    try {
      const request = await api.put(`/profissional/${id}`, profissional, {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status == 200) {
        alert("Editado com sucesso! Retornando à lista de profissionais.");
        return navigate("/profissionais");
      }
    } catch (error) {
      console.log(error);
      alert("Profissional não pôde ser editado. Tente novamente.");
    }
  }

  return (
    <>
      <h1 className="mb-16">{!state ? "Cadastrar" : "Editar"} profissional</h1>

      <section>
        <InputFoto />

        <div className="mt-5 flex flex-col gap-6">
          <InputBordaLabel
            titulo="Nome"
            placeholder="Ex: Fulano de Tal"
            defaultValue={profissional.nome}
            onInput={(e) =>
              setProfissional({ ...profissional, nome: e.target.value })
            }
          />

          <InputBordaLabel
            titulo="Disponibilidde"
            placeholder="Ex: Das terças às quintas às 14h"
            defaultValue={profissional.disponibilidade}
            onInput={(e) =>
              setProfissional({
                ...profissional,
                disponibilidade: e.target.value,
              })
            }
          />

          <InputBordaLabel
            titulo="Contato"
            placeholder="Ex: (11) 91234-1234 ou fulano@email.com"
            defaultValue={profissional.contato}
            onInput={(e) =>
              setProfissional({ ...profissional, contato: e.target.value })
            }
          />
        </div>

        {!state ? (
          <BotaoPrimario
            className="mb-0 mt-10"
            titulo="Cadastrar"
            onClick={cadastrar}
          />
        ) : (
          <BotaoPrimario
            className="mb-0 mt-10"
            titulo="Editar"
            onClick={editar}
          />
        )}
      </section>
    </>
  );
}
