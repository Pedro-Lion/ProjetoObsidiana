import React, { useState } from "react";
import Select, { components } from "react-select";

export function InputSelect() {
  const [itensSelecionados, setItensSelecionados] = useState([]);

  const itens = [
    { value: "ariel", label: "Ariel" },
    { value: "sebastian", label: "Sebastião" },
    { value: "flounder", label: "Linguado" },
    { value: "ursula", label: "Úrsula" },
    { value: "eric", label: "Príncipe Eric" },
  ];

  const handleRemove = (removerEste) => {
    setItensSelecionados((prev) =>
      prev.filter((opt) => opt.value !== removerEste.value)
    );
  };

  // Esconde as tags internas do react-select
  const MultiValue = () => null;

  return (
    <div className="w-72 space-y-4">
      <Select
        isMulti
        isSearchable
        options={itens}
        value={itensSelecionados}
        onChange={setItensSelecionados}
        placeholder="Escolha personagens..."
        closeMenuOnSelect={true}
        components={{ MultiValue }}
      />

      <div className="flex flex-wrap gap-2">
        {itensSelecionados.length > 0 ? 
        (
          itensSelecionados.map((selecionado) => (
            <div
            onClick={() => handleRemove(selecionado)}
              key={selecionado.value}
              className="flex items-center bg-indigo-100 text-indigo-700 px-3 py-1 rounded-full text-sm cursor-pointer">
              {selecionado.label}
              <span className="ml-2">
                ×
              </span>
            </div>
          ))
        ) : (
          <p className="text-gray-400 text-sm">
            Nenhum personagem selecionado.
          </p>
        )}
      </div>
    </div>
  );
}
