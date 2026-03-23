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

  function preSelecaoUsos() {
    if (state == null && state.usosEquipamentos == null) return undefined;
    const preSelecao = state.usosEquipamentos.map((u) => {
      return {
        value: u.id,
        label: u.equipamento.nome,
        quantidade: u.quantidadeUsada,
      };
    });

    return preSelecao;
  }

  function registrarData(dt, atributo) {
    if (moment.isMoment(dt)) {
      const orcamentoCopia = { ...orcamento };
      orcamentoCopia[atributo] = dt.format();
      setOrcamento(orcamentoCopia);
    }
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
          <InputDataBordaLabel
            titulo="Data de início"
            className="w-full"
            defaultValue={
              orcamento.dataInicio ? new Date(orcamento.dataInicio) : undefined
            }
            onChange={(dt) => registrarData(dt, "dataInicio")}
          />
          <InputDataBordaLabel
            titulo="Data de término"
            className="w-full"
            defaultValue={
              orcamento.dataTermino
                ? new Date(orcamento.dataTermino)
                : undefined
            }
            onChange={(dt) => registrarData(dt, "dataTermino")}
          />
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
          preSelecao={ orcamento.equipamentos
              ? formatarOpcoes(orcamento.equipamentos)
              : []}
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
              if(await cadastrar(orcamento)) navigate("/orcamentos");
            }}
          />
        ) : (
          <BotaoPrimario
            titulo="Salvar alterações"
            className="mb-0 mt-0"
            onClick={async () => {
              if (await editar(orcamento, instance)) {
                navigate("/orcamentos")
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