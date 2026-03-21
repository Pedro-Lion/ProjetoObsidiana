import { useState, useEffect, useRef } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { InputFoto } from "../components/Inputs/InputFoto";

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

  // Carrega dados do sessionStorage ao montar
  useEffect(() => {
    const token = sessionStorage.getItem("token");
    const nomeSalvo = sessionStorage.getItem("perfil_nome");
    const emailSalvo = sessionStorage.getItem("perfil_email");
    const fotoSalva = sessionStorage.getItem("perfil_foto");

    if (token) {
      const payload = decodeToken(token);
      setEmail(emailSalvo || payload.sub || payload.email || "");
    }
    if (nomeSalvo) setNome(nomeSalvo);
    if (fotoSalva) setFotoUrl(fotoSalva);
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

  function salvar() {
    const novaSenha = senhaRef.current;
    const confirmar = confirmarRef.current;

    if (novaSenha && novaSenha !== confirmar) {
      setErro("As senhas não coincidem.");
      return;
    }

    // Persiste nome
    const novoNome = nomeRef.current.trim() || nome;
    sessionStorage.setItem("perfil_nome", novoNome);
    setNome(novoNome);

    // Persiste foto como base64
    if (arquivoFoto) {
      const reader = new FileReader();
      reader.onload = (ev) => {
        const base64 = ev.target.result;
        sessionStorage.setItem("perfil_foto", base64);
        setFotoUrl(base64);
        window.dispatchEvent(new CustomEvent("perfil-atualizado", {
          detail: { nome: novoNome, foto: base64 },
        }));
      };
      reader.readAsDataURL(arquivoFoto);
    } else {
      window.dispatchEvent(new CustomEvent("perfil-atualizado", {
        detail: { nome: novoNome, foto: fotoUrl },
      }));
    }

    setModo("view");
  }

  function removerFoto() {
    sessionStorage.removeItem("perfil_foto");
    setFotoUrl(null);
    setArquivoFoto(null);
    window.dispatchEvent(new CustomEvent("perfil-atualizado", {
      detail: { nome, foto: null },
    }));
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

        <div className="flex flex-col sm:flex-row items-center sm:items-start gap-8
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

            <BotaoPrimario
              titulo="Editar perfil"
              className="w-fit mt-0"
              onClick={abrirEdicao}
            />
          </div>
        </div>
      </div>
    );
  }

  // ── MODO EDIÇÃO ──────────────────────────────────────────────────
  return (
    <div className="flex flex-col gap-8 w-full max-w-2xl">
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

      <div className="bg-violet-50 rounded-2xl p-8 border border-violet-100 flex flex-col gap-8">

        {/* Foto */}
        <section>
          <h2 className="text-lg text-slate-700 font-semibold mb-5">Foto de Perfil</h2>
          <div className="flex flex-col sm:flex-row items-center sm:items-start gap-6">
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
        <section>
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
        </section>

        {/* Ações */}
        <div className="flex flex-col sm:flex-row gap-1 sm:justify-end">
          <BotaoSecundario
            titulo="Cancelar"
            className="w-full sm:w-auto"
            onClick={cancelarEdicao}
          />
          <BotaoPrimario
            titulo="Salvar alterações"
            className="w-full sm:w-auto mt-0"
            onClick={salvar}
          />
        </div>
      </div>
    </div>
  );
}