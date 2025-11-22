import { BotaoPrimario } from "../Buttons/BotaoPrimario";
import { BotaoSecundario } from "../Buttons/BotaoSecundario";
import { Foto } from "../Foto";

export function CardServico({
  dados = {
    id: 1,
    nome: "",
    descricao: "",
    valorPorHora: 0,
    equipamentos: [
      {
        id: 1,
        nome: "",
        categoria: "",
        valorPorHora: 0,
      }
    ],
  },
  onClickEdit, onClickDel,
}) {
  const formatarValor = new Intl.NumberFormat("pt-br", {
    style: "currency",
    currency: "BRL",
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format;

  const equipamentos = dados.equipamentos.map((e) => (
    <li key={e.id} className="p-2.5 flex justify-between items-center bg-violet-200 rounded-md text-xl">
      <Foto tamanho="3.5" icone="bi bi-camera text-[2rem]" />
      <span className="ml-3">{e.nome}</span>
      <span className="m-auto">{e.categoria}</span>
      <span>{formatarValor(e.valorPorHora)}</span>
    </li>
  ));

  const valorEquipamentos =
    dados.equipamentos.length > 1
      ? dados.equipamentos.reduce(
          (acumulador, atual) => acumulador + atual.valorPorHora,
          0
        )
      : dados.equipamentos[0].valorPorHora;

  return (
    <div className="w-120 h-160 flex flex-col border rounded-xl">
      <div className="p-4 border-b">
        <div className="flex justify-between">
          <span className="text-4xl font-medium">
            {dados.nome}
          </span>
          <span className="text-2xl">
            {formatarValor(dados.valorPorHora + valorEquipamentos)}
          </span>
        </div>
        <p className="mt-4 text-xl">{dados.descricao}</p>
      </div>

      <div className="p-3 text-2xl flex justify-between">
        <span>
          <b>{dados.equipamentos.length} </b>
          {dados.equipamentos.length > 1 ? "equipamentos" : "equipamento"}
        </span>

        <span>{formatarValor(valorEquipamentos)}</span>
      </div>

      <ul className="h-full px-3 flex flex-col gap-3 overflow-y-auto">
        {equipamentos}
      </ul>

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
