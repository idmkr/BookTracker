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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun AddBookScreen(navController: NavController, bookDao: BookDao) {
    var title by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var pages by remember { mutableStateOf("") }
    var readDate by remember { mutableStateOf("") }
    var coverImageResId by remember { mutableStateOf<Int?>(null) } // Храним ID ресурса изображения

    val scope = rememberCoroutineScope()

    // Массив всех изображений из drawable
    val imageResIds = listOf(
        R.drawable.cover1,
        R.drawable.cover2,
        R.drawable.cover3,
        R.drawable.cover4,
        R.drawable.cover5
    )

    // Оборачиваем все в LazyColumn для прокрутки
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            // Скругленные поля ввода
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

            // Отображаем выбранное изображение или текст "Выберите изображение"
            if (coverImageResId != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Выбранное изображение:", modifier = Modifier.padding(end = 8.dp))
                    Image(
                        painter = painterResource(id = coverImageResId!!),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                }
            } else {
                Text(
                    text = "Выберите изображение",
                    modifier = Modifier.padding(top = 16.dp)
                        .clickable { /* Показать доступные изображения */ },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Подзаголовок "Другие варианты"
            Text(
                text = "Другие варианты:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Отображаем доступные изображения
        items(imageResIds) { imageResId ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        coverImageResId = imageResId // Устанавливаем выбранное изображение
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
                        // Запускаем корутину для вставки книги
                        scope.launch {
                            bookDao.insert(Book(title = title, year = year.toInt(), pages = pages.toInt(), readDate = readDate, coverImageResId = coverImageResId ?: 0))
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}