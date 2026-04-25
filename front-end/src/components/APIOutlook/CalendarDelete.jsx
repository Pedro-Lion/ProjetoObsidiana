import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../../authConfig";
// import { Button } from "react-bootstrap";
import { BotaoSecundario } from "../../components/Buttons/BotaoSecundario";

export const CalendarDelete = (id) => {
    const { instance } = useMsal();
    const account = instance.getActiveAccount();

    const deleteEvent = async () => {
    if (!account) return;

    try {
        const response = await instance.acquireTokenSilent({
            ...loginRequest,
            account : account
        });

        const accessToken = response.accessToken;

        const event = {
            subject: 'Reunião de Teste',
            start: { dateTime: '2025-10-28T09:00:00', timeZone: 'America/Sao_Paulo' },
            end: { dateTime: '2025-10-28T10:00:00', timeZone: 'America/Sao_Paulo' },
            location: { displayName: 'Sala de Reuniões 1' },
            attendees: [{ emailAddress: { address: 'exemplo@dominio.com', name: 'Convidado' }, type: 'required' }]
        };

        await fetch('https://graph.microsoft.com/v1.0/me/calendar/events/AAMkADA1Y2FhMmUyLTc1OTUtNDU1My05ZDNkLTBiYWFmMTE2MDAwOABGAAAAAABbTX6UNP7SSY2zSrEAvyQqBwAzq04nYS6EQaJaosKduAKoAAAAAAENAAAzq04nYS6EQaJaosKduAKoAAFE8c1eAAA=', {
            method: 'DELETE',
            headers: {
                Authorization: `Bearer ${accessToken}`,
                'Content-Type': 'application/json'
            }
        });

        alert('Evento deletado com sucesso!');
        window.location.reload();
        
    } catch (error) {
        console.error(error);
        alert('Erro ao deletar evento');
    }
};

    return <BotaoSecundario className="w-60" onClick={deleteEvent} titulo="Deletar Evento" />;
};