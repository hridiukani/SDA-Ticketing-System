export type TicketStatus =
  | "OPEN"
  | "TRIAGED"
  | "IN_PROGRESS"
  | "WAITING_ON_USER"
  | "RESOLVED"
  | "CLOSED"
  | "REOPENED"
  | "CANCELLED";

export type Priority = "LOW" | "MEDIUM" | "HIGH" | "URGENT";

export interface TicketSummary {
  id: number;
  title: string;
  status: TicketStatus;
  priority: Priority;
  categoryName?: string;
  createdAt: string;
}

export interface TicketDetail extends TicketSummary {
  description: string;
}

