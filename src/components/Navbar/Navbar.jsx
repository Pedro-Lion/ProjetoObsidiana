import { navGrupos } from "./navGrupos.jsx";

export function Navbar() {
  return (
    <nav className="p-3 h-full overflow-auto">
      <ul className="flex flex-col gap-4">{navGrupos}</ul>
    </nav>
  );
}
