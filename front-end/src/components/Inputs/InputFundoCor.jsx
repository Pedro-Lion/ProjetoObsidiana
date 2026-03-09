export function InputFundoCor({titulo, type = "text", placeholder = "Digite aqui", className = "w-80", onInput}) {
  return (
    <div className={"flex flex-col overflow-x-auto " + className}>
      <label
        className="text-slate-700 text-[1.1rem]
        bg-transparent w-fit pb-1"
      >
        {titulo}
      </label>
      <input
        type={type}
        placeholder={placeholder}
        className="px-3 py-3 text-[1.1rem] w-auto
        text-slate-700 bg-violet-50
        border-b-2 border-violet-400/5
        focus:outline-none
        placeholder:text-violet-300"
        onInput={onInput}
      />
    </div>
  );
}
