import { BotaoBordaGradiente } from "../components/Buttons/BotaoBordaGradiente";
import { BackgroundGradient } from "../components/BackgroudGradient";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { InputFundoCor } from "../components/Inputs/InputFundoCor";
import { InputFoto } from "../components/Inputs/InputFoto";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";

export function Home() {
  return (
    <>
      <div className="w-[30rem]">
        <InputBordaLabel
          type="text"
          titulo="Nome completo"
          placeholder="Insira o nome aqui" />
      </div>
      <div className="w-[20rem]">
        <InputFundoCor
          type="number"
          titulo="Telefone"
          placeholder="Somente números" />
      </div>
      <div className="w-[20rem]">
        <InputCheckbox
          texto="Diária" />
      </div>
      <div>
        <BotaoPrimario
          titulo="Pesquisar"
        />
      </div>
      <div>
        <BotaoBordaGradiente
          titulo="Clique aqui" />
      </div>
      <div>
        <BotaoSecundario
          titulo="Meus equipamentos" />
      </div>
      
        <InputFoto
        icone="bi bi-person-up"
        >
        </InputFoto>
      
    </>
  )
}