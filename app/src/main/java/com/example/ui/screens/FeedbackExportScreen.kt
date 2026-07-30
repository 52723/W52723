package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.FeedbackItem
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackExportScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val feedbacks by viewModel.feedbacks.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: 反馈建议, 1: 文档导出

    val primaryGreen = Color(0xFF2E7D32)
    val cardBg = Color(0xFFF1F8E9)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "意见反馈与文档导出",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color(0xFF1B5E20)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF7FBF7))
        ) {
            // Tab Header
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFE8F5E9),
                contentColor = primaryGreen
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "✍️ 意见与体验反馈",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "📄 手记与反馈文档导出",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                FeedbackTabContent(
                    feedbacks = feedbacks,
                    onAddFeedback = { type, stars, content, contact ->
                        viewModel.addFeedback(type, stars, content, contact)
                    },
                    onDeleteFeedback = { id ->
                        viewModel.deleteFeedback(id)
                    }
                )
            } else {
                DocumentExportTabContent(
                    viewModel = viewModel,
                    feedbacks = feedbacks
                )
            }
        }
    }
}

@Composable
fun FeedbackTabContent(
    feedbacks: List<FeedbackItem>,
    onAddFeedback: (String, Int, String, String) -> Unit,
    onDeleteFeedback: (Long) -> Unit
) {
    val context = LocalContext.current
    var feedbackType by remember { mutableStateOf("体感体验") }
    var ratingStars by remember { mutableIntStateOf(5) }
    var contentText by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }

    val feedbackTypes = listOf("功能建议", "体感体验", "网络灵感", "菜谱分享", "界面美化", "错误反馈")
    val primaryGreen = Color(0xFF2E7D32)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Feedback Form Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "💌 给曦曦的 For U 提意见/分享感受",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen
                    )
                    Text(
                        text = "你的每一次反馈都将帮助应用做得更贴心~",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Type Chips
                    Text(
                        text = "反馈类型",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        feedbackTypes.take(3).forEach { type ->
                            val isSelected = feedbackType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { feedbackType = type },
                                label = { Text(type, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        feedbackTypes.drop(3).forEach { type ->
                            val isSelected = feedbackType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { feedbackType = type },
                                label = { Text(type, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rating Stars
                    Text(
                        text = "满意度评分",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= ratingStars) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "$i 星",
                                tint = if (i <= ratingStars) Color(0xFFFFB300) else Color.LightGray,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { ratingStars = i }
                                    .padding(2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${ratingStars} 分",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8F00)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Content TextField
                    OutlinedTextField(
                        value = contentText,
                        onValueChange = { contentText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        placeholder = { Text("在此写下你的意见、建议或使用体验（例如：希望增加更多减脂菜谱、打卡界面动画等）...", fontSize = 13.sp) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryGreen,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contact Info
                    OutlinedTextField(
                        value = contactInfo,
                        onValueChange = { contactInfo = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("联系方式 (选填, 如邮箱/微信)", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryGreen,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (contentText.isBlank()) {
                                Toast.makeText(context, "请填写反馈内容后再提交哦~", Toast.LENGTH_SHORT).show()
                            } else {
                                onAddFeedback(feedbackType, ratingStars, contentText, contactInfo)
                                Toast.makeText(context, "感谢曦曦的反馈，已为您保存！", Toast.LENGTH_SHORT).show()
                                contentText = ""
                                contactInfo = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
                    ) {
                        Icon(imageVector = Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("提交我的反馈建议", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Feedback History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📜 反馈历史记录 (${feedbacks.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }
        }

        if (feedbacks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂无历史反馈，快在上方发表第一条建议吧~", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(feedbacks, key = { it.id }) { item ->
                FeedbackItemCard(
                    item = item,
                    onDelete = { onDeleteFeedback(item.id) }
                )
            }
        }
    }
}

@Composable
fun FeedbackItemCard(
    item: FeedbackItem,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
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
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = item.feedbackType,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "★".repeat(item.ratingStars),
                        color = Color(0xFFFFB300),
                        fontSize = 12.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.dateString, fontSize = 11.sp, color = Color.Gray)
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "删除", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.content,
                fontSize = 13.sp,
                color = Color(0xFF212121),
                lineHeight = 18.sp
            )

            if (item.contactInfo.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "联系方式: ${item.contactInfo}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun DocumentExportTabContent(
    viewModel: MainViewModel,
    feedbacks: List<FeedbackItem>
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isFullReport by remember { mutableStateOf(false) }

    val primaryGreen = Color(0xFF2E7D32)
    val docText = remember(isFullReport, feedbacks) {
        viewModel.generateExportMarkdownDoc(isFullReport)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Option Selector Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "⚙️ 选择文档导出范围",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen
                    )
                    Text(
                        text = "支持生成干净整洁的 Markdown (.md) / TXT 格式文本，方便保存到备忘录或分享给他人",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isFullReport = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (!isFullReport) ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFE8F5E9)) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(
                                text = "📝 仅用户反馈文档",
                                fontSize = 12.sp,
                                fontWeight = if (!isFullReport) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isFullReport) primaryGreen else Color.Gray
                            )
                        }

                        OutlinedButton(
                            onClick = { isFullReport = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (isFullReport) ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFE8F5E9)) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text(
                                text = "🌿 曦曦全量生活档案",
                                fontSize = 12.sp,
                                fontWeight = if (isFullReport) FontWeight.Bold else FontWeight.Normal,
                                color = if (isFullReport) primaryGreen else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Copy Button
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(docText))
                        Toast.makeText(context, "已成功复制全文到剪贴板！", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
                ) {
                    Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("复制文档全文", fontSize = 13.sp)
                }

                // Share Intent Button
                Button(
                    onClick = {
                        try {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, docText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "分享或导出 For U 文档").apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(shareIntent)
                        } catch (e: Exception) {
                            clipboardManager.setText(AnnotatedString(docText))
                            Toast.makeText(context, "无法唤起分享面板，已自动复制文本到剪贴板！", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("导出/分享文档", fontSize = 13.sp)
                }
            }
        }

        // Document Preview Container
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isFullReport) "📄 全量生活与反馈汇总.md (预览)" else "📄 反馈记录文档.md (预览)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF818CF8)
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF334155)
                        ) {
                            Text(
                                text = "Markdown 格式",
                                fontSize = 10.sp,
                                color = Color.LightGray,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = docText,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
