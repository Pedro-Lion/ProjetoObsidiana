import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { Modal } from "../components/Modal/Modal.jsx";

export function CadastroProfissionais() {
  const navigate = useNavigate();

  const [profissional, setProfissional] = useState({
    nome: "",
    disponibilidade: "",
    contato: "",
  });

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

      if (request.status == 201) {
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

  return (
    <>
      <h1 className="mb-16 text-4xl font-bold">Cadastrar Profissional</h1>

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
            onInput={(e) =>
              setProfissional({
                ...profissional,
                disponibilidade: e.target.value,
              })
            }
            value={profissional.disponibilidade}
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

        <BotaoPrimario
          className="mb-0 mt-10"
          titulo="Cadastrar"
          onClick={cadastrar}
        />
      </section>

      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}
