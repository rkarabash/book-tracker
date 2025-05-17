package ru.school57.booktracker.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.school57.booktracker.entity.Book
import java.util.*

interface BookRepository : JpaRepository<Book, Long> {
    fun findByRead(read: Boolean): List<Book>
    override fun findById(id: Long): Optional<Book>
}