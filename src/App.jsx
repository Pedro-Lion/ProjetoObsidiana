import 'bootstrap-icons/font/bootstrap-icons.css';
import { Navbar } from "./components/Navbar/Navbar.jsx";
import { Outlet } from "react-router-dom";
import "./App.css";

export function App() {
  return (
    <>
      <header
        className="w-114 py-6 flex-none text-gray-50 bg-gradient-to-t from-zinc-950 to-zinc-900 shadow-md"
      >
        <div className="mb-10 flex justify-start items-center gap-5">
          <div className="bg-[#f0f0f0] rounded-r-full w-fit py-5 pl-7 pr-6 justify-items-center">
            <img className="h-15" src="/logo.png" alt="Logo Obsidiana" />
          </div>
          <img className="h-15" src="/MM_white.png" alt="Logo Obsidiana" />
        </div>

        <Navbar />
      </header>

      <main className="w-full min-w-0 p-10 overflow-y-auto overflow-x-hidden flex flex-col gap-5 shadow-md bg-white/90">
        <Outlet />
      </main>
    </>
  );
}
