import { BotaoBordaGradiente } from "../components/Buttons/BotaoBordaGradiente";
import { BackgroundGradient } from "../components/BackgroudGradient";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { InputFundoCor } from "../components/Inputs/InputFundoCor";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";

export function Home() {
  return (
    <div className="flex flex-col gap-5
    h-[94vh]
    w-[77vw]
    overflow-y-scroll
    p-20
    my-10
    mr-10
    absolut
    rounded-[2rem]
    bg-white/90">
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
        titulo="Clique aqui"/>
      </div>
      <div>
        <BotaoSecundario
        titulo="Meus equipamentos"/>
      </div>
    </div>
  )
}