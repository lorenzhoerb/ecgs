package at.ac.tuwien.sepm.groupphase.backend.endpoint.exceptionhandler;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BulkErrorListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ErrorListRestDto;

import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenListException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UnauthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationBulkException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.invoke.MethodHandles;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Register all your Java exceptions here to map them into meaningful HTTP exceptions
 * If you have special cases which are only important for specific endpoints, use ResponseStatusExceptions
 * https://www.baeldung.com/exception-handling-for-rest-with-spring#responsestatusexception
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Use the @ExceptionHandler annotation to write handler for custom exceptions.
     */
    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Use the @ExceptionHandler annotation to write handler for custom exceptions.
     */
    @ExceptionHandler(value = {UnauthorizedException.class})
    protected ResponseEntity<Object> handleUnauthorized(RuntimeException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * Override methods from ResponseEntityExceptionHandler to send a customized HTTP response for a know exception
     * from e.g. Spring
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        //Get all errors
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> err.getField() + " " + err.getDefaultMessage())
            .collect(Collectors.toList());

        body.put("Validation errors", errors);

        return new ResponseEntity<>(body.toString(), headers, status);
    }

    /**
     * Use the @ExceptionHandler annotation to write handler for custom exceptions.
     */
    @ExceptionHandler(value = {ValidationException.class, IllegalArgumentException.class})
    protected ResponseEntity<Object> handleValidation(RuntimeException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Use the @ExceptionHandler annotation to write handler for custom exceptions.
     */
    @ExceptionHandler(value = {ValidationBulkException.class})
    protected ResponseEntity<Object> handleBulkValidation(ValidationBulkException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        BulkErrorListDto bulkErrorListDto = new BulkErrorListDto(ex.getMessageSummary(), ex.getBulkErrors());
        return handleExceptionInternal(ex, bulkErrorListDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handle Security Exception.
     */
    @ExceptionHandler(value = {SecurityException.class})
    protected ResponseEntity<Object> handleSecurity(RuntimeException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }


    /**
     * Handles Validation Exception.
     */
    @ExceptionHandler(value = {ValidationListException.class})
    public ResponseEntity<Object> handleValidationException(ValidationListException e, WebRequest request) {
        LOGGER.warn("Terminating request processing with status 422 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
        ErrorListRestDto errorDto = new ErrorListRestDto(e.summary(), e.errors());
        return handleExceptionInternal(e, errorDto, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    /**
     * Handles Conflict Exception.
     */
    @ExceptionHandler(value = {ConflictException.class})
    public ResponseEntity<Object> handleConflictException(ConflictException e, WebRequest request) {
        LOGGER.warn("Terminating request processing with status 409 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
        ErrorListRestDto errorDto = new ErrorListRestDto(e.summary(), e.errors());
        return handleExceptionInternal(e, errorDto, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    /**
     * Handles Forbidden Exception.
     */
    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException e, WebRequest request) {
        LOGGER.info("Terminating request processing with status 403 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
        return handleExceptionInternal(e, new ErrorListRestDto("Forbidden",
            List.of(e.getMessage())), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    /**
     * Handles UsernameNotFound Exception.
     */
    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException e, WebRequest request) {
        LOGGER.info("Terminating request processing with status 401 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
        return handleExceptionInternal(e, new ErrorListRestDto("Unauthorized",
            List.of(e.getMessage())), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {ForbiddenListException.class})
    public ResponseEntity<Object> handleForbiddenListException(ForbiddenListException e, WebRequest request) {
        LOGGER.warn("Terminating request processing with status 403 due to {}: {}", e.getClass().getSimpleName(), e.getMessage());
        ErrorListRestDto errorDto = new ErrorListRestDto(e.summary(), e.errors());
        return handleExceptionInternal(e, errorDto, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
}
