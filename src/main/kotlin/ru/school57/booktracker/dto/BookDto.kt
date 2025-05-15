package ru.school57.booktracker.dto

import ru.school57.booktracker.entity.Book

data class BookDto(
    val title: String,
    val author: String,
    val year: Int,
    val read: Boolean
) {

    fun toEntity(): Book = Book(title=title, author=author, year=year, read=read)
    companion object {
        fun fromEntity(book: Book): BookDto = BookDto(book.title, book.author, book.year, book.read)
    }
}