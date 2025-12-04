export const formatarPreco = (valor) => {
  if (valor === null || valor === undefined || valor === "") {
    return "N/A";
  }

  // Se for string, tenta converter (aceita "1.500,50" ou "1500.50" ou "1500,50")
  if (typeof valor === "string") {
    // remove espaços e troca vírgula por ponto, mas antes trata separador de milhar
    const s = valor.trim()
      .replace(/\s/g, "")         // remove espaços
      .replace(/\.(?=\d{3}\b)/g, "") // remove pontos de milhar (ex: 1.234 -> 1234)
      .replace(",", ".");         // vírgula decimal -> ponto
    const n = Number(s);
    if (Number.isNaN(n)) return "N/A";
    return new Intl.NumberFormat("pt-BR", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(n);
  }

  // Se for number
  if (typeof valor === "number") {
    if (Number.isNaN(valor)) return "N/A";
    return new Intl.NumberFormat("pt-BR", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(valor);
  }

  // Qualquer outro tipo (objeto, boolean) -> N/A
  return "N/A";
};