export class SimpleGradingGroup {
  id: number;
  title: string;
  constraints?: RegisterConstraint[];
}

export class RegisterConstraint {
  description: string;
}
