import { formatarOrcamento } from "../utils.js"
import { api } from "../../../api.js"

export async function cadastrar(orcamento = {}) {
  const orcamentoFormatado = formatarOrcamento(orcamento);

  try {
    const request = await api.post("/orcamento", orcamentoFormatado, {
      headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
    });
    return request.status == 201

  } catch (error) {
    console.log(error);
  }

  return false;
}