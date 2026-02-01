import React from "react";
import { useQuery } from "@tanstack/react-query";
import { useApiClient } from "../../api/client";

export const ReportsPage: React.FC = () => {
  const api = useApiClient();
  const { data } = useQuery({
    queryKey: ["reportsSummary"],
    queryFn: async () => {
      const res = await api.get("/reports/summary");
      return res.data as { totalTickets: number; openTickets: number; resolvedTickets: number };
    }
  });

  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold mb-4">Reports</h1>
      {data ? (
        <div className="grid grid-cols-3 gap-4">
          <MetricCard label="Total Tickets" value={data.totalTickets} />
          <MetricCard label="Open Tickets" value={data.openTickets} />
          <MetricCard label="Resolved Tickets" value={data.resolvedTickets} />
        </div>
      ) : (
        <div>Loading...</div>
      )}
    </div>
  );
};

const MetricCard: React.FC<{ label: string; value: number }> = ({ label, value }) => (
  <div className="bg-white rounded shadow p-4">
    <div className="text-sm text-gray-500">{label}</div>
    <div className="text-2xl font-semibold">{value}</div>
  </div>
);

