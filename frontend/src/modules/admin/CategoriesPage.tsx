import React from "react";
import { useQuery } from "@tanstack/react-query";
import { useApiClient } from "../../api/client";

export const CategoriesPage: React.FC = () => {
  const api = useApiClient();
  const { data } = useQuery({
    queryKey: ["adminCategories"],
    queryFn: async () => {
      const res = await api.get("/admin/categories");
      return res.data as Array<{ id: number; name: string; description?: string }>;
    }
  });

  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold mb-4">Categories</h1>
      <div className="bg-white rounded shadow">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-2 text-left">Name</th>
              <th className="px-4 py-2 text-left">Description</th>
            </tr>
          </thead>
          <tbody>
            {data?.map((c) => (
              <tr key={c.id} className="border-t">
                <td className="px-4 py-2">{c.name}</td>
                <td className="px-4 py-2">{c.description}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

