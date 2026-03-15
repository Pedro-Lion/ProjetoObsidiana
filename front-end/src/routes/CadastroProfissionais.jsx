import { InputFoto } from "../components/Inputs/InputFoto"
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel"
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel"
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario"

export function CadastroProfissionais() {
  return (
    <>
      <h1 className="mb-8 md:mb-16 text-4xl font-bold">Cadastrar Profissional</h1>

      <section className="flex w-full items-center flex-col">
        <div className="w-full max-w-[45rem]">

          <div className="self-start">
            <InputFoto />
          </div>

          <div className="mt-10 flex flex-col md:flex-row md:justify-between md:items-start gap-6">

            <div className="flex flex-col gap-6 md:w-[47%]">
              <InputBordaLabel titulo="Nome" placeholder="Ex: Fulano de Tal" />
              <TextareaBordaLabel titulo="Endereço" placeholder="Rua das Luas, Bairro dos Laranjais 123" rows="2" />
              <InputBordaLabel titulo="Função" placeholder="Ex: Auxiliar de Filmagem" />
            </div>

            <div className="flex flex-col gap-6 md:w-[47%]">
              <InputBordaLabel titulo="E-mail" placeholder="Ex: fulano@outlook.com" />
              <InputBordaLabel titulo="CEP" placeholder="Ex: 01234-567" />
              <InputBordaLabel titulo="Telefone" type="number" placeholder="Ex: 11 12345-6789" />
              <BotaoPrimario className="w-full mb-0 mt-2" titulo="Cadastrar" />
            </div>

          </div>
        </div>
      </section>
    </>
  )
}
