import { useState } from "react";
import {ContainerListagem} from "./Containers/ContainerListagem.jsx";
import { Dropdown } from "../Icons/Dropdown.jsx";
import {ContainerSelectTags} from "./Containers/ContainerSelectTags.jsx";

export function CadastrarNovoServiço() {

    return (
        <div>

            <ContainerListagem/>
            <ContainerSelectTags/>

            Cadastrar Novo Serviço
        
        
        </div>
    )
}