export function InputBordaLabel({ titulo = "", type = "text", placeholder = "Digite aqui", value, defaultValue, className = "w-80", onInput}) {
  return (
    <div className={"flex flex-col overflow-x-auto " + className}>
      <label
        className="relative top-3 ml-[0.7rem] px-[0.3rem]
        text-indigo-500 font-medium text-[1.1rem]
        bg-white w-fit"
      >
        {titulo}
      </label>
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        defaultValue={defaultValue}
        onInput={onInput}
        className="border-indigo-500 text-slate-700
        px-3 py-3 text-[1.1rem] bg-transparent border-1 rounded-lg        
        focus:outline-none placeholder:text-black/25"
      />
    </div>
  );
}
