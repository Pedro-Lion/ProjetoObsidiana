import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api";
import { Modal } from "../components/Modal/Modal.jsx";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useState } from "react";

export function CadastroProfissionais() {
  const navigate = useNavigate();

  const { id } = useParams();
  const state = useLocation().state;
  const [profissional, setProfissional] = useState(state ?? {});

  // Estados do modal
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  async function cadastrar() {
    try {
      const request = await api.post("/profissional", profissional, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status === 201) {
        setModalTitulo("Sucesso!");
        setModalDescricao(
          "Cadastrado com sucesso! Quer retornar à lista de profissionais?"
        );
        setModalActions(
          <>
            <button
              className="bg-blue-500 text-white px-4 py-2 rounded mr-3"
              onClick={() => navigate("/profissionais")}
            >
              Ir para lista
            </button>
            <button
              className="bg-gray-300 px-4 py-2 rounded"
              onClick={() => setModalOpen(false)}
            >
              Continuar
            </button>
          </>
        );
        setModalOpen(true);
      }
    } catch (error) {
      console.log(error);
      setModalTitulo("Erro");
      setModalDescricao(
        "Profissional não pôde ser cadastrado. Tente novamente."
      );
      setModalActions(
        <button
          className="bg-gray-300 px-4 py-2 rounded"
          onClick={() => setModalOpen(false)}
        >
          Fechar
        </button>
      );
      setModalOpen(true);
    }
  }

  async function editar() {
    try {
      const request = await api.put(`/profissional/${id}`, profissional, {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status === 200) {
        setModalTitulo("Sucesso!");
        setModalDescricao(
          "Editado com sucesso! Retornando à lista de profissionais."
        );
        setModalActions(
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded"
            onClick={() => navigate("/profissionais")}
          >
            Ok
          </button>
        );
        setModalOpen(true);
      }
    } catch (error) {
      console.log(error);
      setModalTitulo("Erro");
      setModalDescricao("Profissional não pôde ser editado. Tente novamente.");
      setModalActions(
        <button
          className="bg-gray-300 px-4 py-2 rounded"
          onClick={() => setModalOpen(false)}
        >
          Fechar
        </button>
      );
      setModalOpen(true);
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
            onInput={(e) =>
              setProfissional({ ...profissional, nome: e.target.value })
            }
            value={profissional.nome}
          />

          <InputBordaLabel
            titulo="Disponibilidade"
            placeholder="Ex: Das terças às quintas às 14h"
            value={profissional.disponibilidade}
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
            onInput={(e) =>
              setProfissional({ ...profissional, contato: e.target.value })
            }
            value={profissional.contato}
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

      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}
