import { useState } from "react";
import { ContainerListagem } from "../components/Containers/ContainerListagem.jsx";
// import { Dropdown } from "../Icons/Dropdown.jsx";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags.jsx";
import { InputFoto } from "../components/Inputs/InputFoto"
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel"
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel"
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario"



export function CadastrarNovoServico() {

    return (
        <>
            <h1 className="">Cadastrar Serviço</h1>

            <section className="flex items-center w-full flex-col gap-10 shadow-md p-5">
                <div className="w-320">

                <div className="flex justify-between gap-5 items-start mb-5">
                    <InputBordaLabel
                        type="text"
                        titulo="Nome do Serviço"
                        placeholder="Insira o nome aqui"
                        className="w-150" />
                    <InputBordaLabel
                        type="number"
                        titulo="Valor por Hora"
                        placeholder="Ex: 15.00"
                        className="w-150" />
                </div>
                <TextareaBordaLabel
                    titulo="Descrição do Serviço"
                    placeholder="Digite aqui informações do Serviço"
                    larguraCampo="w-full"
                    rows="4" />

                <div className="w-full mt-10">
                    <ContainerSelectTags
                        titulo="Equipamentos"
                        placeholder="Escolha uma opção" />
                </div>
                <div className="w-full mt-10">
                    <ContainerSelectTags
                        titulo="Profissionais"
                        placeholder="Escolha uma opção" />
                </div>
                <div className="flex justify-end ">
                <BotaoPrimario
                    titulo="Cadastrar Serviço"
                    className="w-flex"
                /></div>

                </div>


            </section>
        </>
    )

}

