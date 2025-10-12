import { Link } from "react-router-dom";
import { BotaoBordaGradiente } from "../components/Buttons/BotaoBordaGradiente";

export function Erro404() {
  return (
    <div>
      <h1 className="text-3xl">Erro 404</h1>
      <p className="text-x">Clique no botão abaixo para voltar à página principal</p>
      <Link to={"/"}>
        <BotaoBordaGradiente />
      </Link>
    </div>
  );
}
