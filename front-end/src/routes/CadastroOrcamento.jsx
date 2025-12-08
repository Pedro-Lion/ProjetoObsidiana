import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { SelectBordaLabel } from "../components/Inputs/SelectBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { api } from "../api";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { InputDataBordaLabel } from "../components/Inputs/InputDataBordaLabel";
import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";
import moment from "moment";
import { Modal } from "../components/Modal/Modal.jsx";

export function CadastroOrcamento() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { instance } = useMsal();
  const account = instance.getActiveAccount();

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
  // Normaliza a lista de equipamentos (pode ser [id,id,...] ou [{id:..}, ...])
  function obterListaIdsEquipamentos(rawLista) {
    if (!rawLista) return [];
    return rawLista.map(item => (typeof item === "number" ? item : item?.id)).filter(Boolean);
  }

  // Converte equipamentos (ids) em usosEquipamentos [{ idEquipamento, quantidadeUsada }]
  function equipamentosParaUsos(equipamentoIds) {
    const mapa = new Map();
    equipamentoIds.forEach(id => {
      mapa.set(id, (mapa.get(id) || 0) + 1);
    });
    const usos = [];
    mapa.forEach((qtd, id) => usos.push({ idEquipamento: id, quantidadeUsada: qtd }));
    return usos;
  }

  // Normaliza um possível usosEquipamentos vindo da UI (pode conter equipamento aninhado)
  function normalizarUsos(usosRaw, equipamentosFallback) {
    if (usosRaw && usosRaw.length > 0) {
      return usosRaw.map(u => {
        // se já vier { idEquipamento, quantidadeUsada } usa direto
        if (u.idEquipamento) return { idEquipamento: u.idEquipamento, quantidadeUsada: u.quantidadeUsada ?? 1 };
        // se vier { equipamento: { id: ... }, quantidadeUsada }
        if (u.equipamento && (u.equipamento.id || u.equipamento.id === 0)) {
          return { idEquipamento: u.equipamento.id, quantidadeUsada: u.quantidadeUsada ?? 1 };
        }
        // se vier { id, ... } (uso antigo)
        if (u.id) return { idEquipamento: u.id, quantidadeUsada: u.quantidadeUsada ?? 1 };
        // fallback
        return null;
      }).filter(Boolean);
    }
    // fallback para equipamentos simples
    const ids = obterListaIdsEquipamentos(equipamentosFallback);
    return equipamentosParaUsos(ids);
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

  async function cadastrar() {
    try {
      // preparar payload padronizado
      const orcamentoFormatado = { ...orcamento };
      orcamentoFormatado.servicos = orcamentoFormatado.servicos ? obterListaIdsEquipamentos(orcamentoFormatado.servicos) : [];
      orcamentoFormatado.profissionais = orcamentoFormatado.profissionais ? obterListaIdsEquipamentos(orcamentoFormatado.profissionais) : [];

      // montar usosEquipamentos do jeito que o backend espera
      orcamentoFormatado.usosEquipamentos = normalizarUsos(orcamentoFormatado.usosEquipamentos, orcamentoFormatado.equipamentos);

      const request = await api.post("/orcamento", orcamentoFormatado, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status == 201) {
        setModalTitulo("Sucesso!");
        setModalDescricao(
          "Cadastrado com sucesso! Quer retornar à lista de orçamentos?"
        );
        setModalActions(
          <>
            <button
              className="bg-blue-500 text-white px-4 py-2 rounded mr-3"
              onClick={() => navigate("/orcamentos")}
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
      const msg = error?.response?.data?.message || error?.response?.data || "Orçamento não pôde ser cadastrado. Tente novamente.";
      console.log(error);
      setModalTitulo("Erro");
      setModalDescricao(msg);
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

    async function tratarEvento() {
      if (!account) return;

      try {
        const response = await instance.acquireTokenSilent({
          ...loginRequest,
          account: account,
        });

        const accessToken = response.accessToken;

        if (
          orcamento.status != "Confirmado" &&
          orcamento.idCalendar
        ) {
          await fetch(
            `https://graph.microsoft.com/v1.0/me/calendar/events/${orcamento.idCalendar}`,
            {
              method: "DELETE",
              headers: {
                Authorization: `Bearer ${accessToken}`,
                "Content-Type": "application/json",
              },
            }
          );

          orcamento.idCalendar = null;
          return;
        }

        if (orcamento.status != "Confirmado") return;

        const event = {
          subject: orcamento.descricao,
          start: {
            dateTime: orcamento.dataInicio.slice(0, 19),
            timeZone: "America/Sao_Paulo",
          },
          end: {
            dateTime: orcamento.dataTermino.slice(0, 19),
            timeZone: "America/Sao_Paulo",
          },
          location: { displayName: orcamento.localEvento },
        };

        if (!orcamento.idCalendar) {
          const idCalendar = await fetch(
            "https://graph.microsoft.com/v1.0/me/events",
            {
              method: "POST",
              headers: {
                Authorization: `Bearer ${accessToken}`,
                "Content-Type": "application/json",
              },
              body: JSON.stringify(event),
            }
          );

          const data = await idCalendar.json();

          orcamento.idCalendar = data.id;
        } else {
          await fetch(
            `https://graph.microsoft.com/v1.0/me/events/${orcamento.idCalendar}`,
            {
              method: "PATCH",
              headers: {
                Authorization: `Bearer ${accessToken}`,
                "Content-Type": "application/json",
              },
              body: JSON.stringify(event),
            }
          );
        }
      } catch (error) {
        alert("Erro ao criar evento");
        console.error(error);
        return;
      }
    }
    tratarEvento();

    try {
      // clona e formata listas many-to-many (servicos/profissionais/equipamentos)
      let orcamentoFormatado = { ...orcamento };
      const chaves = ["servicos", "equipamentos", "profissionais"];

      chaves.forEach((chave) => {
        const lista = orcamentoFormatado[chave];
        if (!lista[0]?.id) return;
        orcamentoFormatado[chave] = lista.map((item) => item.id);
      });

      // garantir usosEquipamentos no formato { idEquipamento, quantidadeUsada }
      orcamentoFormatado.usosEquipamentos = normalizarUsos(orcamentoFormatado.usosEquipamentos, orcamentoFormatado.equipamentos);

      const request = await api.put(`/orcamento/${id}`, orcamentoFormatado, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status == 200) {
        setModalTitulo("Sucesso!");
        setModalDescricao(
          "Editado com sucesso! Retornando à lista de orçamentos."
        );
        setModalActions(
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded"
            onClick={() => navigate("/orcamentos")}
          >
            Ok
          </button>
        );
        setModalOpen(true);
      } else {
        setModalTitulo("Erro");
        setModalDescricao("Orçamento não pôde ser editado. Tente novamente.");
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
      setModalDescricao("Erro ao editar orçamento.");
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
          preSelecao={() =>
            orcamento.equipamentos ? formatarOpcoes(orcamento.equipamentos) : []
          }
          temQuantidade={true}
          onChange={(itensComQtd) => {
            // itensComQtd = [{ value, label, quantidade }] quando temQuantidade=true
            const equipamentoIds = (itensComQtd || []).map(i => i.value);
            const usos = (itensComQtd || []).map(i => ({
              idEquipamento: i.value,
              quantidadeUsada: i.quantidade ?? 1
            }));
            setOrcamento({
              ...orcamento,
              equipamentos: equipamentoIds,
              usosEquipamentos: usos
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

      {!state ? (
        <BotaoPrimario
          titulo="Cadastrar"
          className="self-end"
          onClick={cadastrar}
        />
      ) : (
        <BotaoPrimario titulo="Salvar alterações" className="self-end" onClick={editar} />
      )}

      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}
