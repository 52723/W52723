package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TravelItem
import com.example.ui.MainViewModel
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun TravelFootprintScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val travels by viewModel.travels.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val visitedCitiesCount = remember(travels) { travels.map { it.cityName }.distinct().size }
    val totalTripsCount = travels.size

    Scaffold(
        topBar = { SubPageTopBar(title = "沿途印记 ✈️", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "记旅行")
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
            // Footprint Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🗺️ 足迹城市", fontSize = 13.sp, color = Color(0xFF3F51B5))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$visitedCitiesCount 座", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                        }

                        VerticalDivider(
                            modifier = Modifier.height(36.dp),
                            color = Color(0xFFC5CAE9)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🧳 出行次数", fontSize = 13.sp, color = Color(0xFF3F51B5))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$totalTripsCount 次", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                        }
                    }
                }
            }

            items(travels, key = { it.id }) { travel ->
                TravelCard(
                    travel = travel,
                    onDelete = { viewModel.deleteTravel(travel.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddTravelDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { city, tripTitle, companions, transport, cost, experience, recommend, highlights, date ->
                viewModel.addTravel(city, tripTitle, companions, transport, cost, experience, recommend, highlights, date)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TravelCard(
    travel: TravelItem,
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
                        text = "📍 ${travel.cityName} • ${travel.tripTitle}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                StarRatingBar(rating = travel.recommendIndex)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "同行: ${travel.companions} | 交通: ${travel.transport} | 花费: ¥${travel.costAmount} | 日期: ${travel.travelDate}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "✨ 景点打卡: ${travel.spotHighlights}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = ForestGreenPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = travel.experienceNote,
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
private fun AddTravelDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, Double, String, Int, String, String) -> Unit
) {
    var city by remember { mutableStateOf("") }
    var tripTitle by remember { mutableStateOf("") }
    var companions by remember { mutableStateOf("闺蜜") }
    var transport by remember { mutableStateOf("高铁") }
    var costText by remember { mutableStateOf("2000.0") }
    var experience by remember { mutableStateOf("") }
    var recommendIndex by remember { mutableStateOf(5) }
    var spotHighlights by remember { mutableStateOf("") }
    var travelDate by remember { mutableStateOf("2026-07-30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录旅行足迹", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("城市名称") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = travelDate,
                        onValueChange = { travelDate = it },
                        label = { Text("出行日期") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = tripTitle,
                    onValueChange = { tripTitle = it },
                    label = { Text("行程主题 (如: 洱海风光)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = companions,
                        onValueChange = { companions = it },
                        label = { Text("同行人") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = transport,
                        onValueChange = { transport = it },
                        label = { Text("交通方式") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it },
                    label = { Text("总花费(元)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = spotHighlights,
                    onValueChange = { spotHighlights = it },
                    label = { Text("打卡景点 (如: 喜洲古镇、麦田)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("行程体验感受") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Text("推荐指数", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                StarRatingBar(rating = recommendIndex, onRatingChange = { recommendIndex = it })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (city.isNotBlank()) {
                        val cost = costText.toDoubleOrNull() ?: 0.0
                        onConfirm(city, tripTitle, companions, transport, cost, experience, recommendIndex, spotHighlights, travelDate)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("保存行程")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
