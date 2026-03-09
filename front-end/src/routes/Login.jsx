import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { Label } from "../components/Login/Label";

export function Login(props) {
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [confirmar, setConfirmar] = useState("");

  const navigate = useNavigate();

  async function logar(e) {
    e.preventDefault();

    try {
      const res = await fetch("http://localhost:8080/usuario/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email,
          senha,
        }),
      });

      if (res.ok) {
        const dados = await res.json()
        sessionStorage.setItem("token", dados.token)
        navigate("/")
      }

      if (res.status == 404) alert("Usuário não encontrado")

    } catch (error) {
      console.log(error)
    }
  }

  async function cadastrar(e) {
    e.preventDefault();

    try {
      const res = await fetch("http://localhost:8080/usuario/cadastrar", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          nome,
          email,
          senha,
        }),
      });

      if (res.ok) {
        alert("Cadastro bem sucedido. Redirecionando para a página de login.");
        return navigate("/login");
      }
    } catch (erro) {
      console.log(erro);
    }
  }

  const labelsCadastro =
    props.funcao == "cadastro"
      ? [
          <Label placeholder="Fulano da Silva" onInput={setNome}>
            Nome
          </Label>,

          <Label
            type="password"
            placeholder="••••••••••••"
            onInput={setConfirmar}
          >
            Confirmar senha
          </Label>,
        ]
      : [null, null];

  const estilo = {
    main: props.funcao == "cadastro" ? "flex-row-reverse" : "",
    logo: props.funcao == "cadastro" ? "right-0" : "",
    link: props.funcao == "cadastro" ? "self-end" : "",
  };

  const botao = {
    texto: props.funcao == "cadastro" ? "Cadastrar-se" : "Entrar",
    onClick: props.funcao == "cadastro" ? cadastrar : logar,
  };

  const link = {
    to: props.funcao == "cadastro" ? "/login" : "/cadastro",
    texto:
      props.funcao == "cadastro"
        ? "Já tem conta? Entre"
        : "Não tem conta? Cadastre-se",
  };

  return (
    <main
      className={
        "h-screen w-screen p-7.5 box-border flex justify-between items-center gap-7 text-xl " +
        estilo.main
      }
    >
      <section className="h-full w-120 relative flex-none flex flex-col justify-center">
        <img
          className={"h-15 top-4 absolute " + estilo.logo}
          src="/logo.png"
          alt="Logo Obsidiana"
        />

        <form className="flex flex-col">
          {labelsCadastro[0]}

          <Label type="email" placeholder="fulano@email.com" onInput={setEmail}>
            E-mail
          </Label>

          <Label type="password" placeholder="••••••••••••" onInput={setSenha}>
            Senha
          </Label>

          {labelsCadastro[1]}

          <button
            style={{
              background:
                "#fca5fe linear-gradient(to right, #fca5fe 5%, #9747ff, #a5ccfe 64%, white 76%)",
            }}
            className="h-15 mt-7.5 border border-gray-300 rounded-xl text-white font-semibold"
            onClick={botao.onClick}
          >
            {botao.texto}
          </button>
        </form>

        <Link
          className={"mt-7 text-lg text-[#0a0840] underline " + estilo.link}
          to={link.to}
        >
          {link.texto}
        </Link>
      </section>

      <section className="relative size-full rounded-3xl overflow-hidden">
        <div className="absolute bottom-0 w-full h-1/2 border rounded-t-full blur-[4.4rem] opacity-90 bg-gradient-to-r from-[#a5ccfe] via-[#9747ff] to-[#fca5fe]"></div>
      </section>
    </main>
  );
}
