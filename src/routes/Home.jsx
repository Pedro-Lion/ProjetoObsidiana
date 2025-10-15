import { BotaoBordaGradiente } from "../components/Buttons/BotaoBordaGradiente";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { InputFundoCor } from "../components/Inputs/InputFundoCor";
import { InputFoto } from "../components/Inputs/InputFoto";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { InputSelect } from "../components/Inputs/InputSelect";

export function Home() {
  return (
    <>

      <div className="w-250">
        <InputSelect
        titulo="Equipamentos"
        placeholder="Escolha uma opção"/>
      </div>

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
        texto="Diária"
      />

      <BotaoBordaGradiente
        titulo="Clique aqui"
      />

      <BotaoPrimario
        titulo="Pesquisar"
        className="w-30"
      />

      <BotaoSecundario
        titulo="Meus equipamentos"
        className="w-60"
      />

      <InputFoto
        icone="bi bi-person-up"
      >
      </InputFoto>

    </>
  )
}