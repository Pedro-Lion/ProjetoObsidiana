// Componente React-PDF que descreve o documento de Orçamento individual
// que será compartilhado com o cliente final.
//
// Diferente do CardOrcamento.jsx (UI da app), o layout aqui é pensado para
// impressão/PDF: páginas A4, paleta neutra com acentos indigo da identidade
// visual, espaçamentos generosos e tipografia legível.
//
// Decisões intencionais:
//   • SEM badge de status (cliente não precisa ver "Em análise"/"Cancelado").
//   • Blocos opcionais (serviços, equipamentos, profissionais, observações)
//     são ocultados quando vazios, evitando seções fantasmas no documento.
//   • Os formatadores são duplicados aqui (e não importados do gerarRelatorioExcel)
//     para manter este utilitário independente — alterações de visual no PDF
//     não devem arriscar quebrar a geração do Excel.

import {
  Document,
  Page,
  Text,
  View,
  StyleSheet,
  Image,
} from "@react-pdf/renderer";

// ── Paleta (espelha Tailwind indigo/violet/slate usado no resto do app) ──────
const COR = {
  indigo700: "#3730A3",
  indigo600: "#4F46E5",
  indigo500: "#6366F1",
  indigo100: "#E0E7FF",
  indigo50:  "#EEF2FF",
  violet500: "#8B5CF6",
  white:     "#FFFFFF",
  slate700:  "#334155",
  slate500:  "#64748B",
  slate400:  "#94A3B8",
  slate200:  "#E2E8F0",
  slate100:  "#F1F5F9",
  slate50:   "#F8FAFC",
};

// ── Formatadores ─────────────────────────────────────────────────────────────
// Mantidos locais ao módulo para preservar independência (ver cabeçalho).

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

// ── Estilos ──────────────────────────────────────────────────────────────────
// react-pdf usa um subset CSS-in-JS. Sem media queries, sem flex-wrap por
// padrão (usa-se flexDirection explicitamente). Cores devem ser hex/rgb.

const styles = StyleSheet.create({
  page: {
    backgroundColor: COR.white,
    paddingTop: 36,
    paddingBottom: 56,
    paddingHorizontal: 40,
    fontFamily: "Helvetica",
    fontSize: 10,
    color: COR.slate700,
  },

  // Cabeçalho: logo à esquerda, título/identificador à direita
  cabecalho: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 8,
  },
  logo: {
    width: 80,
    height: "auto",
  },
  cabecalhoInfo: {
    alignItems: "flex-end",
  },
  cabecalhoTitulo: {
    fontSize: 18,
    fontFamily: "Helvetica-Bold",
    color: COR.indigo600,
  },
  cabecalhoSubtitulo: {
    fontSize: 9,
    color: COR.slate400,
    marginTop: 2,
  },

  // Faixa gradiente fake — react-pdf não tem gradiente nativo, então usamos
  // duas barras lado a lado (indigo → violet) para evocar a identidade visual.
  faixaWrap: {
    flexDirection: "row",
    height: 1,
    marginBottom: 24,
  },
  // Tons mais claros para um divisor sutil em vez de uma faixa colorida cheia
  faixaA: { flex: 1, backgroundColor: COR.indigo100 },
  faixaB: { flex: 1, backgroundColor: COR.slate200 },

  // Bloco do evento — destaque do "o quê/onde/quando" do orçamento
  blocoEvento: {
    backgroundColor: COR.slate50,
    borderLeftWidth: 3,
    borderLeftColor: COR.indigo500,
    borderLeftStyle: "solid",
    padding: 14,
    marginBottom: 20,
  },
  eventoTitulo: {
    fontSize: 14,
    fontFamily: "Helvetica-Bold",
    color: COR.slate700,
    marginBottom: 8,
  },
  eventoLinha: {
    flexDirection: "row",
    marginBottom: 3,
  },
  eventoLabel: {
    width: 70,
    fontSize: 10,
    color: COR.slate500,
  },
  eventoValor: {
    flex: 1,
    fontSize: 10,
    color: COR.slate700,
  },

  // Títulos de seção (Serviços, Equipamentos, etc.)
  secaoTitulo: {
    fontSize: 12,
    fontFamily: "Helvetica-Bold",
    color: COR.indigo600,
    marginBottom: 6,
    marginTop: 4,
  },

  // Tabelas
  tabela: {
    width: "100%",
    marginBottom: 16,
    borderWidth: 1,
    borderColor: COR.slate200,
    borderStyle: "solid",
  },
  tabelaCabecalhoLinha: {
    flexDirection: "row",
    backgroundColor: COR.indigo600,
  },
  tabelaCabecalhoCelula: {
    padding: 6,
    fontSize: 9,
    fontFamily: "Helvetica-Bold",
    color: COR.white,
  },
  tabelaLinha: {
    flexDirection: "row",
    borderTopWidth: 1,
    borderTopColor: COR.slate200,
    borderTopStyle: "solid",
  },
  tabelaLinhaListra: {
    backgroundColor: COR.slate50,
  },
  tabelaCelula: {
    padding: 6,
    fontSize: 10,
    color: COR.slate700,
  },

  // Linha de profissionais (texto corrido em vez de tabela)
  profissionaisTexto: {
    fontSize: 10,
    color: COR.slate700,
    marginBottom: 16,
  },

  // Valor total — caixa de destaque alinhada à direita
  totalWrap: {
    flexDirection: "row",
    justifyContent: "flex-end",
    marginBottom: 20,
  },
  totalCaixa: {
    backgroundColor: COR.indigo50,
    borderWidth: 1,
    borderColor: COR.indigo100,
    borderStyle: "solid",
    paddingVertical: 10,
    paddingHorizontal: 18,
    minWidth: 220,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  totalLabel: {
    fontSize: 11,
    fontFamily: "Helvetica-Bold",
    color: COR.slate700,
  },
  totalValor: {
    fontSize: 14,
    fontFamily: "Helvetica-Bold",
    color: COR.indigo700,
  },

  // Observações
  observacoesBloco: {
    backgroundColor: COR.slate50,
    padding: 12,
    marginBottom: 16,
  },
  observacoesTexto: {
    fontSize: 10,
    color: COR.slate700,
    lineHeight: 1.5,
  },

  // Rodapé fixo no fim da página
  rodape: {
    position: "absolute",
    bottom: 24,
    left: 40,
    right: 40,
    textAlign: "center",
    fontSize: 8,
    color: COR.slate400,
  },
});

// ── Sub-componentes ──────────────────────────────────────────────────────────
// Pequenos helpers para deixar o componente principal mais legível.

// Linha "Label: valor" usada no bloco do evento
const LinhaEvento = ({ label, valor }) => (
  <View style={styles.eventoLinha}>
    <Text style={styles.eventoLabel}>{label}</Text>
    <Text style={styles.eventoValor}>{valor}</Text>
  </View>
);

// Tabela genérica usada tanto para serviços quanto para equipamentos.
// `colunas` define largura relativa (flex) e alinhamento de cada coluna;
// `linhas` é array de arrays já formatado para exibição.
const Tabela = ({ colunas, linhas }) => (
  <View style={styles.tabela}>
    <View style={styles.tabelaCabecalhoLinha}>
      {colunas.map((col, i) => (
        <Text
          key={i}
          style={[
            styles.tabelaCabecalhoCelula,
            { flex: col.flex, textAlign: col.align ?? "left" },
          ]}
        >
          {col.titulo}
        </Text>
      ))}
    </View>
    {linhas.map((linha, idx) => (
      <View
        key={idx}
        style={[
          styles.tabelaLinha,
          idx % 2 === 1 ? styles.tabelaLinhaListra : null,
        ]}
      >
        {linha.map((celula, c) => (
          <Text
            key={c}
            style={[
              styles.tabelaCelula,
              { flex: colunas[c].flex, textAlign: colunas[c].align ?? "left" },
            ]}
          >
            {celula}
          </Text>
        ))}
      </View>
    ))}
  </View>
);

// ── Componente principal ─────────────────────────────────────────────────────
// @param {object} orcamento - o mesmo objeto `dados` que o CardOrcamento recebe
// @param {string} logoUrl   - URL absoluta para o logo (passada de fora para
//                              permitir testes/ambientes diferentes)
export function OrcamentoPDF({ orcamento, logoUrl }) {
  // Data de geração formatada para o cabeçalho e rodapé
  const dataGeracao = new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit", month: "2-digit", year: "numeric",
  }).format(new Date());

  // Mapa id-do-equipamento → quantidadeUsada (mesma lógica do CardOrcamento)
  // Liga a relação many-to-many "equipamentos" ao quanto foi alugado neste
  // orçamento. Fallback para 1 quando o uso não está informado.
  const usosPorEquipamento = (orcamento.usosEquipamentos ?? []).reduce((acc, uso) => {
    const id = uso?.equipamento?.id;
    if (id != null) acc[id] = uso.quantidadeUsada ?? 1;
    return acc;
  }, {});

  // Pré-monta as linhas das tabelas
  const linhasServicos = (orcamento.servicos ?? []).map((s) => [
    s.nome ?? "—",
    String(s.horas ?? 0),
    formatarValor(s.valorPorHora),
    formatarValor((s.valorPorHora ?? 0) * (s.horas ?? 0)),
  ]);

  const linhasEquipamentos = (orcamento.equipamentos ?? []).map((e) => {
    const qtd = usosPorEquipamento[e.id] ?? 1;
    return [
      String(qtd),
      e.nome ?? "—",
      formatarValor(e.valorPorHora),
      formatarValor((e.valorPorHora ?? 0) * qtd),
    ];
  });

  const nomesProfissionais = (orcamento.profissionais ?? [])
    .map((p) => p.nome)
    .filter(Boolean)
    .join(", ");

  return (
    <Document
      title={`Orçamento ${orcamento.id}`}
      author="Obsidiana"
    >
      <Page size="A4" style={styles.page}>
        {/* Cabeçalho com logo + identificação do documento */}
        <View style={styles.cabecalho}>
          {/* `src` aceita URL absoluta — passada pelo gerarOrcamentoPDF */}
          {logoUrl ? <Image style={styles.logo} src={logoUrl} /> : <View />}
          <View style={styles.cabecalhoInfo}>
            <Text style={styles.cabecalhoTitulo}>
              Orçamento
            </Text>
            <Text style={styles.cabecalhoSubtitulo}>
              Emitido em {dataGeracao}
            </Text>
          </View>
        </View>

        {/* Faixa decorativa indigo→violet */}
        <View style={styles.faixaWrap}>
          <View style={styles.faixaA} />
          <View style={styles.faixaB} />
        </View>

        {/* Bloco do evento — destaque inicial com o "o quê/onde/quando" */}
        <View style={styles.blocoEvento}>
          <Text style={styles.eventoTitulo}>
            {orcamento.titulo || "Sem título"}
          </Text>
          {orcamento.localEvento && (
            <LinhaEvento label="Local" valor={orcamento.localEvento} />
          )}
          {orcamento.dataInicio && (
            <LinhaEvento label="Início" valor={formatarData(orcamento.dataInicio)} />
          )}
          {orcamento.dataTermino && (
            <LinhaEvento label="Término" valor={formatarData(orcamento.dataTermino)} />
          )}
          {orcamento.duracaoEvento != null && (
            <LinhaEvento label="Duração" valor={formatarDuracao(orcamento.duracaoEvento)} />
          )}
        </View>

        {/* Serviços — oculto se não houver nenhum */}
        {linhasServicos.length > 0 && (
          <>
            <Text style={styles.secaoTitulo}>Serviços</Text>
            <Tabela
              colunas={[
                { titulo: "Serviço",   flex: 3, align: "left"   },
                { titulo: "Horas",     flex: 1, align: "center" },
                { titulo: "Valor/h",   flex: 1, align: "right"  },
                { titulo: "Subtotal",  flex: 1, align: "right"  },
              ]}
              linhas={linhasServicos}
            />
          </>
        )}

        {/* Equipamentos — oculto se não houver nenhum */}
        {linhasEquipamentos.length > 0 && (
          <>
            <Text style={styles.secaoTitulo}>Equipamentos</Text>
            <Tabela
              colunas={[
                { titulo: "Qtd",            flex: 0.6, align: "center" },
                { titulo: "Equipamento",    flex: 3,   align: "left"   },
                { titulo: "Valor unitário", flex: 1,   align: "right"  },
                { titulo: "Subtotal",       flex: 1,   align: "right"  },
              ]}
              linhas={linhasEquipamentos}
            />
          </>
        )}

        {/* Profissionais — texto corrido, ocultado quando vazio */}
        {nomesProfissionais && (
          <>
            <Text style={styles.secaoTitulo}>Profissionais</Text>
            <Text style={styles.profissionaisTexto}>{nomesProfissionais}</Text>
          </>
        )}

        {/* Valor total — caixa de destaque à direita */}
        <View style={styles.totalWrap}>
          <View style={styles.totalCaixa}>
            <Text style={styles.totalLabel}>Valor total</Text>
            <Text style={styles.totalValor}>
              {formatarValor(orcamento.valorTotal)}
            </Text>
          </View>
        </View>

        {/* Observações — bloco opcional ao fim do documento */}
        {orcamento.observacoes && (
          <>
            <Text style={styles.secaoTitulo}>Observações</Text>
            <View style={styles.observacoesBloco}>
              <Text style={styles.observacoesTexto}>{orcamento.observacoes}</Text>
            </View>
          </>
        )}

        {/* Rodapé fixo — `fixed` repete em todas as páginas caso o conteúdo
            estoure A4 (ex.: orçamento com muitos equipamentos) */}
        <Text style={styles.rodape} fixed>
          Documento gerado em {dataGeracao} · Obsidiana
        </Text>
      </Page>
    </Document>
  );
}
