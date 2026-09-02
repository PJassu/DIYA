package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class CareerTrackOption(
    val id: String,
    val title: String,
    val jobRole: String,
    val description: String,
    val coreCourses: List<String>,
    val averageStartingSalary: String,
    val hiringPartners: String,
    val colorHex: Long,
    val icon: String
)

@Composable
fun DiagnosticAssessmentScreen(
    currentTrack: String,
    onApplyTrack: (trackName: String, jobRole: String) -> Unit,
    onBackToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stepIndex by remember { mutableStateOf(0) }
    var selectedGoal by remember { mutableStateOf("Immediate Employment (1-3 Months)") }
    var selectedBackground by remember { mutableStateOf("12th Pass / Graduate (Non-Tech)") }
    var selectedInterest by remember { mutableStateOf("Digital Workplace & Data Executive") }

    val careerTracks = listOf(
        CareerTrackOption(
            id = "track_data",
            title = "Digital Workplace & Office Productivity",
            jobRole = "Data Analyst & Operations Specialist",
            description = "High demand in banking, logistics, and retail back-offices. Focus on advanced Excel formulas, MIS dashboards, Google Workspace, and AI automation.",
            coreCourses = listOf("MS Excel & Workplace Data", "Generative AI for Office", "Google Sheets Pro"),
            averageStartingSalary = "₹18,000 - ₹25,000 / month",
            hiringPartners = "Tata Consultancy Services, Wipro, Capgemini",
            colorHex = 0xFF0D9488,
            icon = "table_chart"
        ),
        CareerTrackOption(
            id = "track_bpo",
            title = "Customer Experience & BPO Operations",
            jobRole = "Voice/Non-Voice Customer Success Executive",
            description = "Specialized in spoken English fluency, customer empathy, corporate telephonic etiquette, and CRM ticketing systems.",
            coreCourses = listOf("Professional Spoken English", "Customer De-escalation Lab", "Business Email Mastery"),
            averageStartingSalary = "₹20,000 - ₹30,000 / month",
            hiringPartners = "Concentrix, Teleperformance, Genpact",
            colorHex = 0xFF1E3A8A,
            icon = "record_voice_over"
        ),
        CareerTrackOption(
            id = "track_it",
            title = "IT & Web Support Fundamentals",
            jobRole = "Junior IT Support & Python Automation Associate",
            description = "Foundational coding, computer networking troubleshooting, Python scripting for data cleaning, and web development basics.",
            coreCourses = listOf("Python Coding Fundamentals", "HTML/CSS Web Basics", "IT Troubleshooting"),
            averageStartingSalary = "₹22,000 - ₹32,000 / month",
            hiringPartners = "Tech Mahindra, Infosys BPM, Cognizant",
            colorHex = 0xFF2563EB,
            icon = "code"
        ),
        CareerTrackOption(
            id = "track_finance",
            title = "Digital Financial Services & FinTech",
            jobRole = "Digital Banking & Accounts Executive",
            description = "Retail banking workflows, Tally GST accounting fundamentals, digital wallet security, and micro-enterprise financial management.",
            coreCourses = listOf("Digital Banking & FinTech", "Tally & GST Basics", "Financial Literacy 101"),
            averageStartingSalary = "₹17,000 - ₹24,000 / month",
            hiringPartners = "HDFC Bank, Axis Securities, Bandhan Bank",
            colorHex = 0xFFD97706,
            icon = "account_balance"
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(PolishPrimary, PolishTertiary)
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "ANUDIP AI DIAGNOSTIC ENGINE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Personalized Vocational Path Recommendation",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tailor your curriculum to match your career ambitions, time commitment, and job market opportunities.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Available Industry Career Tracks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(careerTracks.size) { index ->
            val track = careerTracks[index]
            val isCurrent = currentTrack == track.title

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) PolishPrimaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, if (isCurrent) PolishPrimary.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("track_option_${track.id}")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(track.colorHex).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (track.icon) {
                                        "table_chart" -> Icons.Default.TableChart
                                        "record_voice_over" -> Icons.Default.RecordVoiceOver
                                        "code" -> Icons.Default.Code
                                        else -> Icons.Default.AccountBalance
                                    },
                                    contentDescription = null,
                                    tint = Color(track.colorHex),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Role: ${track.jobRole}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = PolishPrimary
                                )
                            }
                        }

                        if (isCurrent) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PolishPrimary
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = track.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = PolishSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Est. Package: ${track.averageStartingSalary}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Business,
                                    contentDescription = null,
                                    tint = PolishTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recruiters: ${track.hiringPartners}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!isCurrent) {
                        Button(
                            onClick = {
                                onApplyTrack(track.title, track.jobRole)
                                onBackToDashboard()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PolishPrimary)
                        ) {
                            Text("Switch to this Learning Path", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

