package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.GiftLedgerItem
import com.example.ui.MainViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun GiftLedgerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val gifts by viewModel.gifts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalReceivedCount = gifts.count { it.ledgerType == "收到" }
    val totalReceivedAmount = gifts.filter { it.ledgerType == "收到" }.sumOf { it.valueAmount }

    val totalSentCount = gifts.count { it.ledgerType == "送出" }
    val totalSentAmount = gifts.filter { it.ledgerType == "送出" }.sumOf { it.valueAmount }

    Scaffold(
        topBar = { SubPageTopBar(title = "礼尚往来 🎁", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "记人情账")
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
            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🎁 收到礼品", fontSize = 13.sp, color = Color(0xFFC2185B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$totalReceivedCount 件", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF880E4F))
                            Text(text = "¥$totalReceivedAmount", fontSize = 14.sp, color = Color(0xFFC2185B))
                        }

                        VerticalDivider(
                            modifier = Modifier.height(40.dp),
                            color = Color(0xFFF8BBD0)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "✉️ 送出礼品", fontSize = 13.sp, color = Color(0xFFC2185B))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$totalSentCount 件", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF880E4F))
                            Text(text = "¥$totalSentAmount", fontSize = 14.sp, color = Color(0xFFC2185B))
                        }
                    }
                }
            }

            items(gifts, key = { it.id }) { gift ->
                GiftCard(
                    gift = gift,
                    onDelete = { viewModel.deleteGift(gift.id) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddGiftDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { type, item, valAmt, person, rel, occ, feeling, reaction, needReturn, date ->
                viewModel.addGift(type, item, valAmt, person, rel, occ, feeling, reaction, needReturn, date)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun GiftCard(
    gift: GiftLedgerItem,
    onDelete: () -> Unit
) {
    val isReceived = gift.ledgerType == "收到"

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
                    StatusBadge(
                        text = gift.ledgerType,
                        backgroundColor = if (isReceived) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                        textColor = if (isReceived) ForestGreenPrimary else Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = gift.itemTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "¥${gift.valueAmount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "对象: ${gift.targetPerson} (${gift.relationship}) | 场景: ${gift.occasion} | 日期: ${gift.dateString}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "我的感受: ${gift.myFeeling}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (gift.recipientReaction.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "对方反应: ${gift.recipientReaction}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (gift.needReturnGift) {
                    StatusBadge(
                        text = "需安排回礼",
                        backgroundColor = Color(0xFFFFEBEE),
                        textColor = Color(0xFFD32F2F)
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
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
private fun AddGiftDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String, String, String, String, String, Boolean, String) -> Unit
) {
    var ledgerType by remember { mutableStateOf("收到") }
    var itemTitle by remember { mutableStateOf("") }
    var valueAmountText by remember { mutableStateOf("200.0") }
    var targetPerson by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("朋友") }
    var occasion by remember { mutableStateOf("生日") }
    var myFeeling by remember { mutableStateOf("") }
    var recipientReaction by remember { mutableStateOf("") }
    var needReturnGift by remember { mutableStateOf(false) }
    var dateString by remember { mutableStateOf("2026-07-30") }

    val relationships = listOf("亲人", "恋人", "同事", "朋友")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录人情礼品", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = ledgerType == "收到",
                        onClick = { ledgerType = "收到" },
                        label = { Text("收到礼品 🎁", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = ledgerType == "送出",
                        onClick = { ledgerType = "送出" },
                        label = { Text("送出礼品 ✉️", fontSize = 12.sp) }
                    )
                }

                OutlinedTextField(
                    value = itemTitle,
                    onValueChange = { itemTitle = it },
                    label = { Text("礼品/礼金名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = valueAmountText,
                        onValueChange = { valueAmountText = it },
                        label = { Text("价值(元)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetPerson,
                        onValueChange = { targetPerson = it },
                        label = { Text("对象姓名") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("关系属性", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    relationships.forEach { rel ->
                        FilterChip(
                            selected = relationship == rel,
                            onClick = { relationship = rel },
                            label = { Text(rel, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = occasion,
                    onValueChange = { occasion = it },
                    label = { Text("节日/场合 (如: 生日/婚礼)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = myFeeling,
                    onValueChange = { myFeeling = it },
                    label = { Text("我的感受") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = needReturnGift,
                        onCheckedChange = { needReturnGift = it }
                    )
                    Text("标记为需要回礼", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (itemTitle.isNotBlank()) {
                        val valAmt = valueAmountText.toDoubleOrNull() ?: 0.0
                        onConfirm(ledgerType, itemTitle, valAmt, targetPerson, relationship, occasion, myFeeling, recipientReaction, needReturnGift, dateString)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("保存账单")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
