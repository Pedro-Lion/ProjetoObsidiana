import { useState } from "react";

import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { BotaoBordaGradiente } from "../components/Buttons/BotaoBordaGradiente";

import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { InputFundoCor } from "../components/Inputs/InputFundoCor";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { InputFoto } from "../components/Inputs/InputFoto";

import { ContainerListagem } from "../components/Containers/ContainerListagem";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags";

import { CardServico } from "../components/Cards/CardServico";
import { CardOrcamento } from "../components/Cards/CardOrcamento";
import { ContainerProfissional } from "../components/Containers/ContainerProfissional";

export function AplicacaoComponentes() {
  // botões
  function click() {
    alert("Clicado!");
  }

  // inputs
  const [captura, setCaptura] = useState("");
  function input(e) {
    const valor = e.target.value;

    if (e.target.type == "number" && !valor) {
      return alert("Isso não é um número!");
    }

    setCaptura(valor);
  }

  function check(e) {
    alert("Checado? " + e.target.checked);
  }

  const [urlImagem, setUrlImagem] = useState(null);
  function lidarFoto(e) {
    const url = URL.createObjectURL(e.target.files[0]);
    setUrlImagem(url);
  }

  // contariner equipamento e cards
  function clickContainer(funcao, tipo) {
    let mensagem = "";

    if (funcao == "editar") {
      mensagem = `Esse botão deveria fazer uma navegação para a tela de editar um ${tipo}!`;
    } else {
      mensagem = `Esse botão deveria abrir um modal para confirmar a remoção do ${tipo}!`;
    }

    alert(mensagem);
  }

  const dadosCardServico = {
    nome: "Serviço 1",
    descricao: "Descrição do serviço",
    valorPorHora: 200.3,
    equipamentos: [],
  };

  const dadosCardOrcamento = {
    id: 1,
    status: "Confirmado",
    dataEvento: "2025-11-05",
    localEvento: "Estúdio Principal",
    duracaoEvento: 8,
    descricao: "Gravação de videoclipe publicitário",
    servicos: [],
  };

  for (let numero = 1; numero <= 6; numero++) {
    if (numero <= 5) {
      dadosCardServico.equipamentos.push({
        nome: "Câmera 0" + numero,
        categoria: "Fotografia",
        valorPorHora: 20,
      });
    }

    dadosCardOrcamento.servicos.push({
      nome: "Serviço " + numero,
      valorPorHora: 100,
    });
  }

  // container tags
  const [itensSelecionados, setItensSelecionados] = useState([]);

  return (
    <main className="w-full p-6 overflow-y-scroll bg-white flex flex-col gap-15">
      <section>
        <h2>Botões</h2>

        <div className="flex gap-4">
          <BotaoPrimario onClick={click} className="w-3" />
          <BotaoSecundario onClick={click} className="w-60" />
          <BotaoBordaGradiente onClick={click} />
        </div>
      </section>

      <section>
        <h2>Inputs {captura && `- captura: ${captura}`}</h2>

        <div className="flex gap-7">
          <div>
            <InputBordaLabel
              type="text"
              titulo="Nome completo"
              placeholder="Insira o nome aqui"
              className="w-80"
              onInput={input}
            />

            <TextareaBordaLabel
              titulo="Observações"
              placeholder="Digite aqui informações importantes"
              larguraCampo="w-80"
              rows="4"
              onInput={input}
            />
          </div>

          <div>
            <InputFundoCor
              type="number"
              titulo="Telefone"
              placeholder="Somente números"
              onInput={input}
            />

            <InputCheckbox texto="Checkbox" onChange={check} className="mt-2" />
          </div>

          <div className="flex gap-3">
            <InputFoto onChange={lidarFoto} icone="bi bi-person-up" />

            <div>
              <span>Captura do InputFoto:</span>
              <img
                src={urlImagem}
                className="h-40 border-violet-500 border-2 rounded-md"
              />
            </div>
          </div>
        </div>
      </section>

      <section>
        <h2>Containers</h2>

        <ContainerListagem
          dados={{
            nome: "Câmera 01",
            quantidade: 5,
            categoria: "Gravação",
            marca: "Sony",
            modelo: "C9-20mm DisplayHD",
            numeroSerie: "N00123",
            valorPorHora: 25.5,
          }}
          onClickEdit={() => clickContainer("editar", "equipamento")}
          onClickDel={() => clickContainer("", "equipamento")}
        />

        <ContainerProfissional
          dados={{
            nome: "Roberto",
            disponibilidade: "Ter a qui das 12h às 20h",
            contato: "roberto@gmail.com",
          }}
        />

        <ContainerSelectTags
          itens={[
            { value: "ariel", label: "Ariel" },
            { value: "sebastian", label: "Sebastião" },
            { value: "flounder", label: "Linguado" },
            { value: "ursula", label: "Úrsula" },
            { value: "eric", label: "Príncipe Eric" },
          ]}
          preSelecao={[{ value: "ariel", label: "Ariel" }]}
          onChange={(itens) => setItensSelecionados(itens)}
        />

        <div className="text-xl">
          <span className="font-medium">Itens selecionados:</span>

          <ul className="list-disc list-inside">
            {itensSelecionados.map((item, index) => (
              <li key={index}>{item.label}</li>
            ))}
          </ul>
        </div>
      </section>

      <section>
        <h2 className="mb-3">Cards</h2>

        <div className="flex flex-wrap gap-7">
          <CardServico
            dados={dadosCardServico}
            onClickEdit={() => clickContainer("editar", "serviço")}
            onClickDel={() => clickContainer("", "serviço")}
          />

          <CardOrcamento
            dados={dadosCardOrcamento}
            onClickEdit={() => clickContainer("editar", "serviço")}
            onClickDel={() => clickContainer("", "serviço")}
          />
        </div>
      </section>
    </main>
  );
}
