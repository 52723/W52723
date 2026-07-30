package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.MovieItem
import com.example.ui.MainViewModel
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun MovieNotesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val movies by viewModel.movies.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { SubPageTopBar(title = "观影手记 🎬", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "记观影")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies, key = { it.id }) { movie ->
                MovieCard(
                    movie = movie,
                    onDelete = { viewModel.deleteMovie(movie.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddMovieDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, mediaType, platform, rating, tags, review, quote, rewatch, mood, date ->
                viewModel.addMovie(title, mediaType, platform, rating, tags, review, quote, rewatch, mood, date)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MovieCard(
    movie: MovieItem,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = movie.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(text = movie.mediaType)
                }

                StarRatingBar(rating = movie.rating)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "平台: ${movie.platform} | 观影日期: ${movie.viewDate} | 心情: ${movie.mood}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "一句话影评: \"${movie.shortReview}\"",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (movie.memorableQuote.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = "Quote",
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "经典台词: ${movie.memorableQuote}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(
                            text = "复刷指数: ${movie.rewatchLevel}",
                            backgroundColor = Color(0xFFF3E5F5),
                            textColor = Color(0xFF7B1FA2)
                        )

                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddMovieDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int, String, String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var mediaType by remember { mutableStateOf("电影") }
    var platform by remember { mutableStateOf("Bilibili") }
    var rating by remember { mutableStateOf(5) }
    var tags by remember { mutableStateOf("治愈, 美食") }
    var review by remember { mutableStateOf("") }
    var quote by remember { mutableStateOf("") }
    var rewatch by remember { mutableStateOf("值得二刷") }
    var mood by remember { mutableStateOf("轻松") }
    var dateString by remember { mutableStateOf("2026-07-30") }

    val mediaTypes = listOf("电影", "纪录片", "短片", "综艺", "剧集")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录观影手记", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("影视名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("影视类型", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    mediaTypes.take(3).forEach { m ->
                        FilterChip(
                            selected = mediaType == m,
                            onClick = { mediaType = m },
                            label = { Text(m, fontSize = 11.sp) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = platform,
                        onValueChange = { platform = it },
                        label = { Text("观看平台") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        label = { Text("观看日期") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("评分 (1-5星)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                StarRatingBar(rating = rating, onRatingChange = { rating = it })

                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    label = { Text("一句话影评") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quote,
                    onValueChange = { quote = it },
                    label = { Text("印象深刻的台词 (选填)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, mediaType, platform, rating, tags, review, quote, rewatch, mood, dateString)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("保存观影")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
