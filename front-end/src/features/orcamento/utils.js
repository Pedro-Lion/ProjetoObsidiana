export function formatarOrcamento(orcamento = {}) {
  const datasParaFormatar = ["dataInicio", "dataTermino"];
  datasParaFormatar.forEach((chave) => {
    orcamento[chave] = formatarDataParaEnvio(orcamento[chave])
  })

  // transformar as listas de objetos para listas de ids
  const nomesListasParaFormatar = ["servicos", "equipamentos", "profissionais"];
  nomesListasParaFormatar.forEach((chave) => {
    orcamento[chave] = listaObjetosParaNumeros(orcamento[chave])
  })

  orcamento.usosEquipamentos = normalizarUsos(
    orcamento.usosEquipamentos,
    orcamento.equipamentos,
  )

  return orcamento;
}

function formatarDataParaEnvio(data = "") {
  if (!data.includes("-03")) return data;
  return new Date(data).toISOString();
}

function listaObjetosParaNumeros(listaObjs = []) {
  if (!listaObjs) return [];
  return listaObjs
    .map((item) => (typeof item === "number" ? item : item?.id))
}

function equipamentosParaUsos(equipamentoIds) {
  const mapa = new Map();
  equipamentoIds.forEach((id) => {
    mapa.set(id, (mapa.get(id) || 0) + 1);
  });
  const usos = [];
  mapa.forEach((qtd, id) =>
    usos.push({ idEquipamento: id, quantidadeUsada: qtd }),
  );
  return usos;
}

function normalizarUsos(usosRaw, equipamentosFallback) {
  if (usosRaw && usosRaw.length > 0) {
    return usosRaw
      .map((u) => {
        if (u.idEquipamento)
          return {
            idEquipamento: u.idEquipamento,
            quantidadeUsada: u.quantidadeUsada ?? 1,
          };
        if (u.equipamento && (u.equipamento.id || u.equipamento.id === 0)) {
          return {
            idEquipamento: u.equipamento.id,
            quantidadeUsada: u.quantidadeUsada ?? 1,
          };
        }
        if (u.id)
          return {
            idEquipamento: u.id,
            quantidadeUsada: u.quantidadeUsada ?? 1,
          };
        return null;
      })
      .filter(Boolean);
  }
  const ids = listaObjetosParaNumeros(equipamentosFallback);
  return equipamentosParaUsos(ids);
}