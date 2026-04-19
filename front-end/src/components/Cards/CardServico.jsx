import { useNavigate } from "react-router-dom";
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

  const equipamentos = dados.equipamentos.map((e) => (
    <li
      key={e.id}
      className="p-2.5 flex justify-between items-center bg-indigo-50 border border-indigo-100 rounded-md text-xl text-slate-700"
    >
      <Foto tamanho="3.5" icone="bi bi-camera text-[2rem]" />
      <span className="ml-3">{e.nome}</span>
      {/* Categoria em tom mais suave por ser informação secundária */}
      <span className="m-auto text-slate-500">{e.categoria}</span>
      <span>{formatarValor(e.valorPorHora)}</span>
    </li>
  ));

  function definirValorEquipamentos() {
    if (dados.equipamentos.length == 0) return 0;

    return dados.equipamentos.length > 1
      ? dados.equipamentos.reduce(
          (acumulador, atual) => acumulador + atual.valorPorHora,
          0
        )
      : dados.equipamentos[0]?.valorPorHora;
  }
  const valorEquipamentos = definirValorEquipamentos();

  return (
    // overflow-hidden garante que a faixa superior respeite o border-radius do card
    <div className="w-120 h-160 flex flex-col bg-white rounded-xl shadow-md border border-indigo-100 overflow-hidden hover:shadow-lg transition duration-300">

      {/* Faixa de destaque superior com gradiente da identidade visual */}
      <div className="bg-gradient-to-r from-indigo-500 to-violet-500 h-1.5 shrink-0" />

      <div className="p-4 border-b border-indigo-100">
        <div className="flex justify-between">
          {/* Nome do serviço em indigo, alinhado com a cor dos headings do projeto */}
          <span className="text-4xl font-medium text-indigo-400">{dados.nome}</span>
          <span className="text-2xl text-slate-700">
            {formatarValor(dados.valorPorHora + valorEquipamentos)}
          </span>
        </div>
        {/* Descrição em tom mais suave por ser informação secundária */}
        <p className="mt-4 text-xl text-slate-700">{dados.descricao}</p>
      </div>

      <div className="p-3 text-2xl text-slate-700 flex justify-between">
        <span>
          <b>{dados.equipamentos.length} </b>
          {dados.equipamentos.length > 1 ? "equipamentos" : "equipamento"}
        </span>

        <span>{formatarValor(valorEquipamentos)}</span>
      </div>

      {/* flex-1 faz a lista ocupar todo o espaço restante, empurrando o footer de botões para o final do card */}
      <ul className="flex-1 px-3 flex flex-col gap-3 overflow-y-auto">
        {equipamentos}
      </ul>

      <div className="h-20 p-3 border-t border-indigo-100 flex justify-end items-center">
        <BotaoSecundario
          titulo="Editar"
          icone="bi bi-pencil"
          className="mt-0 mb-0"
          onClick={() =>
            navigate("/editar/servico/" + dados.id, {
              state: dados,
            })
          }
        />
        <BotaoPrimario
          titulo="Excluir"
          icone="bi bi-trash3 text-xl"
          className="ml-2 mt-0 mb-0"
          onClick={onClickDel}
        />
      </div>
    </div>
  );
}
