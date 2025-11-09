import 'bootstrap-icons/font/bootstrap-icons.css';
import { Navbar } from "./components/Navbar/Navbar.jsx";
import { Outlet, useNavigate } from "react-router-dom";
import { Foto } from "./components/Foto.jsx"
import "./App.css";
import { Login } from './routes/Login.jsx';
import { useEffect } from 'react';

export function App() {
  const navigate = useNavigate();

  useEffect(() => {
    if(!sessionStorage.getItem("token")) {
      alert("Faça login para usar a aplicação!")
      navigate("/login");
    }
  }, []) 

  return (
    <>
      <header
        className="w-114 py-10 flex-none flex flex-col text-gray-50 bg-gradient-to-t from-zinc-950 to-zinc-900 shadow-md"
      >
        <div className="mb-10 flex justify-start items-center gap-5">
          <div className="bg-[#f0f0f0] rounded-r-full w-fit py-5 pl-7 pr-6 justify-items-center">
            <img className="h-15" src="/logo.png" alt="Logo Obsidiana" />
          </div>
          <img className="h-15" src="/MM_white.png" alt="Logo Obsidiana" />
        </div>

        <Navbar />

        <section 
          onClick={() => navigate("/perfil")} 
          className="px-6 flex items-center gap-3.5 text-2xl cursor-pointer"
        >
          <Foto icone="bi bi-person" tamanho="5" />
          <span>Olá, Usuário</span>
        </section>
      </header>

      <main className="relative w-full min-w-0 p-20 overflow-y-auto overflow-x-hidden flex flex-col gap-5 shadow-md bg-white/90">
        <Outlet />
      </main>
    </>
  );
}
