import React from "react";
import { useQuery } from "@tanstack/react-query";
import { useApiClient } from "../../api/client";

export const SlaPage: React.FC = () => {
  const api = useApiClient();
  const { data } = useQuery({
    queryKey: ["adminSla"],
    queryFn: async () => {
      const res = await api.get("/admin/sla");
      return res.data as Array<{ id: number; name: string; targetResolutionHours: number }>;
    }
  });

  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold mb-4">SLA Policies</h1>
      <div className="bg-white rounded shadow">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-2 text-left">Name</th>
              <th className="px-4 py-2 text-left">Target Resolution (h)</th>
            </tr>
          </thead>
          <tbody>
            {data?.map((s) => (
              <tr key={s.id} className="border-t">
                <td className="px-4 py-2">{s.name}</td>
                <td className="px-4 py-2">{s.targetResolutionHours}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

