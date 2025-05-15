package ru.school57.booktracker.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.verify
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import ru.school57.booktracker.dto.UpsertBookDto
import ru.school57.booktracker.entity.Book
import ru.school57.booktracker.repository.BookRepository
import java.util.*

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
        val bookDto = UpsertBookDto("Test Book", "Author", 2023, false)
        val savedBook = Book(1, "Test Book", "Author", 2023, false)

        every { bookRepository.save(any()) } returns savedBook

        val result = bookService.create(bookDto)

        assertEquals(1L, result.id)
        verify { bookRepository.save(any()) }
    }

    @Test
    fun testGetBookById() {
        val book = Book(1, "Test Book", "Author", 2023, false)

        every { bookRepository.findById(1) } returns Optional.of(book)

        val result = bookService.getById(1)

        assertEquals("Test Book", result.title)
        verify { bookRepository.findById(1) }
    }

    @Test
    fun testUpdateBook() {
        val existingBook = Book(1, "Old Title", "Author", 2020, false)
        val updatedBook = Book(1, "New Title", "Author", 2020, true)
        val bookDto = UpsertBookDto("New Title", "Author", 2020, true)

        every { bookRepository.findById(1) } returns Optional.of(existingBook)
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

        val pagination = Pageable.ofSize(20)

        every { bookRepository.findAllByRead(true, pagination) } returns readBooks

        val result = bookService.list(true, pagination)

        assertEquals(1, result.size)
        assertTrue(result[0].read)
        verify { bookRepository.findAllByRead(true, pagination) }
    }

    @Test
    fun testGetNonExistentBookThrows() {
        every { bookRepository.findById(99) } returns Optional.empty()

        assertThrows<EntityNotFoundException> {
            bookService.getById(99)
        }

        verify { bookRepository.findById(99) }
    }
}
