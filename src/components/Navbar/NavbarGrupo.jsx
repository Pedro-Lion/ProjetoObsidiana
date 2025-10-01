import { NavbarBotao } from "./NavbarBotao.jsx";

export function NavbarGrupo(props) {
  const tituloLower = props.titulo.toLowerCase();
  const tituloSingular = props.tituloSingular
    ? props.tituloSingular
    : tituloLower.slice(0, tituloLower.length - 1)
  ;

  return (
    <li>
      <NavbarBotao temDrop={true}>
        {props.children[0]}
        <span>{props.titulo}</span>
      </NavbarBotao>

      <div className="p-2 pl-4 pr-0 flex flex-col gap-3">
        <NavbarBotao>
          {props.children[1]}
          <span>Todos os {tituloLower}</span>
        </NavbarBotao>

        <NavbarBotao>
          {props.children[2]}
          <span>Novo {tituloSingular}</span>
        </NavbarBotao>
      </div>
    </li>
  );
}
