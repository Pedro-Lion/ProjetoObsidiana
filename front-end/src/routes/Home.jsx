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
import DashboardKpi from "../components/Kpis/DashboardKpi";
import { useEffect, useState } from "react";
import { api } from "../api.js";
import  Calendar from '../components/CalendarAPI/Calendar';
import { MsalProvider, AuthenticatedTemplate, useMsal, UnauthenticatedTemplate } from '@azure/msal-react';
import { loginRequest } from '../authConfig';

export function Home() {

  const [kpis, setKpis] = useState({
    aprovados: 0,
    pendentes: 0,
    concluidos: 0
  });

  useEffect(() => {
    async function fetchKpis() {
      try {
        const response = await api.get("/orcamento/kpis", {
          headers: {
            Authorization: "Bearer " + sessionStorage.getItem("token"),
          },
        });

        setKpis(response.data);
        console.log(response.data);

      } catch (error) {
        console.error("Erro ao buscar KPIs:", error);
      }
    }

    fetchKpis();
  }, []);


  // API Outlook - Calendar 
    const { instance } = useMsal();
    const activeAccount = instance.getActiveAccount();

     const handleRedirect = () => {
        instance
            .loginRedirect({
                ...loginRequest,
                prompt: 'create',
            })
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


  return (
    <>
      <h1 className="">Boas-vindas!</h1>

      <DashboardKpi kpis={kpis} />

      <h3>Próximos eventos</h3>

      {/* border-1 border-indigo-200 */}
      <div className="h-full w-full overflow-auto  p-1.5"> 
        {/* <iframe src="https://outlook.office365.com/calendar/published/05caa2e2759545539d3d0baaf1160008@sptech.school/79f2802dcb82431ebc51f3fc61e6114a3537960576227375636/calendar.html"
          className="h-full w-full"></iframe> */}
      <AuthenticatedTemplate>
        {activeAccount ? ( 
        <>
        <Calendar getAccessToken={getAccessToken} />
        <BotaoSecundario variant="warning" onClick={handleLogoutRedirect}
          titulo="Sign out"
          className="w-60"
        />
        </>
        ) : null}
        </AuthenticatedTemplate>
        <UnauthenticatedTemplate>
          <div className="flex flex-col gap-4">
          <p className="text-xl">Conecte-se na sua conta Outlook para acessar o calendário.</p>

          <BotaoSecundario titulo = "Login Outlook" className="w-60" onClick={handleRedirect} variant="primary">
                  
                </BotaoSecundario>

        </div>
        </UnauthenticatedTemplate>
        
      
      </div>
      


    </>
  )
}