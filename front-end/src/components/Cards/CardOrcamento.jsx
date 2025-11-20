import { BotaoPrimario } from "../Buttons/BotaoPrimario";
import { BotaoSecundario } from "../Buttons/BotaoSecundario";
import { Foto } from "../Foto";

export function CardOrcamento({
  dados = {
    status: "Confirmado",
    dataEvento: "2025-11-05",
    localEvento: "Estúdio Principal",
    duracaoEvento: 8,
    descricao: "Gravação de videoclipe publicitário",
    equipamentos: [{}],
  },
  onClickEdit,
  onClickDel,
}) {
  function formatarData() {
    const dataSemNormalizacao = Intl.DateTimeFormat("pt-br", {
      day: "numeric",
      month: "short",
    }).format(new Date(dados.dataEvento + "T00:00:00-03:00"));

    let dataNormalizada = dataSemNormalizacao.replace(" de ", " ");
    dataNormalizada = dataNormalizada.slice(0, dataNormalizada.length - 1);

    return dataNormalizada;
  }

  const dataFormatada = formatarData();

  function definirCorStatus() {
    let cor;

    switch (dados.status.toLowerCase()) {
      case "confirmado":
        cor = "oklch(62.7% 0.194 149.214)";
        break;

      case "pendente":
        cor = "oklch(68.1% 0.162 75.834)"
        break;
      case "cancelado":
        cor = "oklch(50.5% 0.213 27.518)"
        break;
      default:
        cor = "black";
        break;
    }

    return cor;
  }
  const corStatus = definirCorStatus();
  

  return (
    <div className="w-120 h-160 flex flex-col border rounded-xl">
      <div className="p-4 border-b text-2xl">
        <div className="flex justify-between items-center">
          <span className="text-4xl font-medium">{dataFormatada}</span>
          <div className="flex items-center gap-2">
            <span style={{color: corStatus}}>{dados.status}</span>
            <div style={{backgroundColor: corStatus}} className={"size-3 rounded-full"}></div>
          </div>
        </div>

        <ul className="list-disc list-inside">
          <li>{dados.localEvento}</li>
          <li>Duração: {dados.duracaoEvento}h</li>
        </ul>

        <p className="mt-3 text-xl">{dados.descricao}</p>
      </div>

      <div className="h-full"></div>

      {/* <div className="p-3 text-2xl flex justify-between">
        <span>
          <b>{dados.equipamentos.length} </b>
          {dados.equipamentos.length > 1 ? "equipamentos" : "equipamento"}
        </span>

        <span>{formatarValor(valorEquipamentos)}</span>
      </div>

      <ul className="h-full px-3 flex flex-col gap-3 overflow-y-auto">
        {equipamentos}
      </ul> */}

      <div className="h-20 p-3 border-t">
        <BotaoPrimario
          titulo="Editar"
          icone="bi bi-pencil"
          className="mt-0 mb-0"
          onClick={onClickEdit}
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
