package ru.school57.booktracker.controller

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import ru.school57.booktracker.dto.UpsertBookDto
import ru.school57.booktracker.service.BookService

@RestController
@RequestMapping("/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun create(@RequestBody dto: UpsertBookDto) = bookService.create(dto)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = bookService.getById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: UpsertBookDto) = bookService.update(id, dto)


    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = bookService.delete(id)

    @GetMapping
    fun list(
        @RequestParam(value = "read")
        read: Boolean?,

        pageable: Pageable
    ) = bookService.list(read, pageable)
}
