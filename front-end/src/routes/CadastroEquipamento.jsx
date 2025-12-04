import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { api } from "../api";
import { useState } from "react";

export function CadastroEquipamentos() {
  const navigate = useNavigate();
  const { id } = useParams();

  const state = useLocation().state;
  const [equipamento, setEquipamento] = useState(
    state ?? {
      nome: "",
      categoria: "",
      marca: "",
      quantidade: 0,
      modelo: "",
      numeroSerie: "",
      valorPorHora: null, // Number (double) — pronto para enviar
    }
  );

  // Estado apenas para exibir o valor formatado no input (string "0.00")
  const [valorHora, setValorHora] = useState(
    equipamento.valorPorHora
      ? Number(equipamento.valorPorHora).toFixed(2)
      : "0.00"
  );

  // Funções utilitárias para inputs
  const onChangeTexto = (campo) => (e) => {
    setEquipamento((prev) => ({ ...prev, [campo]: e.target.value }));
  };

  const onChangeNumero = (campo) => (e) => {
    // converte para número (inteiro)
    const n = e.target.value === "" ? 0 : Number(e.target.value);
    setEquipamento((prev) => ({ ...prev, [campo]: n }));
  };

  // Mesma lógica do cadastro de serviço para o campo "Valor por Hora"
  const onInputValorHora = (e) => {
    let v = e.target.value || "";
    // remove tudo que não for número
    v = v.replace(/\D/g, "");
    // transforma centavos -> reais
    const numero = (Number(v) / 100).toFixed(2);
    setValorHora(numero); // string formatada para mostrar no input
    // salva como Number (double) no objeto equipamento
    setEquipamento((prev) => ({ ...prev, valorPorHora: Number(numero) }));
  };

  async function cadastrar() {
    try {
      const request = await api.post("/equipamento", equipamento, {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status == 201) {
        const confirmacao = confirm(
          "Cadastrado com sucesso! Quer retornar à lista de equipamentos?"
        );

        if (confirmacao) {
          navigate("/equipamentos");
        }
        return;
      }

    } catch (error) {
      console.log(error);
      alert("Equipamento não pôde ser cadastrado. Tente novamente.");
    }
  }

  async function editar() {
    try {
      const request = await api.put(`/equipamento/${id}`, equipamento, {
        headers: {
          Authorization: "Bearer " + sessionStorage.getItem("token"),
        },
      });

      if (request.status == 200) {
        alert("Editado com sucesso! Retornando à lista de equipamentos.");
        return navigate("/equipamentos");
      }

    } catch (error) {
      console.log(error);
      alert("Equipamento não pôde ser editado. Tente novamente.");
    }
  }

  return (
    <>
      <h1 className="mb-16 text-4xl font-bold">Cadastrar Equipamento</h1>
      <div className="w-180">
        <div className="self-start">
          <InputFoto />
        </div>

        <div className="h-95  mt-10 flex justify-between items-center">
          <div className="flex flex-col justify-between h-full">
            <InputBordaLabel
              titulo="Nome"
              placeholder="Ex: Memória SD 128gb"
              onInput={onChangeTexto("nome")}
              value={equipamento.nome}
            />
            <InputBordaLabel
              titulo="Categoria"
              placeholder="Ex: Armazenamento"
              onInput={onChangeTexto("categoria")}
              value={equipamento.categoria}
            />
            <InputBordaLabel
              titulo="Marca"
              placeholder="Ex: SanDisk"
              onInput={onChangeTexto("marca")}
              value={equipamento.marca}
            />
            <InputBordaLabel
              titulo="Quantidade"
              type="number"
              placeholder="Ex: 10"
              onInput={onChangeNumero("quantidade")}
              value={equipamento.quantidade}
            />
          </div>
          <div className="flex flex-col justify-between h-full">
            <InputBordaLabel
              titulo="Modelo"
              type="text"
              placeholder="Ex: SD128GB"
              onInput={onChangeTexto("modelo")}
              value={equipamento.modelo}
            />
            <InputBordaLabel
              titulo="Número de Série"
              type="text"
              placeholder="Ex: 123456789"
              onInput={onChangeTexto("numeroSerie")}
              value={equipamento.numeroSerie}
            />

            <InputBordaLabel
              titulo="Valor"
              type="text" // tipo texto para permitir a máscara (vírgulas, zeros à esquerda)
              placeholder="Ex: 150.00"
              onInput={onInputValorHora}
              value={valorHora}
            />

            {!state ? (
              <BotaoPrimario
                titulo="Cadastrar"
                className="w-full mb-0 mt-10"
                onClick={cadastrar}
              />
            ) : (
              <BotaoPrimario
                titulo="Editar"
                className="w-full mb-0 mt-7"
                onClick={editar}
              />
            )}
          </div>
        </div>
      </div>
    </>
  );
}
