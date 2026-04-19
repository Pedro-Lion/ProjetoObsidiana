import { useNavigate } from "react-router-dom";
import { BotaoPrimario } from "../Buttons/BotaoPrimario";
import { BotaoSecundario } from "../Buttons/BotaoSecundario";

export function CardOrcamento({
  dados = {
    id: 1,
    status: "",
    dataEvento: "",
    localEvento: "",
    duracaoEvento: 0,
    descricao: "",
    valorTotal: 0,
    servicos: [
      {
        id: 1,
        nome: "",
        valorPorHora: 0,
        horas: 0,
      },
    ],
  },
  onClickDel,
}) {
  const navigate = useNavigate();

  const formatarValor = new Intl.NumberFormat("pt-br", {
    style: "currency",
    currency: "BRL",
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format;

  // Usa o valorTotal salvo no orçamento (inclui serviços + equipamentos).
  // Fallback para 0 caso ainda não tenha sido calculado.
  const valorTotal = dados.valorTotal ?? 0;

  function formatarData() {
    if (!dados.dataInicio) return "N/A";

    const dataSemNormalizacao = Intl.DateTimeFormat("pt-br", {
      day: "numeric",
      month: "short",
    }).format(new Date(dados.dataInicio));

    let dataNormalizada = dataSemNormalizacao.replace(" de ", " ");
    dataNormalizada = dataNormalizada.slice(0, dataNormalizada.length - 1);

    return dataNormalizada;
  }

  // Formata decimal em horas legível: 3 → "3h", 3.5 → "3h30", 1.25 → "1h15"
  function formatarDuracao(valor) {
    if (valor === null || valor === undefined || valor === "") return "N/A";
    const num = Number(valor);
    if (isNaN(num) || num <= 0) return "N/A";

    const horas = Math.floor(num);
    const minutos = Math.round((num - horas) * 60);

    if (minutos === 0) return `${horas}h`;
    return `${horas}h${String(minutos).padStart(2, "0")}`;
  }

  const dataFormatada = formatarData();
  const duracaoFormatada = formatarDuracao(dados.duracaoEvento);

  // Retorna fundo suave e texto escuro para o badge de status,
  // mantendo a lógica de cores original mas adaptada ao formato pill
  function definirEstilosStatus() {
    const estilosPorStatus = {
      "em análise": {
        fundo: "oklch(97% 0.05 75.834)",
        texto: "oklch(45% 0.162 75.834)",
      },
      confirmado: {
        fundo: "oklch(95% 0.05 149.214)",
        texto: "oklch(35% 0.194 149.214)",
      },
      cancelado: {
        fundo: "oklch(95% 0.05 27.518)",
        texto: "oklch(35% 0.213 27.518)",
      },
    };

    return (
      estilosPorStatus[dados.status?.toLowerCase()] ?? {
        fundo: "#e5e7eb",
        texto: "#374151",
      }
    );
  }
  const estilosStatus = definirEstilosStatus();

  const servicos = dados.servicos.map((s) => (
    <li key={s.id} className="w-full p-3 flex justify-between bg-indigo-50 border border-indigo-100 rounded-md text-xl text-slate-600">
      <span className="font-medium">{s.nome}</span>
      {/* Exibe o valor total do serviço: valorPorHora × horas */}
      {formatarValor((s.valorPorHora ?? 0) * (s.horas ?? 0))}
    </li>
  ));

  return (
    // overflow-hidden garante que a faixa superior respeite o border-radius do card
    <div className="w-120 h-160 flex flex-col bg-white rounded-xl shadow-md border border-indigo-100 overflow-hidden hover:shadow-lg transition duration-300">

      {/* Faixa de destaque superior com gradiente da identidade visual */}
      <div className="bg-gradient-to-r from-indigo-500 to-violet-500 h-1.5 shrink-0" />

      <div className="p-4 border-b border-indigo-100 text-2xl">

        {/* Descrição promovida a destaque principal do card */}
        <div className="flex justify-between items-start gap-3 mb-2.5">
          <span className="text-3xl font-medium text-indigo-400 line-clamp-2">
            {dados.descricao || "Sem descrição"}
          </span>

          {/* Badge de status no formato pill — mais clean que texto + bolinha */}
          <span
            className="px-2.5 py-1 rounded-full text-lg font-medium whitespace-nowrap shrink-0"
            style={{
              backgroundColor: estilosStatus.fundo,
              color: estilosStatus.texto,
            }}
          >
            {dados.status}
          </span>
        </div>

        {/* Data, local e duração em tom suave — informações de apoio à descrição */}
        <ul className="list-disc list-inside text-slate-700">
          <li className="mb-1">{dataFormatada}</li>
          <li className="mb-1">{dados.localEvento}</li>
          <li>Duração: {duracaoFormatada}</li>
        </ul>
      </div>

      <div className="p-3 text-2xl text-slate-700 flex justify-between">
        <span>
          <b>{dados.servicos.length} </b>
          {dados.servicos.length > 1 ? "serviços" : "serviço"}
        </span>

        {/* Total do orçamento (serviços + equipamentos), salvo no banco */}
        <span className="font-semibold text-violet-700">{formatarValor(valorTotal)}</span>
      </div>

      {/* flex-1 faz a lista ocupar todo o espaço restante, empurrando o footer de botões para o final do card */}
      <ul className="flex-1 px-3 flex flex-col gap-3 overflow-y-auto">
        {servicos}
      </ul>

      {/* Footer com botões de largura total separados por borda vertical */}
      <div className="h-14 border-t border-indigo-100 flex shrink-0">
        <button
          onClick={() => navigate(`/editar/orcamento/${dados.id}`, { state: dados })}
          className="flex-1 flex items-center justify-center gap-2 text-xl text-slate-500 hover:text-indigo-500 hover:bg-indigo-50 transition duration-200 rounded-bl-xl"
        >
          <i className="bi bi-pencil"></i>
          Editar
        </button>

        {/* Divisor vertical */}
        <div className="w-px bg-indigo-100" />

        <button
          onClick={onClickDel}
          className="flex-1 flex items-center justify-center gap-2 text-xl text-slate-500 hover:text-red-400 hover:bg-red-50 transition duration-200 rounded-br-xl"
        >
          <i className="bi bi-trash3"></i>
          Excluir
        </button>
      </div>
    </div>
  );
}