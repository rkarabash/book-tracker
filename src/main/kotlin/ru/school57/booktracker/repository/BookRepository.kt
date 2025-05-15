package ru.school57.booktracker.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.school57.booktracker.entity.Book

interface BookRepository : JpaRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {
    fun findAllByRead(read: Boolean?, pageable: Pageable): List<Book>
}