import { toast } from "react-toastify";

export function notificar(
  promise = new Promise(),
  funcaoThen = () => {},
  textos = {
    pending: "Processando...",
    success: ["Sucesso!", "Retornando à página principal"],
    error: "Ocorreu um erro",
  },
) {
  toast
    .promise(promise, {
      pending: textos.pending,
      success: {
        autoClose: 5000,
        render() {
          return (
            <div>
              <h1>{textos.success[0]}</h1>
              <p className="text-xl">{textos.success[1]}</p>
            </div>
          );
        },
      },
      error: {
        autoClose: false,
        className: "text-xl",
        render(d) {
          const corpoErro = d.data.response.data;

          return (
            <div className="text-xl">
              <h1>{textos.error}</h1>
              <p className="mt-3">{corpoErro.message ?? corpoErro}</p>
              {corpoErro.equipamentosEmConflito?.length > 0 && (
                <>
                  <p className="mt-3 mb-1 font-semibold">
                    Equipamentos em conflito:
                  </p>
                  <ul className="list-disc ml-6">
                    {corpoErro.equipamentosEmConflito.map((e, i) => (
                      <li key={"equip_conflito" + i} className="list-item">
                        {e}
                      </li>
                    ))}
                  </ul>
                </>
              )}
            </div>
          );
        },
      },
    })
    .then((req) => {
      funcaoThen(req);
    });
}
