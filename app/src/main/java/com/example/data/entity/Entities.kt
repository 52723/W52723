package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. 今日提醒 / Dashboard Task
@Entity(tableName = "reminder_items")
data class ReminderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String = "",
    val category: String = "日常", // 饮水, 饮食, 绿植, 身体, 个人
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val unit: String = "",
    val isCompleted: Boolean = false,
    val dateString: String = ""
)

// 2. 灵感一现
@Entity(tableName = "inspiration_items")
data class InspirationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // 脑洞, 线索, 创作, 计划, 调研
    val emotion: String, // 平静, 激动, 沮丧, 兴奋
    val status: String, // 孵化中, 已落地, 纯想想
    val stars: Int = 3,
    val createdTimestamp: Long = System.currentTimeMillis()
)

// 3. 整合健康模块：体感、指标与生理期
@Entity(tableName = "health_records")
data class HealthRecord(
    @PrimaryKey val dateString: String, // YYYY-MM-DD
    val bodyCondition: String, // 舒适, 头晕, 睡眠不足, 肚子痛, 姨妈期, 胃胀疲倦
    val heightCm: Float = 165f, // 身高 (cm)
    val weightKg: Float = 48.5f, // 体重 (kg)
    val checkupStatus: String = "", // 体检状况 (如：血压正常、轻度贫血、缺乏维生素D)
    val discomfortArea: String = "", // 不适部位/体感
    val periodDay: Int = 0, // 0 = 非生理期, 1..7 天
    val careNotes: String = "" // 调养手记与备注
)

// 4. 绿植浇水
@Entity(tableName = "plant_items")
data class PlantItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val species: String,
    val location: String, // 客厅, 阳台, 卧室, 书房
    val lightDemand: String, // 喜阴, 半阴, 喜阳
    val wateringIntervalDays: Int = 5,
    val lastWateredTimestamp: Long = System.currentTimeMillis()
)

// 5. 私厨菜谱
@Entity(tableName = "recipe_items")
data class RecipeItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val stepsText: String, // 步骤说明
    val cookingTimeMinutes: Int,
    val difficulty: String, // 新手, 进阶, 大厨
    val levelCategory: String, // 招牌菜, 日常菜, 快手菜, 甜点
    val tagsText: String, // 辣, 清淡, 香辣, 酸甜
    val costAmount: Double,
    val rating: Int = 5,
    val isDailyRecommended: Boolean = false
)

// 6. 电子衣橱
@Entity(tableName = "wardrobe_items")
data class WardrobeItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // 上装, 下装, 外套, 鞋品, 配饰
    val season: String, // 春夏, 秋冬, 四季
    val color: String,
    val tagsText: String,
    val iconName: String = "ic_shirt"
)

// 7. 观影手记
@Entity(tableName = "movie_items")
data class MovieItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val mediaType: String, // 电影, 纪录片, 短片, 综艺, 剧集
    val platform: String, // Netflix, 爱奇艺, 腾讯视频, 电影院, Bilibili
    val rating: Int = 5,
    val tagsText: String, // 悬疑, 爱情, 治愈, 科幻
    val shortReview: String,
    val memorableQuote: String,
    val rewatchLevel: String, // 值得二刷, 值得三刷, 看一遍即可
    val mood: String, // 感动, 震撼, 轻松, 思考
    val viewDate: String
)

// 8. 良品宣物 (红黑榜)
@Entity(tableName = "good_review_items")
data class GoodReviewItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productName: String,
    val listType: String, // 红榜好物, 黑榜避坑
    val category: String, // 护肤, 数码, 家居, 零食, 服饰
    val pros: String,
    val cons: String,
    val purchaseChannel: String,
    val price: Double,
    val ratingStars: Int = 4
)

// 9. 礼尚往来
@Entity(tableName = "gift_ledger_items")
data class GiftLedgerItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ledgerType: String, // 送出, 收到
    val itemTitle: String,
    val valueAmount: Double,
    val targetPerson: String,
    val relationship: String, // 亲人, 恋人, 同事, 朋友
    val occasion: String, // 生日, 节日, 婚礼, 探望
    val myFeeling: String,
    val recipientReaction: String,
    val needReturnGift: Boolean = false,
    val dateString: String
)

// 10. 社交评评
@Entity(tableName = "social_log_items")
data class SocialLogItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String,
    val frequencyCount: Int = 1,
    val energyType: String, // 极速充电, 舒适放松, 轻微消耗, 深度榨干
    val score: Int = 4, // 1..5
    val reviewTags: String, // 聊得投机, 观点冲突, 氛围治愈, 需要独处
    val note: String
)

// 11. 沿途印记 (旅行足迹)
@Entity(tableName = "travel_items")
data class TravelItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityName: String,
    val tripTitle: String,
    val companions: String,
    val transport: String, // 高铁, 飞机, 自驾, 轮船
    val costAmount: Double,
    val experienceNote: String,
    val recommendIndex: Int = 5,
    val spotHighlights: String,
    val travelDate: String
)

// 12. 每日日程与提醒
@Entity(tableName = "schedule_items")
data class ScheduleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dateString: String = "2026-07-30", // YYYY-MM-DD
    val timeString: String = "09:00", // HH:mm
    val category: String = "日常", // 工作, 个人, 约会, 健康, 学习
    val priority: String = "中", // 高, 中, 低
    val locationOrNotes: String = "",
    val isReminderEnabled: Boolean = true,
    val reminderAdvanceMinutes: Int = 15, // 提前15分钟提醒
    val isCompleted: Boolean = false
)

// 13. 美食热量检测与摄入日志
@Entity(tableName = "calorie_log_items")
data class CalorieLogItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodName: String,
    val caloriesKcal: Int,
    val proteinGrams: Float = 0f,
    val carbsGrams: Float = 0f,
    val fatGrams: Float = 0f,
    val portionDesc: String = "1份",
    val dateString: String = "2026-07-30", // YYYY-MM-DD
    val mealType: String = "午餐", // 早餐, 午餐, 晚餐, 零食/下午茶
    val status: String = "EATEN", // EATEN = 已吃 / 计入摄入, SKIPPED = 不吃 / 仅检测
    val imageUri: String = "",
    val healthAdvice: String = ""
)
