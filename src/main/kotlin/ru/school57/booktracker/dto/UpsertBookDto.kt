package ru.school57.booktracker.dto


data class UpsertBookDto(
    val title: String,
    // Название книги. Обязательное поле.

    val author: String,
    // Имя автора книги. Обязательное поле.

    val year: Int,
    // Год издания книги.

    val read: Boolean
    // Признак, прочитана ли книга (true или false)
)