import { useEffect, useState } from "react";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags.jsx";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { api } from "../api.js";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { Modal } from "../components/Modal/Modal.jsx";

export function CadastrarNovoServico() {
  const navigate = useNavigate();
  const { id } = useParams();

  const state = useLocation().state;
  const [servico, setServico] = useState(state ?? {});
  const [opcoes, setOpcoes] = useState([]);

  const [horas, setHoras] = useState(servico.horas ?? 0);
  const [valorHora, setValorHora] = useState(
    servico.valorPorHora ? servico.valorPorHora.toFixed(2) : "0.00"
  );

  // Estados para o modal
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  useEffect(() => {
    async function getEquipamentos() {
      try {
        const request = await api.get("/equipamento", {
          headers: {
            Authorization: "Bearer " + sessionStorage.getItem("token"),
          },
        });

        if (request.status == 200) {
          const dados = request.data.map((equip) => {
            return { value: equip.id, label: equip.nome };
          });
          setOpcoes(dados);
        }
        return;
      } catch (error) {
        console.log(error);
      }
    }
    getEquipamentos();
  }, []);

  async function cadastrar() {
    try {
      const request = await api.post("/servico", servico, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status == 201) {
        setModalTitulo("Sucesso!");
        setModalDescricao(
          "Cadastrado com sucesso! Quer retornar à lista de serviços?"
        );
        setModalActions(
          <>
            <button
              className="bg-blue-500 text-white px-4 py-2 rounded mr-3"
              onClick={() => navigate("/servicos")}
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
      setModalDescricao("Serviço não pôde ser cadastrado. Tente novamente.");
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
      const request = await api.put(`/servico/${id}`, servico, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status == 200) {
        setModalTitulo("Sucesso!");
        setModalDescricao("Editado com sucesso! Retornando à lista de serviços.");
        setModalActions(
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded"
            onClick={() => navigate("/servicos")}
          >
            Ok
          </button>
        );
        setModalOpen(true);
      }
    } catch (error) {
      console.log(error);
      setModalTitulo("Erro");
      setModalDescricao("Serviço não pôde ser editado. Tente novamente.");
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
      {state ? <h1>Editar serviço</h1> : <h1>Cadastrar serviço</h1>}

      <section className="flex flex-col">
        <div className="flex justify-between gap-3 items-start mb">
          <InputBordaLabel
            titulo="Nome do Serviço"
            placeholder="Insira o nome aqui"
            className="w-full"
            value={servico.nome}
            onInput={(e) => setServico({ ...servico, nome: e.target.value })}
          />

          <InputBordaLabel
            type="number"
            titulo="Duração em Horas"
            className="w-full"
            value={horas}
            onInput={(e) => {
              let v = Number(e.target.value);
              if (v > 24) v = 24;
              if (v < 0) v = 0;
              setHoras(v);
              setServico({ ...servico, horas: v });
            }}
          />

          <InputBordaLabel
            type="text"
            titulo="Valor por Hora"
            className="w-full"
            value={valorHora}
            onInput={(e) => {
              let v = e.target.value;
              v = v.replace(/\D/g, "");
              const numero = (Number(v) / 100).toFixed(2);
              setValorHora(numero);
              setServico({ ...servico, valorPorHora: Number(numero) });
            }}
          />
        </div>

        <TextareaBordaLabel
          titulo="Descrição do Serviço"
          placeholder="Digite aqui informações do Serviço"
          defaultValue={servico.descricao}
          onInput={(e) => (servico.descricao = e.target.value)}
          className="mb-3 h-35"
        />

        <ContainerSelectTags
          titulo="Equipamentos"
          itens={opcoes}
          preSelecao={servico?.equipamentos?.map((s) => {
            return { value: s.id, label: s.nome };
          })}
          onChange={(itens) =>
            setServico({
              ...servico,
              equipamentos: itens.map((item) => item.value),
            })
          }
          className="mt-10"
        />

        {/* Botões de ação */}
        <div className="flex gap-3 self-end mt-4">
          {!state ? (
            <BotaoPrimario
              titulo="Cadastrar Serviço"
              className="mb-0 mt-0"
              onClick={cadastrar}
            />
          ) : (
            <BotaoPrimario
              titulo="Editar Serviço"
              className="mb-0 mt-0"
              onClick={editar}
            />
          )}
          <BotaoSecundario
            titulo="Cancelar"
            className="mb-0 mt-0"
            onClick={() => navigate(-1)}
          />
        </div>
      </section>

      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}