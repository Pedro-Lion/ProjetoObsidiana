import { useState, useEffect, useRef } from "react";
import Select from "react-select";

export function ContainerSelectTags({ titulo = "Container", placeholder = "Escolha uma opção", itens, preSelecao, onChange, temQuantidade = false }) {
  // Rastreia o último payload enviado para evitar chamar onChange desnecessariamente
  const payloadAnteriorRef = useRef(null);

  const [itensSelecionados, setItensSelecionados] = useState(preSelecao != undefined ? preSelecao : []);

  function definirQuantidades() {
    if (!temQuantidade) return null;
    if (preSelecao) {
      const qtd = {}
      preSelecao.forEach((item) => {
        qtd[item.value] = item.quantidade
      })
      return qtd;
    } 
  }
  const [quantidades, setQuantidades] = useState(definirQuantidades());

  const handleRemove = (removerEste) => {
    setItensSelecionados(prev =>
      (prev || []).filter(item => item.value !== removerEste.value)
    );

    setQuantidades(prev => {
      if (!temQuantidade) return prev;
      const copia = { ...(prev || {}) };
      delete copia[removerEste.value];
      return copia;
    });
  };


  function handleChange(novosItens) {
    setItensSelecionados(novosItens || []);

    setQuantidades(prev => {
      if (!temQuantidade) return prev;
      const copia = { ...(prev || {}) };
      (novosItens || []).forEach(item => {
        if (!copia[item.value]) copia[item.value] = 1;
      });
      return copia;
    });
  }


  const handleQuantidadeChange = (value, novoValor) => {
    if (!temQuantidade) return;
    const safe = (!novoValor || isNaN(novoValor) || Number(novoValor) < 1)
      ? 1
      : Number(novoValor);

    setQuantidades(prev => ({
      ...(prev || {}),
      [value]: safe
    }));
  };


  /* AFIM DE SANAR CONFLITOS ENTRE OS onChange, vamos usar useEffect:
  1) o filho renderiza
  2) depois, o useEffect dispara
  3) só então o pai atualiza o estado (setOrcamento) */

  // Notifica o pai APENAS se o payload realmente mudou
  useEffect(() => {
    if (!onChange) return;
    
    let payload;
    
    if (!temQuantidade) {
      payload = itensSelecionados || [];
    } else {
      payload = (itensSelecionados || []).map(it => ({
        ...it,
        quantidade: quantidades?.[it.value] ?? 1
      }));
    }

    // Comparação profunda: serializa o payload para comparar com o anterior
    const payloadSerializado = JSON.stringify(payload);
    
    if (payloadAnteriorRef.current !== payloadSerializado) {
      payloadAnteriorRef.current = payloadSerializado;
      onChange(payload);
    }
  }, [itensSelecionados, quantidades, temQuantidade]);

  // SINCRONIZA o estado com a prop preSelecao quando ela muda
  useEffect(() => {
    if (preSelecao && preSelecao.length > 0) {
      setItensSelecionados(preSelecao);
      
      if (temQuantidade) {
        const qtd = {};
        preSelecao.forEach((item) => {
          qtd[item.value] = item.quantidade ?? 1;
        });
        setQuantidades(qtd);
      }
    }
  }, [preSelecao, temQuantidade]);

  const MultiValue = () => null;

  const estilizacao = {
    control: (base, state) => ({
      ...base,
      fontSize: "1.1rem",
      backgroundColor: "white",
      borderRadius: "0.5rem",
      borderColor: state.isFocused ? "#6366f1" : "#d1d5db",
      boxShadow: state.isFocused ? "0 0 0 2px rgba(99, 102, 241, 0.3)" : "none",
      "&:hover": { borderColor: "#6366f1" },
      minHeight: "4rem",
      width: `100%`
    }),
    container: (base) => ({
      ...base,
      marginTop: "0.4rem",
    }),
    input: (base) => ({
      ...base,
      cursor: "text"
    }),
    dropdownIndicator: (base, state) => ({
      ...base,
      color: state.isFocused ? "#c4b5fd" : "#ddd6ff",
      cursor: "pointer",
      "&:hover": { color: "#a78bfa" }
    }),
    indicatorSeparator: (base) => ({
      ...base,
      backgroundColor: "#ddd6ff",
    }),
    clearIndicator: (base) => ({
      ...base,
      cursor: "pointer",
      color: "#ddd6ff",
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
      fontSize: "1.1rem",
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
      width: "100%", marginBlock: "1rem",
      backgroundColor: "#f5f3ff",
      padding: "1rem 2rem",
      borderRadius: "0.5rem",
    }}>
      <div className="flex flex-row justify-between items-baseline">
        <label className="text-slate-700 text-xl w-fit mb-2 px-7 py-1 rounded-full border-2 border-violet-200 bg-white">
          {titulo}
        </label>
        <label className="text-slate-700 text-[1.1rem] bg-transparent w-fit mb-1">
          Selecionados: {itensSelecionados?.length}
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
        {itensSelecionados?.length > 0 ?
          (
            itensSelecionados.map((selecionado) => (
              <div
                key={titulo.toLowerCase() + selecionado.value}
                className="flex gap-2 items-center px-3 py-2 rounded-md text-[1.1rem] bg-violet-200 text-slate-700">
                {temQuantidade ? (
                  <input
                    type="number"
                    placeholder="nº"
                    className="bg-violet-100 rounded-md w-16 focus:outline-none text-center"
                    min={1}
                    value={quantidades[selecionado.value] ?? 1}
                    onChange={(e) => handleQuantidadeChange(selecionado.value, e.target.value)}
                  />
                ) : null}
                <span className="w-fit">{selecionado.label}</span>
                <span className="ml-2 cursor-pointer"
                  onClick={() => handleRemove(selecionado)}>×</span>
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
