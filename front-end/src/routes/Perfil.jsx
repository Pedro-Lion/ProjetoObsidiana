import { useState, useEffect, useRef } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { InputFoto } from "../components/Inputs/InputFoto";
import { useMsal } from "@azure/msal-react";
import { api } from "../api";

// Fallback alinhado com o do api.js — sem isso o fetch ia para o Vite (5173) e voltava index.html
const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

function decodeToken(token) {
  try {
    return JSON.parse(atob(token.split(".")[1]));
  } catch {
    return {};
  }
}

export function Perfil() {
  const [modo, setModo] = useState("view"); // "view" | "edit"
  const [erro, setErro] = useState("");

  // Dados exibidos
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [fotoUrl, setFotoUrl] = useState(null);

  // Refs para os campos de edição (evita conflito value/onInput)
  const nomeRef = useRef("");
  const senhaRef = useRef("");
  const confirmarRef = useRef("");
  const [arquivoFoto, setArquivoFoto] = useState(null);

  // userId vem do claim "userId" do JWT (gerado em TokenService.generateToken)
  const [userId, setUserId] = useState(null);

  // Guarda o objectURL do blob para conseguir revogá-lo ao trocar/sair
  const fotoObjectUrlRef = useRef(null);

  // Carrega a foto do back-end (fonte da verdade); nome continua em sessionStorage por enquanto
  async function carregarFotoDoServidor(idUsuario) {
    if (!idUsuario) return;
    try {
      const token = sessionStorage.getItem("token");
      // cache: no-store + timestamp evita 304 do browser depois de trocar a foto
      const resp = await fetch(`${API_BASE}/usuario/${idUsuario}/imagem?v=${Date.now()}`, {
        method: "GET",
        cache: "no-store",
        headers: { Authorization: token ? "Bearer " + token : "" },
      });
      if (!resp.ok) return;
      const ctype = resp.headers.get("content-type") || "";
      if (!ctype.startsWith("image/")) return;
      const blob = await resp.blob();
      const objectUrl = URL.createObjectURL(blob);
      // Revoga o blob anterior (se houver) antes de trocar
      if (fotoObjectUrlRef.current) {
        try { URL.revokeObjectURL(fotoObjectUrlRef.current); } catch (e) { /* ignore */ }
      }
      fotoObjectUrlRef.current = objectUrl;
      setFotoUrl(objectUrl);
    } catch (e) {
      // Sem foto ou falha de rede: mantém o placeholder
    }
  }

  // Carrega dados iniciais
  useEffect(() => {
    const token = sessionStorage.getItem("token");
    const nomeSalvo = sessionStorage.getItem("perfil_nome");
    const emailSalvo = sessionStorage.getItem("perfil_email");

    if (token) {
      const payload = decodeToken(token);
      // E-mail só fica no cache local enquanto o /me não responde — depois é sobrescrito pela fonte da verdade
      setEmail(emailSalvo || payload.sub || payload.email || "");
    }
    // Mostra o nome do cache imediatamente para evitar flash de "—", mas será sobrescrito pelo /me
    if (nomeSalvo) setNome(nomeSalvo);

    // O JWT emitido pelo auth-microservice só tem o e-mail no subject (sem userId),
    // então buscamos o usuário em /usuario/me para descobrir o id antes de pedir a foto.
    // O /me também é a fonte da verdade para nome e e-mail (persistem após logout/cache clear).
    (async () => {
      try {
        const resp = await api.get("/usuario/me", {
          headers: { Authorization: "Bearer " + token },
        });
        const data = resp?.data || {};
        const id = data.id ?? null;
        setUserId(id);
        // Atualiza nome/e-mail a partir do back (sobrepõe o que veio do sessionStorage)
        if (data.nome) {
          setNome(data.nome);
          sessionStorage.setItem("perfil_nome", data.nome);
        }
        if (data.email) {
          setEmail(data.email);
          sessionStorage.setItem("perfil_email", data.email);
        }
        // Só tenta baixar a imagem se o back disser que ela existe — evita 404 silencioso
        if (data.nomeArquivoImagem) {
          await carregarFotoDoServidor(id);
        }
      } catch (e) {
        // Sem usuário autenticado ou erro de rede: mantém placeholder
      }
    })();

    return () => {
      // Revoga o blob ao desmontar para não vazar memória
      if (fotoObjectUrlRef.current) {
        try { URL.revokeObjectURL(fotoObjectUrlRef.current); } catch (e) { /* ignore */ }
        fotoObjectUrlRef.current = null;
      }
    };
  }, []);

  function abrirEdicao() {
    nomeRef.current = nome;
    senhaRef.current = "";
    confirmarRef.current = "";
    setArquivoFoto(null);
    setErro("");
    setModo("edit");
  }

  function cancelarEdicao() {
    setErro("");
    setModo("view");
  }

  function handleFotoChange(e) {
    setArquivoFoto(e.target.files?.[0] ?? null);
  }

  async function salvar() {
    const novaSenha = senhaRef.current;
    const confirmar = confirmarRef.current;

    if (novaSenha && novaSenha !== confirmar) {
      setErro("As senhas não coincidem.");
      return;
    }

    const novoNome = nomeRef.current.trim() || nome;

    // Persiste nome e (opcionalmente) senha no back via PUT /api/usuario/{id}
    // Antes esses dados ficavam apenas em sessionStorage e sumiam após logout/clearCache —
    // agora a fonte da verdade é o banco; o sessionStorage vira só cache de exibição imediata.
    if (userId) {
      try {
        const payload = { nome: novoNome };
        if (novaSenha) payload.senha = novaSenha;
        await api.put(`/usuario/${userId}`, payload, {
          headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
        });
      } catch (e) {
        setErro("Não foi possível salvar os dados. Tente novamente.");
        return;
      }
    }

    sessionStorage.setItem("perfil_nome", novoNome);
    setNome(novoNome);

    // Persiste foto no back-end via POST /api/usuario/{id}/imagem (mesmo padrão de equipamento/profissional)
    if (arquivoFoto && userId) {
      try {
        const formData = new FormData();
        formData.append("arquivo", arquivoFoto);
        await api.post(`/usuario/${userId}/imagem`, formData, {
          headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
        });
        // Recarrega a foto a partir do back para refletir o que foi efetivamente salvo
        await carregarFotoDoServidor(userId);
      } catch (e) {
        setErro("Não foi possível salvar a foto. Tente novamente.");
        return;
      }
    }

    // Notifica outros componentes (ex.: App.jsx atualiza o avatar da sidebar)
    window.dispatchEvent(new CustomEvent("perfil-atualizado", {
      detail: { nome: novoNome },
    }));

    setModo("view");
  }

  async function removerFoto() {
    // Apaga a foto no back via DELETE /api/usuario/{id}/imagem e limpa a exibição local.
    // Antes a remoção era só visual e a foto voltava no próximo fetch (porque o back ainda guardava nomeArquivoImagem).
    if (userId) {
      try {
        await api.delete(`/usuario/${userId}/imagem`, {
          headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
        });
      } catch (e) {
        setErro("Não foi possível remover a foto. Tente novamente.");
        return;
      }
    }

    sessionStorage.removeItem("perfil_foto");
    if (fotoObjectUrlRef.current) {
      try { URL.revokeObjectURL(fotoObjectUrlRef.current); } catch (e) { /* ignore */ }
      fotoObjectUrlRef.current = null;
    }
    setFotoUrl(null);
    setArquivoFoto(null);
    // foto: null sinaliza para App.jsx limpar o avatar sem refazer fetch
    window.dispatchEvent(new CustomEvent("perfil-atualizado", {
      detail: { nome, foto: null },
    }));
  }

  const { instance } = useMsal();

//   const handleLogout = () => {
//   instance.logoutRedirect(); // ou logoutPopup()
//  };

   function LogOut() {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("perfil_nome");
    sessionStorage.removeItem("perfil_email");
    sessionStorage.removeItem("perfil_foto");
    instance.clearCache();
    
    // setFotoUrl(null);
    // setArquivoFoto(null);
    window.location.href = "/login";
    // window.dispatchEvent(new CustomEvent("perfil-atualizado", {
    //   detail: { nome, foto: null },
    // }));
  }

  // ── MODO EXIBIÇÃO ────────────────────────────────────────────────
  if (modo === "view") {
    return (
      <div className="flex flex-col gap-8 w-full max-w-2xl">
        <h1 className="text-3xl text-slate-950 font-bold">Meu Perfil</h1>

        {erro && (
          <p className="bg-red-50 border border-red-200 text-red-600 rounded-lg px-4 py-3 text-sm">
            {erro}
          </p>
        )}

        <div className="flex flex-col md:flex-row items-center md:items-start gap-8
                        bg-violet-50 rounded-2xl p-8 border border-violet-100">
          <div className="flex-none">
            <InputFoto
              tamanho="20"
              initialPreview={fotoUrl}
              dstv={true}
              icone="bi bi-person"
            />
          </div>

          <div className="flex flex-col gap-5 flex-1 min-w-0 justify-center">
            <div>
              <span className="text-xs font-semibold text-violet-500 uppercase tracking-widest">
                Nome
              </span>
              <p className="text-xl text-slate-800 font-semibold mt-1 truncate">
                {nome || "—"}
              </p>
            </div>

            <div>
              <span className="text-xs font-semibold text-violet-500 uppercase tracking-widest">
                E-mail
              </span>
              <p className="text-lg text-slate-600 mt-1 truncate">{email || "—"}</p>
            </div>

            <div>
              <span className="text-xs font-semibold text-violet-500 uppercase tracking-widest">
                Senha
              </span>
              <p className="text-lg text-slate-500 mt-1 tracking-widest">••••••••••</p>
            </div>

            <div className="flex gap-2">
            <BotaoPrimario
              titulo="Editar perfil"
              className="w-fit mt-0"
              onClick={abrirEdicao}
            />
            <BotaoSecundario
              titulo="Logout"
              className="w-fit mt-0"
              onClick={LogOut}
            /></div>
          </div>
        </div>
      </div>
    );
  }

  // ── MODO EDIÇÃO ──────────────────────────────────────────────────
  return (
    <div className="flex flex-col gap-8 w-full max-w-4xl">
      <div className="flex items-center gap-3">
        <button
          onClick={cancelarEdicao}
          className="text-violet-500 hover:text-violet-700 transition-colors"
        >
          <i className="bi bi-arrow-left text-2xl"></i>
        </button>
        <h1 className="text-3xl text-slate-950 font-bold">Editar Perfil</h1>
      </div>

      {erro && (
        <p className="bg-red-50 border border-red-200 text-red-600 rounded-lg px-4 py-3 text-sm">
          {erro}
        </p>
      )}

      <div className="bg-violet-50 rounded-2xl p-6 border border-violet-100 flex flex-col gap-8">

        {/* Foto */}
        <section>
          <h2 className="text-lg text-slate-700 font-semibold mb-5">Foto de Perfil</h2>
          <div className="flex flex-col md:flex-row items-center gap-6">
            <InputFoto
              tamanho="16"
              initialPreview={fotoUrl}
              onChange={handleFotoChange}
              icone="bi bi-person"
            />
            <div className="flex flex-col gap-3 justify-center text-sm text-slate-500">
              <p>
                Clique na foto para escolher uma nova imagem.<br />
                Formatos aceitos: JPG, PNG.
              </p>
              {fotoUrl && (
                <button
                  onClick={removerFoto}
                  className="text-red-500 hover:text-red-700 underline transition-colors w-fit text-left"
                >
                  Remover foto atual
                </button>
              )}
            </div>
          </div>
        </section>

        <hr className="border-violet-200" />

        {/* Dados */}
        <section className="bg-white rounded-2xl p-5 border-b border-r border-violet-200">
          <h2 className="text-lg text-slate-700 font-semibold mb-5">Dados do Usuário</h2>
          <div className="flex flex-col gap-5">

            <InputBordaLabel
              titulo="Nome"
              placeholder="Seu nome completo"
              defaultValue={nome}
              onInput={(e) => { nomeRef.current = e.target.value; }}
              className="w-full"
            />

            {/* E-mail somente leitura */}
            <div className="flex flex-col gap-1">
              <label className="text-sm font-semibold text-slate-600">E-mail</label>
              <input
                type="email"
                value={email}
                readOnly
                className="w-full px-3 py-3 rounded-lg border border-slate-200
                           bg-slate-100 text-slate-400 cursor-not-allowed
                           text-[1.1rem] outline-none"
              />
              <span className="text-xs text-slate-400">O e-mail não pode ser alterado.</span>
            </div>

            <InputBordaLabel
              titulo="Nova Senha"
              type="password"
              placeholder="Deixe em branco para não alterar"
              onInput={(e) => { senhaRef.current = e.target.value; }}
              className="w-full"
            />

            <InputBordaLabel
              titulo="Confirmar Nova Senha"
              type="password"
              placeholder="Repita a nova senha"
              onInput={(e) => { confirmarRef.current = e.target.value; }}
              className="w-full"
            />
          </div>

          {/* Ações */}
          <div className="flex flex-col md:flex-row gap-0 md:gap-2 md:justify-end md:align-baseline mt-8">
            <BotaoSecundario
              titulo="Cancelar"
              className="w-full md:w-auto h-fit mb-0"
              onClick={cancelarEdicao}
            />
            <BotaoPrimario
              titulo="Salvar alterações"
              className="w-full md:w-auto h-fit"
              onClick={salvar}
            />
          </div>
        </section>
      </div>
    </div>
  );
}