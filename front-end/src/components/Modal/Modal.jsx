export function Modal({ titulo, descricao, children }) {
  return (
    <>
      <div className="absolute w-full h-full -m-20 bg-black opacity-10"></div>

      <section
        style={{
          transform: "translate(-50%, -50%)",
        }}
        className="
          absolute top-1/2 left-1/2 overflow-y-scroll
          border-box w-[80%] max-w-220 h-fit min-h-75 max-h-110 p-10
          flex flex-col justify-between
          bg-white rounded-2xl shadow-xl border border-gray-300
        "
      >
        <div>
          <h2>{titulo}</h2>
          <p className="text-2xl mt-4">{descricao}</p>
        </div>

        <div className="flex items-center">
          {children}
        </div>
      </section>
    </>
  );
}
