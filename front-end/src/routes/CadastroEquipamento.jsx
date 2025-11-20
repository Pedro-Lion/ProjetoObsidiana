import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { useNavigate } from "react-router-dom";
import { api } from "../api";

export function CadastroEquipamentos() {
  const navigate = useNavigate();

  const equipamento = {
    nome: "",
    quantidade: null,
    categoria: "",
    marca: "",
    numeroSerie: "",
    modelo: "",
    valorPorHora: null,
  };

  async function cadastrar() {
    try {
      const request = await api.post("/equipamento", equipamento);
      if (request.status == 201) {
        const confirmacao = confirm(
          "Cadastrado com sucesso! Quer retornar à lista de equipamentos?"
        );

        if (confirmacao) {
          navigate("/equipamentos");
        }
        return;
      }
      alert("Equipamento não pode ser cadastrado. Tente novamente.");
    } catch (error) {
      console.log(error);
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
              onInput={(e) => (equipamento.nome = e.target.value)}
            />
            <InputBordaLabel
              titulo="Categoria"
              placeholder="Ex: Armazenamento"
              onInput={(e) => (equipamento.categoria = e.target.value)}
            />
            <InputBordaLabel
              titulo="Marca"
              placeholder="Ex: SanDisk"
              onInput={(e) => (equipamento.marca = e.target.value)}
            />
            <InputBordaLabel
              titulo="Quantidade"
              type="number"
              placeholder="Ex: 10"
              onInput={(e) => (equipamento.quantidade = e.target.value)}
            />
          </div>
          <div className="flex flex-col justify-between h-full">
            <InputBordaLabel
              titulo="Modelo"
              type="text"
              placeholder="Ex: SD128GB"
              onInput={(e) => (equipamento.modelo = e.target.value)}
            />
            <InputBordaLabel
              titulo="Número de Série"
              type="text"
              placeholder="Ex: 123456789"
              onInput={(e) => (equipamento.numeroSerie = e.target.value)}
            />
            <InputBordaLabel
              titulo="Valor"
              type="number"
              placeholder="Ex: 150.00"
            />
            <BotaoPrimario
              titulo="Cadastrar"
              className="w-full mb-0 mt-10"
              onClick={cadastrar}
            />
          </div>
        </div>
      </div>
    </>
  );
}
