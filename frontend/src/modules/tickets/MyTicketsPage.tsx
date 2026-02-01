import React from "react";
import { useQuery } from "@tanstack/react-query";
import { useApiClient } from "../../api/client";
import { Link } from "react-router-dom";
import { TicketSummary } from "./types";

export const MyTicketsPage: React.FC = () => {
  const api = useApiClient();
  const { data } = useQuery({
    queryKey: ["myTickets"],
    queryFn: async () => {
      const res = await api.get("/tickets", { params: { page: 0, size: 50 } });
      return res.data.content as TicketSummary[];
    }
  });

  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold mb-4">My Tickets</h1>
      <div className="bg-white rounded shadow">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-2 text-left">ID</th>
              <th className="px-4 py-2 text-left">Title</th>
              <th className="px-4 py-2 text-left">Status</th>
              <th className="px-4 py-2 text-left">Priority</th>
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
                <td className="px-4 py-2">{t.status}</td>
                <td className="px-4 py-2">{t.priority}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

