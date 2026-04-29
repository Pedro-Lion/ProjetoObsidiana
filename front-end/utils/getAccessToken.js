// Utilitário reutilizável — crie em um arquivo separado, ex: src/utils/getAccessToken.js
import { loginRequest } from '../src/authConfig';
import { InteractionRequiredAuthError } from '@azure/msal-browser';

export async function getAccessToken(instance, account) {
    try {
        const response = await instance.acquireTokenSilent({
            ...loginRequest,
            account: account,
        });
        return response.accessToken;
    } catch (error) {
        // Se o token silencioso falhar (expirado, permissão pendente, etc.),
        // redireciona para login interativo pedindo consentimento novamente
        if (error instanceof InteractionRequiredAuthError) {
            await instance.acquireTokenRedirect(loginRequest);
        }
        throw error;
    }
}