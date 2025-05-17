package ru.school57.booktracker.service

import io.mockk.*
import io.mockk.junit5.MockKExtension
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.InjectMockKs
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.school57.booktracker.dto.BookDto
import ru.school57.booktracker.entity.Book
import ru.school57.booktracker.repository.BookRepository
import java.util.*

@ExtendWith(MockKExtension::class)
class BookServiceUnitTest {


    @MockK
    lateinit var bookRepository: BookRepository


    @InjectMockKs
    lateinit var bookService: BookService

    @BeforeEach
    fun setUp() {
    }


    @Test
    fun testCreateBook() {
        val dto = BookDto("Test Title", "Test Author", 2020, false)
        val saved = Book(1L, "Test Title", "Test Author", 2020, false)

        every { bookRepository.save(any()) } returns saved

        val result = bookService.create(dto)


        assertEquals("Test Title", result.title)
        verify(exactly = 1) { bookRepository.save(match { it.title == "Test Title" }) }
    }


    @Test
    fun testGetById() {
        val book = Book(1L, "Title", "Author", 1999, true)

        every { bookRepository.findById(1L) } returns Optional.of(book)

        val dto = bookService.getById(1L)

        assertEquals("Title", dto.title)
        verify(exactly = 1) { bookRepository.findById(1L) }
    }

    @Test
    fun testUpdateBook() {
        val existing = Book(1L, "Old", "Author", 1990, false)
        val dto = BookDto( "New Title", "Author", 2022, true)
        val updated = Book(1L, "New Title", "Author", 2022, true)

        every { bookRepository.findById(1L) } returns Optional.of(existing)
        every { bookRepository.save(any()) } returns updated

        val result = bookService.update(1L, dto)

        assertEquals("New Title", result.title)
        assertTrue(result.read)
        verify(exactly = 1) { bookRepository.findById(1L) }
        verify(exactly = 1) { bookRepository.save(match { it.title == "New Title" && it.read }) }
    }


    @Test
    fun testDeleteBook() {
        every { bookRepository.existsById(1L) } returns true
        every { bookRepository.deleteById(1L) } just Runs

        bookService.delete(1L)

        verify(exactly = 1) { bookRepository.existsById(1L) }
        verify(exactly = 1) { bookRepository.deleteById(1L) }
    }

    @Test
    fun testDeleteBookNotFoundThrows() {
        every { bookRepository.existsById(1L) } returns false

        val ex = assertThrows(EntityNotFoundException::class.java) {
            bookService.delete(1L)
        }
        assertEquals("Book with ID 1 not found", ex.message)
        verify(exactly = 1) { bookRepository.existsById(1L) }
        verify(exactly = 0) { bookRepository.deleteById(any()) }
    }


    @Test
    fun testListBooks() {
        val booksRead = listOf(
            Book(1L, "Read Book 1", "Author", 2000, true),
            Book(2L, "Read Book 2", "Author", 2001, true)
        )
        val booksAll = listOf(
            Book(1L, "Read Book 1", "Author", 2000, true),
            Book(2L, "Unread Book", "Author", 2002, false)
        )

        every { bookRepository.findByRead(true) } returns booksRead
        every { bookRepository.findAll() } returns booksAll

        val readList = bookService.list(true)
        val allList = bookService.list(null)

        assertEquals(2, readList.size)
        assertTrue(readList.all { it.read })

        assertEquals(2, allList.size)

        verify(exactly = 1) { bookRepository.findByRead(true) }
        verify(exactly = 1) { bookRepository.findAll() }
    }


    @Test
    fun testGetNotFound() {
        every { bookRepository.findById(999L) } returns Optional.empty()

        val ex = assertThrows(EntityNotFoundException::class.java) {
            bookService.getById(999L)
        }
        assertEquals("Book with ID 999 not found", ex.message)
        verify(exactly = 1) { bookRepository.findById(999L) }
    }
}
