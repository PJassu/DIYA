package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.FeedbackThreadEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MentorFeedbackScreen(
    feedbackList: List<FeedbackThreadEntity>,
    onSubmitAssignment: (courseId: String, title: String, content: String) -> Unit,
    onSendReply: (feedbackId: String, reply: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewSubmissionModal by remember { mutableStateOf(false) }
    var selectedCourseForSub by remember { mutableStateOf("excel_workplace") }
    var assignmentTitleInput by remember { mutableStateOf("") }
    var assignmentContentInput by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewSubmissionModal = true },
                containerColor = PolishPurple,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.AddComment, contentDescription = null) },
                text = { Text("Submit for Review", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("submit_review_fab")
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            // Header Banner
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PolishPurpleContainer.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, PolishPurple.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PolishPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Real-Time Teacher Feedback Loops",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Receive 1-on-1 industry rubric reviews from Anudip vocational master trainers and mentors.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Evaluations & Assignments (${feedbackList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(feedbackList, key = { it.id }) { item ->
                FeedbackItemCard(
                    feedback = item,
                    onSendReply = { reply -> onSendReply(item.id, reply) }
                )
            }
        }
    }

    // New Practical Submission Dialog
    if (showNewSubmissionModal) {
        AlertDialog(
            onDismissRequest = { showNewSubmissionModal = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Submit Work for Mentor Evaluation", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Your submission will trigger immediate rubric evaluation and actionable feedback from center trainers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = assignmentTitleInput,
                        onValueChange = { assignmentTitleInput = it },
                        label = { Text("Assignment / Task Title") },
                        placeholder = { Text("e.g. Pivot Table MIS Report on Retail Sales") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = assignmentContentInput,
                        onValueChange = { assignmentContentInput = it },
                        label = { Text("Your Solution / Response") },
                        placeholder = { Text("Explain your methodology, formulas used, or paste your work details...") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (assignmentTitleInput.isNotBlank() && assignmentContentInput.isNotBlank()) {
                            onSubmitAssignment(selectedCourseForSub, assignmentTitleInput, assignmentContentInput)
                            showNewSubmissionModal = false
                            assignmentTitleInput = ""
                            assignmentContentInput = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary),
                    enabled = assignmentTitleInput.isNotBlank() && assignmentContentInput.isNotBlank()
                ) {
                    Text("Submit to Teacher", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewSubmissionModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FeedbackItemCard(
    feedback: FeedbackThreadEntity,
    onSendReply: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var replyInput by remember { mutableStateOf("") }
    var isReplying by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("feedback_card_${feedback.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Teacher info + Score Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PolishPurple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feedback.teacherAvatarInitial,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = feedback.teacherName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = feedback.teacherRole,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (feedback.status == "Graded") {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (feedback.score >= 90) PolishSuccessContainer else PolishSecondaryContainer,
                        border = BorderStroke(1.dp, if (feedback.score >= 90) PolishSuccess.copy(alpha = 0.3f) else PolishSecondary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "${feedback.score}/${feedback.maxScore}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (feedback.score >= 90) PolishSuccess else PolishSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 2.dp,
                                color = PolishPurple
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Evaluating",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Assignment Title & Student Submission snippet
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Task: ${feedback.assignmentTitle}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = PolishPrimary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Your submission: \"${feedback.studentSubmission}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Teacher Review Commentary
            Text(
                text = feedback.teacherComment,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Strengths & Actionable Tips Callouts
            if (feedback.strengths.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PolishSuccessContainer.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, PolishSuccess.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = null,
                            tint = PolishSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Key Strengths:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PolishSuccess
                            )
                            Text(
                                text = feedback.strengths,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (feedback.actionableTips.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PolishSecondaryContainer.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, PolishSecondary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = PolishSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Actionable Improvement Tips:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PolishSecondary
                            )
                            Text(
                                text = feedback.actionableTips,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Student Reply Thread if exists
            if (feedback.studentReply != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PolishPrimaryContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, PolishPrimary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Icon(
                            imageVector = Icons.Default.Reply,
                            contentDescription = null,
                            tint = PolishPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Your Follow-up Reply:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PolishPrimary
                            )
                            Text(
                                text = feedback.studentReply,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                if (!isReplying) {
                    TextButton(
                        onClick = { isReplying = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(imageVector = Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(16.dp), tint = PolishPurple)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reply to Teacher", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PolishPurple)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = replyInput,
                            onValueChange = { replyInput = it },
                            placeholder = { Text("Ask follow-up question...", fontSize = 12.sp) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (replyInput.isNotBlank()) {
                                    onSendReply(replyInput)
                                    isReplying = false
                                    replyInput = ""
                                }
                            },
                            enabled = replyInput.isNotBlank()
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = PolishPurple)
                        }
                    }
                }
            }
        }
    }
}

