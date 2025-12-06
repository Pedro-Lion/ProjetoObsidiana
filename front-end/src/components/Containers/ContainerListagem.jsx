import { Foto } from "../Foto";
import { formatarPreco } from "../../../utils/formatarPreco";

export function ContainerListagem({ dados = {}, onClickEdit = () => { }, onClickDel = () => { } }) {
  // Valor formatado (tratando null/undefined/string)
  const valorFormatado = formatarPreco(dados.valorPorHora);
  return (
    <>
      <style>
        {`
                .container-item-lista{
                width: 100%;
                margin-block: 1rem;
                background-color: #f5f3ff;
                padding: 0rem 0rem 1rem 0rem;
                border-radius: 0.5rem;
                }

                .estilo-campos{
                display:flex;
                flex-direction:column;
                gap: 0.75rem;
                max-width:15%;
                }

                .estilo-conteudo-campo,
                .estilo-titulo-campo{
                color: #334155;
                font-size:1.1rem;
                line-height: 1.25;
                width:auto;
                overflow-wrap: break-word;
                hyphens: auto;
                }
                
                .estilo-titulo-campo{
                font-weight:500;
                }

            @media(max-width: 900px){
             /*width >= 768px
             width <= 900px*/
                .estilo-campos{
                max-width:10%;
                }
                
                .estilo-conteudo-campo,
                .estilo-titulo-campo{
                font-size:0.75em;
                color:red;
                }
            }
        `}
      </style>
      <div className="container-item-lista">
        <div className="flex flex-row justify-between items-baseline bg-violet-200 px-10 py-1.5 mb-5">
          <label className="sm:wrap-anywhere sm:hyphens-auto text-slate-700 text-xl leading-5.5 font-bold uppercase">
            {dados.nome || "—"}
          </label>
          <label className="sm:wrap-anywhere sm:hyphens-auto text-slate-700 text-[1.1rem] w-fit leading-5.5 uppercase">
            {dados.categoria || "—"}
          </label>
          <label className="sm:wrap-anywhere sm:hyphens-auto text-slate-700 text-[1.1rem] w-fit leading-5.5">
            <b>{dados.quantidadeDisponivel ?? dados.quantidadeTotal ?? 0}</b> disponíveis
          </label>
        </div>
        <div className="flex flex-row justify-between items-start w-full px-7 lg:gap-10">
          {dados.nomeArquivoImagem ? (
            <img
              src={`${process.env.REACT_APP_API_BASE_URL || ""}/equipamento/${dados.id}/imagem`}
              alt={dados.nome}
              className="w-full h-full object-cover"
            />
          ) : (
            <Foto /> //componente placeholder
          )}

          <div className="estilo-campos">
            <label className="estilo-titulo-campo w-fit">
              Marca
            </label>
            <label className="estilo-conteudo-campo">
              {dados.marca || "—"}
            </label>
          </div>
          <div className="estilo-campos">
            <label className="estilo-titulo-campo">
              Modelo
            </label>
            <label className="estilo-conteudo-campo">
              {dados.modelo || "—"}
            </label>
          </div>
          <div className="estilo-campos">
            <label className="estilo-titulo-campo">
              NºSerie
            </label>
            <label className="estilo-conteudo-campo">
              {dados.numeroSerie || "—"}
            </label>
          </div>
          <div className="estilo-campos">
            <label className="estilo-titulo-campo">
              Valor/hora
            </label>
            <label className="estilo-conteudo-campo">
              {valorFormatado === "N/A" ? "R$ N/A" : `R$ ${valorFormatado}`}
            </label>
          </div>
          {dados.diaria && (<div className="estilo-campos">
            <label className="estilo-titulo-campo">
              Diária
            </label>
            <label className="estilo-conteudo-campo">
              R$ {formatarPreco(dados.valorDiaria)}
            </label>
          </div>)}
          <div className="flex flex-col gap-3 lg:max-w-[45%] sm:max-w-[25%]">
            <label className="estilo-titulo-campo w-fit">
              Observações
            </label>
            <label className="estilo-conteudo-campo">
              {dados.observacoes || "—"}
            </label>
          </div>
          <div className="border-l border-violet-200
                        flex flex-row self-center
                        lg:pl-9 sm:pl-4
                        lg:gap-9 sm:gap-4">
            <i className="bi bi-pencil-square
                        text-slate-700 self-center
                        cursor-pointer hover:text-indigo-300
                        lg:text-3xl sm:text-2xl"
              onClick={onClickEdit}></i>
            <i className="bi bi-trash3
                        text-slate-700 self-center
                        cursor-pointer hover:text-indigo-300
                        lg:text-3xl sm:text-2xl"
              onClick={onClickDel}></i>
          </div>
        </div>
      </div>
    </>
  )
}