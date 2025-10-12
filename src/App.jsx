import { BotaoBordaGradiente } from "./components/Buttons/BotaoBordaGradiente.jsx";
import { Navbar } from "./components/Navbar/Navbar.jsx";
import { Outlet } from "react-router-dom";

export function App() {
  return (
    <>
      <header
        style={{ height: "90vh" }}
        className="w-114 p-6 fixed -translate-y-1/2 top-1/2 left-4 rounded-[3.2rem] bg-gray-800 text-gray-50"
      >
        <img className="h-10" src="/logo.png" alt="Logo Obsidiana" />

        <Navbar />
      </header>

      <main className="pl-123">
        <Outlet />
      </main>
    </>
  );
}
