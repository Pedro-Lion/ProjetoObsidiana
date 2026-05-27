import { useEffect, useState } from "react";
import { NotificacaoSpinner } from "./NotificacaoSpinner";

export function Notificacao({ funcaoReq = async () => {}, fecharSucessoApos, textoPending = "Processado...", textoSucesso = "Sucesso!", textoErro = "Ocorreu um erro", closeToast }) {
  const [req, setReq] = useState(null);

  useEffect(() => {
    async function fazerReq() {
      const req = await funcaoReq();
      setReq(req);
    }
    if (!req) {
      fazerReq();
      return
    } 

    if (fecharSucessoApos && req.status <= 299) {
     setTimeout(closeToast, fecharSucessoApos * 1000)
    }
  }, [req]);

  function tratarReq() {
    if (!req) {
      return (
        <div className="flex items-center gap-5">
          <NotificacaoSpinner />
          <h1>{textoPending}</h1>
        </div>
      );
    }

    if (req.status <= 299) return (<h1>{textoSucesso}</h1>);

    const corpoErro = req.data;
    const listaEquips = [];

    if (corpoErro.equipamentosEmConflito) {
      listaEquips.push(
        corpoErro.equipamentosEmConflito.map((e, i) => (
          <li key={"equip_conflito" + i} className="list-item">
            {e}
          </li>
        )),
      );
    }

    return (
      <div className="text-xl">
        <h1>{textoErro}</h1>
        <p className="mt-3">{corpoErro.message}</p>
        {listaEquips.length > 0 && (
          <>
            <p className="mt-3 mb-1 font-semibold">Equipamentos em conflito:</p>
            <ul className="list-disc ml-6">{listaEquips}</ul>
          </>
        )}
      </div>
    );
  }

  return tratarReq();
}
