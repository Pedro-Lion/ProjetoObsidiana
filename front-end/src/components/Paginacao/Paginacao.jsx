export function Paginacao({ paginaAtual, totalPaginas, onMudarPagina }) {
  if (totalPaginas <= 1) return null;

  const paginas = [];
  const delta = 2;
  const esquerda = Math.max(0, paginaAtual - delta);
  const direita = Math.min(totalPaginas - 1, paginaAtual + delta);

  for (let i = esquerda; i <= direita; i++) {
    paginas.push(i);
  }

  return (
    <div className="flex items-center justify-center gap-2 mt-6 flex-wrap">
      {/* Botão anterior */}
      <button
        onClick={() => onMudarPagina(paginaAtual - 1)}
        disabled={paginaAtual === 0}
        className="
          min-h-10 px-3 rounded-lg
          text-[1.1rem] text-slate-700
          border border-indigo-400
          transition ease-in-out duration-300
          hover:bg-gradient-to-r hover:from-indigo-100 hover:to-sky-50
          hover:text-indigo-400 hover:border-sky-50
          disabled:opacity-40 disabled:cursor-not-allowed disabled:hover:bg-transparent
          disabled:hover:text-slate-700 disabled:hover:border-indigo-400
        "
      >
        <i className="bi bi-chevron-left"></i>
      </button>

      {/* Primeira página + reticências */}
      {esquerda > 0 && (
        <>
          <button
            onClick={() => onMudarPagina(0)}
            className="
              min-h-10 min-w-10 rounded-lg
              text-[1.1rem] text-slate-700
              border border-indigo-400
              transition ease-in-out duration-300
              hover:bg-gradient-to-r hover:from-indigo-100 hover:to-sky-50
              hover:text-indigo-400
            "
          >
            1
          </button>
          {esquerda > 1 && (
            <span className="text-slate-400 text-xl px-1">...</span>
          )}
        </>
      )}

      {/* Páginas do intervalo */}
      {paginas.map((p) => (
        <button
          key={p}
          onClick={() => onMudarPagina(p)}
          className={`
            min-h-10 min-w-10 rounded-lg
            text-[1.1rem]
            border transition ease-in-out duration-300
            ${
              p === paginaAtual
                ? "bg-slate-950 text-white border-slate-950"
                : "text-slate-700 border-indigo-400 hover:bg-gradient-to-r hover:from-indigo-100 hover:to-sky-50 hover:text-indigo-400 hover:border-sky-50"
            }
          `}
        >
          {p + 1}
        </button>
      ))}

      {/* Reticências + última página */}
      {direita < totalPaginas - 1 && (
        <>
          {direita < totalPaginas - 2 && (
            <span className="text-slate-400 text-xl px-1">...</span>
          )}
          <button
            onClick={() => onMudarPagina(totalPaginas - 1)}
            className="
              min-h-10 min-w-10 rounded-lg
              text-[1.1rem] text-slate-700
              border border-indigo-400
              transition ease-in-out duration-300
              hover:bg-gradient-to-r hover:from-indigo-100 hover:to-sky-50
              hover:text-indigo-400
            "
          >
            {totalPaginas}
          </button>
        </>
      )}

      {/* Botão próximo */}
      <button
        onClick={() => onMudarPagina(paginaAtual + 1)}
        disabled={paginaAtual === totalPaginas - 1}
        className="
          min-h-10 px-3 rounded-lg
          text-[1.1rem] text-slate-700
          border border-indigo-400
          transition ease-in-out duration-300
          hover:bg-gradient-to-r hover:from-indigo-100 hover:to-sky-50
          hover:text-indigo-400 hover:border-sky-50
          disabled:opacity-40 disabled:cursor-not-allowed disabled:hover:bg-transparent
          disabled:hover:text-slate-700 disabled:hover:border-indigo-400
        "
      >
        <i className="bi bi-chevron-right"></i>
      </button>

      {/* Indicador */}
      <span className="text-slate-400 text-[1rem] ml-2">
        Página {paginaAtual + 1} de {totalPaginas}
      </span>
    </div>
  );
}