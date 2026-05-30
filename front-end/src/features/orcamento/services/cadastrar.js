import { formatarOrcamento } from "../utils/utils.js"
import { api } from "../../../api.js"

export function cadastrar(orcamento = {}) {
  const orcamentoFormatado = formatarOrcamento(orcamento);

  return api.post("/orcamento", orcamentoFormatado, {
    headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
  });
}