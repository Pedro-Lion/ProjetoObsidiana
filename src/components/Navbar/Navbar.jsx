import { NavbarGrupo } from "./NavbarGrupo.jsx";

import { Camera } from "../Icons/Camera.jsx";

export function Navbar() {
  const navGruposInfos = [
    {
      titulo: "Equipamentos",
      children: [
        <Camera className="size-8.5 fill-gray-50" />,
        <Camera className="size-8.5 fill-gray-50" />,
        <Camera variacao="add" className="size-8.5 fill-gray-50" />,
      ],
    },
  ];

  const navGrupos = navGruposInfos.map((g) => (
    <NavbarGrupo
      key={"nav_" + g.titulo.toLowerCase()}
      titulo={g.titulo}
      tituloSingular={g.tituloSingular}
    >
      {g.children}
    </NavbarGrupo>
  ));

  return (
    <header
      style={{ height: "90vh" }}
      className="w-114 p-6 absolute -translate-y-1/2 top-1/2 left-4 rounded-[3.2rem] bg-gray-800 text-gray-50"
    >
      <img className="h-10" src="/logo.png" alt="Logo Obsidiana" />

      <nav className="p-3">
        <ul>{navGrupos}</ul>
      </nav>
    </header>
  );
}
