import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { api } from "../api";
import { useState } from "react";
import { Modal } from "../components/Modal/Modal.jsx";

export function CadastroEquipamentos() {
  const navigate = useNavigate();
  const { id } = useParams();

  const state = useLocation().state;
  const [equipamento, setEquipamento] = useState(
    state ?? {
      nome: "",
      categoria: "",
      marca: "",
      quantidadeTotal: 0,
      modelo: "",
      numeroSerie: "",
      valorPorHora: null,
    }
  );

  const [valorHora, setValorHora] = useState(
    equipamento.valorPorHora
      ? Number(equipamento.valorPorHora).toFixed(2)
      : "0.00"
  );

  // Estados do modal
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  // Funções utilitárias para inputs
  const onChangeTexto = (campo) => (e) => {
    setEquipamento((prev) => ({ ...prev, [campo]: e.target.value }));
  };

  const onChangeNumero = (campo) => (e) => {
    const n = e.target.value === "" ? 0 : Number(e.target.value);
    setEquipamento((prev) => ({ ...prev, [campo]: n }));
  };

  const onInputValorHora = (e) => {
    let v = e.target.value || "";
    v = v.replace(/\D/g, "");
    const numero = (Number(v) / 100).toFixed(2);
    setValorHora(numero);
    setEquipamento((prev) => ({ ...prev, valorPorHora: Number(numero) }));
  };

  async function cadastrar() {
    try {
      const request = await api.post("/equipamento", equipamento, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status == 201) {
        setModalTitulo("Sucesso!");
        setModalDescricao(
          "Cadastrado com sucesso! Quer retornar à lista de equipamentos?"
        );
        setModalActions(
          <>
            <button
              className="bg-blue-500 text-white px-4 py-2 rounded mr-3"
              onClick={() => navigate("/equipamentos")}
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
        "Equipamento não pôde ser cadastrado. Tente novamente."
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
      const request = await api.put(`/equipamento/${id}`, equipamento, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status == 200) {
        setModalTitulo("Sucesso!");
        setModalDescricao(
          "Editado com sucesso! Retornando à lista de equipamentos."
        );
        setModalActions(
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded"
            onClick={() => navigate("/equipamentos")}
          >
            Ok
          </button>
        );
        setModalOpen(true);
      } else {
        setModalTitulo("Erro");
        setModalDescricao("Equipamento não pôde ser editado. Tente novamente.");
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
    } catch (error) {
      console.log(error);
      setModalTitulo("Erro");
      setModalDescricao("Erro ao editar equipamento.");
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
      <h1 className="mb-16 text-4xl font-bold">Cadastrar Equipamento</h1>
      <div className="w-180">
        <div className="self-start">
          <InputFoto />
        </div>

        <div className="h-95 mt-10 flex justify-between items-center">
          <div className="flex flex-col justify-between h-full">
            <InputBordaLabel
              titulo="Nome"
              placeholder="Ex: Memória SD 128gb"
              onInput={onChangeTexto("nome")}
              value={equipamento.nome}
            />
            <InputBordaLabel
              titulo="Categoria"
              placeholder="Ex: Armazenamento"
              onInput={onChangeTexto("categoria")}
              value={equipamento.categoria}
            />
            <InputBordaLabel
              titulo="Marca"
              placeholder="Ex: SanDisk"
              onInput={onChangeTexto("marca")}
              value={equipamento.marca}
            />
            <InputBordaLabel
              titulo="Quantidade"
              type="number"
              placeholder="Ex: 10"
              onInput={onChangeNumero("quantidadeTotal")}
              value={equipamento.quantidadeTotal}
            />
          </div>
          <div className="flex flex-col justify-between h-full">
            <InputBordaLabel
              titulo="Modelo"
              type="text"
              placeholder="Ex: SD128GB"
              onInput={onChangeTexto("modelo")}
              value={equipamento.modelo}
            />
            <InputBordaLabel
              titulo="Número de Série"
              type="text"
              placeholder="Ex: 123456789"
              onInput={onChangeTexto("numeroSerie")}
              value={equipamento.numeroSerie}
            />
            <InputBordaLabel
              titulo="Valor"
              type="text"
              placeholder="Ex: 150.00"
              onInput={onInputValorHora}
              value={valorHora}
            />

            {!state ? (
              <BotaoPrimario
                titulo="Cadastrar"
                className="w-full mb-0 mt-10"
                onClick={cadastrar}
              />
            ) : (
              <BotaoPrimario
                titulo="Editar"
                className="w-full mb-0 mt-7"
                onClick={editar}
              />
            )}
          </div>
        </div>
      </div>

      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}
