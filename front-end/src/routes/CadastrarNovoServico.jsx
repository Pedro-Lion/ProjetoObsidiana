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
  const servico = state ? state : {};
  const [equipamentos, setEquipamentos] = useState([]);
  const [valor, setValor] = useState(servico.horas ?? 0);

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

          setEquipamentos(dados);
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

      alert("Serviço não pôde ser cadastrado. Tente novamente.");
    } catch (error) {
      console.log(error);
    }
  }

  async function editar() {
    try {
      const request = await api.put("/servico/" + id, servico, {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status == 200) {
        alert("Editado com sucesso! Retornando à lista de serviços.")
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
            value={servico.nome}
            className="w-full"
            onChange={(e) => (servico.nome = e.target.value)}
          />

          <InputBordaLabel
            type="number"
            titulo="Duração em Horas"
            placeholder="Insira a duração aqui"
            className="w-full"
            value={valor}
            onChange={(e) => {
              let v = Number(e.target.value);
              if (v > 24) v = 24;
              setValor(v);
              servico.horas = v;
            }}
          />

          <InputBordaLabel
            type="number"
            titulo="Valor por Hora"
            placeholder="Ex: 15.00"
            value={servico.valorPorHora}
            className="w-full"
            onChange={(e) => (servico.valorPorHora = e.target.value)}
          />
        </div>

        <TextareaBordaLabel
          titulo="Descrição do Serviço"
          placeholder="Digite aqui informações do Serviço"
          defaultValue={servico.descricao}
          onChange={(e) => (servico.descricao = e.target.value)}
          className="mb-3 h-35"
        />

        <ContainerSelectTags
          titulo="Equipamentos"
          itens={equipamentos}
          preSelecao={servico?.equipamentos?.map((s) => {
            return { value: s.id, label: s.nome };
          })}
          onChange={(itens) =>
            (servico.equipamentos = itens.map((item) => item.value))
          }
          className="mt-10"
        />

        {state ? (
          <BotaoPrimario
            titulo="Editar Serviço"
            className="self-end"
            onClick={editar}
          />
        ) : (
          <BotaoPrimario
            titulo="Cadastrar Serviço"
            className="self-end"
            onClick={cadastrar}
          />
        )}
      </section>
    </>
  );
}
