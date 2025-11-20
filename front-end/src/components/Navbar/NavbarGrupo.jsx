import { useState } from "react";
import { NavbarBotao } from "./NavbarBotao.jsx";
import { useNavigate } from "react-router-dom";

export function NavbarGrupo({ titulo = "", tituloSingular = "", links = [], icones = [], aberto = false, onClick }) {
  const tituloLower = titulo.toLowerCase();
  const tituloSing = tituloSingular
    ? tituloSingular.toLowerCase()
    : tituloLower.slice(0, tituloLower.length - 1)
  ;

  const estilos = {
    li: !aberto ? "h-12" : "h-46",
    drop: !aberto ? null : {transform: "rotate(0.5turn)"} 
  }

  const navigate = useNavigate();
  
  return (
    <li className={"transition-[height] duration-300 overflow-hidden " + estilos.li}>
      <NavbarBotao onClick={onClick} estiloDrop={estilos.drop} temDrop={true}>
        {icones[0]}
        <span>{titulo}</span>
      </NavbarBotao>

      <div className={"py-2 pl-4 overflow-hidden flex flex-col gap-3"}>
        <NavbarBotao onClick={() => navigate(links[0])}>
          {icones[1]}
          <span>Todos os {tituloLower}</span>
        </NavbarBotao>

        <NavbarBotao onClick={() => navigate(links[1])}>
          {icones[2]}
          <span>Novo {tituloSing}</span>
        </NavbarBotao>
      </div>
    </li>
  );
}
