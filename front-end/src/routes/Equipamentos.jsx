import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { ContainerListagem } from "../components/Containers/ContainerListagem";

export function Equipamentos() {
  const infoItens = [
    { titulo: "Câmera Digital" },
    { titulo: "Câmera Analógica" },
    { titulo: "Led" },
    { titulo: "Tripé" },
    { titulo: "Cartão de memória" },
    { titulo: "Kit Limpeza de lentes" },
    { titulo: "Lentes" }
  ];

  const itens = infoItens.map((item, i) => (
    <div className="pr-5 flex items-center" key={i}>
      <InputCheckbox className="mr-3" />
      <ContainerListagem titulo={item.titulo} />
    </div>
  ));

  return (
    <>
      <h1 className="text-4xl font-medium">Equipamentos</h1>

      <div className="mt-3 flex justify-between">
        <form className="w-330 flex gap-3.5">
          <InputBordaLabel
            className="w-full"
            placeholder="Digite o que deseja pesquisar"
          />

          <BotaoPrimario titulo="Pesquisar" className="mb-0 mt-0" />
        </form>

        <BotaoPrimario
          titulo="+ Novo equipamento"
          className="mt-0 mb-0 mr-5 flex-none"
        />
      </div>

      <section className="h-full mt-5 -ml-10 overflow-y-scroll">{itens}</section>
    </>
  );
}
