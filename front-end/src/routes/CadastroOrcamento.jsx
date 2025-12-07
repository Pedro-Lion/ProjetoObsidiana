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
  const [orcamento, setOrcamento] = useState(state ?? { status: "Em análise" });
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

    if (orcamentoFormatado.status === "Confirmado" && account) {
      try {
        const response = await instance.acquireTokenSilent({
          ...loginRequest,
          account: account,
        });

        const accessToken = response.accessToken;
        console.log(accessToken);

        var inicioFormatado = "";
        var terminoFormatado = "";
        for (let i = 0; i <= 18; i++) {
          const cInicio = orcamentoFormatado.dataInicio[i];
          const cFim = orcamentoFormatado.dataTermino[i];

          if (i == 11) {
            inicioFormatado +=
              Number(cInicio + orcamentoFormatado.dataInicio[i + 1]) - 3;
            terminoFormatado +=
              Number(cFim + orcamentoFormatado.dataTermino[i + 1]) - 3;
          } else if (i >= 13 || i < 11) {
            inicioFormatado += cInicio;
            terminoFormatado += cFim;
          }
        }

        // return console.log(inicioFormatado,orcamentoFormatado.dataInicio)

        const event = {
          subject: orcamentoFormatado.descricao,
          start: { dateTime: inicioFormatado, timeZone: "America/Sao_Paulo" },
          end: { dateTime: terminoFormatado, timeZone: "America/Sao_Paulo" },
          location: { displayName: orcamentoFormatado.localEvento },
        };

        console.log(event);

        if (!orcamentoFormatado.idCalendar) {
          // return console.log("Entrou sem idCalendar",);

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
          // return console.log("Entrou com idCalendar");
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
            defaultValue={orcamento.status}
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
