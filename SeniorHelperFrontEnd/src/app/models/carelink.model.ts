export interface CareLinkModel {
  id: number;
  caregiverId: number;
  caregiverUsername: string;
  caregiverRole: string;
  seniorId: number;
  seniorUsername: string;
  seniorRole: string;
  connectedSince?: string; // ISO date string
}
