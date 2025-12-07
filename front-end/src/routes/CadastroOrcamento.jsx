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

export function CadastroOrcamento() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { instance } = useMsal();
  const account = instance.getActiveAccount();

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

    console.log(copiaState);
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
      const request = await api.post("/orcamento", orcamento, {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status == 201) {
        const confirmacao = confirm(
          "Cadastrado com sucesso! Quer retornar à lista de orçamentos?"
        );

        if (confirmacao) {
          navigate("/orcamentos");
        }
        return;
      }
    } catch (error) {
      console.log(error);
      alert("Orçamento não pôde ser cadastrado. Tente novamente.");
    }
  }

  async function editar() {
    let orcamentoFormatado = { ...orcamento };

    const chaves = ["servicos", "equipamentos", "profissionais"];
    chaves.forEach((chave) => {
      const lista = orcamentoFormatado[chave];
      if (!lista[0]?.id) return;
      orcamentoFormatado[chave] = lista.map((item) => item.id);
    });

    async function tratarEvento() {
      if (!account) return;

      try {
        const response = await instance.acquireTokenSilent({
          ...loginRequest,
          account: account,
        });

        const accessToken = response.accessToken;

        if (
          orcamentoFormatado.status != "Confirmado" &&
          orcamentoFormatado.idCalendar
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

          orcamentoFormatado.idCalendar = null;
          return;
        }

        if (orcamento.status != "Confirmado") return;

        const event = {
          subject: orcamentoFormatado.descricao,
          start: {
            dateTime: orcamentoFormatado.dataInicio.slice(0, 19),
            timeZone: "America/Sao_Paulo",
          },
          end: {
            dateTime: orcamentoFormatado.dataTermino.slice(0, 19),
            timeZone: "America/Sao_Paulo",
          },
          location: { displayName: orcamentoFormatado.localEvento },
        };

        if (!orcamentoFormatado.idCalendar) {
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

          orcamentoFormatado.idCalendar = data.id;
        } else {
          await fetch(
            `https://graph.microsoft.com/v1.0/me/events/${orcamentoFormatado.idCalendar}`,
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
      const request = await api.put(`/orcamento/${id}`, orcamentoFormatado, {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status == 200) {
        alert("Editado com sucesso! Retornando à lista de orçamentos.");
        return navigate("/orcamentos");
      }
    } catch (error) {
      console.log(error);
      alert("Orcamento não pôde ser editado. Tente novamente.");
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
          onChange={(itens) =>
            setOrcamento({
              ...orcamento,
              equipamentos: itens.map((item) => item.value),
            })
          }
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
        <BotaoPrimario titulo="Editar" className="self-end" onClick={editar} />
      )}
    </>
  );
}
