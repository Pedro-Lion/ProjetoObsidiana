export function BotaoBordaGradiente(){
    return(
        <div className="bg-slate-100 mx-auto flex h-screen w-screen items-center justify-center">
            <button type="button" className="h-12 w-36 group transition ease-in-out duration-300
            flex items-center justify-center rounded-full
            bg-gradient-to-l from-fuchsia-400 via-indigo-500 to-green-300
            text-white
            hover:text-gray-950 hover:shadow-sm hover:shadow-indigo-200 hover:p-[.15rem]">
                <div className="rounded-full w-full h-full
                flex items-center justify-center
                bg-slate-950 group-hover:bg-slate-100
                group-hover:transition group-hover:ease-in-out group-hover:duration-300">
                    Clique aqui</div>
            </button>
        </div>
    )
}