export function BotaoPrimario({ titulo = "Clique aqui" }) {
    return (
        <button type="button" className="min-h-12 min-w-30 my-3.5 rounded-lg
        bg-slate-950 p-2.5
        text-white text-[1.1rem]
        transition ease-in-out duration-400
        hover:transition hover:ease-in-out hover:duration-400
        hover:text-shadow-2xs
        hover:text-shadow-indigo-400
        hover:bg-gradient-to-r hover:from-indigo-300 hover: via-violet-300 hover:to-indigo-100
        ">
            {titulo}
        </button>
    )
}