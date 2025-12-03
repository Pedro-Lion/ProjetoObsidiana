import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { InputFoto } from "../components/Inputs/InputFoto";

export function Perfil() {
    return (
        <>
            <h1 className="text-2xl text-slate-950 font-semibold mb-6">
                Foto de Perfil
            </h1>

            <div className="flex items-center gap-10 border-r-4 border-indigo-50 mb-12">
                <InputFoto />
                <div className="flex flex-col">
                    <BotaoSecundario />
                    <span className="text-[1.1rem] text-center">Remover</span>
                </div>
            </div>

            <h1 className="text-2xl text-slate-950 font-semibold mb-6">
                Detalhes do usuário
            </h1>

            <div className="flex flex-col w-200 gap-6">

                <InputBordaLabel titulo="Nome" className="w-full" />
                <InputBordaLabel titulo="Email" className="w-full" />
                <InputBordaLabel titulo="Nova Senha" className="w-full" />
                <InputBordaLabel titulo="Confirmar Senha" className="w-full" />
                <BotaoPrimario className="self-end" />


            </div>
        </>
    );
}