package ru.school57.booktracker.entity

import jakarta.persistence.*
import ru.school57.booktracker.dto.UpsertBookDto

@Entity
// Этот класс представляет таблицу книг в базе данных
data class Book (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    // Это первичный ключ. Нужно добавить аннотации @Id и @GeneratedValue

    @Column(nullable = false)
    val title: String,
    // Это название книги. Обязательное поле, нужно указать ограничение @Column(nullable = false)

    @Column(nullable = false)
    val author: String,
    // Это имя автора. Также обязательно для заполнения

    @Column(nullable = false)
    val year: Int,
    // Это год издания книги

    @Column(nullable = false)
    val read: Boolean
    // Показывает, прочитал ли пользователь книгу

)

fun UpsertBookDto.toEntity() = Book(
    null,
    title,
    author,
    year,
    read
)