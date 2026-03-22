import { formatarOrcamento } from "../utils";
import { tratarEventoCalendario } from "./tratarEventoCalendario";
import { api } from "../../../api";

export async function editar(orcamento = {}, instance = {}) {
  orcamento = formatarOrcamento(orcamento);

  // se o orçamento foi confirmado,
  // ou se ele tem um registro no calendário, precisa fazer o tratamento do evento
  if (orcamento.status == "Confirmado" || orcamento.idCalendar) {
    orcamento = await tratarEventoCalendario(orcamento, instance);
  }

  try {
    const request = await api.put(`/orcamento/${orcamento.id}`, orcamento, {
      headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
    });
    return request.status == 200
  } catch (error) {
    console.log(error);
    return false;
  }
}
