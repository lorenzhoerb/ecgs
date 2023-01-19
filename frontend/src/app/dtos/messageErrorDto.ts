export class MessageErrorDto {
    uuid?: string;
    type: MessageErrorType;
    message: string;
}

export enum MessageErrorType {
    malformed = 'MALFORMED',
    badRequest = 'BAD_REQUEST',
    notFound = 'NOT_FOUND',
    notConnected = 'NOT_CONNECTED',
    unauthorized = 'UNAUTHORIZED',
    validation = 'VALIDATION',
    unknonwnServerError = 'UNKNOWN_SERVER_ERROR'
}
