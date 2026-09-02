package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AchievementEntity
import com.example.data.model.CourseEntity
import com.example.data.model.FeedbackThreadEntity
import com.example.data.model.GoalMilestoneEntity
import com.example.data.model.LessonEntity
import com.example.data.model.StudentProfileEntity
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val center: String,
    val xp: Int,
    val streakDays: Int,
    val isCurrentUser: Boolean = false,
    val avatarColor: Long = 0xFF1E3A8A
)

enum class AppNavTab(val title: String, val iconName: String) {
    DASHBOARD("Dashboard", "dashboard"),
    COURSES("My Paths", "school"),
    PRACTICE("Diagnostic", "psychology"),
    FEEDBACK("Mentor Loop", "rate_review"),
    GAMIFICATION("Rank & Badges", "emoji_events"),
    PROFILE("Profile & Sync", "account_circle")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository.getInstance(application)

    val allCourses: StateFlow<List<CourseEntity>> = repository.coursesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val enrolledCourses: StateFlow<List<CourseEntity>> = repository.enrolledCoursesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedCourses: StateFlow<List<CourseEntity>> = repository.downloadedCoursesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentProfile: StateFlow<StudentProfileEntity?> = repository.profileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val achievements: StateFlow<List<AchievementEntity>> = repository.achievementsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val teacherFeedback: StateFlow<List<FeedbackThreadEntity>> = repository.feedbackFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goalMilestones: StateFlow<List<GoalMilestoneEntity>> = repository.milestonesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Tab
    private val _currentTab = MutableStateFlow(AppNavTab.DASHBOARD)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // Selected Course for Detail/Lessons
    private val _selectedCourse = MutableStateFlow<CourseEntity?>(null)
    val selectedCourse: StateFlow<CourseEntity?> = _selectedCourse.asStateFlow()

    // Selected Lesson for Active Reader / Lab / Quiz
    private val _activeLesson = MutableStateFlow<LessonEntity?>(null)
    val activeLesson: StateFlow<LessonEntity?> = _activeLesson.asStateFlow()

    // Active Lessons for Selected Course
    private val _courseLessons = MutableStateFlow<List<LessonEntity>>(emptyList())
    val courseLessons: StateFlow<List<LessonEntity>> = _courseLessons.asStateFlow()

    // Filter by Category/Track
    private val _selectedCategory = MutableStateFlow("All Tracks")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Offline Only Filter
    private val _offlineOnlyFilter = MutableStateFlow(false)
    val offlineOnlyFilter: StateFlow<Boolean> = _offlineOnlyFilter.asStateFlow()

    // Sync Notification Message
    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Leaderboard List
    val leaderboard = listOf(
        LeaderboardUser(1, "Aakash Roy", "Kolkata Hub", 2420, 14, false, 0xFF7C3AED),
        LeaderboardUser(2, "Sunita Mehra", "Mumbai Hub", 2180, 12, false, 0xFF0D9488),
        LeaderboardUser(3, "Priya Sharma (You)", "Salt Lake Hub", 1850, 9, true, 0xFF1E3A8A),
        LeaderboardUser(4, "Rahul Verma", "Delhi NCR Hub", 1640, 7, false, 0xFFD97706),
        LeaderboardUser(5, "Fatima Khan", "Hyderabad Hub", 1520, 6, false, 0xFFEA580C),
        LeaderboardUser(6, "Deepak Joshi", "Pune Center", 1390, 5, false, 0xFF2563EB)
    )

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun openCourseDetails(course: CourseEntity) {
        _selectedCourse.value = course
        viewModelScope.launch {
            repository.getLessonsForCourse(course.id).collect {
                _courseLessons.value = it
            }
        }
    }

    fun closeCourseDetails() {
        _selectedCourse.value = null
        _activeLesson.value = null
    }

    fun openLesson(lesson: LessonEntity) {
        _activeLesson.value = lesson
    }

    fun closeLesson() {
        _activeLesson.value = null
    }

    fun setCategoryFilter(category: String) {
        _selectedCategory.value = category
    }

    fun toggleOfflineFilter() {
        _offlineOnlyFilter.value = !_offlineOnlyFilter.value
    }

    fun completeLesson(lessonId: String, score: Int, xpEarned: Int, courseId: String) {
        viewModelScope.launch {
            repository.completeLesson(lessonId, score, xpEarned, courseId)
            // Refresh active lesson
            _activeLesson.value = _activeLesson.value?.copy(isCompleted = true, userScore = score)
        }
    }

    fun toggleCourseDownload(course: CourseEntity) {
        viewModelScope.launch {
            repository.toggleCourseDownload(course.id, course.isDownloaded)
            _selectedCourse.value = _selectedCourse.value?.copy(isDownloaded = !course.isDownloaded)
        }
    }

    fun toggleLessonDownload(lesson: LessonEntity) {
        viewModelScope.launch {
            repository.toggleLessonDownload(lesson.id, lesson.isDownloaded)
            _activeLesson.value = _activeLesson.value?.copy(isDownloaded = !lesson.isDownloaded)
        }
    }

    fun enrollInCourse(course: CourseEntity) {
        viewModelScope.launch {
            repository.enrollCourse(course.id)
            _selectedCourse.value = _selectedCourse.value?.copy(enrolled = true)
        }
    }

    fun toggleMilestone(milestone: GoalMilestoneEntity) {
        viewModelScope.launch {
            repository.toggleMilestone(milestone.id, !milestone.isCompleted, milestone.rewardXp)
        }
    }

    fun submitAssignment(courseId: String, title: String, content: String) {
        viewModelScope.launch {
            repository.submitAssignmentForFeedback(courseId, title, content)
            _syncMessage.value = "Assignment submitted! Real-time mentor review in progress..."
        }
    }

    fun sendStudentReply(feedbackId: String, reply: String) {
        viewModelScope.launch {
            repository.replyToTeacherFeedback(feedbackId, reply)
        }
    }

    fun applyDiagnosticCareerTrack(track: String, jobRole: String) {
        viewModelScope.launch {
            repository.updateCareerTrack(track, jobRole)
            _syncMessage.value = "Personalized learning path updated to $track!"
        }
    }

    fun syncAllData() {
        viewModelScope.launch {
            val syncTime = repository.performSync()
            _syncMessage.value = "Successfully synced across your devices!"
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }
}
