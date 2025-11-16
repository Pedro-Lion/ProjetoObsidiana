import { Foto } from "../Foto";

export function ContainerListagem({ titulo = "Nome do item", onClickEdit, onClickDel }) {
  return (
    <div
      style={{
        width: "100%",
        marginBlock: "1rem",
        backgroundColor: "#f5f3ff",
        padding: "0rem 0rem 1rem 0rem",
        borderRadius: "0.5rem",
      }}
    >
      <div className="flex flex-row justify-between items-baseline bg-violet-200 px-10 py-1.5 mb-5">
        <label className="text-slate-700 text-xl font-bold leading-5.5 uppercase">
          {titulo}
        </label>

        <label className="text-slate-700 text-[1.1rem] w-fit leading-5.5 uppercase">
          Câmeras digitais
        </label>
        <label className="text-slate-700 text-[1.1rem] w-fit leading-5.5">
          <b>5</b> disponíveis
        </label>
      </div>
      <div className="flex flex-row items-center gap-10 my-3 justify-start px-10">
        <Foto tamanho="6" />
        <div className="flex flex-row gap-10 justify-between items-start">
          <div className="flex flex-col gap-3 max-w-[15%]">
            <label className="text-slate-700 text-[1.1rem] font-medium w-fit max-w-3/5 leading-5.5">
              Marca
            </label>
            <label className="text-slate-700 text-[1.1rem] leading-5.5">
              Nome da Marca
            </label>
          </div>
          <div className="flex flex-col gap-3 max-w-[15%]">
            <label className="text-slate-700 text-[1.1rem] font-medium leading-5.5">
              Modelo
            </label>
            <label className="text-slate-700 text-[1.1rem] leading-5.5">
              SEL135F18GM
            </label>
          </div>
          <div className="flex flex-col gap-3 max-w-[15%]">
            <label className="text-slate-700 text-[1.1rem] font-medium leading-5.5">
              NºSerie
            </label>
            <label className="text-slate-700 text-[1.1rem] leading-5.5">
              AB1AB123456
            </label>
          </div>
          <div className="flex flex-col gap-3 max-w-[15%]">
            <label className="text-slate-700 text-[1.1rem] font-medium leading-5.5">
              Valor/hora
            </label>
            <label className="text-slate-700 text-[1.1rem] leading-5.5">
              R$100,00
            </label>
          </div>
          <div className="flex flex-col gap-3 max-w-[15%]">
            <label className="text-slate-700 text-[1.1rem] font-medium leading-5.5">
              Diária
            </label>
            <label className="text-slate-700 text-[1.1rem] leading-5.5">
              R$800,00
            </label>
          </div>
          <div className="flex flex-col gap-3 max-w-[25%]">
            <label className="text-slate-700 text-[1.1rem] font-medium leading-5.5 w-fit max-w-3/5">
              Observações
            </label>
            <label className="text-slate-700 text-[1.1rem] w-auto leading-5.5">
              Máquina fotográfica específica para gravações diúrnas.
            </label>
          </div>
          <div
            className="h-15 border-l-1 border-violet-200 pl-9
            flex flex-row gap-9 self-center"
          >
            <i
              className="bi bi-pencil-square
              text-slate-700 text-3xl self-center
              cursor-pointer hover:text-indigo-300"
              onClick={onClickEdit}
            ></i>
            <i
              className="bi bi-trash3
              text-slate-700 text-3xl self-center
              cursor-pointer hover:text-indigo-300"
              onClick={onClickDel}
            ></i>
          </div>
        </div>
      </div>
    </div>
  );
}
