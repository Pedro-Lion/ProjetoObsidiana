import { loginRequest } from "../../../authConfig";

import { getAccessToken } from "../../../../utils/getAccessToken";

export async function tratarEventoCalendario(orcamento = {}, instance = {}) {
  const account = instance.getActiveAccount();
  if (!account) return orcamento;

  try {
    const accessToken = await getAccessToken(instance, account);


    // se o orçamento já foi registrado no calendário,
    // mas agora o status não é mais confirmado,
    // então tem que deletar o evento
    if (orcamento.status != "Confirmado" && orcamento.idCalendar) {
      await fetch(
        `https://graph.microsoft.com/v1.0/me/calendar/events/${orcamento.idCalendar}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        },
      );

      orcamento.idCalendar = null;
      return orcamento;
    }

    const event = {
      subject: orcamento.descricao || "Evento sem título",
      start: {
        dateTime: orcamento.dataInicio,
        timeZone: "America/Sao_Paulo",
      },
      end: {
        dateTime: orcamento.dataTermino,
        timeZone: "America/Sao_Paulo",
      },
      location: { displayName: orcamento.localEvento || "" },
    };

    // se não tem idCalendar, criar um evento no calendário
    if (!orcamento.idCalendar) {
      const req = await fetch("https://graph.microsoft.com/v1.0/me/events", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(event),
      });

      const data = await req.json();

      orcamento.idCalendar = data.id;
      return orcamento;
    }

    // se tem um idCalendar, é pra editar o evento existente
    await fetch(
      `https://graph.microsoft.com/v1.0/me/calendar/events/${orcamento.idCalendar}`,
      {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(event),
      },
    );

  } catch (err) {
    console.error("Erro ao tratar evento no calendário:", err);
  }
}
