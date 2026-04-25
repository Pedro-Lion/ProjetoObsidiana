import { InputFoto } from "../Inputs/InputFoto";
import { formatarPreco } from "../../../utils/formatarPreco";

export function ContainerListagem({ dados = {}, onClickEdit = () => {}, onClickDel = () => {} }) {
  const valorFormatado = formatarPreco(dados.valorPorHora);

  const Campo = ({ titulo, valor }) => (
    <div className="flex flex-row md:flex-col gap-1">
      <span className="text-slate-700 md:text-[1.2rem] font-medium leading-snug">{titulo}<a className=" md:hidden">:</a></span>
      <span className="text-slate-700 md:text-[1.2rem] leading-snug break-words">{valor || "—"}</span>
    </div>
  );

  return (
    <div className="w-full mb-4 bg-[#f5f3ff] rounded-lg overflow-hidden">

      {/* Cabeçalho */}
      <div className="flex flex-row justify-between items-baseline bg-violet-200 px-5 py-1.5 mb-4 gap-2 flex-wrap">
        <label className="text-slate-700 md:text-[1.2rem] font-bold leading-snug uppercase break-words hyphens-auto">
          {dados.nome || "—"}
        </label>
        <label className="text-slate-700 md:text-[1.2rem] leading-snug uppercase whitespace-nowrap">
        {dados.categoria || "—"}
        </label>
        <label className="text-slate-700 md:text-[1.2rem] leading-snug whitespace-nowrap">
          <b>{dados.quantidadeDisponivel ?? dados.quantidadeTotal ?? 0}</b> disponíveis de <b>{dados.quantidadeTotal}</b>
        </label>
      </div>

      {/* Corpo */}
      <div className="px-4 pb-4 flex flex-row items-start gap-4">

        {/* Foto — sempre à esquerda */}
        <div className="flex-none">
          {dados.nomeArquivoImagem && dados.preview ? (
            <a href={dados.preview} target="_blank" rel="noopener noreferrer">
              <InputFoto dstv={true} initialPreview={dados.preview} tamanho="8" />
            </a>
          ) : (
            <InputFoto dstv={true} tamanho="8" />
          )}
        </div>

        {/* Campos: coluna em mobile, linha em desktop */}
        <div className="flex-1 flex flex-col md:justify-between md:px-15 md:flex-row md:flex-wrap md:gap-x-8 gap-y-3">
          <Campo titulo="Marca" valor={dados.marca} />
          <Campo titulo="Modelo" valor={dados.modelo} />
          <Campo titulo="Nº Série" valor={dados.numeroSerie} />
          <Campo titulo="Valor/hora" valor={valorFormatado === "N/A" ? "R$ N/A" : `R$ ${valorFormatado}`} />
          {dados.diaria && (
            <Campo titulo="Diária" valor={`R$ ${formatarPreco(dados.valorDiaria)}`} />
          )}
          {dados.observacoes && (
            <Campo titulo="Observações" valor={dados.observacoes} />
          )}
        </div>

        {/* Botões de ação */}
        <div className="flex flex-col justify-center items-start gap-3 border-l border-violet-200 pl-4 self-center">
          <button
            className="p-2 text-slate-700 hover:text-indigo-400 transition-colors cursor-pointer
            flex flex-row gap-x-1.5 items-center"
            onClick={onClickEdit}
            title="Editar"
          >
            <i className="bi bi-pencil-square text-2xl"></i>
            <a className="text-[1rem] uppercase py-1 px-2.5 bg-violet-100 rounded-lg hidden md:flex">Editar</a>
          </button>
          <button
            className="p-2 text-slate-700 hover:text-indigo-400 transition-colors cursor-pointer
            flex flex-row gap-x-1.5 items-center"
            onClick={onClickDel}
            title="Excluir"
          >
            <i className="bi bi-trash3 text-2xl"></i>
            <a className="text-[1rem] uppercase py-1 px-2.5 bg-violet-100 rounded-lg hidden md:flex">Excluir</a>
          </button>
        </div>

      </div>
    </div>
  );
}
