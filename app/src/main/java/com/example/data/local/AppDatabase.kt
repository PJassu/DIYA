package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.AchievementEntity
import com.example.data.model.CourseEntity
import com.example.data.model.FeedbackThreadEntity
import com.example.data.model.GoalMilestoneEntity
import com.example.data.model.LessonEntity
import com.example.data.model.StudentProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY enrolled DESC, progressPct DESC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE enrolled = 1 ORDER BY progressPct DESC")
    fun getEnrolledCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE isDownloaded = 1")
    fun getDownloadedCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getCourseById(courseId: String): Flow<CourseEntity?>

    @Query("SELECT * FROM courses WHERE careerTrack = :track OR category = :track")
    fun getCoursesByTrack(track: String): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Query("UPDATE courses SET isDownloaded = :isDownloaded WHERE id = :courseId")
    suspend fun setDownloaded(courseId: String, isDownloaded: Boolean)

    @Query("UPDATE courses SET progressPct = :progress WHERE id = :courseId")
    suspend fun updateProgress(courseId: String, progress: Int)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY orderIndex ASC")
    fun getLessonsForCourse(courseId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    fun getLessonById(lessonId: String): Flow<LessonEntity?>

    @Query("SELECT * FROM lessons WHERE isDownloaded = 1")
    fun getDownloadedLessons(): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Query("UPDATE lessons SET isCompleted = :completed, userScore = :score WHERE id = :lessonId")
    suspend fun markLessonCompleted(lessonId: String, completed: Boolean, score: Int)

    @Query("UPDATE lessons SET isDownloaded = :downloaded WHERE id = :lessonId")
    suspend fun setLessonDownloaded(lessonId: String, downloaded: Boolean)

    @Query("SELECT COUNT(*) FROM lessons WHERE isCompleted = 1")
    fun getCompletedLessonCount(): Flow<Int>
}

@Dao
interface StudentProfileDao {
    @Query("SELECT * FROM student_profile WHERE id = 1")
    fun getProfile(): Flow<StudentProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: StudentProfileEntity)

    @Query("UPDATE student_profile SET xpPoints = xpPoints + :xp, completedLessonsCount = completedLessonsCount + 1, weeklySpentMinutes = weeklySpentMinutes + :minutesSpent WHERE id = 1")
    suspend fun addXpAndProgress(xp: Int, minutesSpent: Int)

    @Query("UPDATE student_profile SET streakDays = streakDays + 1, lastActiveDate = :now WHERE id = 1")
    suspend fun incrementStreak(now: Long = System.currentTimeMillis())

    @Query("UPDATE student_profile SET careerTrack = :newTrack, targetJobRole = :jobRole WHERE id = 1")
    suspend fun updateCareerTrack(newTrack: String, jobRole: String)

    @Query("UPDATE student_profile SET isSyncing = :isSyncing, lastSyncTimestamp = :timestamp WHERE id = 1")
    suspend fun updateSyncState(isSyncing: Boolean, timestamp: Long)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, xpReward DESC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :timestamp, progress = maxProgress WHERE id = :id")
    suspend fun unlockAchievement(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE achievements SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int)
}

@Dao
interface FeedbackDao {
    @Query("SELECT * FROM teacher_feedback ORDER BY timestamp DESC")
    fun getAllFeedback(): Flow<List<FeedbackThreadEntity>>

    @Query("SELECT * FROM teacher_feedback WHERE status = 'Pending Review'")
    fun getPendingFeedback(): Flow<List<FeedbackThreadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackThreadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedbackList(feedbackList: List<FeedbackThreadEntity>)

    @Query("UPDATE teacher_feedback SET studentReply = :reply WHERE id = :id")
    suspend fun addStudentReply(id: String, reply: String)
}

@Dao
interface GoalMilestoneDao {
    @Query("SELECT * FROM goal_milestones ORDER BY isCompleted ASC")
    fun getAllMilestones(): Flow<List<GoalMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<GoalMilestoneEntity>)

    @Query("UPDATE goal_milestones SET isCompleted = :completed WHERE id = :id")
    suspend fun setMilestoneCompleted(id: String, completed: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMilestone(milestone: GoalMilestoneEntity)
}

@Database(
    entities = [
        CourseEntity::class,
        LessonEntity::class,
        StudentProfileEntity::class,
        AchievementEntity::class,
        FeedbackThreadEntity::class,
        GoalMilestoneEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun lessonDao(): LessonDao
    abstract fun profileDao(): StudentProfileDao
    abstract fun achievementDao(): AchievementDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun goalDao(): GoalMilestoneDao
}
