package ru.school57.booktracker.service

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import io.mockk.junit5.MockKExtension
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import ru.school57.booktracker.dto.BookDto
import ru.school57.booktracker.entity.Book
import ru.school57.booktracker.repository.BookRepository
import jakarta.persistence.EntityNotFoundException

@ExtendWith(MockKExtension::class)
class BookServiceUnitTest {

    @MockK
    lateinit var bookRepository: BookRepository

    @InjectMockKs
    lateinit var bookService: BookService

    @Test
    fun `testCreateBook should save book to repository`() {
        val bookDto = BookDto("Test Book", "Author", 2023, false)
        val savedBook = Book(1, "Test Book", "Author", 2023, false)

        every { bookRepository.save(any()) } returns savedBook

        val result = bookService.create(bookDto)

        result.title shouldBe "Test Book"
        verify { bookRepository.save(any()) }
    }

    @Test
    fun `testGetById should return book when exists`() {
        val book = Book(1, "Test Book", "Author", 2023, false)

        every { bookRepository.findById(1) } returns book

        val result = bookService.getById(1)

        result.title shouldBe "Test Book"
        verify { bookRepository.findById(1) }
    }

    @Test
    fun `testUpdateBook should update existing book`() {
        val existingBook = Book(1, "Old Title", "Author", 2020, false)
        val updatedBook = Book(1, "New Title", "Author", 2020, true)
        val bookDto = BookDto("New Title", "Author", 2020, true)

        every { bookRepository.findById(1) } returns existingBook
        every { bookRepository.save(any()) } returns updatedBook

        val result = bookService.update(1, bookDto)

        result.title shouldBe "New Title"
        result.read shouldBe true
        verify {
            bookRepository.findById(1)
            bookRepository.save(any())
        }
    }

    @Test
    fun `testDeleteBook should delete when book exists`() {
        every { bookRepository.existsById(1) } returns true
        every { bookRepository.deleteById(1) } returns Unit

        bookService.delete(1)

        verify {
            bookRepository.existsById(1)
            bookRepository.deleteById(1)
        }
    }

    @Test
    fun `testListBooks should filter by read status`() {
        val readBooks = listOf(Book(1, "Book 1", "Author", 2023, true))

        every { bookRepository.findByRead(true) } returns readBooks

        val result = bookService.list(true)

        result.size shouldBe 1
        result[0].read shouldBe true
        verify { bookRepository.findByRead(true) }
    }

    @Test
    fun `testGetNotFound should throw exception when book not exists`() {
        every { bookRepository.findById(99) } returns null

        assertThrows<EntityNotFoundException> {
            bookService.getById(99)
        }

        verify { bookRepository.findById(99) }
    }

    @Test
    fun `testDeleteBook should throw exception when book not exists`() {
        every { bookRepository.existsById(99) } returns false

        assertThrows<EntityNotFoundException> {
            bookService.delete(99)
        }

        verify(exactly = 0) { bookRepository.deleteById(any()) }
    }
}