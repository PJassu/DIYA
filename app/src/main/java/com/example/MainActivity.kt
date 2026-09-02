package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SyncMessageBanner
import com.example.ui.components.TopDiyaHeader
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DiyaAppMain(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DiyaAppMain(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val allCourses by viewModel.allCourses.collectAsStateWithLifecycle()
    val enrolledCourses by viewModel.enrolledCourses.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val offlineOnly by viewModel.offlineOnlyFilter.collectAsStateWithLifecycle()
    val selectedCourse by viewModel.selectedCourse.collectAsStateWithLifecycle()
    val activeLesson by viewModel.activeLesson.collectAsStateWithLifecycle()
    val courseLessons by viewModel.courseLessons.collectAsStateWithLifecycle()
    val feedbackList by viewModel.teacherFeedback.collectAsStateWithLifecycle()
    val milestones by viewModel.goalMilestones.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()

    // Handle Back Press when in detail/lesson viewer
    BackHandler(enabled = activeLesson != null || selectedCourse != null) {
        if (activeLesson != null) {
            viewModel.closeLesson()
        } else if (selectedCourse != null) {
            viewModel.closeCourseDetails()
        }
    }

    Scaffold(
        topBar = {
            if (activeLesson == null && selectedCourse == null) {
                Column {
                    TopDiyaHeader(
                        profile = profile,
                        onSyncClick = { viewModel.syncAllData() },
                        onProfileClick = { viewModel.selectTab(com.example.ui.viewmodel.AppNavTab.PROFILE) }
                    )
                    SyncMessageBanner(
                        message = syncMessage,
                        onDismiss = { viewModel.clearSyncMessage() }
                    )
                }
            }
        },
        bottomBar = {
            if (activeLesson == null && selectedCourse == null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBar(
                        modifier = Modifier.navigationBarsPadding(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        val navItems = listOf(
                            Triple(com.example.ui.viewmodel.AppNavTab.DASHBOARD, Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
                            Triple(com.example.ui.viewmodel.AppNavTab.COURSES, Icons.Filled.School, Icons.Outlined.School),
                            Triple(com.example.ui.viewmodel.AppNavTab.FEEDBACK, Icons.Filled.RateReview, Icons.Outlined.RateReview),
                            Triple(com.example.ui.viewmodel.AppNavTab.GAMIFICATION, Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
                            Triple(com.example.ui.viewmodel.AppNavTab.PROFILE, Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
                        )

                        navItems.forEach { (tab, filledIcon, outlinedIcon) ->
                            val isSelected = currentTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.selectTab(tab) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PolishPrimary,
                                    selectedTextColor = PolishPrimary,
                                    indicatorColor = PolishPrimaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                                        contentDescription = tab.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                activeLesson != null -> {
                    LessonReaderScreen(
                        lesson = activeLesson!!,
                        onBackClick = { viewModel.closeLesson() },
                        onCompleteLesson = { score, xp ->
                            selectedCourse?.let { course ->
                                viewModel.completeLesson(activeLesson!!.id, score, xp, course.id)
                            }
                        },
                        onToggleDownload = {
                            viewModel.toggleLessonDownload(activeLesson!!)
                        },
                        onSubmitAssignmentToMentor = { title, content ->
                            selectedCourse?.let { course ->
                                viewModel.submitAssignment(course.id, title, content)
                            }
                        }
                    )
                }

                selectedCourse != null -> {
                    CourseDetailScreen(
                        course = selectedCourse!!,
                        lessons = courseLessons,
                        onBackClick = { viewModel.closeCourseDetails() },
                        onLessonClick = { lesson -> viewModel.openLesson(lesson) },
                        onToggleCourseDownload = { viewModel.toggleCourseDownload(selectedCourse!!) },
                        onToggleLessonDownload = { lesson -> viewModel.toggleLessonDownload(lesson) },
                        onEnrollClick = { viewModel.enrollInCourse(selectedCourse!!) }
                    )
                }

                else -> {
                    Crossfade(targetState = currentTab, label = "TabTransition") { tab ->
                        when (tab) {
                            com.example.ui.viewmodel.AppNavTab.DASHBOARD -> {
                                DashboardScreen(
                                    profile = profile,
                                    enrolledCourses = enrolledCourses,
                                    latestFeedback = feedbackList,
                                    milestones = milestones,
                                    onCourseClick = { course -> viewModel.openCourseDetails(course) },
                                    onNavigateToDiagnostic = { viewModel.selectTab(com.example.ui.viewmodel.AppNavTab.PRACTICE) },
                                    onNavigateToFeedback = { viewModel.selectTab(com.example.ui.viewmodel.AppNavTab.FEEDBACK) },
                                    onNavigateToCourses = { viewModel.selectTab(com.example.ui.viewmodel.AppNavTab.COURSES) },
                                    onNavigateToLeaderboard = { viewModel.selectTab(com.example.ui.viewmodel.AppNavTab.GAMIFICATION) },
                                    onToggleMilestone = { milestone -> viewModel.toggleMilestone(milestone) }
                                )
                            }

                            com.example.ui.viewmodel.AppNavTab.COURSES -> {
                                CoursesScreen(
                                    courses = allCourses,
                                    selectedCategory = selectedCategory,
                                    offlineOnly = offlineOnly,
                                    onSelectCategory = { cat -> viewModel.setCategoryFilter(cat) },
                                    onToggleOfflineFilter = { viewModel.toggleOfflineFilter() },
                                    onCourseClick = { course -> viewModel.openCourseDetails(course) },
                                    onToggleDownload = { course -> viewModel.toggleCourseDownload(course) }
                                )
                            }

                            com.example.ui.viewmodel.AppNavTab.PRACTICE -> {
                                DiagnosticAssessmentScreen(
                                    currentTrack = profile?.careerTrack ?: "Digital Workplace & Office Productivity",
                                    onApplyTrack = { track, jobRole -> viewModel.applyDiagnosticCareerTrack(track, jobRole) },
                                    onBackToDashboard = { viewModel.selectTab(com.example.ui.viewmodel.AppNavTab.DASHBOARD) }
                                )
                            }

                            com.example.ui.viewmodel.AppNavTab.FEEDBACK -> {
                                MentorFeedbackScreen(
                                    feedbackList = feedbackList,
                                    onSubmitAssignment = { courseId, title, content ->
                                        viewModel.submitAssignment(courseId, title, content)
                                    },
                                    onSendReply = { id, reply ->
                                        viewModel.sendStudentReply(id, reply)
                                    }
                                )
                            }

                            com.example.ui.viewmodel.AppNavTab.GAMIFICATION -> {
                                GamificationScreen(
                                    profile = profile,
                                    achievements = achievements,
                                    leaderboard = viewModel.leaderboard
                                )
                            }

                            com.example.ui.viewmodel.AppNavTab.PROFILE -> {
                                ProfileSyncScreen(
                                    profile = profile,
                                    onSyncNow = { viewModel.syncAllData() },
                                    onNavigateToDiagnostic = { viewModel.selectTab(com.example.ui.viewmodel.AppNavTab.PRACTICE) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

