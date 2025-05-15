package ru.school57.booktracker.entity

import jakarta.persistence.*

@Entity
data class Book (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false)
    val author: String,
    @Column(nullable = false)
    val year: Int,
    @Column(nullable = false)
    val read: Boolean
)