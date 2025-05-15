package ru.school57.booktracker.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.school57.booktracker.entity.Book

interface BookRepository : JpaRepository<Book, Long> {
    fun findByRead(read: Boolean): List<Book>
    fun findById(id:Int): Book?
}