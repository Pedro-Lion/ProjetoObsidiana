export function BotaoBordaGradiente({ titulo = "Clique aqui", className = "w-30" }) {
    return (
        <button type="button" className={`
            ${className}
            h-12 min-w-36 my-3.5 group
            flex items-center justify-center rounded-full
            bg-gradient-to-l from-fuchsia-400 via-indigo-500 to-green-300
            text-white text-[1.1rem]
            transition ease-in-out duration-300
            hover:text-gray-950 hover:shadow-sm hover:shadow-indigo-200 hover:p-[.15rem]
            `}>
            <div className="rounded-full w-full h-full leading-none px-3.5 flex items-center justify-center bg-slate-950
            group-hover:bg-white group-hover:transition group-hover:ease-in-out group-hover:duration-300">
                {titulo}
            </div>
        </button >
    )
}