package com.example.myapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<Book> // Добавлено suspend

    @Insert
    suspend fun insert(book: Book) // Добавлено suspend

    @Insert
    suspend fun insertAll(vararg books: Book) // Метод для вставки нескольких книг

    @Delete
    suspend fun delete(book: Book) // Добавлено suspend

    @Update
    suspend fun update(book: Book) // Добавлено suspend

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Long): Book // Добавлено suspend
}