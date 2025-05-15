package ru.school57.booktracker.error

import jakarta.persistence.EntityNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.school57.booktracker.dto.ErrorDto

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<ErrorDto> {
        logger.warn("Entity not found: ${ex.message}")
        return ResponseEntity.status(404).body(
            ErrorDto(code = "NOT_FOUND", message = ex.message ?: "Resource not found")
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorDto> {
        val errorMessage = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage
            ?: "Validation failed"
        logger.warn("Validation failed: $errorMessage")
        return ResponseEntity.status(400).body(
            ErrorDto(code = "VALIDATION_FAILED", message = errorMessage)
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<ErrorDto> {
        logger.error("Unexpected error occurred", ex)
        return ResponseEntity.status(500).body(
            ErrorDto(code = "INTERNAL_ERROR", message = "Internal server error")
        )
    }
}