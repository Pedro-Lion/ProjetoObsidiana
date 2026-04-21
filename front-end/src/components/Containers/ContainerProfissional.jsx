import { Foto } from "../Foto";
import { useState, useEffect } from "react";

export function ContainerProfissional({ dados = {}, onClickEdit = () => {}, onClickDel = () => {} }) {
  const [fotoUrl, setFotoUrl] = useState(null);
  const API_BASE = import.meta.env.VITE_API_BASE_URL || "";

  // Carrega a foto via fetch autenticado (mesmo padrão do equipamento)
  useEffect(() => {
    if (!dados.id || !dados.nomeArquivoImagem) return;

    let objectUrl = null;
    const token = sessionStorage.getItem("token");

    fetch(`${API_BASE}/profissional/${dados.id}/imagem`, {
      headers: { Authorization: token ? "Bearer " + token : "" },
    })
      .then((resp) => {
        if (!resp.ok) return null;
        const ctype = resp.headers.get("content-type") || "";
        if (!ctype.startsWith("image/")) return null;
        return resp.blob();
      })
      .then((blob) => {
        if (!blob) return;
        objectUrl = URL.createObjectURL(blob);
        setFotoUrl(objectUrl);
      })
      .catch(() => {});

    return () => {
      if (objectUrl) {
        try { URL.revokeObjectURL(objectUrl); } catch (e) { /* ignore */ }
      }
    };
  }, [dados.id, dados.nomeArquivoImagem]);

  const Campo = ({ titulo, valor }) => (
    <div className="flex flex-col gap-1">
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
        <label className="hidden md:flex"></label>
      </div>

      {/* Corpo */}
      <div className="px-4 pb-4 flex flex-row items-start gap-4">

        {/* Foto */}
        <div className="flex-none">
          {fotoUrl ? (
            <img
              src={fotoUrl}
              alt={dados.nome}
              className="w-[8rem] h-[8rem] rounded-full object-cover"
            />
          ) : (
            <Foto icone="bi bi-person" tamanho="8" />
          )}
        </div>

        {/* Campos */}
        <div className="flex-1 flex flex-col md:flex-row md:flex-wrap md:gap-x-8 gap-y-3">
          <Campo titulo="Disponibilidade" valor={dados.disponibilidade} />
          <Campo titulo="Contato" valor={dados.contato} />
          {dados.funcao && <Campo titulo="Função" valor={dados.funcao} />}
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