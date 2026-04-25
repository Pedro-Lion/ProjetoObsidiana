import { BotaoSecundario } from "../Buttons/BotaoSecundario";

export function ModalFormulario({ titulo, onFechar, children }) {
  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black opacity-40 z-40"
        onClick={onFechar}
      />

      {/* Container da modal */}
      <section
        style={{ transform: "translate(-50%, -50%)" }}
        className="
          fixed top-1/2 left-1/2 z-50
          overflow-y-auto
          w-[90%] max-w-4xl max-h-[90vh]
          p-10 flex flex-col gap-4
          bg-white rounded-2xl shadow-xl border border-gray-300
        "
      >
        {/* Header */}
        <div className="flex justify-between items-center mb-2">
          <h2>{titulo}</h2>
          <BotaoSecundario
            titulo="✕"
            className="m-0 h-fit px-3 py-1"
            onClick={onFechar}
          />
        </div>

        {/* Conteúdo do formulário */}
        {children}
      </section>
    </>
  );
}