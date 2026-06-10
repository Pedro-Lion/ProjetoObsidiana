import 'bootstrap-icons/font/bootstrap-icons.css';
import { Navbar } from "./components/Navbar/Navbar.jsx";
import { Outlet, useNavigate } from "react-router-dom";
import { Foto } from "./components/Foto.jsx";
import "./App.css";
import { useEffect, useState, useCallback, useRef } from 'react';
import { ToastContainer } from 'react-toastify';
import { api } from "./api";

// Fallback alinhado com o do api.js — sem isso o fetch ia para o Vite (5173) e voltava index.html
// (usado só na URL do binário da imagem; o /usuario/me vai pelo axios `api`)
const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export function App() {
  const navigate = useNavigate();
    const [menuAberto, setMenuAberto] = useState(false);
  const [nomeUsuario, setNomeUsuario] = useState("Usuário");
  const [fotoUsuario, setFotoUsuario] = useState(null);

  // Guarda o objectURL do blob da foto para conseguir revogá-lo ao trocar
  const fotoObjectUrlRef = useRef(null);

  // Baixa o binário da imagem e cria um objectURL pra usar no <img src>.
  // Recebe o userId pronto — o caller decide se vale a pena chamar isso (usando nomeArquivoImagem do /me).
  const carregarFotoDoServidor = useCallback(async (userId) => {
    const token = sessionStorage.getItem("token");
    if (!token || !userId) return;
    try {
      // cache: no-store + timestamp evita 304 do browser depois de trocar a foto
      const resp = await fetch(`${API_BASE}/usuario/${userId}/imagem?v=${Date.now()}`, {
        method: "GET",
        cache: "no-store",
        headers: { Authorization: "Bearer " + token },
      });
      if (!resp.ok) return;
      const ctype = resp.headers.get("content-type") || "";
      // Aceita qualquer content-type que pareça imagem; se vier octet-stream (tipoImagem nulo no back),
      // ainda assim deixamos o <img> tentar renderizar — antes ficávamos sem foto por causa desse filtro.
      if (!ctype.startsWith("image/") && ctype !== "application/octet-stream") return;
      const blob = await resp.blob();
      const objectUrl = URL.createObjectURL(blob);
      // Revoga o blob anterior antes de trocar
      if (fotoObjectUrlRef.current) {
        try { URL.revokeObjectURL(fotoObjectUrlRef.current); } catch (e) { /* ignore */ }
      }
      fotoObjectUrlRef.current = objectUrl;
      setFotoUsuario(objectUrl);
    } catch (e) {
      // Sem foto ou erro de rede: mantém o placeholder
    }
  }, []);

  // Carrega nome+foto a partir do /usuario/me (fonte da verdade).
  // Antes o nome vinha só do sessionStorage e a foto era buscada sempre — agora consultamos o /me primeiro
  // e só pedimos a imagem se o back disser que existe (nomeArquivoImagem != null), evitando 404 silencioso.
  const carregarPerfil = useCallback(async () => {
    const token = sessionStorage.getItem("token");
    if (!token) return;
    // Cache local pra evitar flash de "Usuário" antes do /me responder
    const nomeSalvo = sessionStorage.getItem("perfil_nome");
    if (nomeSalvo) setNomeUsuario(nomeSalvo);
    try {
      const meResp = await api.get("/usuario/me", {
        headers: { Authorization: "Bearer " + token },
      });
      const data = meResp?.data || {};
      if (data.nome) {
        setNomeUsuario(data.nome);
        sessionStorage.setItem("perfil_nome", data.nome);
      } else {
        setNomeUsuario("Usuário");
      }
      if (data.id && data.nomeArquivoImagem) {
        await carregarFotoDoServidor(data.id);
      }
    } catch (e) {
      // Sem usuário autenticado ou erro de rede: mantém placeholder
    }
  }, [carregarFotoDoServidor]);

  useEffect(() => {
    if (!sessionStorage.getItem("token")) {
      alert("Faça login para usar a aplicação!");
      navigate("/login");
      return;
    }

    carregarPerfil();

    const onPerfilAtualizado = (e) => {
      if (e.detail?.nome) setNomeUsuario(e.detail.nome);
      // Refaz o fetch a partir do back — não depende mais de payload no evento,
      // porque a fonte da verdade da foto agora é o servidor.
      if (e.detail?.foto === null) {
        // remoção local: limpa imediatamente o avatar (back ainda não tem DELETE de imagem)
        if (fotoObjectUrlRef.current) {
          try { URL.revokeObjectURL(fotoObjectUrlRef.current); } catch (err) { /* ignore */ }
          fotoObjectUrlRef.current = null;
        }
        setFotoUsuario(null);
      } else {
        // Recarrega perfil inteiro (nome + foto) a partir do /me — assinatura nova de carregarFotoDoServidor exige userId,
        // então chamamos o carregarPerfil que já resolve isso.
        carregarPerfil();
      }
    };
    window.addEventListener("perfil-atualizado", onPerfilAtualizado);
    return () => {
      window.removeEventListener("perfil-atualizado", onPerfilAtualizado);
      if (fotoObjectUrlRef.current) {
        try { URL.revokeObjectURL(fotoObjectUrlRef.current); } catch (err) { /* ignore */ }
        fotoObjectUrlRef.current = null;
      }
    };
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
          {/* pr-4 em vez de pr-6 reduz o espaço interno direito da pílula, dando mais espaço para o texto "OBSIDIANA" ao lado */}
          <div className="bg-[#f0f0f0] rounded-r-full w-fit py-5 pl-7 pr-4 justify-items-center">
            <img className="h-15" src="/logo.png" alt="Logo Obsidiana" />
          </div>
          <img className="h-15" src="/MM_white.png" alt="Logo Obsidiana" />
        </div>

        <Navbar fecharMenu={() => setMenuAberto(false)} />

        <section
          onClick={() => { navigate("/perfil"); setMenuAberto(false); }}
          className="px-6 flex items-center gap-3.5 text-2xl cursor-pointer"
        >
          <Foto icone="bi bi-person" tamanho="5" src={fotoUsuario} />
          <span className="truncate max-w-[12rem]">Olá, {nomeUsuario}</span>
        </section>
      </header>

      {/* Conteúdo principal */}
      <main className="relative w-full min-w-0 pt-25 px-10 pb-10 md:p-20 overflow-y-auto overflow-x-hidden flex flex-col gap-5 shadow-md bg-white/90">
        <Outlet />
      </main>
      <ToastContainer 
        toastStyle={{
          boxShadow: "0 0 0.5rem rgba(0, 0, 0, 0.3)",
          width: "100%",
          maxWidth: "36rem"
        }}
      />
    </>
  );
}