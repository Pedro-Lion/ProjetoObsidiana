import { NavbarBotao } from "./NavbarBotao.jsx";
import { NavbarGrupo } from "./NavbarGrupo.jsx";

// icones
import { Home } from "../Icons/Home.jsx";
import { Camera } from "../Icons/Camera.jsx";
import { Pasta } from "../Icons/Pasta.jsx";
import { Documentos } from "../Icons/Documento.jsx";
import { Pessoas } from "../Icons/Pessoa.jsx";

const estiloIcone = "size-8.5 fill-gray-50";

export const navGrupos = [
  <NavbarBotao key="nav_home">
    <Home className={estiloIcone} />
    <span>Página principal</span>
  </NavbarBotao>,
];

const navGruposInfos = [
  {
    titulo: "Equipamentos",
    links: ["/equipamentos", "/novo/equipamento"],
    icones: [
      <Camera className={estiloIcone} />,
      <Camera className={estiloIcone} />,
      <Camera variacao="add" className={estiloIcone} />,
    ],
  },
  {
    titulo: "Serviços",
    links: ["/servicos", "/novo/servico"],
    icones: [
      <Pasta className={estiloIcone} />,
      <Pasta variacao="aberta" className={estiloIcone} />,
      <Pasta variacao="add" className={estiloIcone} />,
    ],
  },
  {
    titulo: "Orçamentos",
    links: ["/orcamentos", "/novo/orcamento"],
    icones: [
      <Documentos className={estiloIcone} />,
      <Documentos variacao="grupo" className={estiloIcone} />,
      <Documentos variacao="add" className={estiloIcone} />,
    ],
  },
  {
    titulo: "Profisionais",
    tituloSingular: "profissional",
    links: ["/profissionais", "/novo/profisional"],
    icones: [
      <Pessoas className={estiloIcone} />,
      <Pessoas variacao="grupo" className={estiloIcone} />,
      <Pessoas variacao="add" className={estiloIcone} />,
    ],
  },
];

navGrupos.push(
  navGruposInfos.map((g, i) => (
    <NavbarGrupo
      key={"nav_" + g.titulo.toLowerCase()}
      titulo={g.titulo}
      tituloSingular={g.tituloSingular}
      links={g.links}
      icones={g.icones}
    />
  ))
);
