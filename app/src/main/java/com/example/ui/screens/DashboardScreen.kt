package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.InspirationItem
import com.example.data.entity.ReminderItem
import com.example.ui.MainViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.ForestGreenPrimaryContainer

data class ShortcutNav(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val routeName: String,
    val iconBgColor: Color
)

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateTo: (String) -> Unit
) {
    val reminders by viewModel.reminders.collectAsState()
    val inspirations by viewModel.inspirations.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val calorieLogs by viewModel.calorieLogs.collectAsState()

    val shortcuts = remember {
        listOf(
            ShortcutNav("每日日程", "时间&自动提醒", Icons.Default.Event, "schedule", Color(0xFFE8F5E9)),
            ShortcutNav("热量检测", "拍照识图&卡路里", Icons.Default.LocalDining, "calorie_analyzer", Color(0xFFFFF3E0)),
            ShortcutNav("灵感一现", "脑洞&想法", Icons.Default.Lightbulb, "inspiration", Color(0xFFE8F5E9)),
            ShortcutNav("健康关怀", "BMI/指标/体感/生理期", Icons.Default.Favorite, "health_period", Color(0xFFFFEBEE)),
            ShortcutNav("绿植浇水", "养护日志", Icons.Default.Park, "plant_care", Color(0xFFE8F5E9)),
            ShortcutNav("私厨菜谱", "美食精选", Icons.Default.Restaurant, "recipe", Color(0xFFFFF3E0)),
            ShortcutNav("电子衣橱", "穿搭灵感", Icons.Default.Checkroom, "wardrobe", Color(0xFFE1F5FE)),
            ShortcutNav("观影手记", "影视影评", Icons.Default.Movie, "movie_notes", Color(0xFFF3E5F5)),
            ShortcutNav("良品宣物", "红黑好物", Icons.Default.ThumbUpAlt, "good_review", Color(0xFFFFF8E1)),
            ShortcutNav("礼尚往来", "人情账单", Icons.Default.CardGiftcard, "gift_ledger", Color(0xFFFCE4EC)),
            ShortcutNav("社交评评", "能量复盘", Icons.Default.Psychology, "social_review", Color(0xFFE0F2F1)),
            ShortcutNav("反馈导出", "意见与文档", Icons.Default.RateReview, "feedback_export", Color(0xFFDCEDC8))
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Greeting & Weather Forest Green Card
        item {
            HeaderGreetingCard()
        }

        // 1.2 Today's Schedule & Calorie Quick Access Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Schedule Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTo("schedule") },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = ForestGreenPrimary) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = "Schedule",
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp).size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("📅 每日日程", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val activeRemindersCount = schedules.count { it.isReminderEnabled && !it.isCompleted }
                        Text(
                            text = if (activeRemindersCount > 0) "🔔 $activeRemindersCount 项日程提醒开启中" else "全部日程规划就绪",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Calorie Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateTo("calorie_analyzer") },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFFE65100)) {
                                Icon(
                                    imageVector = Icons.Default.LocalDining,
                                    contentDescription = "Calorie",
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp).size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🥗 热量检测", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val todayEaten = calorieLogs.filter { it.dateString == "2026-07-30" && it.status == "EATEN" }.sumOf { it.caloriesKcal }
                        Text(
                            text = "🔥 今日已摄入 $todayEaten kcal",
                            fontSize = 11.sp,
                            color = Color(0xFFD84315),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 1.5 Quick Feedback & Network Inspiration Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTo("feedback_export") },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ForestGreenPrimary
                        ) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = "Feedback",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "💬 意见反馈与手记导出",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "发表体验建议，一键导出 Markdown/TXT 格式手记",
                                fontSize = 12.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Arrow",
                        tint = ForestGreenPrimary
                    )
                }
            }
        }

        // 2. Today's Reminders (今日提醒)
        item {
            SectionHeader(
                title = "今日提醒",
                actionText = "管理"
            )
        }

        items(reminders) { reminder ->
            ReminderCard(
                reminder = reminder,
                onToggleComplete = { viewModel.toggleReminderCompleted(reminder) },
                onAddWater = { viewModel.incrementWaterProgress(reminder) }
            )
        }

        // 3. Quick Shortcuts Grid
        item {
            SectionHeader(title = "快捷入口")
        }

        item {
            ShortcutGrid(shortcuts = shortcuts, onNavigateTo = onNavigateTo)
        }

        // 4. Recent Notes / Inspirations
        item {
            SectionHeader(
                title = "最近灵感记录",
                actionText = "查看全部",
                onActionClick = { onNavigateTo("inspiration") }
            )
        }

        items(inspirations.take(3)) { inspiration ->
            RecentInspirationItem(inspiration = inspiration, onClick = { onNavigateTo("inspiration") })
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeaderGreetingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ForestGreenPrimary,
                            Color(0xFF388E3C),
                            Color(0xFF4CAF50)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "For U • 治愈生活",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "早安，曦曦！今天也是美好的一天 🌱",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Weather pill widget
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Sunny",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "24°C 晴",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daily quote banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "Quote",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "用温柔的心关怀自己，记录琐碎中的小确幸。",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: ReminderItem,
    onToggleComplete: () -> Unit,
    onAddWater: () -> Unit
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = reminder.isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = ForestGreenPrimary
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = reminder.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (reminder.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(
                            text = reminder.category,
                            backgroundColor = ForestGreenPrimaryContainer,
                            textColor = ForestGreenPrimary
                        )
                    }
                    if (reminder.subtitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = reminder.subtitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // If it's drinking water, show +1 progress button
            if (reminder.category == "饮水") {
                Button(
                    onClick = onAddWater,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimaryContainer),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Drink Water",
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${reminder.currentProgress}/${reminder.targetProgress} ${reminder.unit}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutGrid(
    shortcuts: List<ShortcutNav>,
    onNavigateTo: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val rows = shortcuts.chunked(5)
        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (nav in row) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateTo(nav.routeName) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(nav.iconBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = nav.icon,
                                contentDescription = nav.title,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = nav.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentInspirationItem(
    inspiration: InspirationItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inspiration.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                StatusBadge(text = inspiration.status)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = inspiration.content,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}
