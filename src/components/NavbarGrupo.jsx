export function NavbarGrupo(props) {
  const tituloLower = props.titulo.toLowerCase();
  const tituloSingular = tituloLower.slice(0, tituloLower.length-1);
  
  return (
    <li>
      <button>
        <div>
          <img src={props.iconePrincipal} />
          <span>{props.titulo}</span>
        </div>
        <img src="/icons/dropdown.svg" />
      </button>
      <div hidden>
        <button>
          <img src={props.iconeListar} />
          <span>Todos os {tituloLower}</span>
        </button>
        <button>
          <img src={props.iconeAdd} />
          <span>Novo {tituloSingular}</span>
        </button>
      </div>
    </li>
  );
}