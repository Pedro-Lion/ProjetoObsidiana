import { BotaoBordaGradiente } from "./components/Buttons/BotaoBordaGradiente.jsx";
import { Navbar } from "./components/Navbar/Navbar.jsx";
import { Outlet } from "react-router-dom";
import './App.css';

export function App() {
  return (
    <div>
      <header
        style={{ height: "100vh" }}
        className="w-114 py-6 fixed -translate-y-1/2 top-1/2 left-15 text-gray-50
        bg-gradient-to-t from-zinc-950 to-zinc-900 shadow-md"
      >
        <div className="flex
        justify-start
        items-center
        gap-5
        mb-10
        ">
          <div className="
          bg-[#f0f0f0]
          rounded-r-full
          w-fit
          py-5
          pl-7
          pr-6
          justify-items-center
          ">
            <img className="h-15" src="/logo.png" alt="Logo Obsidiana" />
          </div>
            <img className="h-15" src="/MM_white.png" alt="Logo Obsidiana" />
        </div>

        <Navbar />
      </header>

      <main className="
      flex flex-col gap-5
      shadow-md
      ml-123
      w-[75vw]
      h-screen
      right-15
    overflow-y-auto
    p-20
    bg-white/90">
        <Outlet />
      </main>
    </div>
  );
}
