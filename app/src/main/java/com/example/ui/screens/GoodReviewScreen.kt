package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.GoodReviewItem
import com.example.ui.MainViewModel
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.ForestGreenPrimary

@Composable
fun GoodReviewScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val reviews by viewModel.goodReviews.collectAsState()
    var selectedTab by remember { mutableStateOf("全部") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredList = remember(reviews, selectedTab) {
        when (selectedTab) {
            "红榜好物" -> reviews.filter { it.listType == "红榜好物" }
            "黑榜避坑" -> reviews.filter { it.listType == "黑榜避坑" }
            else -> reviews
        }
    }

    Scaffold(
        topBar = { SubPageTopBar(title = "良品宣物 (红黑榜) 🛍️", onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "新增评测")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("全部", "红榜好物", "黑榜避坑").forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    GoodReviewCard(
                        item = item,
                        onDelete = { viewModel.deleteGoodReview(item.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddGoodReviewDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, listType, category, pros, cons, channel, price, stars ->
                viewModel.addGoodReview(name, listType, category, pros, cons, channel, price, stars)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun GoodReviewCard(
    item: GoodReviewItem,
    onDelete: () -> Unit
) {
    val isRed = item.listType == "红榜好物"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRed) Color(0xFFF1F8E9) else Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isRed) Icons.Default.ThumbUp else Icons.Default.ThumbDown,
                        contentDescription = item.listType,
                        tint = if (isRed) ForestGreenPrimary else Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.productName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                }

                StatusBadge(
                    text = item.listType,
                    backgroundColor = if (isRed) Color(0xFFC8E6C9) else Color(0xFFFFCDD2),
                    textColor = if (isRed) ForestGreenPrimary else Color(0xFFC62828)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "品类: ${item.category} | 渠道: ${item.purchaseChannel} | 价格: ¥${item.price}",
                fontSize = 12.sp,
                color = Color(0xFF616161)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "👍 优点: ${item.pros}",
                fontSize = 13.sp,
                color = Color(0xFF2E7D32)
            )

            if (item.cons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "👎 缺点/避坑: ${item.cons}",
                    fontSize = 13.sp,
                    color = Color(0xFFC62828)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StarRatingBar(rating = item.ratingStars)

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
private fun AddGoodReviewDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, Double, Int) -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var listType by remember { mutableStateOf("红榜好物") }
    var category by remember { mutableStateOf("护肤") }
    var pros by remember { mutableStateOf("") }
    var cons by remember { mutableStateOf("") }
    var channel by remember { mutableStateOf("官方旗舰店") }
    var priceText by remember { mutableStateOf("100.0") }
    var stars by remember { mutableStateOf(5) }

    val categories = listOf("护肤", "数码", "家居", "零食", "服饰")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("记录好物评测", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("产品名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("榜单属性", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = listType == "红榜好物",
                        onClick = { listType = "红榜好物" },
                        label = { Text("红榜好物 👍", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = listType == "黑榜避坑",
                        onClick = { listType = "黑榜避坑" },
                        label = { Text("黑榜避坑 👎", fontSize = 12.sp) }
                    )
                }

                Text("品类", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.forEach { c ->
                        FilterChip(
                            selected = category == c,
                            onClick = { category = c },
                            label = { Text(c, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = pros,
                    onValueChange = { pros = it },
                    label = { Text("优点/推荐理由") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cons,
                    onValueChange = { cons = it },
                    label = { Text("缺点/踩雷体验") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = channel,
                        onValueChange = { channel = it },
                        label = { Text("购买渠道") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("价格(元)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("评分", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                StarRatingBar(rating = stars, onRatingChange = { stars = it })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (productName.isNotBlank()) {
                        val p = priceText.toDoubleOrNull() ?: 0.0
                        onConfirm(productName, listType, category, pros, cons, channel, p, stars)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("保存评测")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}
