package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val description: String,
    val durationHours: Int,
    val modulesCount: Int,
    val level: String,
    val enrolled: Boolean = true,
    val progressPct: Int = 0,
    val isDownloaded: Boolean = false,
    val careerTrack: String,
    val rating: Float = 4.8f,
    val enrolledStudents: Int = 1240,
    val colorHex: Long = 0xFF1E3A8A,
    val iconName: String = "computer"
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val title: String,
    val durationMin: Int,
    val moduleTitle: String,
    val orderIndex: Int,
    val contentType: String, // "interactive_guide", "practical_lab", "video_simulation", "quiz_challenge"
    val contentBody: String,
    val keyTakeaways: String = "",
    val practicalTask: String = "",
    val quizQuestionsJson: String = "",
    val isCompleted: Boolean = false,
    val isDownloaded: Boolean = false,
    val userScore: Int = 0
)

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Priya Sharma",
    val email: String = "priya.anudip@diya.edu",
    val careerTrack: String = "Digital Workplace & Data Executive",
    val targetJobRole: String = "Data Analyst & Operations Specialist",
    val xpPoints: Int = 1450,
    val level: Int = 4,
    val levelTitle: String = "Skill Artisan",
    val streakDays: Int = 8,
    val streakFrozen: Boolean = false,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val weeklyGoalMinutes: Int = 180,
    val weeklySpentMinutes: Int = 125,
    val resumeReadinessPct: Int = 78,
    val centerLocation: String = "Anudip Skill Center - Salt Lake Hub",
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val isSyncing: Boolean = false,
    val completedLessonsCount: Int = 14
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String, // "Streak", "Mastery", "Speed", "Feedback"
    val iconType: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val progress: Int = 0,
    val maxProgress: Int = 100,
    val xpReward: Int = 100
)

@Entity(tableName = "teacher_feedback")
data class FeedbackThreadEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val assignmentTitle: String,
    val studentSubmission: String,
    val teacherName: String,
    val teacherRole: String,
    val teacherAvatarInitial: String = "R",
    val teacherComment: String,
    val strengths: String,
    val actionableTips: String,
    val score: Int,
    val maxScore: Int = 100,
    val status: String = "Graded", // "Graded", "Pending Review", "Needs Revision"
    val timestamp: Long = System.currentTimeMillis() - 3600000 * 5,
    val studentReply: String? = null
)

@Entity(tableName = "goal_milestones")
data class GoalMilestoneEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val dueDate: String,
    val isCompleted: Boolean = false,
    val rewardXp: Int = 50
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)
