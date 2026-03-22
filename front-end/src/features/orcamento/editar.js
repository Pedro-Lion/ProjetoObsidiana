async function editar() {
  const orcamentoCopia = { ...orcamento };
  async function tratarEvento() {
    if (!account) return;

    try {
      const response = await instance.acquireTokenSilent({
        ...loginRequest,
        account: account,
      });

      const accessToken = response.accessToken;

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

        orcamentoCopia.idCalendar = null;
        return;
      }

      if (orcamento.status != "Confirmado") return;

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

        orcamentoCopia.idCalendar = data.id;
      } else {
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
      }
    } catch (err) {
      console.error("Erro ao tratar evento no calendário:", err);
    }
  }
  await tratarEvento();

  try {
    // clona e formata listas many-to-many (servicos/profissionais/equipamentos)
    let orcamentoFormatado = { ...orcamentoCopia };
    const chaves = ["servicos", "equipamentos", "profissionais"];

    orcamentoFormatado.servicos = orcamentoFormatado.servicos
      ? obterListaIdsEquipamentos(orcamentoFormatado.servicos)
      : [];
    orcamentoFormatado.profissionais = orcamentoFormatado.profissionais
      ? obterListaIdsEquipamentos(orcamentoFormatado.profissionais)
      : [];
    orcamentoFormatado.usosEquipamentos = normalizarUsos(
      orcamentoFormatado.usosEquipamentos,
      orcamentoFormatado.equipamentos,
    );

    console.log(orcamentoFormatado.idCalendar);
    console.log("esse é o idCalendar: " + orcamentoFormatado.idCalendar);
    console.log(orcamentoFormatado);

    const request = await api.put(`/orcamento/${id}`, orcamentoFormatado, {
      headers: { Authorization: "Bearer " + sessionStorage.getItem("token") },
    });

    console.log(request);

    if (request.status == 200) {
      setModalTitulo("Sucesso!");
      setModalDescricao(
        "Editado com sucesso! Retornando à lista de orçamentos.",
      );
      setModalActions(
        <button
          className="bg-blue-500 text-white px-4 py-2 rounded"
          onClick={() => navigate("/orcamentos")}
        >
          Ok
        </button>,
      );
      setModalOpen(true);
    } else {
      setModalTitulo("Erro");
      setModalDescricao("Orçamento não pôde ser editado. Tente novamente.");
      setModalActions(
        <button
          className="bg-gray-300 px-4 py-2 rounded"
          onClick={() => setModalOpen(false)}
        >
          Fechar
        </button>,
      );
      setModalOpen(true);
    }
  } catch (error) {
    console.log(error);
    setModalTitulo("Erro");
    setModalDescricao("Erro ao editar orçamento.");
    setModalActions(
      <button
        className="bg-gray-300 px-4 py-2 rounded"
        onClick={() => setModalOpen(false)}
      >
        Fechar
      </button>,
    );
    setModalOpen(true);
  }
}
