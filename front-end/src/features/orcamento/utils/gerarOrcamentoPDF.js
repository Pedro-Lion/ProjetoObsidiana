// Utilitário que gera o PDF do orçamento individual e dispara o download.
// Espelha o padrão da gerarRelatorioExcel.js (módulo isolado, função única
// exportada e chamada direta a partir do componente).
//
// Usa pdf() do @react-pdf/renderer para renderizar o componente OrcamentoPDF
// em um Blob, e cria um link temporário no DOM para acionar o download —
// padrão aceito por todos os navegadores modernos.

import { pdf } from "@react-pdf/renderer";
import React from "react";
import { OrcamentoPDF } from "./OrcamentoPDF.jsx";

// Caminho público do logo (servido pelo Vite a partir de /front-end/public).
// Mantemos absoluto via window.location.origin porque react-pdf precisa de
// uma URL completa para baixar a imagem ao montar o documento.
function resolverLogoUrl() {
  if (typeof window === "undefined") return "";
  return `${window.location.origin}/logo.png`;
}

// @param {object} orcamento - objeto completo do orçamento (mesmo formato que
//                              o CardOrcamento recebe via prop `dados`)
// @returns {Promise<void>}  - resolve quando o download é disparado
export async function gerarOrcamentoPDF(orcamento) {
  // Monta o documento react-pdf e gera o Blob.
  // pdf().toBlob() é assíncrono — a renderização envolve baixar a imagem do
  // logo e construir o PDF em memória.
  const blob = await pdf(
    React.createElement(OrcamentoPDF, {
      orcamento,
      logoUrl: resolverLogoUrl(),
    })
  ).toBlob();

  // Nome simples e previsível: "obsidiana-orcamento-{id}.pdf"
  const nomeArquivo = `obsidiana-orcamento-${orcamento.id}.pdf`;

  // Cria um link temporário, dispara o clique e remove. URL.createObjectURL
  // mantém o blob na memória até o revokeObjectURL ser chamado.
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = nomeArquivo;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);

  // Libera o blob da memória após o navegador iniciar o download
  URL.revokeObjectURL(url);
}
