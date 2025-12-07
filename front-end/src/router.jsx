import { createBrowserRouter } from "react-router-dom";

import { App } from "./App";
import { Erro404 } from "./routes/Erro404";
import { Login } from "./routes/Login";

import { Home } from "./routes/Home";
import { Equipamentos } from "./routes/Equipamentos";
import { CadastroEquipamentos } from "./routes/CadastroEquipamento";
import { Servicos } from "./routes/Servicos";
import { CadastrarNovoServico } from "./routes/CadastrarNovoServico"
import { Orcamentos } from "./routes/Orcamentos";
import { Profissionais } from "./routes/Profissionais";
import { CadastroProfissionais } from "./routes/CadastroProfissionais";
import { Perfil } from "./routes/Perfil";
import { AplicacaoComponentes } from "./routes/AplicacaoComponentes";
import { CadastroOrcamento } from "./routes/CadastroOrcamento";
import {API} from "./routes/API";

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
        path: "/equipamentos",
        element: <Equipamentos />,
      },
      {
        path: "/cadastro/equipamentos",
        element: <CadastroEquipamentos />
      },
      {
        path: "/editar/equipamento/:id",
        element: <CadastroEquipamentos />
      },
      {
        path: "/servicos",
        element: <Servicos />,
      },
      {
        path: "/cadastro/servicos",
        element: <CadastrarNovoServico />
      },
      {
        path: "/editar/servico/:id",
        element: <CadastrarNovoServico />
      },
      {
        path: "/orcamentos",
        element: <Orcamentos />
      },
      {
        path: "/cadastro/orcamentos",
        element: <CadastroOrcamento />
      },
      {
        path: "/editar/orcamento/:id",
        element: <CadastroOrcamento />
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
        path: "/editar/profissional/:id",
        element: <CadastroProfissionais />
      },
      {
      path: "/api",
      element: <API />
      }
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
