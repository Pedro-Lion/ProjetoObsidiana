import { createBrowserRouter } from "react-router-dom";

import { App } from "./App";
import { Erro404 } from "./routes/Erro404";
import { Login } from "./routes/Login";

import { Home } from "./routes/Home";
import { CadastroGenerico } from "./routes/CadastroGenerico";
import { Equipamentos } from "./routes/Equipamentos";
import { CadastroEquipamentos } from "./routes/CadastroEquipamento";
import { Servicos } from "./routes/Servicos";
import { CadastrarNovoServico } from "./routes/CadastrarNovoServico"
import { Orcamentos } from "./routes/Orcamentos";
import { Profissionais } from "./routes/Profissionais";
import { CadastroProfissionais } from "./routes/CadastroProfissionais";
import { Perfil } from "./routes/Perfil";
import { AplicacaoComponentes } from "./routes/AplicacaoComponentes";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    errorElement: <Erro404 />,
    children: [
      {
        path: "/",
        element: <Home />,
      },
      {
        path: "/cadastro/:item",
        element: <CadastroGenerico />,
      },
      {
        path: "/equipamentos",
        element: <Equipamentos />,
      },
      {
        path: "/cadastro/equipamentos",
        element: <CadastroEquipamentos />
      },
      {
        path: "/servicos",
        element: <Servicos />,
      },
      {
        path: "/cadastro/servico",
        element: <CadastrarNovoServico />
      },
      {
        path: "/orcamentos",
        element: <Orcamentos />,
      },
      {
        path: "/profissionais",
        element: <Profissionais />,
      },
      {
        path: "/cadastro/profissionais",
        element: <CadastroProfissionais />
      },
      {
        path: "/perfil",
        element: <Perfil />
      },
    ],
  },
  {
    path: "/login",
    element: <Login />,
    errorElement: <Erro404 />
  },
  {
    path: "/cadastro",
    element: <Login funcao="cadastro" />,
    errorElement: <Erro404 />
  },
  {
    path: "/dev",
    element: <AplicacaoComponentes />
  }
]);
