import { useState } from "react";
import { NavbarBotao } from "./NavbarBotao.jsx";
import { NavbarGrupo } from "./NavbarGrupo.jsx";

export function Navbar() {
  const [abertos, setAbertos] = useState([false, false, false, false]);
  function alterarAbertos(indexAlterar = 0) {
    const novoAbertos = abertos.map((a) => (a = false));

    if (!abertos[indexAlterar]) novoAbertos[indexAlterar] = true;

    setAbertos(novoAbertos);
  }

  const estiloIcone = "text-[2rem] text-gray-50";

  const navGrupos = [
    <NavbarBotao key="nav_home">
      <i className={"bi bi-house " + estiloIcone}></i>
      <span>Página principal</span>
    </NavbarBotao>,
  ];

  const navGruposInfos = [
    {
      titulo: "Equipamentos",
      links: ["/equipamentos", "/cadastro/equipamentos"],
      icones: [
        <i className={"bi bi-camera " + estiloIcone}></i>,
        <i className={"bi bi-camera " + estiloIcone}></i>,
        <i className={"bi bi-plus-circle " + estiloIcone}></i>,
      ]
    },
    {
      titulo: "Serviços",
      links: ["/servicos", "/cadastro/servicos"],
      icones: [
        <i className={"bi bi-folder " + estiloIcone}></i>,
        <i className={"bi bi-folder2-open " + estiloIcone}></i>,
        <i className={"bi bi-folder-plus " + estiloIcone}></i>,
      ]
    },
    {
      titulo: "Profissionais",
      tituloSingular: "profissional",
      links: ["/profissionais", "/cadastro/profissionais"],
      icones: [
        <i className={"bi bi-person " + estiloIcone}></i>,
        <i className={"bi bi-person " + estiloIcone}></i>,
        <i className={"bi bi-person-plus " + estiloIcone}></i>,
      ]
    },
    {
      titulo: "Orçamentos",
      links: ["/orcamentos", "/cadastro/orcamentos"],
      icones: [
        <i className={"bi bi-file-earmark " + estiloIcone}></i>,
        <i className={"bi bi-file-earmark " + estiloIcone}></i>,
        <i className={"bi bi-file-earmark-plus " + estiloIcone}></i>,
      ]
    }
  ];

  navGrupos.push(
    navGruposInfos.map((g, i) => (
      <NavbarGrupo
        key={"nav_" + g.titulo.toLowerCase()}
        titulo={g.titulo}
        tituloSingular={g.tituloSingular}
        links={g.links}
        icones={g.icones}
        aberto={abertos[i]}
        onClick={() => alterarAbertos(i)}
      />
    ))
  );

  return (
    <nav className="p-3 h-full overflow-auto">
      <ul className="flex flex-col gap-4">{navGrupos}</ul>
    </nav>
  );
}
