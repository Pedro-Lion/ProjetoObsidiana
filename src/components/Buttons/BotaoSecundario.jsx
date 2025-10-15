export function BotaoSecundario({ titulo = "Clique aqui", className = "w-32" }) {
    return (
        <button type="button" className={`
        ${className}
        min-h-12 min-w-30 rounded-lg my-3.5 p-2.5
        text-[1.1rem] text-slate-700 text-center
        border-1 border-indigo-400
        hover:transition hover:ease-in-out hover:duration-400
        hover:bg-gradient-to-r hover:from-indigo-100 hover:to-sky-50
        hover:text-indigo-400 hover:border-sky-50
        `}>
            {titulo}
        </button>
    )
}