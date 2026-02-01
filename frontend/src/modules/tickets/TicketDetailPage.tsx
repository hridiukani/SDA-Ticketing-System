import React from "react";
import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { useApiClient } from "../../api/client";
import { TicketDetail } from "./types";

export const TicketDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const api = useApiClient();
  const { data } = useQuery({
    queryKey: ["ticket", id],
    queryFn: async () => {
      const res = await api.get(`/tickets/${id}`);
      return res.data as TicketDetail;
    }
  });

  if (!data) {
    return <div className="p-6">Loading...</div>;
  }

  return (
    <div className="p-6 space-y-4">
      <h1 className="text-xl font-semibold">
        #{data.id} - {data.title}
      </h1>
      <div className="bg-white rounded shadow p-4">
        <p className="mb-2">
          <span className="font-semibold">Status:</span> {data.status}
        </p>
        <p className="mb-2">
          <span className="font-semibold">Priority:</span> {data.priority}
        </p>
        <p className="mb-2">
          <span className="font-semibold">Category:</span> {data.categoryName ?? "-"}
        </p>
        <p className="mt-4 whitespace-pre-line">{data.description}</p>
      </div>
    </div>
  );
};

