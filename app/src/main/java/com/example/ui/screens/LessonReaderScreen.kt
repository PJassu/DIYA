package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LessonEntity
import com.example.ui.theme.*
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonReaderScreen(
    lesson: LessonEntity,
    onBackClick: () -> Unit,
    onCompleteLesson: (score: Int, xp: Int) -> Unit,
    onToggleDownload: () -> Unit,
    onSubmitAssignmentToMentor: (assignmentTitle: String, content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOptionIndex by remember(lesson.id) { mutableStateOf<Int?>(null) }
    var quizSubmitted by remember(lesson.id) { mutableStateOf(lesson.isCompleted) }
    var showSubmitAssignmentDialog by remember { mutableStateOf(false) }
    var assignmentInput by remember { mutableStateOf("") }
    var lessonFinishedCelebration by remember { mutableStateOf(false) }

    // Parse Quiz questions from JSON
    val quizQuestion = remember(lesson.quizQuestionsJson) {
        try {
            if (lesson.quizQuestionsJson.isNotBlank()) {
                val jsonArray = JSONArray(lesson.quizQuestionsJson)
                if (jsonArray.length() > 0) {
                    val obj = jsonArray.getJSONObject(0)
                    val optsArray = obj.getJSONArray("options")
                    val options = mutableListOf<String>()
                    for (i in 0 until optsArray.length()) {
                        options.add(optsArray.getString(i))
                    }
                    ParsedQuiz(
                        id = obj.getInt("id"),
                        question = obj.getString("question"),
                        options = options,
                        correctIndex = obj.getInt("correctIndex"),
                        explanation = obj.getString("explanation")
                    )
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Column {
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${lesson.durationMin} mins • Offline Ready",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleDownload) {
                        Icon(
                            imageVector = if (lesson.isDownloaded) Icons.Filled.OfflinePin else Icons.Outlined.CloudDownload,
                            contentDescription = "Offline Cache",
                            tint = if (lesson.isDownloaded) PolishTertiary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
        ) {
            // Offline Availability Banner
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (lesson.isDownloaded) PolishTertiaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (lesson.isDownloaded) PolishTertiary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (lesson.isDownloaded) Icons.Default.OfflinePin else Icons.Default.CloudQueue,
                            contentDescription = null,
                            tint = if (lesson.isDownloaded) PolishTertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (lesson.isDownloaded) "Cached for instant offline learning" else "Streaming from cloud",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (lesson.isDownloaded) PolishTertiary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Main Lesson Body Content
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PolishPrimary
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Render Formatted Content Lines
                        lesson.contentBody.lines().forEach { line ->
                            val trimmed = line.trim()
                            when {
                                trimmed.startsWith("###") -> {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = trimmed.removePrefix("###").trim(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                trimmed.startsWith("####") -> {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = trimmed.removePrefix("####").trim(),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PolishPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                trimmed.startsWith("•") -> {
                                    Row(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "•",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PolishTertiary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = trimmed.removePrefix("•").trim(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                trimmed.isNotBlank() -> {
                                    Text(
                                        text = trimmed,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        if (lesson.keyTakeaways.isNotBlank()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = PolishTertiaryContainer.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, PolishTertiary.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircleOutline,
                                            contentDescription = null,
                                            tint = PolishTertiary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Key Workplace Takeaways",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = PolishTertiary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = lesson.keyTakeaways,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Practical Task & Mentor Submission Card
            if (lesson.practicalTask.isNotBlank()) {
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = PolishPurpleContainer.copy(alpha = 0.4f)),
                        border = BorderStroke(1.dp, PolishPurple.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = PolishPurple,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Hands-on Practical Task",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PolishPurple
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = lesson.practicalTask,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showSubmitAssignmentDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PolishPurple),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit for Real-time Mentor Review", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Interactive Quiz Challenge Component
            if (quizQuestion != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quiz_section")
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PolishSecondaryContainer
                                ) {
                                    Text(
                                        text = "KNOWLEDGE CHECK",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PolishSecondary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = PolishSuccessContainer,
                                    border = BorderStroke(1.dp, PolishSuccess.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "+50 XP Reward",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = PolishSuccess,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = quizQuestion.question,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quiz Option Buttons
                            quizQuestion.options.forEachIndexed { index, option ->
                                val isSelected = selectedOptionIndex == index
                                val isCorrect = quizQuestion.correctIndex == index
                                val cardBg = when {
                                    quizSubmitted && isCorrect -> PolishSuccessContainer
                                    quizSubmitted && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                                    isSelected -> PolishPrimaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                                val borderStroke = when {
                                    quizSubmitted && isCorrect -> BorderStroke(1.dp, PolishSuccess)
                                    quizSubmitted && isSelected && !isCorrect -> BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                                    isSelected -> BorderStroke(1.5.dp, PolishPrimary)
                                    else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = cardBg,
                                    border = borderStroke,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clickable {
                                            if (!quizSubmitted) {
                                                selectedOptionIndex = index
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected || (quizSubmitted && isCorrect)) PolishPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = ('A' + index).toString(),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(14.dp))

                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (quizSubmitted) {
                                            if (isCorrect) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Correct",
                                                    tint = PolishSuccess,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            } else if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Cancel,
                                                    contentDescription = "Incorrect",
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (!quizSubmitted) {
                                Button(
                                    onClick = {
                                        if (selectedOptionIndex != null) {
                                            quizSubmitted = true
                                            val score = if (selectedOptionIndex == quizQuestion.correctIndex) 100 else 60
                                            onCompleteLesson(score, 50)
                                            lessonFinishedCelebration = true
                                        }
                                    },
                                    enabled = selectedOptionIndex != null,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Verify Answer & Earn XP", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = PolishTertiaryContainer.copy(alpha = 0.4f),
                                    border = BorderStroke(1.dp, PolishTertiary.copy(alpha = 0.25f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Explanation:",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PolishTertiary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = quizQuestion.explanation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Finish Lesson Action Button if no quiz
            if (quizQuestion == null && !lesson.isCompleted) {
                item {
                    Button(
                        onClick = {
                            onCompleteLesson(100, 50)
                            lessonFinishedCelebration = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark Lesson Completed (+50 XP)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Submit Assignment Dialog
    if (showSubmitAssignmentDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitAssignmentDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Submit Practical Assignment", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Your submission will be evaluated by your Anudip vocational master trainer.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = assignmentInput,
                        onValueChange = { assignmentInput = it },
                        label = { Text("Describe your solution / code / formulas") },
                        placeholder = { Text("e.g. Calculated VLOOKUP formulas across 20 records...") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (assignmentInput.isNotBlank()) {
                            onSubmitAssignmentToMentor(lesson.title, assignmentInput)
                            showSubmitAssignmentDialog = false
                            assignmentInput = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    enabled = assignmentInput.isNotBlank()
                ) {
                    Text("Submit to Mentor", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitAssignmentDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

data class ParsedQuiz(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

