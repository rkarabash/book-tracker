package ru.school57.booktracker.service

import org.springframework.stereotype.Service
import org.springframework.data.jpa.repository.JpaRepository
import ru.school57.booktracker.entity.Book
import ru.school57.booktracker.repository.BookRepository
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import ru.school57.booktracker.dto.BookDto

@Service
class BookService(
    private val bookRepository: BookRepository
) {
    private val log = LoggerFactory.getLogger(BookService::class.java)

    fun create(bookDto: BookDto): BookDto {
        log.debug("Creating a new book with data: $bookDto")

        val book = bookDto.toEntity()
        val savedBook = bookRepository.save(book)

        log.info("Created new book with ID: ${savedBook.id}")
        return BookDto.fromEntity(savedBook)
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
        return BookDto.fromEntity(book)
    }

    fun update(id: Long, dto: BookDto): BookDto {
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

        return BookDto.fromEntity(savedBook)
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

    fun list(read: Boolean?): List<BookDto> {
        log.debug("Fetching books with read status: $read")

        val books = when (read) {
            null -> bookRepository.findAll()
            else -> bookRepository.findByRead(read)
        }

        log.info("Fetched ${books.size} books")
        return books.map {BookDto.fromEntity(it)}
    }
}