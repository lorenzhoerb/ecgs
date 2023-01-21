export class GradingGroupDetail {
    name: string;
    description: string;
    formula: string;
    isPublic: boolean;
}

export interface GradingGroupWithCalculations {
    id?: number;
    name: string;
    idCount: number;

    formula: any;
    stationVariables?: any[];
    stations?: any[];

    description?: string;
    editable?: string;
    public?: boolean;
}

export interface SimpleGradingGroupViewEdit {
    id: number;
    name: string;
    editable: boolean;
    public: ViewEditGradingGroupSearchType;
}

export interface ViewEditGradingGroup {
    id: number;
    name: string;
    description: string;
    formula: string;
    isPublic: boolean;
    isTemplate?: boolean;
}

export enum ViewEditGradingGroupSearchType {
    all,
    public,
    private,
}

export interface ViewEditGradingGroupSearch {
    name: string;
    description: string;
    type: ViewEditGradingGroupSearchType;
    onlyEditables: boolean;
}

export interface ViewEditGradingGroupSearch {
    name: string;
    description: string;
    type: ViewEditGradingGroupSearchType;
    onlyEditables: boolean;
}
