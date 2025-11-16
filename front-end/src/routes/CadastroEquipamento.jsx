import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { useNavigate } from "react-router-dom";

export function CadastroEquipamentos() {
  const navigate = useNavigate();

  const equipamento = {
    nome: "",
    quantidade: 5,
    categoria: "Gravação",
    marca: "Canon",
    numeroSerie: "E101204",
    modelo: "2005",
    valorPorHora: 25.5
  }

  async function cadastrar() {
    try {
      const request = await fetch("http://localhost:8080/equipamento", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${sessionStorage.getItem("token")}`,
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify(equipamento)
      });
  
      if (request.ok) {
        alert("Cadastro bem sucedido!\nRedirecionando para tela de equipamentos...")
        return navigate("/equipamentos")
      }

      alert("Não foi possivel cadastrar o equipamento")
    } catch (error) {
      console.log(error)
    }
  }
  
  return (
    <>
      <h1 className="mb-16 text-4xl font-bold">Cadastrar Equipamento</h1>
      <section className="flex w-full items-center flex-col ">
        <div className="w-180">
          <div className="self-start">
            <InputFoto />
          </div>

          <div className="h-95  mt-10 flex justify-between items-center">
            <div className="flex flex-col justify-between h-full">
              <InputBordaLabel
                titulo="Nome"
                placeholder="Ex: Memória SD 128gb"
                onInput={(e) => equipamento.nome = e.target.value}
              />
              <TextareaBordaLabel titulo="Descrição" placeholder="Descrição" />
              <InputBordaLabel
                titulo="Quantidade"
                type="number"
                placeholder="Ex: 10"
              />
            </div>
            <div className="flex flex-col justify-between h-full">
              <InputBordaLabel
                titulo="Categoria"
                placeholder="Ex: Armazenamento"
              />
              <InputBordaLabel titulo="Marca" placeholder="Ex: SanDisk" />
              <InputBordaLabel
                titulo="Valor"
                type="number"
                placeholder="Ex: 150.00"
              />
              <BotaoPrimario titulo="Cadastrar" className="w-full mb-0 mt-10" onClick={cadastrar} />
            </div>
          </div>
        </div>
      </section>
    </>
  );
}
