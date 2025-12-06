import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { api } from "../api";
import { CardOrcamento } from "../components/Cards/CardOrcamento";
import { Modal } from "../components/Modal/Modal.jsx";

export function Orcamentos() {
  const navigate = useNavigate();
  const [orcamentos, setOrcamentos] = useState([]);

  // Estados do modal
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitulo, setModalTitulo] = useState("");
  const [modalDescricao, setModalDescricao] = useState("");
  const [modalActions, setModalActions] = useState(null);

  useEffect(() => {
    async function getOrcamentos() {
      try {
        const request = await api.get("/orcamento", {
          headers: {
            Authorization: "Bearer " + sessionStorage.getItem("token"),
          },
        });
        if (request.status === 200) {
          setOrcamentos(request.data);
        }
      } catch (err) {
        console.error(err);
      }
    }
    getOrcamentos();
  }, []);

  const deletar = (id) => {
    setModalTitulo("Confirmar exclusão");
    setModalDescricao("Tem certeza que deseja excluir este orçamento?");
    setModalActions(
      <>
        <button
          className="bg-red-500 text-white px-4 py-2 rounded mr-3"
          onClick={async () => {
            try {
              const resposta = await api.delete(`/orcamento/${id}`, {
                headers: {
                  Authorization: "Bearer " + sessionStorage.getItem("token"),
                },
              });
              if (resposta.status === 200 || resposta.status === 204) {
                setOrcamentos((prev) => prev.filter((o) => o.id !== id));
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
        <h1>Orçamentos</h1>
        <BotaoPrimario
          titulo="+ Novo orçamento"
          onClick={() => navigate("/cadastro/orcamento")}
        />
      </div>

      <section className="flex flex-wrap gap-5">
        {orcamentos.length !== 0 ? (
          orcamentos.map((o) => (
            <CardOrcamento
              key={o.id}
              dados={o}
              onClickDel={() => deletar(o.id)}
            />
          ))
        ) : (
          <p className="text-xl">Nenhum orçamento cadastrado.</p>
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
