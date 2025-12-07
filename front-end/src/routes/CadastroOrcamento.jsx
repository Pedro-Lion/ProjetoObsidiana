import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { SelectBordaLabel } from "../components/Inputs/SelectBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { api } from "../api";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { InputDataBordaLabel } from "../components/Inputs/InputDataBordaLabel";
import moment from "moment";

export function CadastroOrcamento() {
  const navigate = useNavigate();
  const { id } = useParams();

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

      if (request.status === 201) {
        const confirmacao = confirm("Cadastrado com sucesso! Quer retornar à lista de orçamentos?");
        if (confirmacao) navigate("/orcamentos");
        return;
      }
    } catch (error) {
      console.log(error);
      const msg = error?.response?.data?.message || error?.response?.data || "Orçamento não pôde ser cadastrado. Tente novamente.";
      alert(msg);
    }
  }


  async function editar() {
    try {
      // clona e formata listas many-to-many (servicos/profissionais/equipamentos)
      let orcamentoFormatado = { ...orcamento };

      const chaves = ["servicos", "equipamentos", "profissionais"];
      chaves.forEach((chave) => {
        const lista = orcamentoFormatado[chave];
        if (!lista) {
          orcamentoFormatado[chave] = [];
          return;
        }
        // se for lista de objetos com id, converte; se já for lista de ids, mantém
        if (lista.length > 0 && typeof lista[0] !== "number" && lista[0]?.id) {
          orcamentoFormatado[chave] = lista.map((item) => item.id);
        } else {
          orcamentoFormatado[chave] = lista;
        }
      });

      // garantir usosEquipamentos no formato { idEquipamento, quantidadeUsada }
      orcamentoFormatado.usosEquipamentos = normalizarUsos(orcamentoFormatado.usosEquipamentos, orcamentoFormatado.equipamentos);

      const request = await api.put(`/orcamento/${id}`, orcamentoFormatado, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });

      if (request.status === 200) {
        alert("Editado com sucesso! Retornando à lista de orçamentos.");
        return navigate("/orcamentos");
      }
    } catch (error) {
      console.log(error);
      const msg = error?.response?.data?.message || error?.response?.data || "Orçamento não pôde ser editado. Tente novamente.";
      alert(msg);
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
    </>
  );
}
