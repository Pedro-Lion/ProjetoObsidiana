import { Outlet } from "react-router-dom";
import { Navbar } from "../components/Navbar/Navbar";
import { Camera } from "../components/Icons/Camera";
import { Foto } from "../components/Foto";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { InputFoto } from "../components/Inputs/InputFoto";
import { InputFundoCor } from "../components/Inputs/InputFundoCor";

export function Perfil() {
    return (
        <>
            <header className="w-114 py-10 flex-none text-gray-50 bg-gradient-to-t from-zinc-950 to-zinc-900 shadow-md">
                <div className="mb-10 flex justify-start items-center gap-5">
                    <div className="bg-[#f0f0f0] rounded-r-full w-fit py-5 pl-7 pr-6 justify-items-center">
                        <img className="h-15" src="/logo.png" alt="Logo Obsidiana" />
                    </div>
                    <img className="h-15" src="/MM_white.png" alt="Logo Obsidiana" />
                </div>

                <Navbar />
            </header>

            <main className="w-full min-w-0 p-20 overflow-y-auto overflow-x-hidden flex flex-col gap-5 shadow-md bg-white/90">
                <Outlet />
                <h1 className="text-2xl text-slate-950 font-semibold mb-6">
                    Foto de Perfil
                </h1>

                <div class="flex items-center gap-10 border-r-4 border-indigo-50 mb-12">
                    <InputFoto/>
                    <div class="flex flex-col">
                        <BotaoSecundario/>
                        <span className="text-[1.1rem] text-center">Remover</span>
                    </div>
                </div>

                <h1 className="text-2xl text-slate-950 font-semibold mb-6">
                    Detalhes do usuário 
                </h1>

                <div className="flex flex-col">
                    <div className="flex flex-col gap-2 text-[1.1rem] mb-6">
                        <label>Nome</label>
                        <InputBordaLabel/>
                    </div>
                    <div className="flex flex-col gap-2 text-[1.1rem] mb-6">
                        <label>Email</label>
                        <InputBordaLabel/>
                    </div>
                    <div className="flex flex-col gap-2 text-[1.1rem] mb-6">
                        <label> Nova Senha</label>
                        <InputBordaLabel/>
                    </div>
                    <div className="flex flex-col gap-2 text-[1.1rem] mb-6">
                        <label>Confirmar Senha</label>
                        <InputBordaLabel/>
                    </div>

                    <div>
                        <BotaoPrimario/>
                    </div>
                    
                </div>

            </main>
        </>
    );
}