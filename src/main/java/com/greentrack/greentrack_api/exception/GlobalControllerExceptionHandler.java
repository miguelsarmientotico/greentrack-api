package com.greentrack.greentrack_api.exception;

import jakarta.servlet.http.HttpServletRequest; // <--- CAMBIO IMPORTANTE (Antes era reactive)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@RestControllerAdvice
class GlobalControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Log detallado
        if (auth != null) {
            LOG.warn("⛔ ACCESO DENEGADO en [{}]. Usuario: '{}', Roles: {}, Mensaje: {}", 
                request.getRequestURI(),
                auth.getName(), 
                auth.getAuthorities(), 
                ex.getMessage());
        } else {
             LOG.warn("⛔ ACCESO DENEGADO a usuario anónimo en [{}]", request.getRequestURI());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acceso denegado: No tienes los permisos necesarios (" + ex.getMessage() + ")");
    }

    // --- OTROS ERRORES ---

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public @ResponseBody HttpErrorInfo handleBadRequestExceptions(
            HttpServletRequest request, BadRequestException ex) { // <--- HttpServletRequest

        return createHttpErrorInfo(BAD_REQUEST, request, ex);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public @ResponseBody HttpErrorInfo handleNotFoundExceptions(
            HttpServletRequest request, NotFoundException ex) {

        return createHttpErrorInfo(NOT_FOUND, request, ex);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public @ResponseBody HttpErrorInfo handleInvalidInputException(
            HttpServletRequest request, InvalidInputException ex) {

        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }

    // Método auxiliar corregido para Servlet estándar
    private HttpErrorInfo createHttpErrorInfo(
            HttpStatus httpStatus, HttpServletRequest request, Exception ex) {

        // En MVC estándar se usa getRequestURI(), no getPath()
        final String path = request.getRequestURI(); 
        final String message = ex.getMessage();

        LOG.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
        return new HttpErrorInfo(httpStatus, path, message);
    }
}
