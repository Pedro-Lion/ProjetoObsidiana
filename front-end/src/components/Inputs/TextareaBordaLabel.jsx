export function TextareaBordaLabel({titulo, placeholder = "Digite aqui", larguraCampo = "w-80", rows="5", onInput}) {
  return (
    <div className={"flex flex-col overflow-x-auto " + larguraCampo}>
      <label
        className="relative top-3 ml-[0.7rem] px-[0.3rem]
        text-indigo-500 font-medium text-[1.1rem]
        bg-white w-fit"
      >
        {titulo}
      </label>
      <textarea
        placeholder={placeholder}
        rows={rows}
        className="border-indigo-500 text-slate-700
        px-3 py-3 text-[1.1rem] bg-transparent border-1 rounded-lg        
        focus:outline-none resize-none placeholder:text-black/25"
        onInput={onInput}
      />
    </div>
  );
}
