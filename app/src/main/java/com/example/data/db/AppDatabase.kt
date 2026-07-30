package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ReminderItem::class,
        InspirationItem::class,
        HealthRecord::class,
        PlantItem::class,
        RecipeItem::class,
        WardrobeItem::class,
        MovieItem::class,
        GoodReviewItem::class,
        GiftLedgerItem::class,
        SocialLogItem::class,
        TravelItem::class,
        FeedbackItem::class,
        ScheduleItem::class,
        CalorieLogItem::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao
    abstract fun inspirationDao(): InspirationDao
    abstract fun healthDao(): HealthDao
    abstract fun plantDao(): PlantDao
    abstract fun recipeDao(): RecipeDao
    abstract fun wardrobeDao(): WardrobeDao
    abstract fun movieDao(): MovieDao
    abstract fun goodReviewDao(): GoodReviewDao
    abstract fun giftLedgerDao(): GiftLedgerDao
    abstract fun socialLogDao(): SocialLogDao
    abstract fun travelDao(): TravelDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun calorieDao(): CalorieDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "for_u_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        suspend fun populateInitialData(db: AppDatabase) {
            // Reminders
            val rDao = db.reminderDao()
            rDao.insertReminder(ReminderItem(title = "喝水打卡", subtitle = "今日目标 8 杯", category = "饮水", currentProgress = 4, targetProgress = 8, unit = "杯", isCompleted = false, dateString = "2026-07-30"))
            rDao.insertReminder(ReminderItem(title = "午餐营养提醒", subtitle = "补充维生素与优质蛋白", category = "饮食", currentProgress = 1, targetProgress = 1, unit = "", isCompleted = true, dateString = "2026-07-30"))
            rDao.insertReminder(ReminderItem(title = "阳台绿植浇水", subtitle = "小绿萝 & 桃蛋多肉检查", category = "绿植", currentProgress = 0, targetProgress = 1, unit = "", isCompleted = false, dateString = "2026-07-30"))
            rDao.insertReminder(ReminderItem(title = "生理期关怀", subtitle = "喝一杯温热红枣姜茶，注意保暖", category = "身体", currentProgress = 0, targetProgress = 1, unit = "", isCompleted = false, dateString = "2026-07-30"))

            // Inspiration
            val iDao = db.inspirationDao()
            iDao.insertInspiration(InspirationItem(title = "周末森林公园徒步拍摄案", content = "捕捉晨雾、松针落叶与光影。计划带上相机，顺便录一段治愈森林声音素材。", category = "创作", emotion = "兴奋", status = "孵化中", stars = 5))
            iDao.insertInspiration(InspirationItem(title = "阳台手作微景观设计", content = "用苔藓、白沙和小小的龙猫摆件搭建迷你雨林，给生活增加一点仪式感。", category = "脑洞", emotion = "平静", status = "已落地", stars = 4))
            iDao.insertInspiration(InspirationItem(title = "For U 私密生活手帐 APP", content = "森林绿治愈系配色，整合打卡、灵感、健康、菜谱、衣橱等全方位资产。", category = "计划", emotion = "激动", status = "已落地", stars = 5))

            // Health records for matrix
            val hDao = db.healthDao()
            val conditions = listOf("舒适", "舒适", "睡眠不足", "肚子痛", "姨妈期", "姨妈期", "胃胀疲倦", "微热", "舒适", "舒适", "头晕", "舒适", "舒适", "舒适")
            conditions.forEachIndexed { idx, cond ->
                val day = if (idx < 9) "0${idx + 1}" else "${idx + 1}"
                hDao.insertOrUpdateHealthRecord(
                    HealthRecord(
                        dateString = "2026-07-$day",
                        bodyCondition = cond,
                        heightCm = 165.0f,
                        weightKg = 48.5f + (idx % 3) * 0.2f,
                        checkupStatus = if (idx % 2 == 0) "血红蛋白偏低，缺乏维生素D" else "血压血脂正常，轻度疲劳",
                        discomfortArea = if (cond == "肚子痛" || cond == "姨妈期") "下腹微胀不适" else if (cond == "胃胀疲倦") "上腹消化不良" else "",
                        periodDay = if (cond == "姨妈期") (idx - 3) else 0,
                        careNotes = if (cond == "姨妈期") "少吃生冷，用热水袋敷腹部" else "多喝温水，规律作息"
                    )
                )
            }

            // Plants
            val pDao = db.plantDao()
            pDao.insertPlant(PlantItem(name = "小绿萝", species = "绿萝", location = "客厅电视柜", lightDemand = "半阴", wateringIntervalDays = 6, lastWateredTimestamp = System.currentTimeMillis() - 86400000L * 5))
            pDao.insertPlant(PlantItem(name = "桃蛋多肉", species = "多肉", location = "阳台窗台", lightDemand = "喜阳", wateringIntervalDays = 10, lastWateredTimestamp = System.currentTimeMillis() - 86400000L * 2))
            pDao.insertPlant(PlantItem(name = "琴叶榕", species = "观叶植物", location = "书房角落", lightDemand = "半阴", wateringIntervalDays = 7, lastWateredTimestamp = System.currentTimeMillis() - 86400000L * 7))

            // Recipes
            val recDao = db.recipeDao()
            recDao.insertRecipe(RecipeItem(name = "秘制辣炒排骨", stepsText = "1. 排骨焯水洗净；\n2. 锅中姜蒜炒香，下排骨翻炒至微黄；\n3. 加入生抽、老抽、料酒与特调辣酱，加水慢炖30分钟；\n4. 大火收汁，撒上青红椒与芝麻。", cookingTimeMinutes = 45, difficulty = "进阶", levelCategory = "招牌菜", tagsText = "香辣, 浓郁, 招牌", costAmount = 38.0, rating = 5, isDailyRecommended = true))
            recDao.insertRecipe(RecipeItem(name = "日式清炖牛肉萝卜", stepsText = "1. 牛腩切块焯水；\n2. 白萝卜切厚块；\n3. 昆布柴鱼高汤小火炖煮40分钟；\n4. 淋少许味醂与薄盐生抽，清甜暖胃。", cookingTimeMinutes = 50, difficulty = "日常", levelCategory = "日常菜", tagsText = "清淡, 鲜美, 暖胃", costAmount = 42.0, rating = 5, isDailyRecommended = false))
            recDao.insertRecipe(RecipeItem(name = "快手番茄浓汤虾仁", stepsText = "1. 番茄切碎炒出浓汤；\n2. 放入鲜虾仁与豆腐块；\n3. 撒少许黑胡椒粉即可出锅，美味低卡。", cookingTimeMinutes = 15, difficulty = "新手", levelCategory = "快手菜", tagsText = "减脂, 酸甜", costAmount = 25.0, rating = 4, isDailyRecommended = false))

            // Wardrobe
            val wDao = db.wardrobeDao()
            wDao.insertWardrobeItem(WardrobeItem(name = "森林绿软糯针织开衫", category = "外套", season = "春秋", color = "森林绿", tagsText = "温柔, 治愈, 宽松"))
            wDao.insertWardrobeItem(WardrobeItem(name = "纯棉米白色短袖T恤", category = "上装", season = "春夏", color = "米白色", tagsText = "百搭, 基础款"))
            wDao.insertWardrobeItem(WardrobeItem(name = "高腰浅蓝色直筒牛仔裤", category = "下装", season = "四季", color = "浅蓝色", tagsText = "显瘦, 舒适"))
            wDao.insertWardrobeItem(WardrobeItem(name = "复古德训帆布鞋", category = "鞋品", season = "四季", color = "米白黑拼色", tagsText = "好穿, 百搭"))

            // Movies
            val mDao = db.movieDao()
            mDao.insertMovie(MovieItem(title = "小森林 夏秋篇", mediaType = "电影", platform = "Bilibili", rating = 5, tagsText = "治愈, 美食, 自然", shortReview = "简单自然的生活，食物里藏着对大自然的敬畏与内心的宁静。", memorableQuote = "用心地做一顿饭，是对生活最好的尊重。", rewatchLevel = "值得三刷", mood = "轻松", viewDate = "2026-07-20"))
            mDao.insertMovie(MovieItem(title = "地球脉动 第三季", mediaType = "纪录片", platform = "iQIYI", rating = 5, tagsText = "自然, 壮丽, 震撼", shortReview = "生命的顽强与自然的伟力让人由衷敬畏。", memorableQuote = "在大自然的奇迹面前，人类只是谦卑的过客。", rewatchLevel = "值得二刷", mood = "震撼", viewDate = "2026-07-15"))

            // Good Reviews
            val gDao = db.goodReviewDao()
            gDao.insertGoodReview(GoodReviewItem(productName = "植物萃取保湿洁面乳", listType = "红榜好物", category = "护肤", pros = "泡沫极其细腻，洗完温和不紧绷，带着淡淡草本香。", cons = "价格稍贵，消耗比较快。", purchaseChannel = "官方旗舰店", price = 128.0, ratingStars = 5))
            gDao.insertGoodReview(GoodReviewItem(productName = "某网红静音无线键盘", listType = "黑榜避坑", category = "数码", pros = "外观马卡龙配色蛮好看。", cons = "按键粘手，延迟明显，蓝牙经常断连。", purchaseChannel = "第三方店", price = 89.0, ratingStars = 1))

            // Gift Ledger
            val giftDao = db.giftLedgerDao()
            giftDao.insertGift(GiftLedgerItem(ledgerType = "收到", itemTitle = "手作陶艺马克杯", valueAmount = 168.0, targetPerson = "小林", relationship = "朋友", occasion = "生日", myFeeling = "特别感动，造型独一无二！", recipientReaction = "期待我用来喝热可可", needReturnGift = true, dateString = "2026-07-10"))
            giftDao.insertGift(GiftLedgerItem(ledgerType = "送出", itemTitle = "黑胶唱片咖啡机", valueAmount = 520.0, targetPerson = "妈妈", relationship = "亲人", occasion = "节日", myFeeling = "看到妈妈开心一切都值了！", recipientReaction = "爱不释手，当天就冲了咖啡", needReturnGift = false, dateString = "2026-06-18"))

            // Social Log
            val sDao = db.socialLogDao()
            sDao.insertSocialLog(SocialLogItem(dateString = "2026-07-28", frequencyCount = 1, energyType = "舒适放松", score = 5, reviewTags = "氛围治愈, 聊得投机", note = "和老朋友在树荫咖啡馆坐了半天，畅谈近况，没有任何社交负担。"))
            sDao.insertSocialLog(SocialLogItem(dateString = "2026-07-25", frequencyCount = 1, energyType = "轻微消耗", score = 3, reviewTags = "需要独处", note = "参加了多人的商务聚餐，寒暄较多，回家后听舒缓音乐给自己充电。"))

            // Travel
            val tDao = db.travelDao()
            tDao.insertTravel(TravelItem(cityName = "大理", tripTitle = "洱海风光与古城慢时光", companions = "闺蜜", transport = "高铁", costAmount = 2450.0, experienceNote = "在喜洲古城骑单车，吹着洱海的风，吃喜洲粑粑与野生菌火锅，彻底放松！", recommendIndex = 5, spotHighlights = "洱海生态廊道、喜洲麦田、双廊日落", travelDate = "2026-06-12"))

            // Feedback
            val fbDao = db.feedbackDao()
            fbDao.insertFeedback(FeedbackItem(feedbackType = "体感体验", ratingStars = 5, content = "界面森林绿主题特别治愈，每天打卡生理期和喝水都很方便，曦曦非常喜欢！希望能增加更多网络菜谱灵感建议。", contactInfo = "walker47575@gmail.com", dateString = "2026-07-30"))

            // Schedule Items
            val schDao = db.scheduleDao()
            schDao.insertSchedule(ScheduleItem(title = "晨间瑜伽与身体拉伸", dateString = "2026-07-30", timeString = "08:00", category = "健康", priority = "高", locationOrNotes = "阳台瑜伽垫，配舒缓森林音效", isReminderEnabled = true, reminderAdvanceMinutes = 15, isCompleted = true))
            schDao.insertSchedule(ScheduleItem(title = "团队产品周度复盘例会", dateString = "2026-07-30", timeString = "10:30", category = "工作", priority = "高", locationOrNotes = "线上会议室 888", isReminderEnabled = true, reminderAdvanceMinutes = 10, isCompleted = false))
            schDao.insertSchedule(ScheduleItem(title = "拍摄午餐低卡绿植沙拉", dateString = "2026-07-30", timeString = "12:30", category = "健康", priority = "中", locationOrNotes = "私厨厨房", isReminderEnabled = true, reminderAdvanceMinutes = 5, isCompleted = false))
            schDao.insertSchedule(ScheduleItem(title = "晚上树荫公园散步放松", dateString = "2026-07-30", timeString = "19:30", category = "个人", priority = "低", locationOrNotes = "湖边步道", isReminderEnabled = true, reminderAdvanceMinutes = 30, isCompleted = false))

            // Calorie Log Items
            val calDao = db.calorieDao()
            calDao.insertCalorieLog(CalorieLogItem(foodName = "牛油果水煮蛋全麦沙拉", caloriesKcal = 380, proteinGrams = 18f, carbsGrams = 28f, fatGrams = 16f, portionDesc = "1碗 (约300g)", dateString = "2026-07-30", mealType = "早餐", status = "EATEN", imageUri = "sample_salad", healthAdvice = "优质健康脂肪与高膳食纤维组合，非常利于晨间稳糖和饱腹感！"))
            calDao.insertCalorieLog(CalorieLogItem(foodName = "香煎黑椒低脂鸡胸肉", caloriesKcal = 320, proteinGrams = 35f, carbsGrams = 8f, fatGrams = 12f, portionDesc = "1盘 (约200g)", dateString = "2026-07-30", mealType = "午餐", status = "EATEN", imageUri = "sample_chicken", healthAdvice = "高蛋白低碳水，能有效补充肌肉合成所需能量。"))
            calDao.insertCalorieLog(CalorieLogItem(foodName = "法式高糖芝士拿破仑蛋糕", caloriesKcal = 520, proteinGrams = 6f, carbsGrams = 62f, fatGrams = 28f, portionDesc = "1块", dateString = "2026-07-30", mealType = "零食/下午茶", status = "SKIPPED", imageUri = "sample_cake", healthAdvice = "🎉 成功忍住没吃！为你成功节省了 520 kcal 热量，避免了血糖骤升！"))
        }
    }
}
