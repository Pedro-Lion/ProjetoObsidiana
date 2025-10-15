export function BotaoPrimario({ titulo = "Clique aqui" }) {
    return (
        <button type="button" className="min-h-12 min-w-30 my-3.5 rounded-lg
        bg-slate-950 p-2.5
        text-white text-[1.1rem]
        transition ease-in-out duration-300
        hover:bg-gradient-to-r hover:from-fuchsia-400 hover:via-violet-400 hover:to-blue-300
        hover:shadow-sm hover:shadow-violet-200">
            {titulo}
        </button>
    )
}