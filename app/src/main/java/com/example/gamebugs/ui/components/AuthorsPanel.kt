package com.example.gamebugs.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AuthorsPanel() {
    val authors = listOf(
        Author(
            name = "Семён Сивов",
            photoResId = android.R.drawable.ic_menu_add,
            role = "Разработчик"
        ),
        Author(
            name = "Максим Иванов",
            photoResId = android.R.drawable.ic_menu_delete,
            role = "Разработчик"
        ),
        Author(
            name = "Павел Швецов",
            photoResId = android.R.drawable.ic_menu_edit,
            role = "Разработчик"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Команда разработчиков",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 24.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(authors) { author ->
                AuthorCardEnhanced(author = author)
            }
        }
    }
}

@Composable
fun AuthorCardEnhanced(author: Author) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватарка с обводкой
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = author.photoResId),
                    contentDescription = "Фото ${author.name}",
                    modifier = Modifier
                        .size(60.dp)
                )
            }

            // Информация об авторе
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = author.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!author.role.isNullOrEmpty()) {
                    Text(
                        text = author.role,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

data class Author(
    val name: String,
    val photoResId: Int,
    val role: String? = null
)