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
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.RecipeItem
import com.example.ui.MainViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.ForestGreenPrimaryContainer

@Composable
fun RecipeScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val recipes by viewModel.recipes.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val dailyRec = recipes.find { it.isDailyRecommended } ?: recipes.firstOrNull()

    Scaffold(
        topBar = { SubPageTopBar(title = "私厨菜谱 🍳", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "新增菜谱")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Daily Recommended Recipe Card
            if (dailyRec != null) {
                item {
                    DailyRecipeBanner(recipe = dailyRec)
                }
            }

            item {
                SectionHeader(title = "我的烹饪菜谱库")
            }

            items(recipes, key = { it.id }) { recipe ->
                RecipeCardItem(
                    recipe = recipe,
                    onDelete = { viewModel.deleteRecipe(recipe.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddRecipeDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, steps, time, difficulty, level, tags, cost, isRec ->
                viewModel.addRecipe(name, steps, time, difficulty, level, tags, cost, isRec)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun DailyRecipeBanner(recipe: RecipeItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = "Daily",
                        tint = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "今日私厨推荐 🌟",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }

                StatusBadge(
                    text = recipe.levelCategory,
                    backgroundColor = Color(0xFFFFE0B2),
                    textColor = Color(0xFFE65100)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recipe.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "难度: ${recipe.difficulty} | 耗时: ${recipe.cookingTimeMinutes} 分钟 | 成本: ¥${recipe.costAmount}",
                fontSize = 12.sp,
                color = Color(0xFF616161)
            )

            Spacer(modifier = Modifier.height(6.dp))

            StarRatingBar(rating = recipe.rating)
        }
    }
}

@Composable
private fun RecipeCardItem(
    recipe: RecipeItem,
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
                        text = recipe.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(text = recipe.levelCategory)
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "难度: ${recipe.difficulty} • ${recipe.cookingTimeMinutes}分钟 • 预估成本: ¥${recipe.costAmount}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "标签: ${recipe.tagsText}",
                fontSize = 12.sp,
                color = ForestGreenPrimary
            )

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "烹饪步骤与心得:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = recipe.stepsText,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
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
private fun AddRecipeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String, String, String, Double, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var timeText by remember { mutableStateOf("30") }
    var difficulty by remember { mutableStateOf("日常") }
    var levelCategory by remember { mutableStateOf("招牌菜") }
    var tags by remember { mutableStateOf("香辣, 浓郁") }
    var costText by remember { mutableStateOf("30.0") }
    var isRecommended by remember { mutableStateOf(false) }

    val difficulties = listOf("新手", "进阶", "大厨")
    val levels = listOf("招牌菜", "日常菜", "快手菜", "甜点")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("录入菜谱", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("菜品名称 (如: 辣炒排骨)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    label = { Text("做法步骤说明") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = timeText,
                        onValueChange = { timeText = it },
                        label = { Text("耗时(分)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = costText,
                        onValueChange = { costText = it },
                        label = { Text("成本(元)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("烹饪难度", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    difficulties.forEach { diff ->
                        FilterChip(
                            selected = difficulty == diff,
                            onClick = { difficulty = diff },
                            label = { Text(diff, fontSize = 11.sp) }
                        )
                    }
                }

                Text("菜品分类", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    levels.forEach { lvl ->
                        FilterChip(
                            selected = levelCategory == lvl,
                            onClick = { levelCategory = lvl },
                            label = { Text(lvl, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("口味标签 (如: 辣/清淡)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isRecommended,
                        onCheckedChange = { isRecommended = it }
                    )
                    Text("设为今日推荐菜", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val time = timeText.toIntOrNull() ?: 30
                        val cost = costText.toDoubleOrNull() ?: 20.0
                        onConfirm(name, steps, time, difficulty, levelCategory, tags, cost, isRecommended)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("保存菜谱")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
