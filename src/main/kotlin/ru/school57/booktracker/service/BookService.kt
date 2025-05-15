package ru.school57.booktracker.service

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.school57.booktracker.dto.BookDto
import ru.school57.booktracker.dto.UpsertBookDto
import ru.school57.booktracker.dto.toDto
import ru.school57.booktracker.entity.toEntity
import ru.school57.booktracker.repository.BookRepository

@Service
class BookService(
    private val bookRepository: BookRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun create(bookDto: UpsertBookDto): BookDto {
        log.debug("Creating a new book with data: $bookDto")

        val book = bookDto.toEntity()
        val savedBook = bookRepository.save(book)

        log.info("Created new book with ID: ${savedBook.id}")
        return savedBook.toDto()
    }

    fun getById(id: Long): BookDto {
        log.debug("Fetching book with ID: $id")

        val book = bookRepository.findById(id)
            .orElseThrow {
                EntityNotFoundException("Book with ID $id not found").also {
                    log.error("Book with ID $id not found")
                }
            }

        log.info("Successfully fetched book with ID: $id")
        return book.toDto()
    }

    fun update(id: Long, dto: UpsertBookDto): BookDto {
        log.debug("Updating book with ID: $id, new data: $dto")

        val existingBook = bookRepository.findById(id)
            .orElseThrow {
                EntityNotFoundException("Book with ID $id not found").also {
                    log.error("Book with ID $id not found")
                }
            }

        val updatedBook = existingBook.copy(
            title = dto.title,
            author = dto.author,
            year = dto.year,
            read = dto.read
        )

        val savedBook = bookRepository.save(updatedBook)
        log.info("Updated book with ID: $id")

        return savedBook.toDto()
    }

    fun delete(id: Long) {
        log.debug("Deleting book with ID: $id")

        if (!bookRepository.existsById(id)) {
            throw EntityNotFoundException("Book with ID $id not found").also {
                log.error("Book with ID $id not found")
            }
        }

        bookRepository.deleteById(id)
        log.info("Deleted book with ID: $id")
    }

    fun list(read: Boolean?, pagination: Pageable): List<BookDto> {
        log.debug("Fetching books with read status: $read")

        val books = when (read) {
            null -> bookRepository.findAll(pagination)
            else -> bookRepository.findAllByRead(read, pagination)
        }

        log.info("Fetched ${pagination.pageSize} books")
        return books.map { it.toDto() }
    }
}