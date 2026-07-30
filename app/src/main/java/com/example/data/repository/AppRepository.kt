package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {

    // 1. Reminders
    val allReminders: Flow<List<ReminderItem>> = db.reminderDao().getAllReminders()
    suspend fun insertReminder(item: ReminderItem) = db.reminderDao().insertReminder(item)
    suspend fun updateReminder(item: ReminderItem) = db.reminderDao().updateReminder(item)
    suspend fun deleteReminder(id: Long) = db.reminderDao().deleteReminderById(id)

    // 2. Inspiration
    val allInspirations: Flow<List<InspirationItem>> = db.inspirationDao().getAllInspirations()
    suspend fun insertInspiration(item: InspirationItem) = db.inspirationDao().insertInspiration(item)
    suspend fun updateInspiration(item: InspirationItem) = db.inspirationDao().updateInspiration(item)
    suspend fun deleteInspiration(id: Long) = db.inspirationDao().deleteInspirationById(id)

    // 3. Health & Period
    val allHealthRecords: Flow<List<HealthRecord>> = db.healthDao().getAllHealthRecords()
    suspend fun saveHealthRecord(record: HealthRecord) = db.healthDao().insertOrUpdateHealthRecord(record)

    // 4. Plant
    val allPlants: Flow<List<PlantItem>> = db.plantDao().getAllPlants()
    suspend fun insertPlant(plant: PlantItem) = db.plantDao().insertPlant(plant)
    suspend fun updatePlant(plant: PlantItem) = db.plantDao().updatePlant(plant)
    suspend fun deletePlant(id: Long) = db.plantDao().deletePlantById(id)

    // 5. Recipe
    val allRecipes: Flow<List<RecipeItem>> = db.recipeDao().getAllRecipes()
    suspend fun insertRecipe(recipe: RecipeItem) = db.recipeDao().insertRecipe(recipe)
    suspend fun deleteRecipe(id: Long) = db.recipeDao().deleteRecipeById(id)

    // 6. Wardrobe
    val allWardrobeItems: Flow<List<WardrobeItem>> = db.wardrobeDao().getAllWardrobeItems()
    suspend fun insertWardrobeItem(item: WardrobeItem) = db.wardrobeDao().insertWardrobeItem(item)
    suspend fun deleteWardrobeItem(id: Long) = db.wardrobeDao().deleteWardrobeItemById(id)

    // 7. Movie
    val allMovies: Flow<List<MovieItem>> = db.movieDao().getAllMovies()
    suspend fun insertMovie(movie: MovieItem) = db.movieDao().insertMovie(movie)
    suspend fun deleteMovie(id: Long) = db.movieDao().deleteMovieById(id)

    // 8. Good Review (Red/Black)
    val allGoodReviews: Flow<List<GoodReviewItem>> = db.goodReviewDao().getAllGoodReviews()
    suspend fun insertGoodReview(item: GoodReviewItem) = db.goodReviewDao().insertGoodReview(item)
    suspend fun deleteGoodReview(id: Long) = db.goodReviewDao().deleteGoodReviewById(id)

    // 9. Gift Ledger
    val allGifts: Flow<List<GiftLedgerItem>> = db.giftLedgerDao().getAllGifts()
    suspend fun insertGift(gift: GiftLedgerItem) = db.giftLedgerDao().insertGift(gift)
    suspend fun deleteGift(id: Long) = db.giftLedgerDao().deleteGiftById(id)

    // 10. Social Log
    val allSocialLogs: Flow<List<SocialLogItem>> = db.socialLogDao().getAllSocialLogs()
    suspend fun insertSocialLog(log: SocialLogItem) = db.socialLogDao().insertSocialLog(log)
    suspend fun deleteSocialLog(id: Long) = db.socialLogDao().deleteSocialLogById(id)

    // 11. Travel
    val allTravels: Flow<List<TravelItem>> = db.travelDao().getAllTravels()
    suspend fun insertTravel(travel: TravelItem) = db.travelDao().insertTravel(travel)
    suspend fun deleteTravel(id: Long) = db.travelDao().deleteTravelById(id)

    // 12. Feedback
    val allFeedbacks: Flow<List<FeedbackItem>> = db.feedbackDao().getAllFeedbacks()
    suspend fun insertFeedback(feedback: FeedbackItem) = db.feedbackDao().insertFeedback(feedback)
    suspend fun deleteFeedback(id: Long) = db.feedbackDao().deleteFeedback(id)

    // 13. Schedule
    val allSchedules: Flow<List<ScheduleItem>> = db.scheduleDao().getAllSchedules()
    suspend fun insertSchedule(item: ScheduleItem) = db.scheduleDao().insertSchedule(item)
    suspend fun updateSchedule(item: ScheduleItem) = db.scheduleDao().updateSchedule(item)
    suspend fun deleteSchedule(id: Long) = db.scheduleDao().deleteScheduleById(id)

    // 14. Calorie Log
    val allCalorieLogs: Flow<List<CalorieLogItem>> = db.calorieDao().getAllCalorieLogs()
    suspend fun insertCalorieLog(item: CalorieLogItem) = db.calorieDao().insertCalorieLog(item)
    suspend fun deleteCalorieLog(id: Long) = db.calorieDao().deleteCalorieLogById(id)
}
