export function Modal({ titulo, descricao, children }) {
  return (
    <>
      {/* Overlay fixo à viewport, cobre a tela inteira independente do scroll */}
      <div className="fixed inset-0 bg-black opacity-30 z-40"></div>

      {/* Modal fixo à viewport, sempre centralizado na tela visível */}
      <section
        style={{
          transform: "translate(-50%, -50%)",
        }}
        className="
          fixed top-1/2 left-1/2 overflow-y-auto z-50
          border-box w-[80%] max-w-220 h-fit min-h-75 p-10
          flex flex-col justify-between
          bg-white rounded-2xl shadow-xl border border-gray-300
        "
      >
        <div>
          <h2>{titulo}</h2>
          <p className="my-5 text-2xl">{descricao}</p>
        </div>

        <div className="flex items-center">
          {children}
        </div>
      </section>
    </>
  );
}
