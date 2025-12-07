import { Foto } from "../Foto";

export function ContainerProfissional({ dados = {}, onClickEdit = () => { }, onClickDel = () => { } }) {
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
                color:black;
                }
            }
        `}
      </style>
      <div className="container-item-lista">
        <div className="flex flex-row justify-between items-baseline bg-violet-200 px-10 py-1.5 mb-5">
          <label className="sm:wrap-anywhere sm:hyphens-auto text-slate-700 text-xl leading-5.5 font-bold uppercase">
            {dados.nome || "—"}
          </label>
        </div>
        <div className="flex flex-row gap-15 items-start w-full px-7 lg:gap-10">
          {dados.nomeArquivoImagem ? (
            <img
              src={`${process.env.REACT_APP_API_BASE_URL || ""}/equipamento/${dados.id}/imagem`}
              alt={dados.nome}
              className="w-full h-full object-cover"
            />
          ) : (
            <Foto icone="bi bi-person" /> //componente placeholder
          )}

          <div className="estilo-campos">
            <label className="estilo-titulo-campo w-fit">
              Disponibilidade
            </label>
            <label className="estilo-conteudo-campo">
              {dados.disponibilidade || "—"}
            </label>
          </div>
          <div className="estilo-campos">
            <label className="estilo-titulo-campo">
              Contato
            </label>
            <label className="estilo-conteudo-campo">
              {dados.contato || "—"}
            </label>
          </div>
          <div className="border-l-1 border-violet-200
                        flex flex-row self-center
                        lg:pl-9 sm:pl-4
                        lg:gap-9 sm:gap-4 ml-auto">
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