import { InputFoto } from "../components/Inputs/InputFoto";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { api } from "../api";
import { Modal } from "../components/Modal/Modal.jsx";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useState, useEffect, useRef } from "react";

export function CadastroProfissionais({ onSucesso, onCancelar }) {
  const navigate = useNavigate();
  const { id } = useParams();
  const state = useLocation().state;
  const [profissional, setProfissional] = useState(state ?? {});
  const [arquivoFoto, setArquivoFoto] = useState(null);
  const [previewFoto, setPreviewFoto] = useState(null);
  const previewRef = useRef(null);
  const API_BASE = import.meta.env.VITE_API_BASE_URL || "";

  // Erros de validação
  const [erros, setErros] = useState({});

  // Carrega preview de foto existente no modo edição
  useEffect(() => {
    async function carregarPreview() {
      if (!id) return;
      try {
        const token = sessionStorage.getItem("token");
        const url = `${API_BASE}/profissional/${id}/imagem`;
        const resp = await fetch(url, {
          method: "GET",
          headers: { Authorization: token ? "Bearer " + token : "" },
        });
        if (!resp.ok) return;
        const ctype = resp.headers.get("content-type") || "";
        if (!ctype.startsWith("image/")) return;
        const blob = await resp.blob();
        const objectUrl = URL.createObjectURL(blob);
        if (previewRef.current) {
          try { URL.revokeObjectURL(previewRef.current); } catch (e) { /* ignore */ }
        }
        previewRef.current = objectUrl;
        setPreviewFoto(objectUrl);
      } catch (err) {
        console.log("Sem imagem existente:", err);
      }
    }
    carregarPreview();

    return () => {
      if (previewRef.current) {
        try { URL.revokeObjectURL(previewRef.current); } catch (e) { /* ignore */ }
        previewRef.current = null;
      }
    };
  }, [id]);

  function irParaLista() {
    if (onSucesso) {
      onSucesso();
    } else {
      navigate("/profissionais");
    }
  }

  function cancelar() {
    if (onCancelar) {
      onCancelar();
    } else {
      navigate("/profissionais");
    }
  }

  function handleFotoChange(e) {
    const file = e.target.files?.[0] ?? null;
    setArquivoFoto(file);
  }

  function validar() {
    const novosErros = {};
    if (!profissional.nome || profissional.nome.trim() === "") {
      novosErros.nome = "Nome é obrigatório.";
    }
    // Contato é obrigatório para garantir comunicação com o profissional
    if (!profissional.contato || profissional.contato.trim() === "") {
      novosErros.contato = "Contato é obrigatório.";
    }
    setErros(novosErros);
    return Object.keys(novosErros).length === 0;
  }

  async function uploadFoto(profissionalId) {
    if (!arquivoFoto) return;
    const formData = new FormData();
    formData.append("arquivo", arquivoFoto);
    try {
      await api.post(`/profissional/${profissionalId}/imagem`, formData, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
    } catch (err) {
      console.error("Erro ao enviar foto:", err);
    }
  }

  function cadastrar() {
    if (!validar()) return;

    notificar(
      api.post("/profissional", profissional, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      }),
      async (req) => {
        if (req.status == 201) {
          const criado = req.data;
          if (arquivoFoto && criado?.id) {
            await uploadFoto(criado.id);
          }
          irParaLista();
        }
      },
      {
        pending: "Cadastrando profissional...",
        success: [
          "Profissional cadastrado com sucesso!",
          "Retornando à página de profissionais"
        ],
        error: "Ocorreu um erro ao cadastrar o profissional"
      }
    )
  }

  function editar() {
    if (!validar()) return;

    notificar(
      api.put(`/profissional/${id}`, profissional, {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      }),
      async (req) => {
        if (req.status == 200) {
          if (arquivoFoto && id) {
            await uploadFoto(id);
          }
          irParaLista();
        }
      },
      {
        pending: "Salvando alterações...",
        success: [
          "Alterações do profissinal salvas com sucesso!",
          "Retornando à página de profissionais"
        ],
        error: "Ocorreu um erro durante o registro das alterações"
      }
    )
  }

  const ErroMsg = ({ campo }) =>
    erros[campo] ? (
      <span className="text-red-500 text-[1rem] mt-1">{erros[campo]}</span>
    ) : null;

  return (
    <>
      {!onCancelar && (
        <h1 className="mb-16">{!state ? "Cadastrar" : "Editar"} profissional</h1>
      )}

      <section>
        {/* InputFoto agora com onChange para capturar o arquivo */}
        <InputFoto onChange={handleFotoChange} initialPreview={previewFoto} />

        <div className="mt-5 flex flex-col gap-6">
          <div className="flex flex-col">
            <InputBordaLabel
              titulo="Nome"
              placeholder="Ex: Fulano de Tal"
              onInput={(e) => setProfissional({ ...profissional, nome: e.target.value })}
              value={profissional.nome ?? ""}
            />
            <ErroMsg campo="nome" />
          </div>

          <InputBordaLabel
            titulo="Disponibilidade"
            placeholder="Ex: Das terças às quintas às 14h"
            value={profissional.disponibilidade ?? ""}
            onInput={(e) => setProfissional({ ...profissional, disponibilidade: e.target.value })}
          />
          {/* Contato agora é obrigatório — envolvido em div para exibir a mensagem de erro */}
          <div className="flex flex-col">
            <InputBordaLabel
              titulo="Contato"
              placeholder="Ex: (11) 91234-1234 ou fulano@email.com"
              onInput={(e) => setProfissional({ ...profissional, contato: e.target.value })}
              value={profissional.contato ?? ""}
            />
            <ErroMsg campo="contato" />
          </div>
          <InputBordaLabel
            titulo="Categoria"
            placeholder="Ex: Fotógrafo, Videógrafo, Editor..."
            value={profissional.categoria ?? ""}
            onInput={(e) => setProfissional({ ...profissional, categoria: e.target.value })}
          />
        </div>

        <div className="flex gap-3 mt-10">
          {!state ? (
            <BotaoPrimario titulo="Cadastrar" className="mb-0 mt-0" onClick={cadastrar} />
          ) : (
            <BotaoPrimario titulo="Salvar alterações" className="mb-0 mt-0" onClick={editar} />
          )}
          <BotaoSecundario titulo="Cancelar" className="mb-0 mt-0" onClick={cancelar} />
        </div>
      </section>
    </>
  );
}