import React, { createContext, useContext, useEffect, useState } from "react";
import jwtDecode from "jwt-decode";

type Role = "REQUESTER" | "TECHNICIAN" | "ADMIN";

interface JwtPayload {
  sub: string;
  role?: string;
  exp?: number;
}

interface AuthState {
  token: string | null;
  email: string | null;
  role: Role | null;
}

interface AuthContextValue extends AuthState {
  login: (token: string) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const STORAGE_KEY = "ticketing_jwt";

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [state, setState] = useState<AuthState>({ token: null, email: null, role: null });

  useEffect(() => {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      try {
        const payload = jwtDecode<JwtPayload>(stored);
        if (!payload.exp || payload.exp * 1000 > Date.now()) {
          setState({
            token: stored,
            email: payload.sub ?? null,
            role: payload.role ? (payload.role.replace("ROLE_", "") as Role) : null
          });
        } else {
          localStorage.removeItem(STORAGE_KEY);
        }
      } catch {
        localStorage.removeItem(STORAGE_KEY);
      }
    }
  }, []);

  const login = (token: string) => {
    localStorage.setItem(STORAGE_KEY, token);
    const payload = jwtDecode<JwtPayload>(token);
    setState({
      token,
      email: payload.sub ?? null,
      role: payload.role ? (payload.role.replace("ROLE_", "") as Role) : null
    });
  };

  const logout = () => {
    localStorage.removeItem(STORAGE_KEY);
    setState({ token: null, email: null, role: null });
  };

  return (
    <AuthContext.Provider
      value={{
        ...state,
        login,
        logout,
        isAuthenticated: !!state.token
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return ctx;
};

