import { useState, useEffect } from "react";

//importado no package.json
import FullCalendar from "@fullcalendar/react";
import { formatDate } from "@fullcalendar/core";
import Grid from "@mui/material/Unstable_Grid2";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import listPlugin from "@fullcalendar/list"; 
import {
  Box,
  List,
  ListItem,
  ListItemText,
  Typography,
  useTheme,
} from "@mui/material";

// import "@fullcalendar/core/index.css";
// import "@fullcalendar/daygrid/index.css";
// import "@fullcalendar/timegrid/index.css";
// import "@fullcalendar/list/index.css";

//Adequar ao nosso tema 
import Header from "./Header";
import { tokens } from "./Theme";

export default function Calendar({getAccessToken}) {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const [currentEvents, setCurrentEvents] = useState([]);

  // Teste do outro código com chamadas por mês a microsoft graph
  const [events, setEvents] = useState([]);
  async function loadCalendarRange(rangeInfo) {
  const token = await getAccessToken();

  const start = rangeInfo.start.toISOString();
  const end = rangeInfo.end.toISOString();

  const url = `https://graph.microsoft.com/v1.0/me/calendarView?startDateTime=${start}&endDateTime=${end}`;

  const response = await fetch(url, {
    headers: { Authorization: `Bearer ${token}` },
  });

  const data = await response.json();
  console.log(data)

  const mapped = data.value.map(ev => ({
    id: ev.id,
    title: ev.subject,
    start: ev.start.dateTime,
    end: ev.end.dateTime,
  }));

  setEvents(mapped);
  console.log(mapped)
}

  //Teste com código sugerido
  // const [events, setEvents] = useState([]);
  // async function loadEvents() {
    // const token = await getAccessToken();
    // console.log("Token recebido no Calendar.jsx: ", token);




    // const response = await fetch(
    //   "https://graph.microsoft.com/v1.0/me/events?$orderby=start/dateTime",
    //   {
    //     headers: { Authorization: `Bearer ${token}` }
    //   }
    // );

    // const data = await response.json();
    // console.log(data)


    // const mappedEvents = data.value.map(ev => ({
    //   id: ev.id,
    //   title: ev.subject || "(Sem título)",
    //   start: new Date(ev.start.dateTime),
    //   end: new Date(ev.end.dateTime),
    //   location: ev.location?.displayName || "",
    // }));
    
    // const formatted = data.value.map(ev => ({
    //   id: ev.id,
    //   title: ev.subject,
    //   start: ev.start.dateTime,
    //   end: ev.end?.dateTime,
    //   extendedProps: {
    //     outlook: ev
    //   }
    // }));

  //   setEvents(mappedEvents);
  // }

  //  useEffect(() => {
  //   loadEvents();
  // }, []);

  function onEventClick(info) {
    alert(`ID do evento clicado: ${info.event.id}`);
  }
  const handleDateClick = (selected) => {
    const title = prompt("Please enter a new title for your event");
    const calendarApi = selected.view.calendar;
    calendarApi.unselect();

    if (title) {
      calendarApi.addEvent({
        id: `${selected.dateStr}-${title}`,
        title,
        start: selected.startStr,
        end: selected.endStr,
        allDay: selected.allDay,
      });
    }
  };

  const handleEventClick = (selected) => {
    if (
      window.confirm(
        `Are you sure you want to delete the event '${selected.event.title}'`
      )
    ) {
      selected.event.remove();
    }
  };

  //  function onEventClick(info) {
  //   const eventId = info.event.id;

  //   alert(
  //     "ID do evento clicado: " + eventId +
  //     "\nAqui você pode abrir modal para atualizar ou deletar!"
  //   );
  // }

  return (
    <>
    <Box m="20px">
      <Header title="Calendar" subtitle="Full Calendar Interactive Page" />
      <Grid container spacing={2}>
        <Grid xs={12} md={4}>
          <Box
            backgroundColor={colors.primary[400]}
            p="15px"
            borderRadius="4px"
          >
            <Typography variant="h5">Events</Typography>
            <List>
              {events.map((event) => (
                <ListItem
                  key={event.id}
                  sx={{
                    backgroundColor: colors.greenAccent[500],
                    margin: "10px 0",
                    borderRadius: "2px",
                  }}
                >
                  <ListItemText
                    primary={event.title}
                    secondary={
                      <Typography>
                        {formatDate(event.start, {
                          year: "numeric",
                          month: "short",
                          day: "numeric",
                        })}
                      </Typography>
                    }
                  />
                </ListItem>
              ))}
            </List>
          </Box>
        </Grid>
        <Grid xs={12} md={8}>
          <Box ml="15px">
            <FullCalendar
              height="75vh"
              plugins={[
                dayGridPlugin,
                timeGridPlugin,
                interactionPlugin,
                listPlugin,
              ]}
              headerToolbar={{
                left: "prev,next today",
                center: "title",
                right: "dayGridMonth,timeGridWeek,timeGridDay,listMonth",
              }}
              initialView="dayGridMonth"
              editable={true}
              events={events}
              eventClick={onEventClick}
              datesSet={loadCalendarRange}
              selectable={true}
              selectMirror={true}
              dayMaxEvents={true}
              select={handleDateClick}
              // eventClick={handleEventClick}
              eventsSet={(events) => setCurrentEvents(events)}
              initialEvents={[
                {
                  id: "12315",
                  title: "All-day event",
                  date: "2022-09-14",
                },
                {
                  id: "5123",
                  title: "Timed event",
                  date: "2022-09-28",
                },
              ]}
            />
          </Box>
        </Grid>
      </Grid>
    </Box>
    <div className="p-4 bg-white rounded shadow">
      <FullCalendar
        plugins={[dayGridPlugin, interactionPlugin]}
        initialView="dayGridMonth"
        events={events}
        eventClick={onEventClick}
        datesSet={loadCalendarRange}
      />
    </div>
    </>
  );
  
};

