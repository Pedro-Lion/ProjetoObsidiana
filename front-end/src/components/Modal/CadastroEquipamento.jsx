import { InputFoto } from "../Inputs/InputFoto";
import { InputBordaLabel } from "../Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../Inputs/TextareaBordaLabel";
import { BotaoPrimario } from "../Buttons/BotaoPrimario";
import { BotaoSecundario } from "../Buttons/BotaoSecundario";

export function CadastroEquipamento({ funcaoCancelar = undefined }) {
  return (
    <>
      <div className="absolute w-full h-full -m-20 bg-black opacity-10"></div>

      <section
        style={{
          transform: "translate(-50%, -50%)",
        }}
        className="
          absolute top-1/2 left-1/2 overflow-y-scroll
          border-box w-[80%] max-w-230 h-[80%] max-h-150 p-10
          flex flex-col justify-between gap-3
          bg-white rounded-2xl shadow-xl border border-gray-300
        "
      >
        <div className="flex justify-between mb-5">
          <h2>Cadastrar Equipamento</h2>
          <BotaoSecundario titulo="X" className="mb-0 mt-0 h-fit" onClick={funcaoCancelar} />
        </div>

        <section>
          <div className="flex-none mb-2">
            <InputFoto />
          </div>

          <div className="flex gap-10">
            <div className="flex flex-col justify-between">
              <InputBordaLabel
                titulo="Nome"
                placeholder="Ex: Memória SD 128gb"
              />
              <TextareaBordaLabel titulo="Descrição" placeholder="Descrição" />
              <InputBordaLabel
                titulo="Quantidade"
                type="number"
                placeholder="Ex: 10"
              />
            </div>

            <div className="flex flex-col justify-between">
              <InputBordaLabel
                titulo="Categoria"
                placeholder="Ex: Armazenamento"
              />
              <InputBordaLabel titulo="Marca" placeholder="Ex: SanDisk" />
              <InputBordaLabel
                titulo="Valor"
                type="number"
                placeholder="Ex: 150.00"
              />
            </div>
          </div>
        </section>

        <div className="mt-8">
          <BotaoPrimario titulo="Cadastrar" className="mb-0 mt-0 mr-4 w-42" />
          <BotaoSecundario
            titulo="Cancelar"
            className="mb-0 mt-0"
            onClick={funcaoCancelar}
          />
        </div>
      </section>
    </>
  );
}
