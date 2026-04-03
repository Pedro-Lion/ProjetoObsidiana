import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { Label } from "../components/Login/Label";
import { api } from "../api";

// Regex para email válido
const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function Login(props) {
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [confirmar, setConfirmar] = useState("");
  const [erros, setErros] = useState({});

  const navigate = useNavigate();

  function validarCadastro() {
    const novosErros = {};

    if (!nome.trim()) {
      novosErros.nome = "Nome é obrigatório.";
    }
    if (!REGEX_EMAIL.test(email)) {
      novosErros.email = "Insira um e-mail válido (ex: fulano@email.com).";
    }
    if (senha.length < 6) {
      novosErros.senha = "A senha deve ter ao menos 6 dígitos.";
    }
    if (senha !== confirmar) {
      novosErros.confirmar = "As senhas não coincidem.";
    }

    setErros(novosErros);
    return Object.keys(novosErros).length === 0;
  }

  function validarLogin() {
    const novosErros = {};

    if (!REGEX_EMAIL.test(email)) {
      novosErros.email = "Insira um e-mail válido.";
    }
    if (!senha) {
      novosErros.senha = "Informe a senha.";
    }

    setErros(novosErros);
    return Object.keys(novosErros).length === 0;
  }

  async function logar(e) {
    e.preventDefault();
    if (!validarLogin()) return;

    try {
      const res = await api.post("/usuarios/login", {
        email, senha
      });

      if (res.status == 404) return alert("Usuário não encontrado");

      sessionStorage.setItem("token", res.data.token);
      navigate("/");
    } catch (error) {
      console.log(error);
    }
  }

  async function cadastrar(e) {
    e.preventDefault();
    if (!validarCadastro()) return;

    try {
      const res = await api.post("/usuarios/login", {
        nome, email, senha
      })

      if (res.status == 201) {
        alert("Cadastro bem sucedido. Redirecionando para a página de login.");
        return navigate("/login");
      }
    } catch (erro) {
      console.log(erro);
    }
  }

  // Componente auxiliar de erro inline
  const ErroMsg = ({ campo }) =>
    erros[campo] ? (
      <span className="text-red-400 text-sm mt-1 pl-1">{erros[campo]}</span>
    ) : null;

  const labelsCadastro =
    props.funcao == "cadastro"
      ? [
          <>
            <Label placeholder="Fulano da Silva" onInput={setNome}>Nome</Label>
            <ErroMsg campo="nome" />
          </>,
          <>
            <Label type="password" placeholder="••••••••••••" onInput={setConfirmar}>Confirmar senha</Label>
            <ErroMsg campo="confirmar" />
          </>,
        ]
      : [null, null];

  const estilo = {
    main: props.funcao == "cadastro" ? "md:flex-row-reverse" : "",
    logo: props.funcao == "cadastro" ? "right-0" : "",
    link: props.funcao == "cadastro" ? "self-end" : "",
  };

  const botao = {
    texto: props.funcao == "cadastro" ? "Cadastrar-se" : "Entrar",
    onClick: props.funcao == "cadastro" ? cadastrar : logar,
  };

  const link = {
    to: props.funcao == "cadastro" ? "/login" : "/cadastro",
    texto: props.funcao == "cadastro" ? "Já tem conta? Entre" : "Não tem conta? Cadastre-se",
  };

  return (
    <main
      className={
        "h-screen w-screen p-7.5 box-border flex flex-col md:flex-row justify-center md:justify-between items-center gap-7 text-xl " +
        estilo.main
      }
    >
      <section className="w-full md:w-120 md:h-full relative flex-none flex flex-col justify-center">
        <img
          className={"h-15 top-4 absolute " + estilo.logo}
          src="/logo.png"
          alt="Logo Obsidiana"
        />

        <form className="flex flex-col">
          {labelsCadastro[0]}

          <>
            <Label type="email" placeholder="fulano@email.com" onInput={setEmail}>
              E-mail
            </Label>
            <ErroMsg campo="email" />
          </>

          <>
            <Label type="password" placeholder="••••••••••••" onInput={setSenha}>
              Senha
            </Label>
            <ErroMsg campo="senha" />
          </>

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

      {/* Seção decorativa: oculta em mobile */}
      <section className="hidden md:block relative size-full rounded-3xl overflow-hidden">
        <div className="absolute bottom-0 w-full h-1/2 border rounded-t-full blur-[4.4rem] opacity-90 bg-gradient-to-r from-[#a5ccfe] via-[#9747ff] to-[#fca5fe]"></div>
      </section>
    </main>
  );
}