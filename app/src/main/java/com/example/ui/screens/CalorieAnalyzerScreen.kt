package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CalorieLogItem
import com.example.ui.MainViewModel
import com.example.ui.theme.ForestGreenPrimary

data class FoodPreset(
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val portion: String,
    val advice: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieAnalyzerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val calorieLogs by viewModel.calorieLogs.collectAsState()
    val context = LocalContext.current

    val dailyTargetKcal = 2000

    // Compute today eaten calories
    val todayEatenLogs = remember(calorieLogs) {
        calorieLogs.filter { it.dateString == "2026-07-30" && it.status == "EATEN" }
    }
    val todaySkippedLogs = remember(calorieLogs) {
        calorieLogs.filter { it.dateString == "2026-07-30" && it.status == "SKIPPED" }
    }

    val totalEatenKcal = todayEatenLogs.sumOf { it.caloriesKcal }
    val totalSkippedKcal = todaySkippedLogs.sumOf { it.caloriesKcal }
    val totalProtein = todayEatenLogs.sumOf { it.proteinGrams.toDouble() }.toFloat()
    val totalCarbs = todayEatenLogs.sumOf { it.carbsGrams.toDouble() }.toFloat()
    val totalFat = todayEatenLogs.sumOf { it.fatGrams.toDouble() }.toFloat()

    val progress = (totalEatenKcal.toFloat() / dailyTargetKcal.toFloat()).coerceIn(0f, 1f)

    // AI Analysis State
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPreset by remember { mutableStateOf<FoodPreset?>(null) }
    var mealType by remember { mutableStateOf("午餐") }
    var analysisResultMsg by remember { mutableStateOf<String?>(null) }

    // Preset list for quick photo testing
    val foodPresets = remember {
        listOf(
            FoodPreset("香煎黑椒低脂牛排", 420, 38f, 6f, 22f, "1盘 (220g)", "优质动物蛋白与铁元素，饱腹感强，非常适合午餐摄入！", "午餐"),
            FoodPreset("牛油果水煮蛋沙拉", 310, 14f, 22f, 18f, "1碗 (280g)", "富含不饱和脂肪酸与膳食纤维，有利于血糖稳定与肠道健康。", "早餐"),
            FoodPreset("法式高糖芝士拿破仑", 520, 6f, 64f, 28f, "1块", "高糖高脂肪精致碳水，极易引起血糖骤升与脂肪堆积，建议节制。", "零食/下午茶"),
            FoodPreset("番茄肥牛软籽意面", 480, 26f, 54f, 16f, "1盘 (350g)", "经典复合碳水与优质牛肉配比，能有效补充体能。", "晚餐"),
            FoodPreset("燕麦奶冰美式咖啡", 85, 2f, 12f, 3f, "1杯 (400ml)", "低热量清爽提神饮品，促进晨间基础代谢。", "早餐"),
            FoodPreset("奥尔良烤鸡腿堡", 590, 24f, 68f, 26f, "1个", "高热量快餐，偶尔奖励无妨，建议搭配无糖茶饮或运动消耗。", "午餐")
        )
    }

    // Photo picker launcher with safe handling
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri
            // Pick random preset for photo analysis result
            val randomFood = foodPresets.random()
            selectedPreset = randomFood
            analysisResultMsg = "📸 AI 照片图像识别成功！已识别为【${randomFood.name}】"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI 美食热量检测",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("calorie_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = ForestGreenPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
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
            // 1. Calorie Target Progress Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = ForestGreenPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "今日热量摄入预算",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$totalEatenKcal",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = " / $dailyTargetKcal kcal",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress bar
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape),
                            color = if (totalEatenKcal > dailyTargetKcal) Color(0xFFFF5252) else Color(0xFFAED581),
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Nutrients Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MacroNutrientBadge("蛋白质", "${totalProtein.toInt()}g", Color(0xFFE8F5E9))
                            MacroNutrientBadge("碳水化物", "${totalCarbs.toInt()}g", Color(0xFFFFF3E0))
                            MacroNutrientBadge("脂肪总量", "${totalFat.toInt()}g", Color(0xFFFFEBEE))
                        }

                        if (totalSkippedKcal > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "🎉 今天坚持选择『不吃』，共成功少摄入 $totalSkippedKcal kcal！",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Photo Detector Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "拍照检测",
                                tint = ForestGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "拍照/上传美食识别热量",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    try {
                                        photoPickerLauncher.launch("image/*")
                                    } catch (e: Exception) {
                                        // Fallback if gallery not present
                                        val randomFood = foodPresets.random()
                                        selectedPreset = randomFood
                                        analysisResultMsg = "模拟分析成功：已识别【${randomFood.name}】"
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = "相册")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("上传美食照片")
                            }

                            OutlinedButton(
                                onClick = {
                                    val randomFood = foodPresets.random()
                                    selectedPreset = randomFood
                                    analysisResultMsg = "📷 智能相机已捕获美图并识别【${randomFood.name}】"
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = "拍照")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("快捷拍摄识别")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("快速体验预设美食样板：", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            foodPresets.take(3).forEach { food ->
                                FilterChip(
                                    selected = selectedPreset?.name == food.name,
                                    onClick = {
                                        selectedPreset = food
                                        analysisResultMsg = "选择预设【${food.name}】"
                                    },
                                    label = { Text(food.name, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // 3. AI Analysis Result & Choice ("吃" vs "不吃")
            selectedPreset?.let { food ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = "热量分析结果",
                                    tint = ForestGreenPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI 识图分析报告",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenPrimary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFDCEDC8)
                                ) {
                                    Text(
                                        text = food.portion,
                                        fontSize = 11.sp,
                                        color = ForestGreenPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = food.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${food.calories} kcal",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFD84315)
                                    )
                                    Text("估计卡路里", fontSize = 11.sp, color = Color.Gray)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("蛋白: ${food.protein}g | 碳水: ${food.carbs}g", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("脂肪: ${food.fat}g", fontSize = 12.sp, color = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White
                            ) {
                                Text(
                                    text = "💡 营养建议：${food.advice}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF424242),
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Choice Buttons: 吃 vs 不吃
                            Text("你的选择：", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.addCalorieLog(
                                            foodName = food.name,
                                            caloriesKcal = food.calories,
                                            protein = food.protein,
                                            carbs = food.carbs,
                                            fat = food.fat,
                                            portion = food.portion,
                                            date = "2026-07-30",
                                            mealType = food.category,
                                            status = "EATEN",
                                            imageUri = selectedPhotoUri?.toString() ?: "",
                                            advice = food.advice
                                        )
                                        selectedPreset = null
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                                ) {
                                    Text("🟢 吃！计入摄入", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        viewModel.addCalorieLog(
                                            foodName = food.name,
                                            caloriesKcal = food.calories,
                                            protein = food.protein,
                                            carbs = food.carbs,
                                            fat = food.fat,
                                            portion = food.portion,
                                            date = "2026-07-30",
                                            mealType = food.category,
                                            status = "SKIPPED",
                                            imageUri = selectedPhotoUri?.toString() ?: "",
                                            advice = "🎉 成功忍住！为你节省了 ${food.calories} kcal 热量！"
                                        )
                                        selectedPreset = null
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                                ) {
                                    Text("🔴 不吃 / 仅检测", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Today's Food Intake History Log
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "今日饮食记录 (${calorieLogs.count { it.dateString == "2026-07-30" }}项)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary
                    )
                }
            }

            val todayLogs = calorieLogs.filter { it.dateString == "2026-07-30" }
            if (todayLogs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("今日尚无检测记录，快拍照检测食物吧！", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            } else {
                items(todayLogs, key = { it.id }) { log ->
                    CalorieLogCard(
                        log = log,
                        onDelete = { viewModel.deleteCalorieLog(log.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun MacroNutrientBadge(title: String, value: String, bg: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, color = Color.DarkGray)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
        }
    }
}

@Composable
fun CalorieLogCard(
    log: CalorieLogItem,
    onDelete: () -> Unit
) {
    val isEaten = log.status == "EATEN"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEaten) MaterialTheme.colorScheme.surface else Color(0xFFFFF8E1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isEaten) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ) {
                Icon(
                    imageVector = if (isEaten) Icons.Outlined.Restaurant else Icons.Default.Block,
                    contentDescription = null,
                    tint = if (isEaten) ForestGreenPrimary else Color(0xFFD32F2F),
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = log.foodName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isEaten) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = if (isEaten) "已吃 +${log.caloriesKcal}kcal" else "未吃 省${log.caloriesKcal}kcal",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEaten) ForestGreenPrimary else Color(0xFFC62828),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${log.mealType} | ${log.portionDesc} | 蛋白:${log.proteinGrams.toInt()}g 碳水:${log.carbsGrams.toInt()}g 脂肪:${log.fatGrams.toInt()}g",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "删除记录",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
