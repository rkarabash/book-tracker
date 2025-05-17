package ru.school57.booktracker.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.school57.booktracker.dto.BookDto
import ru.school57.booktracker.entity.Book
import ru.school57.booktracker.repository.BookRepository
import java.util.*

@SpringBootTest
class BookServiceIntegrationTest {

    @MockkBean
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var bookService: BookService

    private val sampleBook = Book(id = 1, title = "Title", author = "Author", year = 2023, read = false)
    private val sampleBookDto = BookDto.fromEntity(sampleBook)

    @BeforeEach
    fun setUp() {
        // SpringMockK сам инициализирует моки, можно не делать ничего
    }

    @Test
    fun testCreateBook() {
        every { bookRepository.save(any<Book>()) } returns sampleBook

        val created = bookService.create(sampleBookDto)

        verify(exactly = 1) { bookRepository.save(any<Book>()) }

        assertEquals(sampleBook.title, created.title)
    }

    @Test
    fun testGetBookById() {
        every { bookRepository.findById(1) } returns Optional.of(sampleBook)

        val found = bookService.getById(1)

        verify(exactly = 1) { bookRepository.findById(1) }

        assertEquals(sampleBook.author, found.author)
    }

    @Test
    fun testUpdateBook() {
        val updatedDto = BookDto(title = "Updated", author = "Author", year = 2024, read = true)
        val updatedBook = sampleBook.copy(
            title = updatedDto.title,
            author = updatedDto.author,
            year = updatedDto.year,
            read = updatedDto.read
        )

        every { bookRepository.findById(1) } returns Optional.of(sampleBook)
        every { bookRepository.save(any<Book>()) } returns updatedBook

        val result = bookService.update(1, updatedDto)

        verify(exactly = 1) { bookRepository.findById(1) }
        verify(exactly = 1) { bookRepository.save(any<Book>()) }
        assertEquals(updatedDto.title, result.title)
        assertEquals(updatedDto.read, result.read)
    }

    @Test
    fun testDeleteBook() {
        every { bookRepository.existsById(1) } returns true
        every { bookRepository.deleteById(1) } just runs

        bookService.delete(1)

        verify(exactly = 1) { bookRepository.existsById(1) }
        verify(exactly = 1) { bookRepository.deleteById(1) }
    }

    @Test
    fun testFilterByRead() {
        val readBooks = listOf(sampleBook)
        every { bookRepository.findByRead(true) } returns readBooks

        val result = bookService.list(true)

        verify(exactly = 1) { bookRepository.findByRead(true) }
        assertEquals(1, result.size)
        assertEquals(sampleBook.title, result[0].title)
    }

    @Test
    fun testGetNonExistentBookThrows() {
        every { bookRepository.findById(999) } returns Optional.empty()

        val exception = assertThrows(EntityNotFoundException::class.java) {
            bookService.getById(999)
        }

        verify(exactly = 1) { bookRepository.findById(999) }
        assertEquals("Book with ID 999 not found", exception.message)
    }
}
