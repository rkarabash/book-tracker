package ru.school57.booktracker.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.school57.booktracker.dto.BookDto
import ru.school57.booktracker.entity.Book
import ru.school57.booktracker.repository.BookRepository
import jakarta.persistence.EntityNotFoundException

@SpringBootTest
class BookServiceIntegrationTest {

    @MockkBean
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var bookService: BookService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testCreateBook() {
        val bookDto = BookDto("Test Book", "Author", 2023, false)
        val savedBook = Book(1, "Test Book", "Author", 2023, false)

        every { bookRepository.save(any()) } returns savedBook

        val result = bookService.create(bookDto)

        assertEquals(1L, result.toEntity().id)
        verify { bookRepository.save(any()) }
    }

    @Test
    fun testGetBookById() {
        val book = Book(1, "Test Book", "Author", 2023, false)

        every { bookRepository.findById(1) } returns book

        val result = bookService.getById(1)

        assertEquals("Test Book", result.title)
        verify { bookRepository.findById(1) }
    }

    @Test
    fun testUpdateBook() {
        val existingBook = Book(1, "Old Title", "Author", 2020, false)
        val updatedBook = Book(1, "New Title", "Author", 2020, true)
        val bookDto = BookDto("New Title", "Author", 2020, true)

        every { bookRepository.findById(1) } returns existingBook
        every { bookRepository.save(any()) } returns updatedBook

        val result = bookService.update(1, bookDto)

        assertEquals("New Title", result.title)
        assertTrue(result.read)
        verify {
            bookRepository.findById(1)
            bookRepository.save(any())
        }
    }

    @Test
    fun testDeleteBook() {
        every { bookRepository.existsById(1) } returns true
        every { bookRepository.deleteById(1) } returns Unit

        bookService.delete(1)

        verify {
            bookRepository.existsById(1)
            bookRepository.deleteById(1)
        }
    }

    @Test
    fun testFilterByRead() {
        val readBooks = listOf(Book(1, "Book 1", "Author", 2023, true))

        every { bookRepository.findByRead(true) } returns readBooks

        val result = bookService.list(true)

        assertEquals(1, result.size)
        assertTrue(result[0].read)
        verify { bookRepository.findByRead(true) }
    }

    @Test
    fun testGetNonExistentBookThrows() {
        every { bookRepository.findById(99) } returns null

        assertThrows<EntityNotFoundException> {
            bookService.getById(99)
        }

        verify { bookRepository.findById(99) }
    }
}