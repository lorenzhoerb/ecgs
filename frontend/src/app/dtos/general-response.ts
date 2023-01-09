export interface GeneralResponseDto {
    status: StatusText;
    message: string;
}

export type StatusText = 'OK' | 'FAIL';
