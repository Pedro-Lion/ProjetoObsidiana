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
    servicos: [
      {
        id: 1,
        nome: "",
        valorPorHora: 0,
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

  const valorServicos = dados.servicos.reduce(
    (acumulador, atual) => acumulador + atual.valorPorHora,
    0
  );

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

  function definirCorStatus() {
    const corPorStatus = {
      "em análise": "oklch(68.1% 0.162 75.834)",
      confirmado: "oklch(62.7% 0.194 149.214)",
      cancelado: "oklch(50.5% 0.213 27.518)",
    };

    const cor = corPorStatus[dados.status?.toLowerCase()];
    return cor ? cor : "black";
  }
  const corStatus = definirCorStatus();

  const servicos = dados.servicos.map((s) => (
    <li key={s.id} className="w-full p-3 flex justify-between bg-violet-200 rounded-md text-xl">
      <span className="font-medium">{s.nome}</span>
      {formatarValor(s.valorPorHora)}
    </li>
  ));

  return (
    <div className="w-120 h-160 flex flex-col border rounded-xl">
      <div className="p-4 border-b text-2xl">
        <div className="flex justify-between items-center">
          <span className="text-4xl font-medium">{dataFormatada}</span>
          <div className="flex items-center gap-2">
            <span style={{ color: corStatus }}>{dados.status}</span>
            <div
              style={{ backgroundColor: corStatus }}
              className={"size-3 rounded-full"}
            ></div>
          </div>
        </div>

        <ul className="list-disc list-inside my-2.5">
          <li className="mb-1">{dados.localEvento}</li>
          <li>Duração: {duracaoFormatada}</li>
        </ul>

        <p className="text-xl">{dados.descricao}</p>
      </div>

      <div className="p-3 text-2xl flex justify-between">
        <span>
          <b>{dados.servicos.length} </b>
          {dados.servicos.length > 1 ? "serviços" : "serviço"}
        </span>

        <span>{formatarValor(valorServicos)}</span>
      </div>

      <ul className="h-full px-3 flex flex-col gap-3 overflow-y-auto">
        {servicos}
      </ul>

      <div className="h-20 p-3 border-t">
        <BotaoPrimario
          titulo="Editar"
          icone="bi bi-pencil"
          className="mt-0 mb-0"
          onClick={() =>
            navigate(`/editar/orcamento/${dados.id}`, { state: dados })
          }
        />
        <BotaoSecundario
          titulo="Excluir"
          icone="bi bi-trash3 text-xl"
          className="ml-2 mt-0 mb-0"
          onClick={onClickDel}
        />
      </div>
    </div>
  );
}