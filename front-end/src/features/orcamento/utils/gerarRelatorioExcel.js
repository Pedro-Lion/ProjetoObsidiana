import * as XLSX from "xlsx";

// ──────────────────────────────────────────────────────────────────────────────
// gerarRelatorioExcel
//
// Gera um arquivo .xlsx com duas abas a partir da lista de orçamentos:
//   • "Resumo Anual"   — totais por status e valor total geral
//   • "Orçamentos"     — listagem detalhada de cada orçamento
//
// @param {Array}  orcamentos  - lista completa vinda da API (/api/orcamento)
// @param {number} ano         - ano de referência usado no nome do arquivo
// ──────────────────────────────────────────────────────────────────────────────
export function gerarRelatorioExcel(orcamentos, ano) {
  // ── Formatadores ──────────────────────────────────────────────────────────

  // Formata uma data ISO/Date para "dd/MM/yyyy HH:mm" em pt-BR
  const formatarData = (valor) => {
    if (!valor) return "—";
    return new Intl.DateTimeFormat("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }).format(new Date(valor));
  };

  // Formata número decimal de horas para "3h30" ou "2h"
  const formatarDuracao = (valor) => {
    if (valor == null || isNaN(Number(valor)) || Number(valor) <= 0) return "—";
    const h = Math.floor(valor);
    const m = Math.round((valor - h) * 60);
    return m === 0 ? `${h}h` : `${h}h${String(m).padStart(2, "0")}`;
  };

  // Formata valor numérico para "R$ 1.234,56"
  const formatarValor = (valor) =>
    new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
      minimumFractionDigits: 2,
    }).format(valor ?? 0);

  // ── Filtra pelo ano selecionado (com base em dataInicio) ──────────────────
  const orcamentosDoAno = orcamentos.filter((o) => {
    if (!o.dataInicio) return false;
    return new Date(o.dataInicio).getFullYear() === ano;
  });

  // ── ABA 1: Resumo Anual ───────────────────────────────────────────────────
  const statusPossiveis = ["Confirmado", "Em análise", "Cancelado"];

  const totaisPorStatus = statusPossiveis.map((status) => {
    const grupo = orcamentosDoAno.filter(
      (o) => o.status?.toLowerCase() === status.toLowerCase()
    );
    const totalValor = grupo.reduce((acc, o) => acc + (o.valorTotal ?? 0), 0);
    return {
      Status: status,
      Quantidade: grupo.length,
      "Valor Total": formatarValor(totalValor),
    };
  });

  // Linha de total geral
  const totalGeral = orcamentosDoAno.reduce(
    (acc, o) => acc + (o.valorTotal ?? 0),
    0
  );
  totaisPorStatus.push({
    Status: "TOTAL",
    Quantidade: orcamentosDoAno.length,
    "Valor Total": formatarValor(totalGeral),
  });

  const wsResumo = XLSX.utils.json_to_sheet(totaisPorStatus);

  // Largura das colunas da aba Resumo
  wsResumo["!cols"] = [{ wch: 18 }, { wch: 14 }, { wch: 20 }];

  // ── ABA 2: Listagem completa ──────────────────────────────────────────────
  const linhasOrcamentos = orcamentosDoAno.map((o) => {
    const nomesServicos = (o.servicos ?? []).map((s) => s.nome).join(", ");
    const nomesEquipamentos = (o.equipamentos ?? []).map((e) => e.nome).join(", ");
    const nomesProfissionais = (o.profissionais ?? []).map((p) => p.nome).join(", ");

    return {
      ID: o.id,
      Descrição: o.descricao ?? "—",
      "Local do Evento": o.localEvento ?? "—",
      "Data Início": formatarData(o.dataInicio),
      "Data Término": formatarData(o.dataTermino),
      "Duração": formatarDuracao(o.duracaoEvento),
      Status: o.status ?? "—",
      "Valor Total": formatarValor(o.valorTotal),
      Serviços: nomesServicos || "—",
      Equipamentos: nomesEquipamentos || "—",
      Profissionais: nomesProfissionais || "—",
    };
  });

  const wsOrcamentos = XLSX.utils.json_to_sheet(linhasOrcamentos);

  // Largura das colunas da aba Orçamentos
  wsOrcamentos["!cols"] = [
    { wch: 6 },  // ID
    { wch: 28 }, // Descrição
    { wch: 24 }, // Local
    { wch: 20 }, // Data Início
    { wch: 20 }, // Data Término
    { wch: 10 }, // Duração
    { wch: 14 }, // Status
    { wch: 18 }, // Valor Total
    { wch: 30 }, // Serviços
    { wch: 30 }, // Equipamentos
    { wch: 30 }, // Profissionais
  ];

  // ── Montar workbook e disparar download ───────────────────────────────────
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, wsResumo, "Resumo Anual");
  XLSX.utils.book_append_sheet(wb, wsOrcamentos, "Orçamentos");

  XLSX.writeFile(wb, `relatorio_orcamentos_${ano}.xlsx`);
}
