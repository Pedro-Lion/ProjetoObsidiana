import { BotaoBordaGradiente } from "../components/Buttons/BotaoBordaGradiente";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { InputFundoCor } from "../components/Inputs/InputFundoCor";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";

export function Home() {
  return (
    <>
      <InputBordaLabel
        type="text"
        titulo="Nome completo"
        placeholder="Insira o nome aqui" 
        className="w-80" />
      <InputFundoCor
        type="number"
        titulo="Telefone"
        placeholder="Somente números" />
      <InputCheckbox
      texto="Diária" />
      <div>
        <BotaoPrimario
        titulo="Pesquisar"
        />
        <BotaoBordaGradiente
        titulo="Clique aqui"/>
        <BotaoSecundario
        titulo="Meus equipamentos"/>
      </div>
    </>
  )
}