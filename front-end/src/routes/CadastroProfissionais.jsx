import { InputFoto } from "../components/Inputs/InputFoto"
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel"
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel"
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario"


export function CadastroProfissionais() {




    return (
        <>
            <h1 className="mb-16 text-4xl font-bold">Cadastrar Profissional</h1>
            <section className= "flex w-full items-center flex-col ">
            <div className="w-180">
                <div className="self-start">
                    <InputFoto />
                </div>
                    <div className="h-95  mt-10 flex justify-between items-center">
                        <div className="flex flex-col justify-between h-full">
                            <InputBordaLabel titulo="Nome" placeholder="Ex: Fulano de Tal" />
                            <TextareaBordaLabel titulo="Endereço" placeholder="Rua das Luas, Bairro dos Laranjais 123" rows="2" />
                            <InputBordaLabel titulo="Função" type="number" placeholder="Ex: Auxiliar de Filmagem" />
                        </div>
                        <div className="flex flex-col justify-between h-full">
                            <InputBordaLabel titulo="E-mail" placeholder="Ex: fulano@outlook.com" />
                            <InputBordaLabel titulo="CEP" placeholder="Ex: 01234-567" />
                            <InputBordaLabel titulo="Telefone" type="number" placeholder="Ex: 11 12345-6789" />
                            <BotaoPrimario className="w-full mb-0 mt-10" titulo="Cadastrar" />
                        </div>
                    </div>
            </div>
            </section>
        </>
    )

}