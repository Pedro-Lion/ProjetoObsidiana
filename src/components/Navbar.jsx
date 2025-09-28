import { NavbarGrupo } from "./NavbarGrupo.jsx";

export function Navbar() {
  return (
    <nav>
      <ul className="list-none">
        <NavbarGrupo 
          titulo="Equipamentos"
          iconePrincipal="/icons/camera.svg"
          iconeListar="/icons/camera.svg"
          iconeAdd="/icons/camera_add.svg"
        />
      </ul>
    </nav>
  );
}
