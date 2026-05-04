import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";

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

  const [equipamentosDoServico, setEquipamentosDoServico] = useState([]);

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

  // Controla se o usuário deseja editar o valor manualmente (sobrescrevendo o cálculo automático)
  const [valorManual, setValorManual] = useState(false);

  // Estado de exibição formatado do valor total no modo manual (ex: "1500.00")
  const [valorTotalDisplay, setValorTotalDisplay] = useState(
    (orcamento.valorTotal ?? 0).toFixed(2)
  );

  // Máscara decimal: remove não-dígitos, divide por 100 e formata com 2 casas
  // Mesmo padrão usado no input de valor por hora do cadastro de equipamento
  function onInputValorTotal(e) {
    const v = (e.target.value || "").replace(/\D/g, "");
    const numero = (Number(v) / 100).toFixed(2);
    setValorTotalDisplay(numero);
    setOrcamento((o) => ({ ...o, valorTotal: Number(numero) }));
  }

  // Indica se as opções já foram carregadas da API (evita zerar o valorTotal antes do carregamento)
  const [opcoesCarregadas, setOpcoesCarregadas] = useState(false);

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
    // Compara apenas o dia (ignora hora/minuto), pois o picker passa cada célula do calendário
    // como meia-noite do respectivo dia. Assim, o mesmo dia do início fica disponível para
    // seleção — o usuário pode escolher um horário mais tarde. A validação de hora é feita
    // pela função validar() ao tentar salvar.
    return currentDate.isSameOrAfter(momentoInicio, "day");
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

  // Formata serviços incluindo dados de preço para o cálculo automático
  function formatarOpcoesServico(lista = []) {
    return lista.map((item) => ({
      value: item.id,
      label: item.nome,
      valorPorHora: item.valorPorHora ?? 0,
      horas: item.horas ?? 0,
    }));
  }

  // Formata equipamentos incluindo dados de preço para o cálculo automático
  function formatarOpcoesEquipamento(lista = []) {
    return lista.map((item) => ({
      value: item.id,
      label: item.nome,
      valorPorHora: item.valorPorHora ?? 0,
    }));
  }

  // Formata número como moeda BRL
  function formatarMoeda(valor = 0) {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(valor ?? 0);
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

        // Mapa de formatadores específicos por tipo:
        // serviço e equipamento incluem dados de preço para o cálculo automático
        const formatadores = {
          servico: formatarOpcoesServico,
          equipamento: formatarOpcoesEquipamento,
          profissional: formatarOpcoes,
        };

        const novasOpcoes = { ...opcoes };
        respostaKeys.forEach((key, index) => {
          const dados = respostas[index].data;
          if (dados.length != 0) novasOpcoes[key] = formatadores[key](dados);
        });

        setOpcoes(novasOpcoes);
        setOpcoesCarregadas(true); // sinaliza que as opções foram carregadas da API
      } catch (error) {
        console.log(error);
      }
    }
    getOpcoes();
  }, []);

  useEffect(() => {
    async function buscarEquipamentosDoServico() {
      const servicoIds = (orcamento.servicos || []).map((s) =>
        typeof s === "number" ? s : s?.id
      );

      try {
        // Se não há serviços, remove apenas os equipamentos que vieram de serviço
        // (mantém os adicionados manualmente)
        if (servicoIds.length === 0) {
          setEquipamentosDoServico([]);
          setOrcamento((prev) => ({
            ...prev,
            usosEquipamentos: (prev.usosEquipamentos || []).filter(
              (u) => !u.veioDeServico
            ),
            equipamentos: (prev.usosEquipamentos || [])
              .filter((u) => !u.veioDeServico)
              .map((u) => u.idEquipamento),
          }));
          return;
        }

        const respostas = await Promise.all(
          servicoIds.map((id) =>
            api.get(`/servico/${id}`, {
              headers: {
                Authorization: "Bearer " + sessionStorage.getItem("token"),
              },
            })
          )
        );

        const todosEquipamentos = respostas
          .flatMap((r) => r.data.equipamentos ?? [])
          .filter(
            (eq, index, self) => self.findIndex((e) => e.id === eq.id) === index
          );

        setEquipamentosDoServico(todosEquipamentos);

        setOrcamento((prev) => {
          // Mantém apenas os usos adicionados manualmente
          const usosManuais = (prev.usosEquipamentos || []).filter(
            (u) => !u.veioDeServico
          );

          // Cria usos para equipamentos do serviço que ainda não estão nos manuais
          const usosDoServico = todosEquipamentos
            .filter((eq) => !usosManuais.some((u) => u.idEquipamento === eq.id))
            .map((eq) => ({
              idEquipamento: eq.id,
              quantidadeUsada: 1,
              veioDeServico: true, // flag para identificar origem
            }));

          const todosUsos = [...usosManuais, ...usosDoServico];

          return {
            ...prev,
            usosEquipamentos: todosUsos,
            equipamentos: todosUsos.map((u) => u.idEquipamento),
          };
        });
      } catch (error) {
        console.error("Erro ao buscar equipamentos do serviço:", error);
      }
    }

    buscarEquipamentosDoServico();
  }, [orcamento.servicos]);

  // Cálculo automático do valor total com base nos itens selecionados
  const { totalServicos, totalEquipamentos, valorCalculado } = useMemo(() => {
    // Normaliza IDs de serviços (podem chegar como número ou como objeto {id, nome})
    const servicoIds = (orcamento.servicos || []).map((s) =>
      typeof s === "number" ? s : s?.id
    );

    // Soma o preço de cada serviço selecionado: valorPorHora × horas
    const totalServicos = servicoIds.reduce((soma, id) => {
      const opcao = opcoes.servico.find((o) => o.value === id);
      if (!opcao) return soma;
      return soma + (opcao.valorPorHora ?? 0) * (opcao.horas ?? 0);
    }, 0);

    // Soma o preço de cada equipamento selecionado: valorPorHora × quantidadeUsada
    const totalEquipamentos = (orcamento.usosEquipamentos || []).reduce((soma, uso) => {
      // Normaliza o ID (pode vir como idEquipamento, equipamento.id ou id diretamente)
      const idEq = uso.idEquipamento ?? uso.equipamento?.id ?? uso.id;
      const opcao = opcoes.equipamento.find((o) => o.value === idEq);
      if (!opcao) return soma;
      return soma + (opcao.valorPorHora ?? 0) * (uso.quantidadeUsada ?? 1);
    }, 0);

    return {
      totalServicos,
      totalEquipamentos,
      valorCalculado: totalServicos + totalEquipamentos,
    };
  }, [orcamento.servicos, orcamento.usosEquipamentos, opcoes.servico, opcoes.equipamento]);

  // Adicione esse memo para ter os IDs dos equipamentos dos serviços sempre atualizados
  const idsEquipamentosDoServico = useMemo(
    () => equipamentosDoServico.map((e) => e.id),
    [equipamentosDoServico]
  );

  // Sincroniza o valorTotal do orçamento com o valor calculado automaticamente,
  // mas apenas depois que as opções foram carregadas e o usuário não editou manualmente
  useEffect(() => {
    if (!valorManual && opcoesCarregadas) {
      setOrcamento((prev) => ({ ...prev, valorTotal: valorCalculado }));
    }
  }, [valorCalculado, valorManual, opcoesCarregadas]);

  // Converte usosEquipamentos em formato que o ContainerSelectTags entende,
  // incluindo nome (label) e quantidade já selecionada
  function formatarOpcoesComQuantidade(usos = [], opcoesEquipamento = []) {
    return usos
      .map((uso) => {
        const idEq = uso.idEquipamento ?? uso.equipamento?.id;
        const opcao = opcoesEquipamento.find((o) => o.value === idEq);
        if (!opcao) return null;
        return {
          value: opcao.value,
          label: opcao.label,
          quantidade: uso.quantidadeUsada ?? 1,
        };
      })
      .filter(Boolean);
  }

  const ErroMsg = ({ campo }) =>
    erros[campo] ? (
      <span className="text-red-500 text-[1rem] mt-1">{erros[campo]}</span>
    ) : null;

  return (
    <>
      <h1>{!state ? "Cadastrar" : "Editar"} orçamento</h1>

      <section className="flex flex-col gap-2">
        <div className="flex justify-between md:gap-5 flex-wrap md:flex-nowrap">
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
              titulo="Início"
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
              titulo="Fim"
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
          key={idsEquipamentosDoServico.join(",")}
          titulo="Equipamentos"
          itens={opcoes.equipamento}
          preSelecao={formatarOpcoesComQuantidade(
            (orcamento.usosEquipamentos || []),
            opcoes.equipamento
          )}
          temQuantidade={true}
          onChange={(itensComQtd) => {
            const equipamentoIds = (itensComQtd || []).map((i) => i.value);

            setOrcamento((prev) => {
              // Usos do serviço que continuam presentes na seleção atual
              const usosServicoRestantes = (prev.usosEquipamentos || [])
                .filter((u) => u.veioDeServico && equipamentoIds.includes(u.idEquipamento))
                .map((u) => ({
                  ...u,
                  // Atualiza quantidade se o usuário editou
                  quantidadeUsada:
                    itensComQtd.find((i) => i.value === u.idEquipamento)?.quantidade ??
                    u.quantidadeUsada,
                }));

              // Usos manuais: itens selecionados que não são do serviço
              const idsDoServico = (prev.usosEquipamentos || [])
                .filter((u) => u.veioDeServico)
                .map((u) => u.idEquipamento);

              const usosManuais = itensComQtd
                .filter((i) => !idsDoServico.includes(i.value))
                .map((i) => ({
                  idEquipamento: i.value,
                  quantidadeUsada: i.quantidade ?? 1,
                  veioDeServico: false,
                }));

              const todosUsos = [...usosServicoRestantes, ...usosManuais];

              return {
                ...prev,
                usosEquipamentos: todosUsos,
                equipamentos: todosUsos.map((u) => u.idEquipamento),
              };
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

      {/* Seção de valor do orçamento */}
      <div className="justify-start border-t border-slate-300 pt-5">

        <h3 className="mb-2">Valor do Orçamento</h3>

        {!valorManual ? (
          /* Modo calculado: exibe o total e o detalhamento por categoria */
          <div className="flex flex-col gap-1">
            <div className="flex flex-col md:flex-row md:items-center justify-start gap-x-4">
              <span className="text-3xl font-semibold text-slate-700 mt-1">
                {formatarMoeda(valorCalculado)}
              </span>
              {/* Botão para alternar entre modo calculado e manual */}
              <button
                type="button"
                onClick={() => {
                  setValorManual((prev) => {
                    // Ao desativar o modo manual, volta ao valor calculado automaticamente
                    if (prev) {
                      setOrcamento((o) => ({ ...o, valorTotal: valorCalculado }));
                    } else {
                      // Ao ativar o modo manual, inicializa o display com o valor calculado atual
                      setValorTotalDisplay(valorCalculado.toFixed(2));
                    }
                    return !prev;
                  });
                }}
                className={`w-fit h-fit mt-2 md:mt-0 text-[1rem] px-4 py-0.5 cursor-pointer transition-color rounded-full border ${valorManual
                  ? "border-rose-500 bg-white text-rose-500 hover:border-red-400 hover:text-red-400 hover:bg-red-50 transition duration-200"
                  : "border-indigo-400 bg-white text-slate-700 hover:border-indigo-50 hover:transition hover:ease-in-out hover:duration-400 hover:bg-gradient-to-r hover:from-indigo-100 hover:to-sky-50 hover:text-indigo-400 hover:border-sky-50"
                  }`}
              >
                {valorManual ? "🗙 Cancelar edição" : "✎ Editar valor"}
              </button>
            </div>
            {/* Detalhamento por categoria */}
            <div className="flex gap-6 flex-wrap mt-1 text-slate-500 text-[0.95rem]">
              <span>Serviços: <strong>{formatarMoeda(totalServicos)}</strong></span>
              <span>Equipamentos: <strong>{formatarMoeda(totalEquipamentos)}</strong></span>
            </div>
            <span className="text-slate-400 text-[0.95rem]">
              Calculado automaticamente com base nos itens selecionados
            </span>
          </div>
        ) : (
          /* Modo manual: exibe o valor sugerido e permite ao usuário definir o valor livremente */
          <div className="flex flex-col gap-3">
            <span className="text-slate-400 text-[0.95rem]">
              Valor sugerido (calculado automaticamente):{" "}
              <strong className="text-slate-700">{formatarMoeda(valorCalculado)}</strong>
            </span>
            <div className="flex flex-col items-start gap-3">
              <InputBordaLabel
                titulo="Valor total (R$)"
                type="text"
                placeholder="0,00"
                value={valorTotalDisplay}
                onInput={onInputValorTotal}
                className="w-fit max-w-xs"
              />
              {/* Botão para cancelar edição manual e voltar ao valor calculado automaticamente */}
              <button
                type="button"
                onClick={() => {
                  setValorManual(false);
                  setValorTotalDisplay(valorCalculado.toFixed(2));
                  setOrcamento((o) => ({ ...o, valorTotal: valorCalculado }));
                }}
                className="w-fit h-fit mb-[0.15rem] text-[1rem] px-4 py-0.5 cursor-pointer transition-color rounded-full border border-rose-500 bg-white text-rose-500 hover:border-red-400 hover:text-red-400 hover:bg-red-50 transition duration-200"
              >
                🗙 Cancelar edição
              </button>
            </div>
          </div>
        )}
      </div>

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