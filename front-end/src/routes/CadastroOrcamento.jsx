import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";

import { useMsal } from "@azure/msal-react";

import { Modal } from "../components/Modal/Modal.jsx";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { SelectBordaLabel } from "../components/Inputs/SelectBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { InputDataBordaLabel } from "../components/Inputs/InputDataBordaLabel";

import moment from "moment";

import { api } from "../api";
import { cadastrar } from "../features/orcamento/services/cadastrar.js";
import { editar } from "../features/orcamento/services/editar.js";

export function CadastroOrcamento() {
  const navigate = useNavigate();

  const { instance } = useMsal();

  // Estados do modal
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  // Erros de validação
  const [erros, setErros] = useState({});

  const state = useLocation().state;
  function definirState() {
    if (!state) return undefined;
    let copiaState = { ...state };

    const formatador = new Intl.DateTimeFormat("pt-BR", {
      timeZone: "America/Sao_Paulo",
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
      hour12: false,
    }).format;

    function formatarData(dt) {
      if (!dt) return undefined;
      return (
        formatador(new Date(dt))
          .replace(/(\d{2})\/(\d{2})\/(\d{4})/, "$3-$2-$1")
          .replace(", ", "T") + "-03:00"
      );
    }

    copiaState.dataInicio = formatarData(copiaState.dataInicio);
    copiaState.dataTermino = formatarData(copiaState.dataTermino);

    return copiaState;
  }

  const [orcamento, setOrcamento] = useState(
    definirState() ?? { status: "Em análise" }
  );
  const [opcoes, setOpcoes] = useState({
    servico: [],
    equipamento: [],
    profissional: [],
  });

  // Guarda o momento de dataInicio para bloquear o picker de término
  const [momentoInicio, setMomentoInicio] = useState(
    orcamento.dataInicio ? moment(orcamento.dataInicio) : null
  );

  function registrarData(dt, atributo) {
    if (moment.isMoment(dt)) {
      const orcamentoCopia = { ...orcamento };
      orcamentoCopia[atributo] = dt.format();
      setOrcamento(orcamentoCopia);

      if (atributo === "dataInicio") {
        setMomentoInicio(dt);
        // Se a data de término já está definida e é antes da nova início, limpa
        if (orcamento.dataTermino && moment(orcamento.dataTermino).isBefore(dt)) {
          setOrcamento((prev) => ({ ...prev, dataInicio: dt.format(), dataTermino: undefined }));
        }
      }
    }
  }

  // Função para bloquear datas inválidas no picker de término
  function isValidDataTermino(currentDate) {
    if (!momentoInicio) return true;
    // Permite apenas datas/horas estritamente depois de dataInicio
    return currentDate.isAfter(momentoInicio);
  }

  function validar() {
    const novosErros = {};

    if (!orcamento.dataInicio) {
      novosErros.dataInicio = "Data de início é obrigatória.";
    }
    if (!orcamento.dataTermino) {
      novosErros.dataTermino = "Data de término é obrigatória.";
    }
    if (
      orcamento.dataInicio &&
      orcamento.dataTermino &&
      moment(orcamento.dataTermino).isSameOrBefore(moment(orcamento.dataInicio))
    ) {
      novosErros.dataTermino = "Data de término deve ser após a data de início.";
    }

    setErros(novosErros);
    return Object.keys(novosErros).length === 0;
  }

  function formatarOpcoes(lista = []) {
    return lista.map((item) => {
      return { value: item.id, label: item.nome };
    });
  }

  useEffect(() => {
    async function getOpcoes() {
      try {
        const respostaKeys = ["servico", "equipamento", "profissional"];
        const respostas = await Promise.all(
          respostaKeys.map((key) =>
            api.get(`/${key}`, {
              headers: {
                Authorization: "Bearer " + sessionStorage.getItem("token"),
              },
            })
          )
        );

        const novasOpcoes = { ...opcoes };
        respostaKeys.forEach((key, index) => {
          const dados = respostas[index].data;
          if (dados.length != 0) novasOpcoes[key] = formatarOpcoes(dados);
        });

        setOpcoes(novasOpcoes);
      } catch (error) {
        console.log(error);
      }
    }
    getOpcoes();
  }, []);

  const ErroMsg = ({ campo }) =>
    erros[campo] ? (
      <span className="text-red-500 text-[1rem] mt-1">{erros[campo]}</span>
    ) : null;

  return (
    <>
      <h1>{!state ? "Cadastrar" : "Editar"} orçamento</h1>

      <section className="flex flex-col gap-2">
        <div className="flex justify-between gap-5">
          <SelectBordaLabel
            className="w-full"
            titulo="Status"
            options={[
              { label: "Em análise" },
              { label: "Confirmado" },
              { label: "Cancelado" },
            ]}
            disabled={!state ? true : false}
            onChange={(e) =>
              setOrcamento({ ...orcamento, status: e.target.value })
            }
            value={orcamento.status}
          />

          <div className="flex flex-col w-full">
            <InputDataBordaLabel
              titulo="Data de início"
              className="w-full"
              defaultValue={
                orcamento.dataInicio ? new Date(orcamento.dataInicio) : undefined
              }
              onChange={(dt) => registrarData(dt, "dataInicio")}
            />
            <ErroMsg campo="dataInicio" />
          </div>

          <div className="flex flex-col w-full">
            <InputDataBordaLabel
              titulo="Data de término"
              className="w-full"
              defaultValue={
                orcamento.dataTermino
                  ? new Date(orcamento.dataTermino)
                  : undefined
              }
              onChange={(dt) => registrarData(dt, "dataTermino")}
              // Passa a função de validação para bloquear datas inválidas no picker
              isValidDate={isValidDataTermino}
            />
            <ErroMsg campo="dataTermino" />
          </div>
        </div>

        <InputBordaLabel
          titulo="Local do evento"
          className="w-full -mt-4"
          onInput={(e) =>
            setOrcamento({ ...orcamento, localEvento: e.target.value })
          }
          defaultValue={orcamento.localEvento}
        />
        <TextareaBordaLabel
          titulo="Descrição"
          className="h-40 -mt-4 mb-3"
          onInput={(e) =>
            setOrcamento({ ...orcamento, descricao: e.target.value })
          }
          defaultValue={orcamento.descricao}
        />

        <ContainerSelectTags
          titulo="Serviços"
          itens={opcoes.servico}
          preSelecao={() =>
            orcamento.servicos ? formatarOpcoes(orcamento.servicos) : []
          }
          onChange={(itens) =>
            setOrcamento({
              ...orcamento,
              servicos: itens.map((item) => item.value),
            })
          }
        />

        <ContainerSelectTags
          titulo="Equipamentos"
          itens={opcoes.equipamento}
          preSelecao={
            orcamento.equipamentos
              ? formatarOpcoes(orcamento.equipamentos)
              : []
          }
          temQuantidade={true}
          onChange={(itensComQtd) => {
            const equipamentoIds = (itensComQtd || []).map((i) => i.value);
            const usos = (itensComQtd || []).map((i) => ({
              idEquipamento: i.value,
              quantidadeUsada: i.quantidade ?? 1,
            }));
            setOrcamento({
              ...orcamento,
              equipamentos: equipamentoIds,
              usosEquipamentos: usos,
            });
          }}
        />

        <ContainerSelectTags
          titulo="Profissionais"
          itens={opcoes.profissional}
          preSelecao={() =>
            orcamento.profissionais
              ? formatarOpcoes(orcamento.profissionais)
              : []
          }
          onChange={(itens) =>
            setOrcamento({
              ...orcamento,
              profissionais: itens.map((item) => item.value),
            })
          }
        />
      </section>

      {/* Botões de ação */}
      <div className="flex gap-3 self-end">
        {!state ? (
          <BotaoPrimario
            titulo="Cadastrar"
            className="mb-0 mt-0"
            onClick={async () => {
              if (!validar()) return;
              if (await cadastrar(orcamento)) navigate("/orcamentos");
            }}
          />
        ) : (
          <BotaoPrimario
            titulo="Salvar alterações"
            className="mb-0 mt-0"
            onClick={async () => {
              if (!validar()) return;
              if (await editar(orcamento, instance)) {
                navigate("/orcamentos");
              }
            }}
          />
        )}
        <BotaoSecundario
          titulo="Cancelar"
          className="mb-0 mt-0"
          onClick={() => navigate(-1)}
        />
      </div>

      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}