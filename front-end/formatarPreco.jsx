export const formatarPreco = (valor) => {
  if (valor === null || valor === undefined || valor === "") {
    return "N/A";
  }

  // Se for string, tenta converter
  const numero = typeof valor === "number" ? valor : Number(String(valor).replace(",", "."));

  if (Number.isNaN(numero)) {
    return "N/A";
  }

  return new Intl.NumberFormat("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(numero);
};