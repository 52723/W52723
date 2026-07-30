package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.WardrobeItem
import com.example.ui.MainViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.ForestGreenPrimaryContainer

@Composable
fun WardrobeScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.wardrobeItems.collectAsState()
    var selectedCategory by remember { mutableStateOf("全部") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("全部", "上装", "下装", "外套", "鞋品", "配饰")

    val filteredList = remember(items, selectedCategory) {
        if (selectedCategory == "全部") items else items.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = { SubPageTopBar(title = "电子衣橱 👗", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "录入衣物")
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
            // Weather Outfit Recommendation Card
            item {
                OutfitRecommendationCard(items = items)
            }

            // Category filter row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreenPrimaryContainer,
                                selectedLabelColor = ForestGreenPrimary
                            )
                        )
                    }
                }
            }

            item {
                SectionHeader(title = "我的衣橱单品 (${filteredList.size})")
            }

            items(filteredList, key = { it.id }) { wardrobe ->
                WardrobeCard(
                    item = wardrobe,
                    onDelete = { viewModel.deleteWardrobeItem(wardrobe.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddWardrobeDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, category, season, color, tags ->
                viewModel.addWardrobeItem(name, category, season, color, tags)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun OutfitRecommendationCard(items: List<WardrobeItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Weather",
                        tint = Color(0xFF0288D1)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "今日穿搭推荐 (晴 24°C)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF01579B)
                    )
                }

                StatusBadge(
                    text = "春夏清爽风",
                    backgroundColor = Color(0xFFB3E5FC),
                    textColor = Color(0xFF01579B)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val top = items.find { it.category == "上装" }?.name ?: "纯棉米白色短袖T恤"
            val bottom = items.find { it.category == "下装" }?.name ?: "高腰浅蓝色直筒牛仔裤"
            val shoes = items.find { it.category == "鞋品" }?.name ?: "复古德训帆布鞋"

            Text(
                text = "👕 建议搭配：$top + $bottom + $shoes",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0277BD),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun WardrobeCard(
    item: WardrobeItem,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ForestGreenPrimaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Checkroom,
                            contentDescription = item.category,
                            tint = ForestGreenPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusBadge(text = item.category)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "颜色: ${item.color} | 季节: ${item.season} | 标签: ${item.tagsText}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

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

@Composable
private fun AddWardrobeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("上装") }
    var season by remember { mutableStateOf("春夏") }
    var color by remember { mutableStateOf("米白色") }
    var tags by remember { mutableStateOf("百搭, 基础款") }

    val categories = listOf("上装", "下装", "外套", "鞋品", "配饰")
    val seasons = listOf("春夏", "秋冬", "四季")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("录入新衣物", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("衣物名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("品类分类", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.drop(3).forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Text("适用季节", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    seasons.forEach { s ->
                        FilterChip(
                            selected = season == s,
                            onClick = { season = s },
                            label = { Text(s, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("主色调 (如: 浅蓝色/米白)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("风格标签 (如: 显瘦/治愈/宽松)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, category, season, color, tags)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("保存衣物")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
