package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch

@Composable
fun BookListScreen(navController: NavController, bookDao: BookDao) {
    val books = remember { mutableStateListOf<Book>() }
    val filteredBooks = remember { mutableStateListOf<Book>() }
    var searchQuery by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val allBooks = bookDao.getAllBooks()
            books.addAll(allBooks)
            filteredBooks.addAll(allBooks)
        }
    }

    LaunchedEffect(searchQuery) {
        filteredBooks.clear()
        filteredBooks.addAll(books.filter { it.title.contains(searchQuery, ignoreCase = true) })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск по названию книги") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        LazyColumn {
            items(filteredBooks) { book ->
                BookItem(book, onEdit = {
                    navController.navigate("edit/${book.id}")
                }, onDelete = {
                    scope.launch {
                        bookDao.delete(book)
                        books.remove(book)
                        filteredBooks.remove(book)
                    }
                })
            }
        }

        Button(
            onClick = { navController.navigate("add") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Добавить книгу")
        }
    }
}

@Composable
fun BookItem(book: Book, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(modifier = Modifier
            .background(Color(0xFFFFC0CB))
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onDelete()
                    }
                )
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val imageResId = book.coverImageResId.takeIf { it != 0 } ?: R.drawable.cover1
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = book.title,
                    modifier = Modifier.size(64.dp).padding(end = 8.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(book.title, style = MaterialTheme.typography.titleMedium)
                    Text("Год: ${book.year}", style = MaterialTheme.typography.bodySmall)
                    Text("Страниц: ${book.pages}", style = MaterialTheme.typography.bodySmall)
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}