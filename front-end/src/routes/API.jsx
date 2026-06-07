import { BotaoBordaGradiente } from "../components/Buttons/BotaoBordaGradiente";
import { InputBordaLabel } from "../components/Inputs/InputBordaLabel";
import { InputFundoCor } from "../components/Inputs/InputFundoCor";
import { InputFoto } from "../components/Inputs/InputFoto";
import { InputCheckbox } from "../components/Inputs/InputCheckbox";
import { BotaoPrimario } from "../components/Buttons/BotaoPrimario";
import { BotaoSecundario } from "../components/Buttons/BotaoSecundario";
import { ContainerSelectTags } from "../components/Containers/ContainerSelectTags";
import { ContainerListagem } from "../components/Containers/ContainerListagem";
import { TextareaBordaLabel } from "../components/Inputs/TextareaBordaLabel";
import { MsalProvider, AuthenticatedTemplate, useMsal, UnauthenticatedTemplate } from '@azure/msal-react';
import { loginRequest } from '../authConfig';
import { CalendarCreate } from '../components/APIOutlook/CalendarCreate';
import { CalendarDelete } from '../components/APIOutlook/CalendarDelete';
import { CalendarUpdate } from '../components/APIOutlook/CalendarUpdate';
import  Calendar from '../components/CalendarAPI/Calendar';
import { getAccessToken } from "../../utils/getAccessToken";



export function API() {
    const { instance } = useMsal();
    const activeAccount = instance.getActiveAccount();

    const handleRedirect = () => {
        instance
            // .loginRedirect({
            //     ...loginRequest,
            //     prompt: 'create',
            // })
            .loginRedirect(loginRequest)
            .catch((error) => console.log(error));
    };

     const handleLogoutRedirect = () => {
        instance.logoutRedirect().catch((error) => console.log(error));
    };

      const getAccessToken = async () => {
      const response = await instance.acquireTokenSilent({
        ...loginRequest,
        account: activeAccount
      });
      return response.accessToken;
    };

    function apagarEvento(id) {
      
    }

  return (
    <>
      <h1 className="">Bem Vinde - Entre com a sua conta Microsoft!</h1>

    
      <div className="flex gap-3.5">
        <BotaoSecundario
          titulo="Novo orçamento"
          className="w-60"
        />
        <BotaoSecundario
          titulo="Novo serviço"
          className="w-60"
        />
        <BotaoSecundario
          titulo="Novo equipamento"
          className="w-60"
        />
        <BotaoSecundario
          titulo="Novo profissional"
          className="w-60"
        />
      </div>

      <AuthenticatedTemplate>
         {activeAccount ? (
            <>
              <h3>Próximos eventos</h3>

              <div className="h-full w-full overflow-auto border-1 border-indigo-200 p-1.5">
                <iframe src="https://outlook.office365.com/calendar/published/05caa2e2759545539d3d0baaf1160008@sptech.school/79f2802dcb82431ebc51f3fc61e6114a3537960576227375636/calendar.html"
                  className="h-full w-full"></iframe>
              </div>
        {/* <button variant="warning" onClick={handleLogoutRedirect}>
             Sign out
        </button> */}
        <div className="flex gap-3.5">
        <CalendarCreate/>
        <CalendarUpdate/>
        <CalendarDelete/>
        <BotaoSecundario variant="warning" onClick={handleLogoutRedirect}
          titulo="Sign out"
          className="w-60"
        />
        </div>
        <Calendar getAccessToken={getAccessToken} />
        
        </>
        ) : null}
      </AuthenticatedTemplate>
      <UnauthenticatedTemplate>
        <div className="h-full w-full overflow-auto border-1 border-indigo-200 p-1.5">
                <BotaoSecundario className="w-60" onClick={handleRedirect} variant="primary">
                    Sign up
                </BotaoSecundario>
                </div>
        </UnauthenticatedTemplate>

    </> 
  )
}