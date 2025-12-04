import { useEffect, useState } from "react";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags.jsx";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api.js";
import { useLocation, useNavigate, useParams } from "react-router-dom";

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
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status == 201) {
        const confirmacao = confirm(
          "Cadastrado com sucesso! Quer retornar à lista de serviços?"
        );

        if (confirmacao) {
          navigate("/servicos");
        }
        return;
      }
    } catch (error) {
      console.log(error);
      alert("Serviço não pôde ser cadastrado. Tente novamente.");
    }
  }

  async function editar() {
    let servicoFormatado = { ...servico };
    const equips = servicoFormatado.equipamentos;
    // caso os equipamentos não forem alterardos, terá a propriedade id (que deve ser formatada)
    if (equips[0] && equips[0].id) {
      servicoFormatado.equipamentos = equips.map(
        (equip) => equip.id
      );
    }


    console.log(servicoFormatado)

    try {
      const request = await api.put(`/servico/${id}`, servicoFormatado, {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status == 200) {
        alert("Editado com sucesso! Retornando à lista de serviços.");
        return navigate("/servicos");
      }

      alert("Serviço não pôde ser editado. Tente novamente.");
    } catch (error) {
      console.log(error);
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
              v = v.replace(/\D/g, ""); // remove tudo que não for número
              const numero = (Number(v) / 100).toFixed(2); // transforma centavos → valor real
              setValorHora(numero); // formata no input a variável acima ↝
              setServico({ ...servico, valorPorHora: Number(numero) }); // salva no objeto Servico como double
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

        {!state ? (
          <BotaoPrimario
            titulo="Cadastrar Serviço"
            className="self-end"
            onClick={cadastrar}
          />
        ) : (
          <BotaoPrimario
            titulo="Editar Serviço"
            className="self-end"
            onClick={editar}
          />
        )}
      </section>
    </>
  );
}
