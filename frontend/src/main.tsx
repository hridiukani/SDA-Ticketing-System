import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { AuthProvider, useAuth } from "./modules/auth/AuthContext";
import { LoginPage } from "./modules/auth/LoginPage";
import { NewTicketPage } from "./modules/tickets/NewTicketPage";
import { MyTicketsPage } from "./modules/tickets/MyTicketsPage";
import { QueuePage } from "./modules/tickets/QueuePage";
import { TicketDetailPage } from "./modules/tickets/TicketDetailPage";
import { UsersPage } from "./modules/admin/UsersPage";
import { CategoriesPage } from "./modules/admin/CategoriesPage";
import { SlaPage } from "./modules/admin/SlaPage";
import { ReportsPage } from "./modules/reports/ReportsPage";
import "./index.css";

const queryClient = new QueryClient();

const PrivateRoute: React.FC<{ children: React.ReactElement }> = ({ children }) => {
  const { isAuthenticated } = useAuth();
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

const AppRoutes = () => (
  <Routes>
    <Route path="/login" element={<LoginPage />} />
    <Route
      path="/tickets/new"
      element={
        <PrivateRoute>
          <NewTicketPage />
        </PrivateRoute>
      }
    />
    <Route
      path="/tickets"
      element={
        <PrivateRoute>
          <MyTicketsPage />
        </PrivateRoute>
      }
    />
    <Route
      path="/queue"
      element={
        <PrivateRoute>
          <QueuePage />
        </PrivateRoute>
      }
    />
    <Route
      path="/tickets/:id"
      element={
        <PrivateRoute>
          <TicketDetailPage />
        </PrivateRoute>
      }
    />
    <Route
      path="/admin/users"
      element={
        <PrivateRoute>
          <UsersPage />
        </PrivateRoute>
      }
    />
    <Route
      path="/admin/categories"
      element={
        <PrivateRoute>
          <CategoriesPage />
        </PrivateRoute>
      }
    />
    <Route
      path="/admin/sla"
      element={
        <PrivateRoute>
          <SlaPage />
        </PrivateRoute>
      }
    />
    <Route
      path="/reports"
      element={
        <PrivateRoute>
          <ReportsPage />
        </PrivateRoute>
      }
    />
    <Route path="*" element={<Navigate to="/tickets" replace />} />
  </Routes>
);

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <BrowserRouter>
          <AppRoutes />
        </BrowserRouter>
      </AuthProvider>
    </QueryClientProvider>
  </React.StrictMode>
);

