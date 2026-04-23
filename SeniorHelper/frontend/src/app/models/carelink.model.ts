export interface CareLinkModel {
  id: number;
  caregiverId: number;
  caregiverUsername: string;
  caregiverFirstName: string;
  caregiverLastName: string;
  caregiverRole: string;
  seniorId: number;
  seniorUsername: string;
  seniorRole: string;
  seniorFirstName: string;
  seniorLastName: string;
  connectedSince: string;
  status: 'PENDING' | 'ACCEPTED';
}