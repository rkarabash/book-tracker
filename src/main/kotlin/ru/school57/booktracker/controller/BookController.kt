package ru.school57.booktracker.controller

import org.springframework.web.bind.annotation.*
import ru.school57.booktracker.dto.BookDto
import ru.school57.booktracker.service.BookService

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun create(@RequestBody dto: BookDto): BookDto {
        return bookService.create(dto)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): BookDto {
        return bookService.getById(id)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody dto: BookDto
    ): BookDto {
        return bookService.update(id, dto)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) {
        bookService.delete(id)
    }

    @GetMapping
    fun list(@RequestParam read: Boolean?): List<BookDto> {
        return bookService.list(read)
    }
}