import { useState } from "react";
import { NavbarBotao } from "./NavbarBotao.jsx";

export function NavbarGrupo(props) {
  const tituloLower = props.titulo.toLowerCase();
  const tituloSingular = props.tituloSingular
    ? props.tituloSingular
    : tituloLower.slice(0, tituloLower.length - 1)
  ;

  const [dropado, setDropado] = useState(false);
  const [estiloDrop, setEstiloDrop] = useState({});

  function alterarDrop() {
    if (dropado) {
      setDropado(false);
      setEstiloDrop({});
    } else {
      setDropado(true);
      setEstiloDrop({transform: "rotate(0.5turn)"});
    }
  }

  return (
    <li>
      <NavbarBotao onClick={alterarDrop} estiloDrop={estiloDrop} temDrop={true}>
        {props.icones[0]}
        <span>{props.titulo}</span>
      </NavbarBotao>

      {dropado && (
        <div className="p-2 pl-4 pr-0 flex flex-col gap-3">
          <NavbarBotao link={props.links[0]}>
            {props.icones[1]}
            <span>Todos os {tituloLower}</span>
          </NavbarBotao>

          <NavbarBotao link={props.links[1]}>
            {props.icones[2]}
            <span>Novo {tituloSingular}</span>
          </NavbarBotao>
        </div>
      )}
    </li>
  );
}
