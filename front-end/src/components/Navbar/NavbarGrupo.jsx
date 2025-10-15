import { useState } from "react";
import { NavbarBotao } from "./NavbarBotao.jsx";
import { useNavigate } from "react-router-dom";

export function NavbarGrupo(props) {
  const tituloLower = props.titulo.toLowerCase();
  const tituloSingular = props.tituloSingular
    ? props.tituloSingular
    : tituloLower.slice(0, tituloLower.length - 1)
  ;

  const estilosIniciais = {
    li: "h-12", drop: null
  };

  const [estilos, setEstilos] = useState(estilosIniciais);

  function alterarDrop() {
    if (estilos.drop != null) {
      setEstilos(estilosIniciais);
    } else {
      setEstilos({
        li: "h-43", drop: {transform: "rotate(0.5turn)"}
      });
    }
  }

  const navigate = useNavigate();
  
  return (
    <li className={"transition-[height] duration-300 overflow-hidden " + estilos.li}>
      <NavbarBotao onClick={alterarDrop} estiloDrop={estilos.drop} temDrop={true}>
        {props.icones[0]}
        <span>{props.titulo}</span>
      </NavbarBotao>

      <div className={"py-2 pl-4 overflow-hidden flex flex-col gap-3"}>
        <NavbarBotao onClick={() => navigate(props.links[0])}>
          {props.icones[1]}
          <span>Todos os {tituloLower}</span>
        </NavbarBotao>

        <NavbarBotao onClick={() => navigate(props.links[1])}>
          {props.icones[2]}
          <span>Novo {tituloSingular}</span>
        </NavbarBotao>
      </div>
    </li>
  );
}
