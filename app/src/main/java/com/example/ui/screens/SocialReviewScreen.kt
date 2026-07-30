package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SocialLogItem
import com.example.ui.MainViewModel
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun SocialReviewScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val logs by viewModel.socialLogs.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { SubPageTopBar(title = "社交状态复盘 🔋", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "复盘社交")
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
            // Energy Header Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = "Battery",
                                tint = ForestGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "社交能耗电量总结",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "💡 适当的高质量社交带来温暖与启发，但也请坦然拥抱独处，随时为内心的能量充电。",
                            fontSize = 13.sp,
                            color = Color(0xFF00695C),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            items(logs, key = { it.id }) { log ->
                SocialLogCard(
                    log = log,
                    onDelete = { viewModel.deleteSocialLog(log.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddSocialLogDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { date, energyType, score, reviewTags, note ->
                viewModel.addSocialLog(date, energyType, score, reviewTags, note)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun SocialLogCard(
    log: SocialLogItem,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                        text = log.dateString,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(
                        text = log.energyType,
                        backgroundColor = Color(0xFFB2DFDB),
                        textColor = Color(0xFF004D40)
                    )
                }

                StarRatingBar(rating = log.score)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "复盘标签: ${log.reviewTags}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ForestGreenPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = log.note,
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

@Composable
private fun AddSocialLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String, String) -> Unit
) {
    var dateString by remember { mutableStateOf("2026-07-30") }
    var energyType by remember { mutableStateOf("舒适放松") }
    var score by remember { mutableStateOf(5) }
    var reviewTags by remember { mutableStateOf("氛围治愈, 聊得投机") }
    var note by remember { mutableStateOf("") }

    val energyTypes = listOf("极速充电", "舒适放松", "轻微消耗", "深度榨干")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("复盘今天社交感受", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = dateString,
                    onValueChange = { dateString = it },
                    label = { Text("日期") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("能量感受", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    energyTypes.take(2).forEach { et ->
                        FilterChip(
                            selected = energyType == et,
                            onClick = { energyType = et },
                            label = { Text(et, fontSize = 11.sp) }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    energyTypes.drop(2).forEach { et ->
                        FilterChip(
                            selected = energyType == et,
                            onClick = { energyType = et },
                            label = { Text(et, fontSize = 11.sp) }
                        )
                    }
                }

                Text("舒适指数 (1-5分)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                StarRatingBar(rating = score, onRatingChange = { score = it })

                OutlinedTextField(
                    value = reviewTags,
                    onValueChange = { reviewTags = it },
                    label = { Text("复盘标签 (如: 聊得投机/需要独处)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("社交心得/感受复盘") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(dateString, energyType, score, reviewTags, note)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("保存复盘")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
