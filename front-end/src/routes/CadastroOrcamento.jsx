import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { SelectBordaLabel } from "../components/Inputs/SelectBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";

export function CadastroOrcamento() {
  return (
    <>
      <h1>Cadastro orçamento</h1>

      <section className="flex flex-col gap-2">
        <div className="flex justify-between gap-5">
          <SelectBordaLabel
            className="w-full"
            titulo="Status"
            options={[
              { label: "Em análise" },
              { label: "Confirmado" },
              { label: "Cancelado" },
            ]}
            disabled={true}
          />
          <InputBordaLabel
            className="w-full"
            type="date"
            titulo="Data do evento"
          />
          <InputBordaLabel
            className="w-full"
            type="number"
            titulo="Duração em horas"
          />
        </div>

        <TextareaBordaLabel titulo="Descrição" className="h-40 -mt-4 mb-3" />

        <ContainerSelectTags titulo="Serviços" />

        <ContainerSelectTags titulo="Equipamentos" />

        <ContainerSelectTags titulo="Profissionais" />
      </section>

      <BotaoPrimario titulo="Cadastrar" className="self-end" />
    </>
  );
}
