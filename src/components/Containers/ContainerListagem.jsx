import { Foto } from "../Foto";

export function ContainerListagem({titulo="Nome do item"}) {
    return (
        <div style={{
            width: "100%", marginBlock: "1rem",
            backgroundColor: "#f5f3ff",
            padding: "1rem 2rem",
            borderRadius: "0.5rem"
        }}>
            <div className="flex flex-row justify-between items-baseline">
                    <label className="text-slate-700 text-xl font-bold w-fit max-w-3/5 leading-5.5 mb-2">
                        {titulo}
                    </label>
                    
                <label className="text-slate-700 text-[1.1rem] w-fit max-w-3/5 leading-5.5 mb-1">
                    Categoria
                </label>
                <label className="text-slate-700 text-[1.1rem] w-fit max-w-3/5 leading-5.5 mb-1">
                    <b>5</b> disponíveis
                </label>
            </div>
            <div className="flex flex-row items-center gap-10">
                <Foto
                    tamanho="6"/>
                <div className="flex flex-row gap-10 justify-between items-start bg-amber-500">
                    <div className="flex flex-col gap-3 w-fit">
                        <label className="text-slate-700 text-[1.1rem] font-medium w-fit max-w-3/5 leading-5.5">
                            Marca
                        </label>
                        <label className="text-slate-700 text-[1.1rem] w-fit max-w-3/5 leading-5.5">
                            Nome da Marca
                        </label>
                    </div>
                    <div className="flex flex-col gap-3 w-fit">
                        <label className="text-slate-700 text-[1.1rem] font-medium w-fit max-w-3/5 leading-5.5">
                            Modelo
                        </label>
                        <label className="text-slate-700 text-[1.1rem] w-fit max-w-3/5 leading-5.5">
                            SEL135F18GM
                        </label>
                    </div>
                    <div className="flex flex-col gap-3 w-fit">
                        <label className="text-slate-700 text-[1.1rem] font-medium w-fit max-w-3/5 leading-5.5">
                            NºSerie
                        </label>
                        <label className="text-slate-700 text-[1.1rem] w-fit max-w-3/5 leading-5.5">
                            AB1AB123456
                        </label>
                    </div>
                    <div className="flex flex-col gap-3 w-fit">
                        <label className="text-slate-700 text-[1.1rem] font-medium w-fit max-w-3/5 leading-5.5">
                            Valor/hora
                        </label>
                        <label className="text-slate-700 text-[1.1rem] w-fit max-w-3/5 leading-5.5">
                            R$100,00
                        </label>
                    </div>
                    <div className="flex flex-col gap-3 w-fit">
                        <label className="text-slate-700 text-[1.1rem] font-medium w-fit max-w-3/5 leading-5.5">
                            Diária
                        </label>
                        <label className="text-slate-700 text-[1.1rem] w-fit max-w-3/5 leading-5.5">
                            R$800,00
                        </label>
                    </div>
                    <div className="flex flex-col gap-3 w-fit">
                        <label className="text-slate-700 text-[1.1rem] font-medium w-fit max-w-3/5 leading-5.5">
                            Observações
                        </label>
                        <label className="text-slate-700 text-[1.1rem] w-auto max-w-3/5 leading-5.5">
                            Máquina fotográfica específica para gravações diúrnas.
                        </label>
                    </div>
                </div>
            </div>
        </div>
    )
}