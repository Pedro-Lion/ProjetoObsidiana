import 'bootstrap-icons/font/bootstrap-icons.css';
import { Navbar } from "./components/Navbar/Navbar.jsx";
import { Outlet, useNavigate } from "react-router-dom";
import { Foto } from "./components/Foto.jsx";
import "./App.css";
import { useEffect, useState, useCallback } from 'react';

export function App() {
  const navigate = useNavigate();
    const [menuAberto, setMenuAberto] = useState(false);
  const [nomeUsuario, setNomeUsuario] = useState("Usuário");
  const [fotoUsuario, setFotoUsuario] = useState(null);

  const carregarPerfil = useCallback(() => {
    const nomeSalvo = sessionStorage.getItem("perfil_nome");
    const fotoSalva = sessionStorage.getItem("perfil_foto");
    setNomeUsuario(nomeSalvo || "Usuário");
    setFotoUsuario(fotoSalva || null);
  }, []);

  useEffect(() => {
    if (!sessionStorage.getItem("token")) {
      alert("Faça login para usar a aplicação!");
      navigate("/login");
      return;
    }

    carregarPerfil();

    const onPerfilAtualizado = (e) => {
      if (e.detail?.nome) setNomeUsuario(e.detail.nome);
      setFotoUsuario(e.detail?.foto || null);
    };
    window.addEventListener("perfil-atualizado", onPerfilAtualizado);
    return () => window.removeEventListener("perfil-atualizado", onPerfilAtualizado);
  }, []);

  return (
    <>
      {/* Overlay para fechar o menu em mobile */}
      {menuAberto && (
        <div
          className="fixed inset-0 bg-black/50 z-20 md:hidden"
          onClick={() => setMenuAberto(false)}
        />
      )}

      {/* Botão hambúrguer — visível só em mobile */}
      <button
        className="fixed top-4 left-4 z-30 md:hidden text-white bg-zinc-900 p-2 rounded-md shadow-lg"
        onClick={() => setMenuAberto(!menuAberto)}
      >
        <i className={`bi ${menuAberto ? 'bi-x-lg' : 'bi-list'} text-[2rem]`}></i>
      </button>

      {/* Sidebar */}
      <header
        className={[
          "fixed md:static top-0 left-0 h-full z-30",
          "w-80 md:w-114",
          "py-10 flex-none flex flex-col text-gray-50",
          "bg-gradient-to-t from-zinc-950 to-zinc-900 shadow-md",
          "transition-transform duration-300 ease-in-out",
          menuAberto ? "translate-x-0" : "-translate-x-full",
          "md:translate-x-0",
        ].join(" ")}
>
        <div
          className="mb-10 flex justify-start items-center gap-5 cursor-pointer"
          onClick={() => { navigate("/"); setMenuAberto(false); }}
        >
          <div className="bg-[#f0f0f0] rounded-r-full w-fit py-5 pl-7 pr-6 justify-items-center">
            <img className="h-15" src="/logo.png" alt="Logo Obsidiana" />
          </div>
          <img className="h-15" src="/MM_white.png" alt="Logo Obsidiana" />
        </div>

        <Navbar />

        <section
          onClick={() => { navigate("/perfil"); setMenuAberto(false); }}
          className="px-6 flex items-center gap-3.5 text-2xl cursor-pointer"
        >
          <Foto icone="bi bi-person" tamanho="5" src={fotoUsuario} />
          <span className="truncate max-w-[12rem]">Olá, {nomeUsuario}</span>
        </section>
      </header>

      {/* Conteúdo principal */}
      <main className="relative w-full min-w-0 pt-20 px-10 pb-10 md:p-20 overflow-y-auto overflow-x-hidden flex flex-col gap-5 shadow-md bg-white/90">
        <Outlet />
      </main>
    </>
  );
}