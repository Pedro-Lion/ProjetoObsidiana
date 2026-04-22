// Usamos xlsx-js-style (fork do SheetJS) que adiciona suporte a estilos
// (cores, fontes, bordas, alinhamento) sem mudar a API base do xlsx.
import * as XLSX from "xlsx-js-style";

// ── Paleta de cores da aplicação (espelha Tailwind indigo/violet/slate) ──────
const COR = {
  indigo700: "3730A3",
  indigo600: "4F46E5", // cor principal — botões, cabeçalhos
  indigo500: "6366F1",
  indigo300: "A5B4FC",
  indigo100: "E0E7FF",
  indigo50:  "EEF2FF",
  violet50:  "F5F3FF", // listras alternadas nas linhas de dados
  white:     "FFFFFF",
  slate700:  "334155", // texto principal
  slate400:  "94A3B8", // texto secundário
  // Status — cores aproximadas do CardOrcamento.jsx (oklch → hex)
  verde100:  "DCFCE7",
  verde800:  "166534",
  amber100:  "FEF9C3",
  amber800:  "854D0E",
  red100:    "FEE2E2",
  red800:    "991B1B",
};

// ── Fábrica de estilos ────────────────────────────────────────────────────────
// Cada função retorna um objeto de estilo xlsx-js-style pronto para usar em .s

// Linha de título principal (fundo indigo, texto branco, negrito grande)
const esTitulo = () => ({
  fill: { fgColor: { rgb: COR.indigo600 } },
  font: { name: "Calibri", bold: true, sz: 16, color: { rgb: COR.white } },
  alignment: { horizontal: "center", vertical: "center" },
});

// Linha de subtítulo (fundo indigo claro, texto indigo, itálico)
const esSubtitulo = () => ({
  fill: { fgColor: { rgb: COR.indigo50 } },
  font: { name: "Calibri", sz: 10, italic: true, color: { rgb: COR.indigo600 } },
  alignment: { horizontal: "center", vertical: "center" },
});

// Linha de espaçador (fundo branco puro)
const esEspacador = () => ({
  fill: { fgColor: { rgb: COR.white } },
});

// Cabeçalho de coluna (fundo indigo, texto branco, negrito)
const esCabecalho = () => ({
  fill: { fgColor: { rgb: COR.indigo600 } },
  font: { name: "Calibri", bold: true, sz: 11, color: { rgb: COR.white } },
  alignment: { horizontal: "center", vertical: "center" },
  border: {
    top:    { style: "medium", color: { rgb: COR.indigo600 } },
    bottom: { style: "medium", color: { rgb: COR.indigo700 } },
    left:   { style: "thin",   color: { rgb: COR.indigo500 } },
    right:  { style: "thin",   color: { rgb: COR.indigo500 } },
  },
});

// Célula de dado comum (listras alternadas branco/violet-50)
const esDado = (listra = false, horizontal = "left") => ({
  fill: { fgColor: { rgb: listra ? COR.violet50 : COR.white } },
  font: { name: "Calibri", sz: 10, color: { rgb: COR.slate700 } },
  alignment: { horizontal, vertical: "center", wrapText: false },
  border: {
    bottom: { style: "thin", color: { rgb: COR.indigo100 } },
    left:   { style: "thin", color: { rgb: COR.indigo100 } },
    right:  { style: "thin", color: { rgb: COR.indigo100 } },
  },
});

// Célula de status colorida (espelha as cores do CardOrcamento.jsx)
const esStatus = (status) => {
  const mapa = {
    "confirmado":  { bg: COR.verde100, fg: COR.verde800 },
    "em análise":  { bg: COR.amber100, fg: COR.amber800 },
    "cancelado":   { bg: COR.red100,   fg: COR.red800   },
  };
  const cores = mapa[status?.toLowerCase()] ?? { bg: COR.indigo50, fg: COR.slate700 };
  return {
    fill: { fgColor: { rgb: cores.bg } },
    font: { name: "Calibri", bold: true, sz: 10, color: { rgb: cores.fg } },
    alignment: { horizontal: "center", vertical: "center" },
    border: {
      bottom: { style: "thin", color: { rgb: COR.indigo100 } },
      left:   { style: "thin", color: { rgb: COR.indigo100 } },
      right:  { style: "thin", color: { rgb: COR.indigo100 } },
    },
  };
};

// Linha de total geral (fundo indigo suave, texto indigo escuro, negrito)
const esTotal = () => ({
  fill: { fgColor: { rgb: COR.indigo100 } },
  font: { name: "Calibri", bold: true, sz: 11, color: { rgb: COR.indigo700 } },
  alignment: { horizontal: "center", vertical: "center" },
  border: {
    top:    { style: "medium", color: { rgb: COR.indigo600 } },
    bottom: { style: "medium", color: { rgb: COR.indigo600 } },
    left:   { style: "thin",   color: { rgb: COR.indigo300 } },
    right:  { style: "thin",   color: { rgb: COR.indigo300 } },
  },
});

// ── Helpers ───────────────────────────────────────────────────────────────────

// Cria um objeto de célula xlsx-js-style com valor, tipo e estilo
// type: 's' = string | 'n' = número
function cel(valor, estilo, type = "s") {
  return { v: valor, t: type, s: estilo };
}

// Preenche uma linha inteira com o mesmo estilo
// (necessário para células mescladas renderizarem o fundo corretamente)
function preencherLinha(ws, linha, qtdCols, estilo, valorPrimeiro = "") {
  for (let c = 0; c < qtdCols; c++) {
    const addr = XLSX.utils.encode_cell({ r: linha, c });
    ws[addr] = cel(c === 0 ? valorPrimeiro : "", estilo);
  }
}

// ── Formatadores ──────────────────────────────────────────────────────────────

const formatarData = (valor) => {
  if (!valor) return "—";
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit", month: "2-digit", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  }).format(new Date(valor));
};

const formatarDuracao = (valor) => {
  if (valor == null || isNaN(Number(valor)) || Number(valor) <= 0) return "—";
  const h = Math.floor(valor);
  const m = Math.round((valor - h) * 60);
  return m === 0 ? `${h}h` : `${h}h${String(m).padStart(2, "0")}`;
};

const formatarValor = (valor) =>
  new Intl.NumberFormat("pt-BR", {
    style: "currency", currency: "BRL", minimumFractionDigits: 2,
  }).format(valor ?? 0);

// ── Função principal ──────────────────────────────────────────────────────────
// @param {Array}  orcamentos - lista completa vinda da API (/api/orcamento)
// @param {number} ano        - ano de referência (filtra orçamentos por dataInicio)
export function gerarRelatorioExcel(orcamentos, ano) {
  const dataGeracao = new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit", month: "2-digit", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  }).format(new Date());

  const subtitulo = `Obsidiana · Gerado em: ${dataGeracao}`;

  // Filtra somente orçamentos do ano informado (por dataInicio)
  const orcamentosDoAno = orcamentos.filter((o) => {
    if (!o.dataInicio) return false;
    return new Date(o.dataInicio).getFullYear() === ano;
  });

  // ────────────────────────────────────────────────────────────────────────────
  // ABA 1 — RESUMO ANUAL
  // Estrutura: Título | Subtítulo | Espaçador | Cabeçalhos | 3 status | Total
  // ────────────────────────────────────────────────────────────────────────────
  const wsResumo = {};
  wsResumo["!ref"] = "A1:C8";

  preencherLinha(wsResumo, 0, 3, esTitulo(), `Relatório de Orçamentos – ${ano}`);
  preencherLinha(wsResumo, 1, 3, esSubtitulo(), subtitulo);
  preencherLinha(wsResumo, 2, 3, esEspacador());

  ["Status", "Quantidade", "Valor Total"].forEach((h, c) => {
    wsResumo[XLSX.utils.encode_cell({ r: 3, c })] = cel(h, esCabecalho());
  });

  const statusPossiveis = ["Confirmado", "Em análise", "Cancelado"];
  statusPossiveis.forEach((status, i) => {
    const grupo = orcamentosDoAno.filter(
      (o) => o.status?.toLowerCase() === status.toLowerCase()
    );
    const totalValor = grupo.reduce((acc, o) => acc + (o.valorTotal ?? 0), 0);
    const r = 4 + i;
    const listra = i % 2 === 1;
    wsResumo[XLSX.utils.encode_cell({ r, c: 0 })] = cel(status, esStatus(status));
    wsResumo[XLSX.utils.encode_cell({ r, c: 1 })] = cel(grupo.length, esDado(listra, "center"), "n");
    wsResumo[XLSX.utils.encode_cell({ r, c: 2 })] = cel(formatarValor(totalValor), esDado(listra, "right"));
  });

  const totalGeral = orcamentosDoAno.reduce((acc, o) => acc + (o.valorTotal ?? 0), 0);
  wsResumo[XLSX.utils.encode_cell({ r: 7, c: 0 })] = cel("TOTAL GERAL", esTotal());
  wsResumo[XLSX.utils.encode_cell({ r: 7, c: 1 })] = cel(orcamentosDoAno.length, esTotal(), "n");
  wsResumo[XLSX.utils.encode_cell({ r: 7, c: 2 })] = cel(formatarValor(totalGeral), esTotal());

  // Mesclas: título e subtítulo cobrem as 3 colunas
  wsResumo["!merges"] = [
    { s: { r: 0, c: 0 }, e: { r: 0, c: 2 } },
    { s: { r: 1, c: 0 }, e: { r: 1, c: 2 } },
  ];
  wsResumo["!rows"] = [
    { hpt: 36 }, // título
    { hpt: 18 }, // subtítulo
    { hpt: 8  }, // espaçador
    { hpt: 24 }, // cabeçalhos
    { hpt: 22 }, // confirmado
    { hpt: 22 }, // em análise
    { hpt: 22 }, // cancelado
    { hpt: 26 }, // total geral
  ];
  wsResumo["!cols"] = [{ wch: 18 }, { wch: 14 }, { wch: 22 }];

  // ────────────────────────────────────────────────────────────────────────────
  // ABA 2 — ORÇAMENTOS (listagem completa, 11 colunas A–K)
  // Estrutura: Título | Subtítulo | Espaçador | Cabeçalhos | uma linha por orçamento
  // ────────────────────────────────────────────────────────────────────────────
  const cabecalhos = [
    "ID", "Descrição", "Local do Evento", "Data Início", "Data Término",
    "Duração", "Status", "Valor Total", "Serviços", "Equipamentos", "Profissionais",
  ];
  const colsOrc = cabecalhos.length; // 11
  const lastColOrc = XLSX.utils.encode_col(colsOrc - 1); // "K"
  const totalLinhasOrc = 4 + orcamentosDoAno.length;

  const wsOrcamentos = {};
  wsOrcamentos["!ref"] = `A1:${lastColOrc}${Math.max(totalLinhasOrc, 5)}`;

  preencherLinha(wsOrcamentos, 0, colsOrc, esTitulo(), `Orçamentos – ${ano}`);
  preencherLinha(wsOrcamentos, 1, colsOrc, esSubtitulo(), subtitulo);
  preencherLinha(wsOrcamentos, 2, colsOrc, esEspacador());

  cabecalhos.forEach((h, c) => {
    wsOrcamentos[XLSX.utils.encode_cell({ r: 3, c })] = cel(h, esCabecalho());
  });

  // Linhas de dados: uma por orçamento, com listras alternadas
  orcamentosDoAno.forEach((o, i) => {
    const r = 4 + i;
    const listra = i % 2 === 1;

    const nomesServicos      = (o.servicos      ?? []).map((s) => s.nome).join(", ") || "—";
    const nomesEquipamentos  = (o.equipamentos  ?? []).map((e) => e.nome).join(", ") || "—";
    const nomesProfissionais = (o.profissionais ?? []).map((p) => p.nome).join(", ") || "—";

    const colunas = [
      { v: o.id,                            s: esDado(listra, "center"), t: "n" },
      { v: o.descricao        ?? "—",       s: esDado(listra, "left")           },
      { v: o.localEvento      ?? "—",       s: esDado(listra, "left")           },
      { v: formatarData(o.dataInicio),      s: esDado(listra, "center")         },
      { v: formatarData(o.dataTermino),     s: esDado(listra, "center")         },
      { v: formatarDuracao(o.duracaoEvento),s: esDado(listra, "center")         },
      { v: o.status           ?? "—",       s: esStatus(o.status)               },
      { v: formatarValor(o.valorTotal),     s: esDado(listra, "right")          },
      { v: nomesServicos,                   s: esDado(listra, "left")           },
      { v: nomesEquipamentos,               s: esDado(listra, "left")           },
      { v: nomesProfissionais,              s: esDado(listra, "left")           },
    ];

    colunas.forEach((coluna, c) => {
      wsOrcamentos[XLSX.utils.encode_cell({ r, c })] = {
        v: coluna.v,
        t: coluna.t ?? "s",
        s: coluna.s,
      };
    });
  });

  // Mesclas: título e subtítulo cobrem as 11 colunas
  wsOrcamentos["!merges"] = [
    { s: { r: 0, c: 0 }, e: { r: 0, c: colsOrc - 1 } },
    { s: { r: 1, c: 0 }, e: { r: 1, c: colsOrc - 1 } },
  ];
  wsOrcamentos["!rows"] = [
    { hpt: 36 }, // título
    { hpt: 18 }, // subtítulo
    { hpt: 8  }, // espaçador
    { hpt: 24 }, // cabeçalhos
    ...orcamentosDoAno.map(() => ({ hpt: 20 })), // linhas de dados
  ];
  wsOrcamentos["!cols"] = [
    { wch: 6  }, // ID
    { wch: 28 }, // Descrição
    { wch: 22 }, // Local do Evento
    { wch: 18 }, // Data Início
    { wch: 18 }, // Data Término
    { wch: 10 }, // Duração
    { wch: 14 }, // Status
    { wch: 18 }, // Valor Total
    { wch: 28 }, // Serviços
    { wch: 28 }, // Equipamentos
    { wch: 28 }, // Profissionais
  ];

  // ── Montar workbook e disparar download ──────────────────────────────────
  // cellStyles: true é obrigatório para o xlsx-js-style aplicar os estilos
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, wsResumo,     "Resumo Anual");
  XLSX.utils.book_append_sheet(wb, wsOrcamentos, "Orçamentos");
  XLSX.writeFile(wb, `relatorio_orcamentos_${ano}.xlsx`, { cellStyles: true });
}
