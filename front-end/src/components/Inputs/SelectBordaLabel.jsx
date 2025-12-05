export function SelectBordaLabel({
  titulo = "",
  placeholder = "",
  className = "w-80",
  onChange,
  options = [{ value, label: "" }],
  disabled = false,
  value,
  defaulValue,
}) {
  const estilos = {
    cursor: !disabled ? "cursor-default" : "cursor-not-allowed",
    label: !disabled ? "text-indigo-500" : "text-indigo-300",
    selectBorda: !disabled ? "border-indigo-500" : "border-indigo-300",
    selectTexto: !disabled ? "text-slate-700" : "text-slate-400"
  };

  return (
    <div className={`flex flex-col overflow-x-auto ${estilos.cursor} ${className}`}>
      <label
        className={`relative top-3 ml-[0.7rem] px-[0.3rem]
        font-medium text-[1.1rem]
        bg-white w-fit ${estilos.label}`}
      >
        {titulo}
      </label>

      <select
        onChange={onChange}
        disabled={disabled}
        className={`px-3 py-3 text-[1.1rem] bg-transparent border-1 rounded-lg        
        focus:outline-none placeholder:text-black/25
        ${estilos.selectBorda} ${estilos.selectTexto}`}
        value={value}
        defaultValue={defaulValue}
      >
        {placeholder && <option value="">{placeholder}</option>}
        {options.map((o) => (
          <option
            key={"opt_" + o.label.toLowerCase()}
            value={o.value ? o.value : o.label}
          >
            {o.label}
          </option>
        ))}
      </select>
    </div>
  );
}
