import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import { PublicClientApplication, EventType } from "@azure/msal-browser";
import { MsalProvider } from "@azure/msal-react";
import { msalConfig } from "./authConfig";

import { RouterProvider } from "react-router-dom";
import { router } from "./router.jsx";

// const pca = new PublicClientApplication(msalConfig);

// 1) Criar fora do React
const msalInstance = new PublicClientApplication(msalConfig);

// 2) Ajustar active account
if (!msalInstance.getActiveAccount() && msalInstance.getAllAccounts().length > 0) {
  msalInstance.setActiveAccount(msalInstance.getAllAccounts()[0]);
}

// 3) Registrar callbacks
msalInstance.addEventCallback((event) => {
  if (event.eventType === EventType.LOGIN_SUCCESS) {
    msalInstance.setActiveAccount(event.payload.account);
  }
});

// 4) Renderizar com provider
createRoot(document.getElementById("root")).render(
  <StrictMode>
    <MsalProvider instance={msalInstance}>
      <RouterProvider router={router} />
    </MsalProvider>
  </StrictMode>
);