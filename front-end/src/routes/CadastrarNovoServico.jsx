import { useEffect, useState } from "react";
import { ContainerListagem } from "../components/Containers/ContainerListagem.jsx";
// import { Dropdown } from "../Icons/Dropdown.jsx";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags.jsx";
import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api.js";
import { useNavigate } from "react-router-dom";

export function CadastrarNovoServico() {
  const servico = {};
  const navigate = useNavigate();
  const [equipamentos, setEquipamentos] = useState([]);
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

      alert("Serviço não pode ser cadastrado. Tente novamente.");
    } catch (error) {
      console.log(error);
    }
  }

  return (
    <>
      <h1 className="">Cadastrar Serviço</h1>

      <section className="flex items-center w-full flex-col gap-10 shadow-md p-5">
        <div className="w-320">
          <div className="flex justify-between gap-5 items-start mb-5">
            <InputBordaLabel
              type="text"
              titulo="Nome do Serviço"
              placeholder="Insira o nome aqui"
              className="w-150"
              onInput={(e) => (servico.nome = e.target.value)}
            />
            <InputBordaLabel
              type="number"
              titulo="Duração em Horas"
              placeholder="Insira a duração aqui"
              className="w-150"
              onInput={(e) => (servico.horas = e.target.value)}
            />
            <InputBordaLabel
              type="number"
              titulo="Valor por Hora"
              placeholder="Ex: 15.00"
              className="w-150"
              onInput={(e) => (servico.valorPorHora = e.target.value)}
            />
          </div>
          <TextareaBordaLabel
            titulo="Descrição do Serviço"
            placeholder="Digite aqui informações do Serviço"
            larguraCampo="w-full"
            rows="4"
            onInput={(e) => (servico.descricao = e.target.value)}
          />

          <div className="w-full mt-10">
            <ContainerSelectTags
              titulo="Equipamentos"
              itens={equipamentos}
              onChange={(itens) =>
                (servico.equipamentos = itens.map((item) => item.value))
              }
              placeholder="Escolha uma opção"
            />
          </div>
          {/* <div className="w-full mt-10">
            <ContainerSelectTags
              titulo="Profissionais"
              placeholder="Escolha uma opção"
            />
          </div> */}
          <BotaoPrimario
            titulo="Cadastrar Serviço"
            className="w-flex"
            onClick={cadastrar}
          />
        </div>
      </section>
    </>
  );
}
