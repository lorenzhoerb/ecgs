export interface StompErrorDto {
    message: string;
    type: StompErrorType;
}

export enum StompErrorType {
    unauthorized = 'Unauthorized'
}
