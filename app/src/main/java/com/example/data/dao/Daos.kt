package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder_items ORDER BY isCompleted ASC, id DESC")
    fun getAllReminders(): Flow<List<ReminderItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(item: ReminderItem)

    @Update
    suspend fun updateReminder(item: ReminderItem)

    @Query("DELETE FROM reminder_items WHERE id = :id")
    suspend fun deleteReminderById(id: Long)
}

@Dao
interface InspirationDao {
    @Query("SELECT * FROM inspiration_items ORDER BY createdTimestamp DESC")
    fun getAllInspirations(): Flow<List<InspirationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspiration(item: InspirationItem)

    @Update
    suspend fun updateInspiration(item: InspirationItem)

    @Query("DELETE FROM inspiration_items WHERE id = :id")
    suspend fun deleteInspirationById(id: Long)
}

@Dao
interface HealthDao {
    @Query("SELECT * FROM health_records ORDER BY dateString DESC")
    fun getAllHealthRecords(): Flow<List<HealthRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHealthRecord(record: HealthRecord)
}

@Dao
interface PlantDao {
    @Query("SELECT * FROM plant_items ORDER BY name ASC")
    fun getAllPlants(): Flow<List<PlantItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantItem)

    @Update
    suspend fun updatePlant(plant: PlantItem)

    @Query("DELETE FROM plant_items WHERE id = :id")
    suspend fun deletePlantById(id: Long)
}

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipe_items ORDER BY isDailyRecommended DESC, id DESC")
    fun getAllRecipes(): Flow<List<RecipeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeItem)

    @Query("DELETE FROM recipe_items WHERE id = :id")
    suspend fun deleteRecipeById(id: Long)
}

@Dao
interface WardrobeDao {
    @Query("SELECT * FROM wardrobe_items ORDER BY category ASC, name ASC")
    fun getAllWardrobeItems(): Flow<List<WardrobeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWardrobeItem(item: WardrobeItem)

    @Query("DELETE FROM wardrobe_items WHERE id = :id")
    suspend fun deleteWardrobeItemById(id: Long)
}

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie_items ORDER BY id DESC")
    fun getAllMovies(): Flow<List<MovieItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieItem)

    @Query("DELETE FROM movie_items WHERE id = :id")
    suspend fun deleteMovieById(id: Long)
}

@Dao
interface GoodReviewDao {
    @Query("SELECT * FROM good_review_items ORDER BY id DESC")
    fun getAllGoodReviews(): Flow<List<GoodReviewItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoodReview(item: GoodReviewItem)

    @Query("DELETE FROM good_review_items WHERE id = :id")
    suspend fun deleteGoodReviewById(id: Long)
}

@Dao
interface GiftLedgerDao {
    @Query("SELECT * FROM gift_ledger_items ORDER BY id DESC")
    fun getAllGifts(): Flow<List<GiftLedgerItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGift(gift: GiftLedgerItem)

    @Query("DELETE FROM gift_ledger_items WHERE id = :id")
    suspend fun deleteGiftById(id: Long)
}

@Dao
interface SocialLogDao {
    @Query("SELECT * FROM social_log_items ORDER BY dateString DESC")
    fun getAllSocialLogs(): Flow<List<SocialLogItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocialLog(log: SocialLogItem)

    @Query("DELETE FROM social_log_items WHERE id = :id")
    suspend fun deleteSocialLogById(id: Long)
}

@Dao
interface TravelDao {
    @Query("SELECT * FROM travel_items ORDER BY travelDate DESC")
    fun getAllTravels(): Flow<List<TravelItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravel(travel: TravelItem)

    @Query("DELETE FROM travel_items WHERE id = :id")
    suspend fun deleteTravelById(id: Long)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_items ORDER BY isCompleted ASC, dateString ASC, timeString ASC")
    fun getAllSchedules(): Flow<List<ScheduleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(item: ScheduleItem)

    @Update
    suspend fun updateSchedule(item: ScheduleItem)

    @Query("DELETE FROM schedule_items WHERE id = :id")
    suspend fun deleteScheduleById(id: Long)
}

@Dao
interface CalorieDao {
    @Query("SELECT * FROM calorie_log_items ORDER BY id DESC")
    fun getAllCalorieLogs(): Flow<List<CalorieLogItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalorieLog(item: CalorieLogItem)

    @Query("DELETE FROM calorie_log_items WHERE id = :id")
    suspend fun deleteCalorieLogById(id: Long)
}
