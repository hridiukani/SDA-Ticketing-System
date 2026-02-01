import { Client } from "@stomp/stompjs";

const wsUrl = import.meta.env.VITE_WEBSOCKET_URL ?? "ws://localhost:8080/ws";

export const connectQueueWebsocket = (onUpdate: () => void) => {
  const client = new Client({
    brokerURL: wsUrl,
    reconnectDelay: 5000
  });

  client.onConnect = () => {
    client.subscribe("/topic/queue-updates", () => {
      onUpdate();
    });
  };

  client.activate();

  return () => client.deactivate();
};

