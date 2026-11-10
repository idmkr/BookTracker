package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.ui.res.painterResource

@Composable
fun EditBookScreen(navController: NavController, bookId: Long, bookDao: BookDao) {
    val scope = rememberCoroutineScope()
    var book by remember { mutableStateOf<Book?>(null) }

    // Загружаем книгу асинхронно
    LaunchedEffect(bookId) {
        book = bookDao.getBookById(bookId)
    }

    // Если книга еще не загружена, показываем индикатор загрузки
    if (book == null) {
        CircularProgressIndicator()
        return
    }

    // Поля для редактирования
    var title by remember { mutableStateOf(book!!.title) }
    var year by remember { mutableStateOf(book!!.year.toString()) }
    var pages by remember { mutableStateOf(book!!.pages.toString()) }
    var readDate by remember { mutableStateOf(book!!.readDate ?: "") }
    var coverImageResId by remember { mutableStateOf(book!!.coverImageResId) }

    // Оборачиваем все в LazyColumn для прокрутки
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            // Округлые поля ввода
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название") },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Год") },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = pages,
                onValueChange = { pages = it },
                label = { Text("Количество страниц") },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = readDate,
                onValueChange = { readDate = it },
                label = { Text("Дата прочтения") },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Отображаем выбранное изображение
            coverImageResId?.let { imageResId ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Выбранное изображение:", modifier = Modifier.padding(end = 8.dp))
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            // Подзаголовок
            Text("Другие варианты:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        }

        // Отображаем доступные изображения
        items(listOf(R.drawable.cover1, R.drawable.cover2, R.drawable.cover3, R.drawable.cover4, R.drawable.cover5)) { imageResId ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        coverImageResId = imageResId
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).padding(end = 8.dp)
                )
            }
        }

        item {
            // Используем Box для выравнивания кнопки
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Button(
                    onClick = {
                        // Запускаем корутину для обновления книги
                        scope.launch {
                            bookDao.update(Book(id = bookId, title = title, year = year.toInt(), pages = pages.toInt(), readDate = readDate, coverImageResId = coverImageResId ?: 0))
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Сохранить изменения")
                }
            }
        }
    }
}