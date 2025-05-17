package ru.school57.booktracker.error

import jakarta.persistence.EntityNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
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

        val error = ErrorDto(code = "NOT_FOUND", message = ex.message ?: "Entity not found")
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorDto> {

        logger.warn("Validation failed: ${ex.message}")

        val message = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Validation error"
        val error = ErrorDto(code = "VALIDATION_FAILED", message = message)
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }


    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<ErrorDto> {

        logger.error("Unexpected error occurred", ex)

        val error = ErrorDto(code = "INTERNAL_ERROR", message = "An unexpected error occurred")
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
