import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api";
import { ContainerProfissional } from "../components/Containers/ContainerProfissional";
import { Modal } from "../components/Modal/Modal.jsx";
import { ModalFormulario } from "../components/Modal/ModalFormulario.jsx";
import { CadastroProfissionais } from "./CadastroProfissionais.jsx";

export function Profissionais() {
  const navigate = useNavigate();
  const [profissionais, setProfissionais] = useState([]);

  // Modal de confirmação/exclusão
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  // Modal de cadastro
  const [modalCadastroAberta, setModalCadastroAberta] = useState(false);

  /* ── Carregamento da lista ── */
  async function getProfissionais() {
    try {
      const request = await api.get("/profissional", {
        headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
      });
      if (request.status === 200) {
        setProfissionais(request.data);
      }
    } catch (err) {
      console.error(err);
    }
  }

  useEffect(() => {
    getProfissionais();
  }, []);

  /* ── Deletar ── */
  const deletar = (id) => {
    setModalTitulo("Confirmar exclusão");
    setModalDescricao("Tem certeza que deseja excluir este profissional?");
    setModalActions(
      <>
        <button
          className="bg-red-500 text-white px-4 py-2 rounded mr-3"
          onClick={async () => {
            try {
              const resposta = await api.delete(`/profissional/${id}`, {
                headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
              });
              if (resposta.status === 200 || resposta.status === 204) {
                setProfissionais((prev) => prev.filter((p) => p.id !== id));
              }
              setModalOpen(false);
            } catch (err) {
              console.error(err);
              setModalTitulo("Erro");
              setModalDescricao("Não foi possível excluir. Tente novamente.");
              setModalActions(
                <button
                  className="bg-gray-300 px-4 py-2 rounded"
                  onClick={() => setModalOpen(false)}
                >
                  Fechar
                </button>
              );
              setModalOpen(true);
            }
          }}
        >
          Excluir
        </button>
        <button
          className="bg-gray-300 px-4 py-2 rounded"
          onClick={() => setModalOpen(false)}
        >
          Cancelar
        </button>
      </>
    );
    setModalOpen(true);
  };

  return (
    <>
      <div className="flex justify-between">
        <h1>Profissionais</h1>
        <BotaoPrimario
          titulo="+ Novo profissional"
          onClick={() => setModalCadastroAberta(true)}
        />
      </div>

      <section>
        {profissionais.length !== 0 ? (
          profissionais.map((p) => (
            <ContainerProfissional
              key={p.id}
              dados={p}
              onClickDel={() => deletar(p.id)}
              onClickEdit={() => navigate(`/editar/profissional/${p.id}`, { state: p })}
            />
          ))
        ) : (
          <p className="text-xl">Nenhum profissional cadastrado.</p>
        )}
      </section>

      {/* Modal de confirmação/exclusão */}
      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}

      {/* Modal de cadastro de profissional */}
      {modalCadastroAberta && (
        <ModalFormulario
          titulo="Novo Profissional"
          onFechar={() => setModalCadastroAberta(false)}
        >
          <CadastroProfissionais
            onSucesso={() => {
              setModalCadastroAberta(false);
              getProfissionais();
            }}
            onCancelar={() => setModalCadastroAberta(false)}
          />
        </ModalFormulario>
      )}
    </>
  );
}