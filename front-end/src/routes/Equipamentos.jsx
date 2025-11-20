import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { ContainerListagem } from "../components/Containers/ContainerListagem";
import { useEffect, useState } from "react";
import { Modal } from "../components/Modal/Modal.jsx";
import { api } from "../api.js";

export function Equipamentos() {
  const [equipamentos, setEquipamentos] = useState("Buscando equipamentos...");
  const [modal, setModal] = useState(false);

  useEffect(() => {
    async function buscarEquipamentos(params) {
      try {
        const resposta = await api.get("/equipamento");

        if (resposta.status == 200) {
          const dados = resposta.data;

          setEquipamentos(
            dados.map((equip) => (
              <div className="pr-5 flex items-center" key={equip.id}>
                <InputCheckbox className="mr-3" />
                <ContainerListagem dados={equip} />
              </div>
            ))
          );
        }
      } catch (erro) {
        console.log(erro);
      }
    }

    buscarEquipamentos();
  }, []);

  return (
    <>
      {modal && (
        <Modal
          titulo="Ocorreu um erro"
          descricao="Ocorreu um erro deconhecido. Por favor, tente novamente mais tarde"
        >
          <BotaoPrimario
            titulo="Fechar"
            className="mb-0 mt-0"
            onClick={() => setModal(false)}
          />
        </Modal>
      )}

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
          onClick={() => setModal(true)}
        />
      </div>

      <section className="h-full mt-5 -ml-10 overflow-y-scroll">
        {equipamentos}
      </section>
    </>
  );
}
