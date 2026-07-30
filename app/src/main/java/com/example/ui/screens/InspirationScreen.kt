package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.InspirationItem
import com.example.ui.MainViewModel
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.ForestGreenPrimaryContainer

@Composable
fun InspirationScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val inspirations by viewModel.inspirations.collectAsState()
    var selectedFilter by remember { mutableStateOf("全部") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filterOptions = listOf("全部", "🌐联网推荐", "5星高分", "孵化中", "已落地", "脑洞", "创作", "计划")

    val filteredList = remember(inspirations, selectedFilter) {
        when (selectedFilter) {
            "5星高分" -> inspirations.filter { it.stars == 5 }
            "孵化中" -> inspirations.filter { it.status == "孵化中" }
            "已落地" -> inspirations.filter { it.status == "已落地" }
            "脑洞" -> inspirations.filter { it.category == "脑洞" }
            "创作" -> inspirations.filter { it.category == "创作" }
            "计划" -> inspirations.filter { it.category == "计划" }
            else -> inspirations
        }
    }

    Scaffold(
        topBar = { SubPageTopBar(title = "灵感一现 💡", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "记灵感")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(text = filter, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForestGreenPrimaryContainer,
                            selectedLabelColor = ForestGreenPrimary
                        )
                    )
                }
            }

            if (selectedFilter == "🌐联网推荐") {
                OnlineInspirationSamplesSection(
                    onSaveSample = { title, content, category, emotion ->
                        viewModel.addInspiration(title, content, category, emotion, "孵化中", 5)
                    }
                )
            } else if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无相关灵感，点击右下角 + 随时捕捉大脑奇思妙想~",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        InspirationExpandableCard(
                            item = item,
                            onDelete = { viewModel.deleteInspiration(item.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddInspirationDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, content, category, emotion, status, stars ->
                viewModel.addInspiration(title, content, category, emotion, status, stars)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun InspirationExpandableCard(
    item: InspirationItem,
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
                    StatusBadge(text = item.category)
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(
                        text = item.emotion,
                        backgroundColor = Color(0xFFFFF3E0),
                        textColor = Color(0xFFE65100)
                    )
                }
                StarRatingBar(rating = item.stars)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (expanded) Int.MAX_VALUE else 2
            )

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "状态: ${item.status}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ForestGreenPrimary
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
private fun AddInspirationDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("脑洞") }
    var emotion by remember { mutableStateOf("平静") }
    var status by remember { mutableStateOf("孵化中") }
    var stars by remember { mutableStateOf(3) }

    val categories = listOf("脑洞", "线索", "创作", "计划", "调研")
    val emotions = listOf("平静", "激动", "沮丧", "兴奋")
    val statuses = listOf("孵化中", "已落地", "纯想想")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录新灵感", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("灵感标题") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("详细描述/文案") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Text("分类", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Text("心情", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    emotions.forEach { emo ->
                        FilterChip(
                            selected = emotion == emo,
                            onClick = { emotion = emo },
                            label = { Text(emo, fontSize = 11.sp) }
                        )
                    }
                }

                Text("孵化状态", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    statuses.forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st, fontSize = 11.sp) }
                        )
                    }
                }

                Text("星级评定", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                StarRatingBar(rating = stars, onRatingChange = { stars = it })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, content, category, emotion, status, stars)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
private fun OnlineInspirationSamplesSection(
    onSaveSample: (String, String, String, String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    val samples = listOf(
        OnlineSample(
            title = "🥗 蒜香黄油虾仁炒西兰花（减脂治愈食谱）",
            content = "高蛋白低碳水配方：准备虾仁150g、西兰花1小朵、大蒜3瓣。虾仁用黑胡椒与少许料酒腌制；西兰花焯水30秒；蒜末小火炒香后下虾仁炒至变色，最后加入西兰花与少许低钠酱油翻炒均匀。",
            category = "菜谱灵感",
            tag = "网络热门减脂"
        ),
        OnlineSample(
            title = "👗 莫兰迪豆沙绿衬衫 + 米白直筒裤（夏末秋初穿搭）",
            content = "温柔知性配色：低饱和度的豆沙绿棉麻衬衫，搭配高腰米白色垂坠感直筒裤，脚踩复古德训鞋。整体干净舒适，非常适合职场通勤与周末咖啡馆放松。",
            category = "穿搭美学",
            tag = "四季搭配"
        ),
        OnlineSample(
            title = "🍵 暑夏祛湿三豆饮（节气与生理期养生）",
            content = "将红小豆、黑豆、绿豆各20g洗净，加入适量清水与陈皮1小片，大火煮开后转小火慢炖30分钟。饮用温汤，有助于消除暑热湿气，调理气血不适。",
            category = "健康养生",
            tag = "网络养生指南"
        ),
        OnlineSample(
            title = "🌟 治愈系金句：允许自己停下来",
            content = "「生活不是严丝合缝的日程表，允许自己偶尔停下来看一看云朵，听一听雨声。充电也是前进非常重要的一部分。」",
            category = "治愈心语",
            tag = "网联每日能量"
        )
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🌐 互联网治愈灵感例讯库",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "基于全网热门生活的治愈例讯（菜谱、穿搭、养生、心理）。点击任意例讯右下角【一键存入】即可加入你的灵感宝库！",
                        fontSize = 12.sp,
                        color = Color(0xFF2E7D32),
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        items(samples) { sample ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F8E9)
                        ) {
                            Text(
                                text = sample.tag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Text(text = "网络例讯", fontSize = 11.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = sample.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = sample.content,
                        fontSize = 13.sp,
                        color = Color(0xFF555555),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                onSaveSample(sample.title, sample.content, sample.category, "激动")
                                android.widget.Toast.makeText(context, "已存入灵感库！", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32))
                        ) {
                            Text("一键存入灵感库", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private data class OnlineSample(
    val title: String,
    val content: String,
    val category: String,
    val tag: String
)
