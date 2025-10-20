import { InputFoto } from "../components/Inputs/InputFoto"
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel"
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel"
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario"


export function CadastroEquipamentos() {




    return (
        <>
            <h1 className="mb-16 text-4xl font-bold">Cadastrar Equipamento</h1>
            <section className= "flex w-full items-center flex-col ">
            <div className="w-180">
                <div className="self-start">
                    <InputFoto />
                </div>
                    <div className="h-95  mt-10 flex justify-between items-center">
                        <div className="flex flex-col justify-between h-full">
                            <InputBordaLabel titulo="Nome" placeholder="Ex: Memória SD 128gb" />
                            <TextareaBordaLabel titulo="Descrição" placeholder="Descrição" />
                            <InputBordaLabel titulo="Quantidade" type="number" placeholder="Ex: 10" />
                        </div>
                        <div className="flex flex-col justify-between h-full">
                            <InputBordaLabel titulo="Categoria" placeholder="Ex: Armazenamento" />
                            <InputBordaLabel titulo="Marca" placeholder="Ex: SanDisk" />
                            <InputBordaLabel titulo="Valor" type="number" placeholder="Ex: 150.00" />
                            <BotaoPrimario className="w-full mb-0 mt-10" titulo="Cadastrar" />
                        </div>
                    </div>
            </div>
            </section>
        </>
    )

}