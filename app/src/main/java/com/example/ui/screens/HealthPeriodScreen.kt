package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.HealthRecord
import com.example.ui.MainViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun HealthPeriodScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val records by viewModel.healthRecords.collectAsState()
    var showLogDialog by remember { mutableStateOf(false) }

    val latestRecord = records.firstOrNull() ?: HealthRecord(
        dateString = "2026-07-30",
        bodyCondition = "舒适",
        heightCm = 165f,
        weightKg = 48.5f,
        checkupStatus = "血红蛋白偏低，缺乏维生素D",
        discomfortArea = "",
        periodDay = 0,
        careNotes = "注意保持作息"
    )

    Scaffold(
        topBar = { SubPageTopBar(title = "健康管理与体感关怀 🌿", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showLogDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "打卡")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("记录健康", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF7FBF7)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. BMI Indicator Block & Height/Weight Metrics
            item {
                BmiIndicatorCard(record = latestRecord)
            }

            // 2. Automated AI Health Evaluation & Personalized Recommendations
            item {
                AutoHealthEvaluationCard(record = latestRecord)
            }

            // 3. Menstrual Cycle Care Box
            item {
                PeriodCareCard(latestRecord = latestRecord)
            }

            // 4. 30-Day Body Condition Matrix
            item {
                SectionHeader(title = "近 30 天体感日历矩阵")
            }

            item {
                BodyMatrixGrid(records = records)
            }

            // 5. Recent Health Log History
            item {
                SectionHeader(title = "健康打卡与体格记录明细 (${records.size})")
            }

            items(items = records, key = { it.dateString }) { record ->
                HealthRecordItemCard(record = record)
            }
        }
    }

    if (showLogDialog) {
        AddHealthRecordDialog(
            latestRecord = latestRecord,
            onDismiss = { showLogDialog = false },
            onConfirm = { date, condition, height, weight, checkup, discomfort, periodDay, notes ->
                viewModel.saveHealthRecord(date, condition, height, weight, checkup, discomfort, periodDay, notes)
                showLogDialog = false
            }
        )
    }
}

/**
 * 1. BMI Indicator Block & Physical Metrics Card
 */
@Composable
private fun BmiIndicatorCard(record: HealthRecord) {
    val heightM = (if (record.heightCm > 0) record.heightCm else 165f) / 100f
    val weight = if (record.weightKg > 0) record.weightKg else 48.5f
    val bmi = weight / (heightM * heightM)

    val (bmiCategory, bmiColor, bmiBadgeBg) = when {
        bmi < 18.5f -> Triple("偏瘦 (BMI < 18.5)", Color(0xFF0288D1), Color(0xFFE1F5FE))
        bmi in 18.5f..23.9f -> Triple("标准理想 (18.5-23.9)", Color(0xFF2E7D32), Color(0xFFE8F5E9))
        bmi in 24.0f..27.9f -> Triple("偏重 (24.0-27.9)", Color(0xFFF57C00), Color(0xFFFFF3E0))
        else -> Triple("肥胖 (≥ 28.0)", Color(0xFFD32F2F), Color(0xFFFFEBEE))
    }

    // Ideal weight range for this height (BMI 18.5 ~ 23.9)
    val minIdealWeight = 18.5f * heightM * heightM
    val maxIdealWeight = 23.9f * heightM * heightM

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonitorWeight,
                        contentDescription = "BMI",
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "身体指标与 BMI 指块",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = bmiBadgeBg
                ) {
                    Text(
                        text = bmiCategory,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = bmiColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Height, Weight, BMI Big Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Height
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF1F8E9), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "身高", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%.1f", record.heightCm)} cm",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Weight
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF1F8E9), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "体重", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%.1f", weight)} kg",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // BMI Value
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .background(bmiBadgeBg, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "BMI 指数", fontSize = 11.sp, color = bmiColor)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%.1f", bmi),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = bmiColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BMI Spectrum Visual Bar
            Text(
                text = "BMI 范围分布图",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(6.dp))

            val progressFraction = ((bmi - 15f) / (32f - 15f)).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEEEEEE))
            ) {
                // Colored Segments: Underweight (Blue), Normal (Green), Overweight (Orange), Obese (Red)
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(3.5f).fillMaxHeight().background(Color(0xFF81D4FA))) // 15..18.5
                    Box(modifier = Modifier.weight(5.4f).fillMaxHeight().background(Color(0xFFA5D6A7))) // 18.5..23.9
                    Box(modifier = Modifier.weight(4.0f).fillMaxHeight().background(Color(0xFFFFCC80))) // 23.9..27.9
                    Box(modifier = Modifier.weight(4.1f).fillMaxHeight().background(Color(0xFFEF9A9A))) // 27.9..32
                }

                // Current Indicator Pin
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressFraction)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(Color.Black)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("15.0 (偏瘦)", fontSize = 10.sp, color = Color.Gray)
                Text("18.5", fontSize = 10.sp, color = Color.Gray)
                Text("23.9 (理想)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                Text("28.0", fontSize = 10.sp, color = Color.Gray)
                Text("32.0 (肥胖)", fontSize = 10.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "💡 标准推荐：对于 ${record.heightCm.toInt()}cm 身高，理想标准体重区间为 ${String.format("%.1f", minIdealWeight)} kg ~ ${String.format("%.1f", maxIdealWeight)} kg。",
                fontSize = 11.sp,
                color = Color(0xFF555555)
            )
        }
    }
}

/**
 * 2. Automated AI Health Evaluation & Recommendations Card
 */
@Composable
private fun AutoHealthEvaluationCard(record: HealthRecord) {
    val heightM = (if (record.heightCm > 0) record.heightCm else 165f) / 100f
    val weight = if (record.weightKg > 0) record.weightKg else 48.5f
    val bmi = weight / (heightM * heightM)

    // Build automated evaluation report
    val evaluationPoints = remember(record) {
        val list = mutableListOf<String>()

        // BMI Evaluation
        when {
            bmi < 18.5f -> list.add("🏋️ **BMI评估**: 当前BMI为 ${String.format("%.1f", bmi)}，属于轻度偏瘦。提示身体能量储备与肌肉量可能偏低，建议适当增加优质碳水与高蛋白质饮食（如瘦肉、蛋类、坚果），并配合力量训练提高代谢。")
            bmi in 18.5f..23.9f -> list.add("🌟 **BMI评估**: 当前BMI为 ${String.format("%.1f", bmi)}，处于极其理想的健康标准区间！请保持当前的规律饮食与适度有氧/瑜伽锻炼。")
            bmi in 24.0f..27.9f -> list.add("🥗 **BMI评估**: 当前BMI为 ${String.format("%.1f", bmi)}，处于偏重区间。建议减少高糖甜品与油炸食品摄入，餐后保持15分钟散步，每周安排3次中等强度运动。")
            else -> list.add("⚠️ **BMI评估**: 当前BMI为 ${String.format("%.1f", bmi)}，达到肥胖预警。建议控制膳食总热量，多食用膳食纤维丰富的水果蔬菜，并在专业指导下循序渐进减脂。")
        }

        // Checkup Evaluation
        val checkup = record.checkupStatus
        if (checkup.contains("贫血") || checkup.contains("血红蛋白")) {
            list.add("🩸 **体检评估**: 提示血红蛋白偏低或轻度贫血。结合女性生理期铁质流失，推荐日常冲泡红枣枸杞茶，多吃黑木耳、菠菜与动物肝脏，饭后避免立即饮用浓茶和咖啡。")
        }
        if (checkup.contains("维D") || checkup.contains("维生素") || checkup.contains("缺钙")) {
            list.add("☀️ **体检评估**: 提示微量元素/维生素D需补充。建议在上午9-10点晒太阳15-20分钟，增加牛奶与豆制品摄入。")
        }
        if (checkup.contains("正常") || checkup.contains("优")) {
            list.add("✨ **体检评估**: 体检整体指标良好！请继续保持良好的生活习惯与年度健康检查。")
        } else if (checkup.isNotBlank() && !checkup.contains("贫血") && !checkup.contains("维D")) {
            list.add("📋 **体检评估**: 记录体检状况「$checkup」。注意关注身体微小变化，遇不适及时咨询专业医师。")
        }

        // Feelings & Symptoms Evaluation
        when (record.bodyCondition) {
            "睡眠不足", "头晕" -> list.add("🌙 **体感调养**: 提示神经疲劳与自主神经紊乱。建议今晚睡前1小时关掉电子设备，用40℃温水泡脚20分钟，适量补充镁元素与温热洋甘菊茶。")
            "肚子痛", "姨妈期" -> list.add("🌸 **生理期调养**: 处于经期或腹部隐痛状态。注意腹部保暖，使用热水袋热敷，多饮红糖姜茶，严禁生冷冰饮，暂停剧烈运动。")
            "胃胀疲倦" -> list.add("🍵 **体感调养**: 消化道动力偏弱，伴随倦怠。建议少食多餐，细嚼慢咽，饮用陈皮大麦茶，避免生冷油腻。")
            else -> list.add("🌱 **体感调养**: 今日体感良好（${record.bodyCondition}）。多喝温水，保持好心情！")
        }

        list
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Evaluation",
                        tint = ForestGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🤖 智能健康评估与调养指南",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF2E7D32)
                ) {
                    Text(
                        text = "自动实时评估",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            evaluationPoints.forEach { point ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ) {
                    Text(
                        text = point,
                        fontSize = 12.sp,
                        color = Color(0xFF333333),
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

/**
 * 3. Menstrual Cycle Care Card
 */
@Composable
private fun PeriodCareCard(latestRecord: HealthRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Care",
                        tint = Color(0xFFE91E63),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "曦曦的生理期温暖关怀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF880E4F)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8BBD0)
                ) {
                    val periodText = if (latestRecord.bodyCondition == "姨妈期" || latestRecord.periodDay > 0) {
                        "姨妈期 (第 ${if (latestRecord.periodDay > 0) latestRecord.periodDay else 2} 天)"
                    } else {
                        "安全卵泡期"
                    }
                    Text(
                        text = periodText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF880E4F),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "💡 专属调养建议：多喝温水，避免生冷辛辣。保持规律作息，用热水袋敷下腹部可以有效缓解疲劳不适。",
                fontSize = 12.sp,
                color = Color(0xFF4A148C),
                lineHeight = 18.sp
            )

            if (latestRecord.checkupStatus.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🏥 最近体检/健康诊断: ${latestRecord.checkupStatus}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF880E4F)
                )
            }
        }
    }
}

/**
 * 4. 30-Day Body Condition Matrix
 */
@Composable
private fun BodyMatrixGrid(records: List<HealthRecord>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "图例: 🟢 舒适 | 🔴 姨妈期 | 🟡 睡眠不足 | 🟣 肚子痛 | 🟠 胃胀疲倦 | 🔵 头晕",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val last30Days = (1..30).toList()
                items(last30Days) { dayIndex ->
                    val dayStr = if (dayIndex < 10) "0$dayIndex" else "$dayIndex"
                    val rec = records.find { it.dateString.endsWith(dayStr) }

                    val blockColor = when (rec?.bodyCondition) {
                        "舒适" -> Color(0xFF81C784)
                        "姨妈期" -> Color(0xFFE91E63)
                        "睡眠不足" -> Color(0xFFFFD54F)
                        "肚子痛" -> Color(0xFFAB47BC)
                        "胃胀疲倦" -> Color(0xFFFF8A65)
                        "头晕" -> Color(0xFF90A4AE)
                        else -> Color(0xFFE0E0E0)
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(blockColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$dayIndex",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * 5. Health Record History Item Card
 */
@Composable
private fun HealthRecordItemCard(record: HealthRecord) {
    val heightM = (if (record.heightCm > 0) record.heightCm else 165f) / 100f
    val weight = if (record.weightKg > 0) record.weightKg else 48.5f
    val bmi = weight / (heightM * heightM)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.dateString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(text = record.bodyCondition)
                }

                Text(
                    text = "${record.heightCm.toInt()}cm | ${record.weightKg}kg (BMI ${String.format("%.1f", bmi)})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                )
            }

            if (record.checkupStatus.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🩺 体检诊断: ${record.checkupStatus}",
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32)
                )
            }

            if (record.discomfortArea.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🤒 不适感受: ${record.discomfortArea}",
                    fontSize = 12.sp,
                    color = Color(0xFFC62828)
                )
            }

            if (record.careNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 调养手记: ${record.careNotes}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * 6. Add/Update Health Record Dialog
 */
@Composable
private fun AddHealthRecordDialog(
    latestRecord: HealthRecord,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Float, Float, String, String, Int, String) -> Unit
) {
    val context = LocalContext.current
    var dateString by remember { mutableStateOf("2026-07-30") }
    var condition by remember { mutableStateOf(latestRecord.bodyCondition.ifBlank { "舒适" }) }
    var heightText by remember { mutableStateOf(latestRecord.heightCm.toString()) }
    var weightText by remember { mutableStateOf(latestRecord.weightKg.toString()) }
    var checkupStatus by remember { mutableStateOf(latestRecord.checkupStatus) }
    var discomfort by remember { mutableStateOf(latestRecord.discomfortArea) }
    var periodDay by remember { mutableIntStateOf(latestRecord.periodDay) }
    var notes by remember { mutableStateOf(latestRecord.careNotes) }

    val conditions = listOf("舒适", "睡眠不足", "肚子痛", "姨妈期", "胃胀疲倦", "头晕", "精神饱满")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "📝 记录健康档案与体感",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1B5E20)
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        label = { Text("打卡日期 (YYYY-MM-DD)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Height & Weight
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = heightText,
                            onValueChange = { heightText = it },
                            label = { Text("身高 (cm)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { weightText = it },
                            label = { Text("体重 (kg)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Body Condition Chips
                item {
                    Text("当前身体感受/体感", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        conditions.take(3).forEach { cond ->
                            FilterChip(
                                selected = condition == cond,
                                onClick = { condition = cond },
                                label = { Text(cond, fontSize = 11.sp) }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        conditions.drop(3).forEach { cond ->
                            FilterChip(
                                selected = condition == cond,
                                onClick = { condition = cond },
                                label = { Text(cond, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Medical Checkup Status
                item {
                    OutlinedTextField(
                        value = checkupStatus,
                        onValueChange = { checkupStatus = it },
                        label = { Text("体检/健康状况 (例: 血红蛋白偏低, 缺乏维D, 指标正常)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Discomfort Area
                item {
                    OutlinedTextField(
                        value = discomfort,
                        onValueChange = { discomfort = it },
                        label = { Text("身体不适部位/体感感受 (选填)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Period Day
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("生理期阶段:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row {
                            FilterChip(
                                selected = periodDay == 0,
                                onClick = { periodDay = 0 },
                                label = { Text("非生理期", fontSize = 11.sp) }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            FilterChip(
                                selected = periodDay > 0,
                                onClick = { if (periodDay == 0) periodDay = 1 },
                                label = { Text(if (periodDay > 0) "姨妈期 (第${periodDay}天)" else "姨妈期", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                // Care Notes
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("调养手记 / 饮水 / 饮食备注", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val h = heightText.toFloatOrNull() ?: 165f
                    val w = weightText.toFloatOrNull() ?: 48.5f
                    val pDay = if (condition == "姨妈期" && periodDay == 0) 1 else periodDay
                    onConfirm(dateString, condition, h, w, checkupStatus, discomfort, pDay, notes)
                    Toast.makeText(context, "健康记录已更新，生成最新评估！", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("保存记录")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
