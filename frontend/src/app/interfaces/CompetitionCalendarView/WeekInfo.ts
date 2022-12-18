export interface ExtendedWeekInfo extends WeekInfo{
    start: Date;
    end: Date;
};

export interface WeekInfo {
    weekNumber: number;
    year: number;
};
