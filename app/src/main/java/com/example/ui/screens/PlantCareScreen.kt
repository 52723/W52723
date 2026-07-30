package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.PlantItem
import com.example.ui.MainViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.ForestGreenPrimaryContainer

@Composable
fun PlantCareScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val plants by viewModel.plants.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { SubPageTopBar(title = "绿植养护日志 🌿", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "新增植物")
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
            items(plants, key = { it.id }) { plant ->
                PlantCard(
                    plant = plant,
                    onWaterNow = { viewModel.waterPlantNow(plant) },
                    onDelete = { viewModel.deletePlant(plant.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddPlantDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, species, location, light, interval ->
                viewModel.addPlant(name, species, location, light, interval)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun PlantCard(
    plant: PlantItem,
    onWaterNow: () -> Unit,
    onDelete: () -> Unit
) {
    val millisSinceWatered = System.currentTimeMillis() - plant.lastWateredTimestamp
    val daysSinceWatered = (millisSinceWatered / (1000 * 60 * 60 * 24)).toInt()
    val daysUntilNextWater = plant.wateringIntervalDays - daysSinceWatered
    val needsWater = daysUntilNextWater <= 0

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
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plant.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(text = plant.species)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "位置: ${plant.location} | 光照: ${plant.lightDemand}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (needsWater) "⚠️ 建议今日浇水（距上次 ${daysSinceWatered} 天）"
                           else "🌱 状态良好（还剩 ${daysUntilNextWater} 天需浇水）",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (needsWater) Color(0xFFD32F2F) else ForestGreenPrimary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = onWaterNow,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (needsWater) ForestGreenPrimary else ForestGreenPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Water",
                            tint = if (needsWater) Color.White else ForestGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "浇水",
                            fontSize = 12.sp,
                            color = if (needsWater) Color.White else ForestGreenPrimary
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
}

@Composable
private fun AddPlantDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("客厅") }
    var lightDemand by remember { mutableStateOf("半阴") }
    var intervalText by remember { mutableStateOf("6") }

    val locations = listOf("客厅", "阳台", "卧室", "书房")
    val lights = listOf("喜阴", "半阴", "喜阳")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加绿植档案", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("植物昵称 (如: 小绿萝)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = species,
                    onValueChange = { species = it },
                    label = { Text("品种 (如: 绿萝/多肉/琴叶榕)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("摆放位置", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    locations.forEach { loc ->
                        FilterChip(
                            selected = location == loc,
                            onClick = { location = loc },
                            label = { Text(loc, fontSize = 11.sp) }
                        )
                    }
                }

                Text("光照需求", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    lights.forEach { l ->
                        FilterChip(
                            selected = lightDemand == l,
                            onClick = { lightDemand = l },
                            label = { Text(l, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = intervalText,
                    onValueChange = { intervalText = it },
                    label = { Text("浇水间隔 (天)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val interval = intervalText.toIntOrNull() ?: 5
                        onConfirm(name, species, location, lightDemand, interval)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("添加档案")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
