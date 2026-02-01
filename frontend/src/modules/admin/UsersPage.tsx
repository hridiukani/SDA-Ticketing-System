import React from "react";
import { useQuery } from "@tanstack/react-query";
import { useApiClient } from "../../api/client";

export const UsersPage: React.FC = () => {
  const api = useApiClient();
  const { data } = useQuery({
    queryKey: ["adminUsers"],
    queryFn: async () => {
      const res = await api.get("/admin/users");
      return res.data as Array<{ id: number; email: string; fullName: string; role: string }>;
    }
  });

  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold mb-4">Users</h1>
      <div className="bg-white rounded shadow">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-2 text-left">Email</th>
              <th className="px-4 py-2 text-left">Name</th>
              <th className="px-4 py-2 text-left">Role</th>
            </tr>
          </thead>
          <tbody>
            {data?.map((u) => (
              <tr key={u.id} className="border-t">
                <td className="px-4 py-2">{u.email}</td>
                <td className="px-4 py-2">{u.fullName}</td>
                <td className="px-4 py-2">{u.role}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

