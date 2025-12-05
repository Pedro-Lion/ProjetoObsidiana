import { useState } from "react";
import Select from "react-select";

export function ContainerSelectTags({titulo = "Container", placeholder="Escolha uma opção", itens,  preSelecao, onChange}) {
  const [itensSelecionados, setItensSelecionados] = useState(preSelecao ?? []);

  const handleRemove = (removerEste) => {
    const itens = itensSelecionados.filter((item) => item.value !== removerEste.value)

    setItensSelecionados(itens);
    if (onChange) onChange(itens)
  };

  function handleChange(itens) {
    setItensSelecionados(itens)
    if (onChange) onChange(itens)
  }

  const MultiValue = () => null;

  const estilizacao = {
    control: (base, state) => ({
      ...base,
      fontSize:"1.1rem",
      backgroundColor: "white",
      borderRadius: "0.5rem",
      borderColor: state.isFocused ? "#6366f1" : "#d1d5db",
      boxShadow: state.isFocused ? "0 0 0 2px rgba(99, 102, 241, 0.3)" : "none",
      "&:hover": { borderColor: "#6366f1" },
      minHeight: "4rem",
      width:`100%`
    }),
    container:(base) => ({
        ...base,
        marginTop:"0.4rem",
    }),
    input: (base) => ({
      ...base,
      cursor:"text"
    }),
    dropdownIndicator: (base,state) => ({
      ...base,
      color: state.isFocused ? "#c4b5fd" : "#ddd6ff",
      cursor:"pointer",
      "&:hover": { color: "#a78bfa" }
    }),
    indicatorSeparator: (base) => ({
      ...base,
      backgroundColor: "#ddd6ff",
    }),
    clearIndicator: (base) => ({
      ...base,
      cursor:"pointer",
      color:"#ddd6ff",
      "&:hover": { color: "#a78bfa" }
    }),
    menu: (base) => ({
      ...base,
      borderRadius: "0.5rem",
      overflow: "hidden",
      zIndex: 20,
    }),
    option: (base, state) => ({
      ...base,
      fontSize:"1.1rem",
      backgroundColor: state.isSelected ? "#ede9fe" : state.isFocused ? "#e0e7ff" : "white",
      color: state.isSelected ? "white" : "#374151",
      cursor: "pointer",
    }),
    placeholder: (base) => ({
      ...base,
      color: "#9ca3af",
    }),
  };

  return (
    <div style={{
    width:"100%", marginBlock:"1rem",
    backgroundColor:"#f5f3ff",
    padding:"1rem 2rem",
    borderRadius:"0.5rem",
    }}>
    <div className="flex flex-row justify-between items-baseline">
        <label className="text-slate-700 text-xl w-fit mb-2 px-7 py-1 rounded-full border-2 border-violet-200 bg-white">
            {titulo}
        </label>
        <label className="text-slate-700 text-[1.1rem] bg-transparent w-fit mb-1">
            Selecionados: {itensSelecionados.length}
        </label>
    </div>
    <Select
        isMulti
        isSearchable
        options={itens}
        value={itensSelecionados}
        onChange={handleChange}
        placeholder={placeholder}
        closeMenuOnSelect={true}
        components={{ MultiValue }}
        styles={estilizacao}
        noOptionsMessage={() => "Nenhum resultado encontrado"}
      />

      <div className="flex flex-wrap gap-2 mt-4">
        {itensSelecionados.length > 0 ? 
        (
          itensSelecionados.map((selecionado) => (
            <div
            onClick={() => handleRemove(selecionado)}
            key={selecionado.value}
            className="flex items-center px-3 py-2 rounded-md text-[1.1rem] cursor-pointer
            bg-violet-200 text-slate-700">
              {selecionado.label}
              <span className="ml-2">×</span>
            </div>
          ))
        ) : (
          <p className="text-gray-400 text-[1.1rem]">
            Nenhuma opção selecionada.
          </p>
        )}
      </div>
    </div>
  );
}
