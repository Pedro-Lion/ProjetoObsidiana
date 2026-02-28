import { Foto } from "../Foto";
import { InputFoto } from "../Inputs/InputFoto";
import { formatarPreco } from "../../../utils/formatarPreco";

export function ContainerListagemDinamico({
  dados = {},
  onClickEdit = () => {},
  onClickDel = () => {},
}) {
  const listaCampos = [];

  for (const [chave, valor] of Object.entries(dados)) {
    // se for a chave for qualquer uma dessas, não deve virar um campo
    const chaveEspecial = [
      "nome",
      "categoria",
      "quantidadeDisponivel",
      "quantidadeTotal",
    ].includes(chave);

    if (chaveEspecial) continue;

    const chaveCapitalizada =
      chave[0].toUpperCase() + chave.slice(1, chave.length);

    listaCampos.push(
      <div className="estilo-campos">
        <label className="estilo-titulo-campo w-fit">{chaveCapitalizada}</label>
        <label className="estilo-conteudo-campo">{valor || "—"}</label>
      </div>,
    );
  }

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

        .estilo-campos {
          display:flex;
          flex-direction:column;
          gap: 0.75rem;
          max-width:15%;
        }

        .estilo-conteudo-campo,
        .estilo-titulo-campo {
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

        @media(max-width: 900px) {
          /*width >= 768px
          width <= 900px*/
            
          .estilo-campos{
            max-width:10%;
          }
            
          .estilo-conteudo-campo,
          .estilo-titulo-campo {
            font-size:1em;
          }
        }
      `}
      </style>

      <div className="container-item-lista">
        <div className="flex flex-row justify-between items-baseline bg-violet-200 px-10 py-1.5 mb-5">
          <label className="sm:wrap-anywhere sm:hyphens-auto text-slate-700 text-xl leading-5.5 font-bold uppercase">
            {dados.nome || "—"}
          </label>

          {dados.categoria && (
            <label className="sm:wrap-anywhere sm:hyphens-auto text-slate-700 text-[1.1rem] w-fit leading-5.5 uppercase">
              {dados.categoria}
            </label>
          )}

          {(dados.quantidadeDisponivel || dados.quantidadeTotal) && (
            <label className="sm:wrap-anywhere sm:hyphens-auto text-slate-700 text-[1.1rem] w-fit leading-5.5">
              <b>{dados.quantidadeDisponivel ?? dados.quantidadeTotal ?? 0}</b>{" "}
              disponíveis
            </label>
          )}
        </div>

        <div className="flex flex-row justify-between items-start w-full px-7 lg:gap-10">
          {dados.nomeArquivoImagem && dados.preview ? (
            <a href={dados.preview} target="_blank" rel="noopener noreferrer">
              <InputFoto dstv={true} initialPreview={dados.preview} />
            </a>
          ) : (
            <InputFoto dstv={true} />
          )}

          {listaCampos}

          <div
            className="
              border-l border-violet-200
              flex flex-row self-center
              lg:pl-9 sm:pl-4
              lg:gap-9 sm:gap-4"
          >
            <i
              className="
              bi bi-pencil-square
              text-slate-700 self-center
              cursor-pointer hover:text-indigo-300
              lg:text-3xl sm:text-2xl"
              onClick={onClickEdit}
            ></i>
            <i
              className="bi bi-trash3
              text-slate-700 self-center
              cursor-pointer hover:text-indigo-300
              lg:text-3xl sm:text-2xl"
              onClick={onClickDel}
            ></i>
          </div>
        </div>
      </div>
    </>
  );
}
