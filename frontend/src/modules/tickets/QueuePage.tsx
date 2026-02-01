import React, { useEffect } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useApiClient } from "../../api/client";
import { Link } from "react-router-dom";
import { TicketSummary } from "./types";
import { connectQueueWebsocket } from "../ws/queueSocket";

export const QueuePage: React.FC = () => {
  const api = useApiClient();
  const queryClient = useQueryClient();

  const { data } = useQuery({
    queryKey: ["queueTickets"],
    queryFn: async () => {
      const res = await api.get("/tickets", {
        params: { page: 0, size: 50, status: "OPEN" }
      });
      return res.data.content as TicketSummary[];
    }
  });

  useEffect(() => {
    const disconnect = connectQueueWebsocket(() => {
      queryClient.invalidateQueries({ queryKey: ["queueTickets"] });
    });
    return () => disconnect();
  }, [queryClient]);

  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold mb-4">Tech Queue</h1>
      <div className="bg-white rounded shadow">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-2 text-left">ID</th>
              <th className="px-4 py-2 text-left">Title</th>
              <th className="px-4 py-2 text-left">Priority</th>
              <th className="px-4 py-2 text-left">Actions</th>
            </tr>
          </thead>
          <tbody>
            {data?.map((t) => (
              <tr key={t.id} className="border-t">
                <td className="px-4 py-2">
                  <Link to={`/tickets/${t.id}`} className="text-indigo-600 hover:underline">
                    #{t.id}
                  </Link>
                </td>
                <td className="px-4 py-2">{t.title}</td>
                <td className="px-4 py-2">{t.priority}</td>
                <td className="px-4 py-2">
                  <ClaimButton ticketId={t.id} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

const ClaimButton: React.FC<{ ticketId: number }> = ({ ticketId }) => {
  const api = useApiClient();
  const queryClient = useQueryClient();
  const handleClaim = async () => {
    await api.post(`/tickets/${ticketId}/claim`);
    queryClient.invalidateQueries({ queryKey: ["queueTickets"] });
  };
  return (
    <button
      onClick={handleClaim}
      className="bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700 text-xs"
    >
      Claim
    </button>
  );
};

