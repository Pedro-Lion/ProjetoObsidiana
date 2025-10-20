import { createBrowserRouter } from "react-router-dom";

import { App } from "./App";
import { Erro404 } from "./routes/Erro404";
import { Login } from "./routes/Login";

import { Home } from "./routes/Home";
import { Novo } from "./routes/Novo";
import { Equipamentos } from "./routes/Equipamentos";
import { Servicos } from "./routes/Servicos";
import { Orcamentos } from "./routes/Orcamentos";
import { Profissionais } from "./routes/Profissionais";
import { CadastroProfissionais } from "./routes/CadastroProfissionais";

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
        path: "/novo/:item",
        element: <Novo />,
      },
      {
        path: "/equipamentos",
        element: <Equipamentos />,
      },
      {
        path: "/servicos",
        element: <Servicos />,
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
  }
]);
