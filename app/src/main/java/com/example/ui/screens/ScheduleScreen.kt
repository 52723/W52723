package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ScheduleItem
import com.example.ui.MainViewModel
import com.example.ui.theme.ForestGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val schedules by viewModel.schedules.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("全部") }
    var selectedDateFilter by remember { mutableStateOf("今日") } // 今日, 待办, 全部
    var activeNoticeMessage by remember { mutableStateOf<String?>(null) }

    val filteredSchedules = remember(schedules, selectedCategoryFilter, selectedDateFilter) {
        schedules.filter { item ->
            val matchCategory = selectedCategoryFilter == "全部" || item.category == selectedCategoryFilter
            val matchDate = when (selectedDateFilter) {
                "今日" -> item.dateString == "2026-07-30"
                "待办" -> !item.isCompleted
                else -> true
            }
            matchCategory && matchDate
        }
    }

    val activeRemindersCount = schedules.count { it.isReminderEnabled && !it.isCompleted }
    val nextReminder = schedules.firstOrNull { it.isReminderEnabled && !it.isCompleted && it.dateString == "2026-07-30" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "每日日程与提醒",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreenPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "共 ${schedules.size} 项",
                                fontSize = 11.sp,
                                color = ForestGreenPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("schedule_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = ForestGreenPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            activeNoticeMessage = if (nextReminder != null) {
                                "🔔 已为您开启提醒：即将于 ${nextReminder.timeString} 提醒【${nextReminder.title}】(提前${nextReminder.reminderAdvanceMinutes}分钟)"
                            } else {
                                "🔔 提醒功能已在后台就绪！设置提醒时间后将自动响应推送"
                            }
                        }
                    ) {
                        BadgedBox(
                            badge = {
                                if (activeRemindersCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFE53935),
                                        contentColor = Color.White
                                    ) {
                                        Text("$activeRemindersCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "提醒中心",
                                tint = ForestGreenPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = "新增日程") },
                text = { Text("添加日程", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("add_schedule_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Dynamic Reminder Alert Banner
            if (nextReminder != null || activeNoticeMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "提醒警报",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "日程自动提醒已生效",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = activeNoticeMessage ?: "最近提醒：${nextReminder?.timeString} 【${nextReminder?.title}】(提前${nextReminder?.reminderAdvanceMinutes}分钟提醒)",
                                fontSize = 12.sp,
                                color = Color(0xFF8D6E63)
                            )
                        }
                    }
                }
            }

            // 2. Filter Bar
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("今日", "待办", "全部").forEach { filter ->
                        FilterChip(
                            selected = selectedDateFilter == filter,
                            onClick = { selectedDateFilter = filter },
                            label = { Text(filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreenPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("全部", "工作", "个人", "约会", "健康", "学习").forEach { cat ->
                        AssistChip(
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selectedCategoryFilter == cat) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Schedule List
            if (filteredSchedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = "暂无日程",
                            tint = Color.Gray,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "当前分类下暂无日程事项",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                        ) {
                            Text("创建一条新日程")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSchedules, key = { it.id }) { schedule ->
                        ScheduleCardItem(
                            schedule = schedule,
                            onToggleComplete = { viewModel.toggleScheduleCompleted(schedule) },
                            onToggleReminder = { viewModel.toggleScheduleReminder(schedule) },
                            onDelete = { viewModel.deleteSchedule(schedule.id) }
                        )
                    }
                }
            }
        }
    }

    // Add Schedule Dialog
    if (showAddDialog) {
        AddScheduleDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, date, time, category, priority, notes, isReminder, advance ->
                viewModel.addSchedule(
                    title = title,
                    date = date,
                    time = time,
                    category = category,
                    priority = priority,
                    notes = notes,
                    isReminderEnabled = isReminder,
                    advanceMinutes = advance
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ScheduleCardItem(
    schedule: ScheduleItem,
    onToggleComplete: () -> Unit,
    onToggleReminder: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityBg = when (schedule.priority) {
        "高" -> Color(0xFFFFEBEE)
        "中" -> Color(0xFFE8F5E9)
        else -> Color(0xFFF5F5F5)
    }
    val priorityText = when (schedule.priority) {
        "高" -> Color(0xFFC62828)
        "中" -> ForestGreenPrimary
        else -> Color(0xFF616161)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("schedule_item_${schedule.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (schedule.isCompleted) Color(0xFFFAFAFA) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = schedule.isCompleted,
                onCheckedChange = { onToggleComplete() },
                colors = CheckboxDefaults.colors(checkedColor = ForestGreenPrimary)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Time Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ForestGreenPrimary
                    ) {
                        Text(
                            text = schedule.timeString,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Priority Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = priorityBg
                    ) {
                        Text(
                            text = "${schedule.priority}优先",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = priorityText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Category Tag
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE1F5FE)
                    ) {
                        Text(
                            text = schedule.category,
                            fontSize = 10.sp,
                            color = Color(0xFF0277BD),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title
                Text(
                    text = schedule.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (schedule.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (schedule.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (schedule.locationOrNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "地点/备注",
                            tint = Color.Gray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = schedule.locationOrNotes,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Reminder Advance Notice
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleReminder() }
                        .padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (schedule.isReminderEnabled) Icons.Outlined.NotificationsActive else Icons.Outlined.Notifications,
                        contentDescription = "提醒设置",
                        tint = if (schedule.isReminderEnabled) Color(0xFFE65100) else Color.Gray,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (schedule.isReminderEnabled) "🔔 提前${schedule.reminderAdvanceMinutes}分钟提醒已开启" else "🔕 提醒已关闭",
                        fontSize = 11.sp,
                        color = if (schedule.isReminderEnabled) Color(0xFFE65100) else Color.Gray,
                        fontWeight = if (schedule.isReminderEnabled) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "删除日程",
                    tint = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        date: String,
        time: String,
        category: String,
        priority: String,
        notes: String,
        isReminder: Boolean,
        advanceMinutes: Int
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("2026-07-30") }
    var timeString by remember { mutableStateOf("10:00") }
    var category by remember { mutableStateOf("工作") }
    var priority by remember { mutableStateOf("高") }
    var notes by remember { mutableStateOf("") }
    var isReminderEnabled by remember { mutableStateOf(true) }
    var advanceMinutes by remember { mutableIntStateOf(15) }

    val categories = listOf("工作", "个人", "约会", "健康", "学习")
    val priorities = listOf("高", "中", "低")
    val advanceOptions = listOf(5, 10, 15, 30, 60)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("添加新日程事项", fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("日程名称") },
                    placeholder = { Text("例如：与项目组会议、晚间健身打卡") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        label = { Text("日期") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = timeString,
                        onValueChange = { timeString = it },
                        label = { Text("时间 (HH:mm)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Category selection
                Text("分类", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                // Priority selection
                Text("优先级", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    priorities.forEach { pri ->
                        FilterChip(
                            selected = priority == pri,
                            onClick = { priority = pri },
                            label = { Text("$pri 优先", fontSize = 11.sp) }
                        )
                    }
                }

                // Reminder settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("自动提醒服务", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = isReminderEnabled,
                        onCheckedChange = { isReminderEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ForestGreenPrimary)
                    )
                }

                if (isReminderEnabled) {
                    Text("提前提醒时间", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        advanceOptions.forEach { mins ->
                            FilterChip(
                                selected = advanceMinutes == mins,
                                onClick = { advanceMinutes = mins },
                                label = { Text("${mins}分钟", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("地点/备注 (可选)") },
                    placeholder = { Text("例如：会议室 302、带笔记本电脑") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, dateString, timeString, category, priority, notes, isReminderEnabled, advanceMinutes)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("确认添加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
