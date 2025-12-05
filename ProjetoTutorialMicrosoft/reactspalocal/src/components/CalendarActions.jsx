import { useMsal } from "@azure/msal-react";
import { loginRequest } from "../authConfig";
import { Button } from "react-bootstrap";

export const CalendarActions = () => {
    const { instance } = useMsal();
    const account = instance.getActiveAccount();

    const createEvent = async () => {
    if (!account) return;

    try {
        const response = await instance.acquireTokenSilent({
            ...loginRequest,
            account : account
        });

        const accessToken = response.accessToken;

        const event = {
            subject: 'Reunião de Teste',
            start: { dateTime: '2025-10-23T09:00:00', timeZone: 'America/Sao_Paulo' },
            end: { dateTime: '2025-10-23T10:00:00', timeZone: 'America/Sao_Paulo' },
            location: { displayName: 'Sala de Reuniões 1' },
            attendees: [{ emailAddress: { address: 'exemplo@dominio.com', name: 'Convidado' }, type: 'required' }]
        };

        await fetch('https://graph.microsoft.com/v1.0/me/events', {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${accessToken}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(event)
        });

        alert('Evento criado com sucesso!');
        window.location.reload();
        
    } catch (error) {
        console.error(error);
        alert('Erro ao criar evento');
    }
};

    return <Button onClick={createEvent}>Criar Evento</Button>;
};