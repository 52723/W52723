package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.entity.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    val reminders: StateFlow<List<ReminderItem>>
    val inspirations: StateFlow<List<InspirationItem>>
    val healthRecords: StateFlow<List<HealthRecord>>
    val plants: StateFlow<List<PlantItem>>
    val recipes: StateFlow<List<RecipeItem>>
    val wardrobeItems: StateFlow<List<WardrobeItem>>
    val movies: StateFlow<List<MovieItem>>
    val goodReviews: StateFlow<List<GoodReviewItem>>
    val gifts: StateFlow<List<GiftLedgerItem>>
    val socialLogs: StateFlow<List<SocialLogItem>>
    val travels: StateFlow<List<TravelItem>>
    val feedbacks: StateFlow<List<FeedbackItem>>
    val schedules: StateFlow<List<ScheduleItem>>
    val calorieLogs: StateFlow<List<CalorieLogItem>>

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = AppRepository(database)

        val flowStopTimeout = SharingStarted.WhileSubscribed(5000)

        reminders = repository.allReminders.stateIn(viewModelScope, flowStopTimeout, emptyList())
        inspirations = repository.allInspirations.stateIn(viewModelScope, flowStopTimeout, emptyList())
        healthRecords = repository.allHealthRecords.stateIn(viewModelScope, flowStopTimeout, emptyList())
        plants = repository.allPlants.stateIn(viewModelScope, flowStopTimeout, emptyList())
        recipes = repository.allRecipes.stateIn(viewModelScope, flowStopTimeout, emptyList())
        wardrobeItems = repository.allWardrobeItems.stateIn(viewModelScope, flowStopTimeout, emptyList())
        movies = repository.allMovies.stateIn(viewModelScope, flowStopTimeout, emptyList())
        goodReviews = repository.allGoodReviews.stateIn(viewModelScope, flowStopTimeout, emptyList())
        gifts = repository.allGifts.stateIn(viewModelScope, flowStopTimeout, emptyList())
        socialLogs = repository.allSocialLogs.stateIn(viewModelScope, flowStopTimeout, emptyList())
        travels = repository.allTravels.stateIn(viewModelScope, flowStopTimeout, emptyList())
        feedbacks = repository.allFeedbacks.stateIn(viewModelScope, flowStopTimeout, emptyList())
        schedules = repository.allSchedules.stateIn(viewModelScope, flowStopTimeout, emptyList())
        calorieLogs = repository.allCalorieLogs.stateIn(viewModelScope, flowStopTimeout, emptyList())
    }

    // --- Reminders Actions ---
    fun toggleReminderCompleted(item: ReminderItem) {
        viewModelScope.launch {
            repository.updateReminder(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun incrementWaterProgress(item: ReminderItem) {
        if (item.currentProgress < item.targetProgress) {
            viewModelScope.launch {
                val newProgress = item.currentProgress + 1
                repository.updateReminder(
                    item.copy(
                        currentProgress = newProgress,
                        isCompleted = newProgress >= item.targetProgress
                    )
                )
            }
        }
    }

    fun addReminder(title: String, subtitle: String, category: String, target: Int, unit: String) {
        viewModelScope.launch {
            repository.insertReminder(
                ReminderItem(
                    title = title,
                    subtitle = subtitle,
                    category = category,
                    targetProgress = target,
                    unit = unit,
                    dateString = "2026-07-30"
                )
            )
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch { repository.deleteReminder(id) }
    }

    // --- Inspiration Actions ---
    fun addInspiration(title: String, content: String, category: String, emotion: String, status: String, stars: Int) {
        viewModelScope.launch {
            repository.insertInspiration(
                InspirationItem(
                    title = title,
                    content = content,
                    category = category,
                    emotion = emotion,
                    status = status,
                    stars = stars
                )
            )
        }
    }

    fun deleteInspiration(id: Long) {
        viewModelScope.launch { repository.deleteInspiration(id) }
    }

    // --- Health Record Actions ---
    fun saveHealthRecord(
        date: String,
        condition: String,
        height: Float,
        weight: Float,
        checkupStatus: String,
        discomfort: String,
        periodDay: Int,
        notes: String
    ) {
        viewModelScope.launch {
            repository.saveHealthRecord(
                HealthRecord(
                    dateString = date,
                    bodyCondition = condition,
                    heightCm = height,
                    weightKg = weight,
                    checkupStatus = checkupStatus,
                    discomfortArea = discomfort,
                    periodDay = periodDay,
                    careNotes = notes
                )
            )
        }
    }

    // --- Plant Actions ---
    fun waterPlantNow(plant: PlantItem) {
        viewModelScope.launch {
            repository.updatePlant(plant.copy(lastWateredTimestamp = System.currentTimeMillis()))
        }
    }

    fun addPlant(name: String, species: String, location: String, lightDemand: String, intervalDays: Int) {
        viewModelScope.launch {
            repository.insertPlant(
                PlantItem(
                    name = name,
                    species = species,
                    location = location,
                    lightDemand = lightDemand,
                    wateringIntervalDays = intervalDays
                )
            )
        }
    }

    fun deletePlant(id: Long) {
        viewModelScope.launch { repository.deletePlant(id) }
    }

    // --- Recipe Actions ---
    fun addRecipe(name: String, steps: String, timeMinutes: Int, difficulty: String, levelCategory: String, tags: String, cost: Double, isRecommended: Boolean) {
        viewModelScope.launch {
            repository.insertRecipe(
                RecipeItem(
                    name = name,
                    stepsText = steps,
                    cookingTimeMinutes = timeMinutes,
                    difficulty = difficulty,
                    levelCategory = levelCategory,
                    tagsText = tags,
                    costAmount = cost,
                    isDailyRecommended = isRecommended
                )
            )
        }
    }

    fun deleteRecipe(id: Long) {
        viewModelScope.launch { repository.deleteRecipe(id) }
    }

    // --- Wardrobe Actions ---
    fun addWardrobeItem(name: String, category: String, season: String, color: String, tags: String) {
        viewModelScope.launch {
            repository.insertWardrobeItem(
                WardrobeItem(
                    name = name,
                    category = category,
                    season = season,
                    color = color,
                    tagsText = tags
                )
            )
        }
    }

    fun deleteWardrobeItem(id: Long) {
        viewModelScope.launch { repository.deleteWardrobeItem(id) }
    }

    // --- Movie Actions ---
    fun addMovie(title: String, mediaType: String, platform: String, rating: Int, tags: String, review: String, quote: String, rewatch: String, mood: String, date: String) {
        viewModelScope.launch {
            repository.insertMovie(
                MovieItem(
                    title = title,
                    mediaType = mediaType,
                    platform = platform,
                    rating = rating,
                    tagsText = tags,
                    shortReview = review,
                    memorableQuote = quote,
                    rewatchLevel = rewatch,
                    mood = mood,
                    viewDate = date
                )
            )
        }
    }

    fun deleteMovie(id: Long) {
        viewModelScope.launch { repository.deleteMovie(id) }
    }

    // --- Good Review Actions ---
    fun addGoodReview(name: String, listType: String, category: String, pros: String, cons: String, channel: String, price: Double, stars: Int) {
        viewModelScope.launch {
            repository.insertGoodReview(
                GoodReviewItem(
                    productName = name,
                    listType = listType,
                    category = category,
                    pros = pros,
                    cons = cons,
                    purchaseChannel = channel,
                    price = price,
                    ratingStars = stars
                )
            )
        }
    }

    fun deleteGoodReview(id: Long) {
        viewModelScope.launch { repository.deleteGoodReview(id) }
    }

    // --- Gift Ledger Actions ---
    fun addGift(type: String, item: String, value: Double, person: String, relationship: String, occasion: String, myFeeling: String, reaction: String, needReturn: Boolean, date: String) {
        viewModelScope.launch {
            repository.insertGift(
                GiftLedgerItem(
                    ledgerType = type,
                    itemTitle = item,
                    valueAmount = value,
                    targetPerson = person,
                    relationship = relationship,
                    occasion = occasion,
                    myFeeling = myFeeling,
                    recipientReaction = reaction,
                    needReturnGift = needReturn,
                    dateString = date
                )
            )
        }
    }

    fun deleteGift(id: Long) {
        viewModelScope.launch { repository.deleteGift(id) }
    }

    // --- Social Log Actions ---
    fun addSocialLog(date: String, energyType: String, score: Int, reviewTags: String, note: String) {
        viewModelScope.launch {
            repository.insertSocialLog(
                SocialLogItem(
                    dateString = date,
                    energyType = energyType,
                    score = score,
                    reviewTags = reviewTags,
                    note = note
                )
            )
        }
    }

    fun deleteSocialLog(id: Long) {
        viewModelScope.launch { repository.deleteSocialLog(id) }
    }

    // --- Travel Actions ---
    fun addTravel(city: String, tripTitle: String, companions: String, transport: String, cost: Double, experience: String, recommendIndex: Int, spotHighlights: String, travelDate: String) {
        viewModelScope.launch {
            repository.insertTravel(
                TravelItem(
                    cityName = city,
                    tripTitle = tripTitle,
                    companions = companions,
                    transport = transport,
                    costAmount = cost,
                    experienceNote = experience,
                    recommendIndex = recommendIndex,
                    spotHighlights = spotHighlights,
                    travelDate = travelDate
                )
            )
        }
    }

    fun deleteTravel(id: Long) {
        viewModelScope.launch { repository.deleteTravel(id) }
    }

    // --- Feedback Actions ---
    fun addFeedback(type: String, rating: Int, content: String, contactInfo: String) {
        viewModelScope.launch {
            repository.insertFeedback(
                FeedbackItem(
                    feedbackType = type,
                    ratingStars = rating,
                    content = content,
                    contactInfo = contactInfo,
                    dateString = "2026-07-30"
                )
            )
        }
    }

    fun deleteFeedback(id: Long) {
        viewModelScope.launch { repository.deleteFeedback(id) }
    }

    // --- Schedule Actions ---
    fun addSchedule(
        title: String,
        date: String,
        time: String,
        category: String,
        priority: String,
        notes: String,
        isReminderEnabled: Boolean,
        advanceMinutes: Int
    ) {
        viewModelScope.launch {
            repository.insertSchedule(
                ScheduleItem(
                    title = title,
                    dateString = date,
                    timeString = time,
                    category = category,
                    priority = priority,
                    locationOrNotes = notes,
                    isReminderEnabled = isReminderEnabled,
                    reminderAdvanceMinutes = advanceMinutes,
                    isCompleted = false
                )
            )
        }
    }

    fun toggleScheduleCompleted(item: ScheduleItem) {
        viewModelScope.launch {
            repository.updateSchedule(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun toggleScheduleReminder(item: ScheduleItem) {
        viewModelScope.launch {
            repository.updateSchedule(item.copy(isReminderEnabled = !item.isReminderEnabled))
        }
    }

    fun deleteSchedule(id: Long) {
        viewModelScope.launch { repository.deleteSchedule(id) }
    }

    // --- Calorie Log Actions ---
    fun addCalorieLog(
        foodName: String,
        caloriesKcal: Int,
        protein: Float,
        carbs: Float,
        fat: Float,
        portion: String,
        date: String,
        mealType: String,
        status: String, // EATEN or SKIPPED
        imageUri: String,
        advice: String
    ) {
        viewModelScope.launch {
            repository.insertCalorieLog(
                CalorieLogItem(
                    foodName = foodName,
                    caloriesKcal = caloriesKcal,
                    proteinGrams = protein,
                    carbsGrams = carbs,
                    fatGrams = fat,
                    portionDesc = portion,
                    dateString = date,
                    mealType = mealType,
                    status = status,
                    imageUri = imageUri,
                    healthAdvice = advice
                )
            )
        }
    }

    fun deleteCalorieLog(id: Long) {
        viewModelScope.launch { repository.deleteCalorieLog(id) }
    }

    // --- Document Export Generator ---
    fun generateExportMarkdownDoc(exportFullReport: Boolean = false): String {
        val sb = StringBuilder()
        val dateStr = "2026-07-30"
        if (exportFullReport) {
            sb.append("# 🌿 For U - 曦曦的专属生活与反馈档案汇总\n")
            sb.append("> 导出时间：$dateStr | 导出设备：For U Android App\n\n")

            sb.append("--- \n\n")
            sb.append("## 一、用户反馈与意见汇总 (${feedbacks.value.size} 条)\n")
            if (feedbacks.value.isEmpty()) {
                sb.append("暂无反馈记录。\n\n")
            } else {
                feedbacks.value.forEachIndexed { idx, fb ->
                    val stars = "★".repeat(fb.ratingStars) + "☆".repeat(5 - fb.ratingStars)
                    sb.append("### Feedback #${idx + 1} [$stars]\n")
                    sb.append("- **反馈类型**: ${fb.feedbackType}\n")
                    sb.append("- **提交时间**: ${fb.dateString}\n")
                    if (fb.contactInfo.isNotBlank()) sb.append("- **联系方式**: ${fb.contactInfo}\n")
                    sb.append("- **反馈内容**: ${fb.content}\n\n")
                }
            }

            sb.append("--- \n\n")
            sb.append("## 二、每日打卡与关怀提醒 (${reminders.value.size} 项)\n")
            reminders.value.forEach { r ->
                val status = if (r.isCompleted) "✓ 已完成" else "○ 进行中"
                val prog = if (r.targetProgress > 0) " (${r.currentProgress}/${r.targetProgress} ${r.unit})" else ""
                sb.append("- [$status] **${r.title}** - ${r.subtitle}$prog [${r.category}]\n")
            }
            sb.append("\n")

            sb.append("## 三、健康、体感与生理期档案 (${healthRecords.value.size} 篇)\n")
            healthRecords.value.take(7).forEach { h ->
                val heightM = h.heightCm / 100f
                val bmi = if (heightM > 0) h.weightKg / (heightM * heightM) else 0f
                val bmiStr = String.format("%.1f", bmi)
                sb.append("- **${h.dateString}**: 状态[${h.bodyCondition}] | 身高: ${h.heightCm}cm | 体重: ${h.weightKg}kg (BMI: $bmiStr)")
                if (h.checkupStatus.isNotBlank()) sb.append(" | 体检状况: ${h.checkupStatus}")
                if (h.periodDay > 0) sb.append(" | 姨妈期第 ${h.periodDay} 天")
                if (h.careNotes.isNotBlank()) sb.append(" | 调养手记: ${h.careNotes}")
                sb.append("\n")
            }
            sb.append("\n")

            sb.append("## 四、灵感脑洞集锦 (${inspirations.value.size} 条)\n")
            inspirations.value.forEach { i ->
                sb.append("- **${i.title}** [${i.category}/${i.emotion}/${i.status}]: ${i.content}\n")
            }
            sb.append("\n")

            sb.append("## 五、治愈菜谱与美食 (${recipes.value.size} 道)\n")
            recipes.value.forEach { r ->
                sb.append("- **${r.name}** [${r.levelCategory}/${r.difficulty}/${r.cookingTimeMinutes}分钟]: 预算￥${r.costAmount}\n")
            }
            sb.append("\n")

            sb.append("--- \n")
            sb.append("*本篇文档由 For U 自动汇总导出，感谢曦曦对应用的支持！*\n")
        } else {
            sb.append("# 📝 For U - 曦曦的用户反馈导出文档\n")
            sb.append("> 导出时间：$dateStr\n\n")
            if (feedbacks.value.isEmpty()) {
                sb.append("当前暂无反馈记录。\n")
            } else {
                feedbacks.value.forEachIndexed { idx, fb ->
                    val stars = "★".repeat(fb.ratingStars) + "☆".repeat(5 - fb.ratingStars)
                    sb.append("### 反馈记录 #${idx + 1}\n")
                    sb.append("• 类型：${fb.feedbackType}\n")
                    sb.append("• 评分：$stars (${fb.ratingStars}/5)\n")
                    sb.append("• 时间：${fb.dateString}\n")
                    if (fb.contactInfo.isNotBlank()) sb.append("• 联系方式：${fb.contactInfo}\n")
                    sb.append("• 详细内容：\n${fb.content}\n\n")
                    sb.append("----------------------------------------\n\n")
                }
            }
        }
        return sb.toString()
    }
}
