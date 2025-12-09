import { BotaoBordaGradiente } from "../components/Buttons/BotaoBordaGradiente";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { InputFundoCor } from "../components/Inputs/InputFundoCor";
import { InputFoto } from "../components/Inputs/InputFoto";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags";
import { ContainerListagem } from "../components/Containers/ContainerListagem";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";

export function Home() {
  return (
    <>
      <h1 className="">Boas-vindas!</h1>

      <div className="flex gap-3.5">
        <BotaoSecundario
          titulo="Novo orçamento"
          className="w-60"
        />
        <BotaoSecundario
          titulo="Novo serviço"
          className="w-60"
        />
        <BotaoSecundario
          titulo="Novo equipamento"
          className="w-60"
        />
        <BotaoSecundario
          titulo="Novo profissional"
          className="w-60"
        />
      </div>

      <h3>Próximos eventos</h3>

      <div className="h-full w-full overflow-auto border-1 border-indigo-200 p-1.5">
        <iframe src="https://outlook.office365.com/calendar/published/05caa2e2759545539d3d0baaf1160008@sptech.school/79f2802dcb82431ebc51f3fc61e6114a3537960576227375636/calendar.html"
          className="h-full w-full"></iframe>
      </div>

    </>
  )
}