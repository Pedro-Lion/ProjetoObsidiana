import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { useNavigate } from "react-router-dom";
import { api } from "../api";
import { CardServico } from "../components/Cards/CardServico";
import { Modal } from "../components/Modal/Modal.jsx";

export function Servicos() {
  const navigate = useNavigate();
  const [servicos, setServicos] = useState([]);

  // Estados do modal
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  useEffect(() => {
    async function getServicos() {
      try {
        const request = await api.get("/servico", {
          headers: {
            Authorization: "Bearer " + sessionStorage.getItem("token"),
          },
        });
        if (request.status === 200) {
          setServicos(request.data);
        }
      } catch (err) {
        console.error(err);
      }
    }
    getServicos();
  }, []);

  const deletar = (id) => {
    setModalTitulo("Confirmar exclusão");
    setModalDescricao("Tem certeza que deseja excluir este serviço?");
    setModalActions(
      <>
        <button
          className="bg-red-500 text-white px-4 py-2 rounded mr-3"
          onClick={async () => {
            try {
              const resposta = await api.delete(`/servico/${id}`, {
                headers: {
                  Authorization: "Bearer " + sessionStorage.getItem("token"),
                },
              });
              if (resposta.status === 200 || resposta.status === 204) {
                setServicos((prev) => prev.filter((s) => s.id !== id));
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
        <h1>Serviços</h1>
        <BotaoPrimario
          titulo="+ Novo serviço"
          onClick={() => navigate("/cadastro/servicos")}
        />
      </div>

      <section className="flex flex-wrap gap-5">
        {servicos.length !== 0 ? (
          servicos.map((s) => (
            <CardServico
              key={s.id}
              dados={s}
              onClickDel={() => deletar(s.id)}
            />
          ))
        ) : (
          <p className="text-xl">Nenhum serviço cadastrado.</p>
        )}
      </section>

      {modalOpen && (
        <Modal titulo={modalTitulo} descricao={modalDescricao}>
          {modalActions}
        </Modal>
      )}
    </>
  );
}
