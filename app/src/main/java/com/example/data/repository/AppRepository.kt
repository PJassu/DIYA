package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.model.AchievementEntity
import com.example.data.model.CourseEntity
import com.example.data.model.FeedbackThreadEntity
import com.example.data.model.GoalMilestoneEntity
import com.example.data.model.LessonEntity
import com.example.data.model.StudentProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppRepository private constructor(context: Context) {

    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "diya_skills_db"
    ).fallbackToDestructiveMigration().build()

    val coursesFlow: Flow<List<CourseEntity>> = database.courseDao().getAllCourses()
    val enrolledCoursesFlow: Flow<List<CourseEntity>> = database.courseDao().getEnrolledCourses()
    val downloadedCoursesFlow: Flow<List<CourseEntity>> = database.courseDao().getDownloadedCourses()
    val profileFlow: Flow<StudentProfileEntity?> = database.profileDao().getProfile()
    val achievementsFlow: Flow<List<AchievementEntity>> = database.achievementDao().getAllAchievements()
    val feedbackFlow: Flow<List<FeedbackThreadEntity>> = database.feedbackDao().getAllFeedback()
    val milestonesFlow: Flow<List<GoalMilestoneEntity>> = database.goalDao().getAllMilestones()

    fun getLessonsForCourse(courseId: String): Flow<List<LessonEntity>> =
        database.lessonDao().getLessonsForCourse(courseId)

    fun getLessonById(lessonId: String): Flow<LessonEntity?> =
        database.lessonDao().getLessonById(lessonId)

    fun getCourseById(courseId: String): Flow<CourseEntity?> =
        database.courseDao().getCourseById(courseId)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        val existingProfile = database.profileDao().getProfile().firstOrNull()
        if (existingProfile == null) {
            // Seed profile
            database.profileDao().insertOrUpdate(
                StudentProfileEntity(
                    id = 1,
                    name = "Priya Sharma",
                    email = "priya.anudip@diya.edu",
                    careerTrack = "Digital Workplace & Office Productivity",
                    targetJobRole = "Data Entry & Office Operations Associate",
                    xpPoints = 1850,
                    level = 4,
                    levelTitle = "Vocational Specialist",
                    streakDays = 9,
                    streakFrozen = false,
                    weeklyGoalMinutes = 180,
                    weeklySpentMinutes = 140,
                    resumeReadinessPct = 82,
                    centerLocation = "Anudip Skill Center - Salt Lake Hub",
                    completedLessonsCount = 18
                )
            )

            // Seed Courses
            val courses = listOf(
                CourseEntity(
                    id = "excel_workplace",
                    title = "MS Excel & Workplace Data Analysis",
                    category = "Office Productivity",
                    description = "Master formulas (VLOOKUP, XLOOKUP), Pivot Tables, formatting, data cleaning, and MIS report preparation for office jobs.",
                    durationHours = 18,
                    modulesCount = 6,
                    level = "Job-Ready",
                    enrolled = true,
                    progressPct = 65,
                    isDownloaded = true,
                    careerTrack = "Digital Workplace & Office Productivity",
                    rating = 4.9f,
                    enrolledStudents = 2840,
                    colorHex = 0xFF0D9488,
                    iconName = "table_chart"
                ),
                CourseEntity(
                    id = "spoken_english_pro",
                    title = "Professional Spoken English & BPO Voice",
                    category = "Communication & BPO",
                    description = "Accent neutralization, formal email etiquette, customer empathy phrasing, active listening, and telephonic interview readiness.",
                    durationHours = 24,
                    modulesCount = 8,
                    level = "Intermediate",
                    enrolled = true,
                    progressPct = 40,
                    isDownloaded = true,
                    careerTrack = "Customer Experience & BPO Operations",
                    rating = 4.8f,
                    enrolledStudents = 3450,
                    colorHex = 0xFF1E3A8A,
                    iconName = "record_voice_over"
                ),
                CourseEntity(
                    id = "python_coding_fundamentals",
                    title = "Python Coding & Automation Basics",
                    category = "IT & Code",
                    description = "Learn variables, loops, data structures, scripting for CSV automation, and foundational coding for IT entry-level roles.",
                    durationHours = 30,
                    modulesCount = 10,
                    level = "Beginner to Intermediate",
                    enrolled = true,
                    progressPct = 20,
                    isDownloaded = false,
                    careerTrack = "IT & Web Support",
                    rating = 4.7f,
                    enrolledStudents = 1920,
                    colorHex = 0xFF2563EB,
                    iconName = "code"
                ),
                CourseEntity(
                    id = "digital_financial_literacy",
                    title = "Digital Banking, FinTech & Accounting Basics",
                    category = "Banking & Finance",
                    description = "Practical training on Tally basics, GST overview, UPI security, digital payment gateways, and retail banking protocols.",
                    durationHours = 15,
                    modulesCount = 5,
                    level = "Beginner",
                    enrolled = false,
                    progressPct = 0,
                    isDownloaded = false,
                    careerTrack = "Digital Financial Services",
                    rating = 4.9f,
                    enrolledStudents = 1560,
                    colorHex = 0xFFD97706,
                    iconName = "account_balance"
                ),
                CourseEntity(
                    id = "generative_ai_workplace",
                    title = "Generative AI & Smart Tools for Office Work",
                    category = "AI & Future Skills",
                    description = "Harness Gemini and AI productivity tools for quick report summarization, email drafting, data categorization, and mock interview prep.",
                    durationHours = 12,
                    modulesCount = 4,
                    level = "All Levels",
                    enrolled = true,
                    progressPct = 80,
                    isDownloaded = true,
                    careerTrack = "Digital Workplace & Office Productivity",
                    rating = 5.0f,
                    enrolledStudents = 4120,
                    colorHex = 0xFF7C3AED,
                    iconName = "auto_awesome"
                ),
                CourseEntity(
                    id = "retail_customer_service",
                    title = "Modern Retail & Customer Relations",
                    category = "Retail & Sales",
                    description = "POS systems operation, customer inventory management, grievance handling, and upselling techniques for organized retail.",
                    durationHours = 16,
                    modulesCount = 5,
                    level = "Beginner",
                    enrolled = false,
                    progressPct = 0,
                    isDownloaded = false,
                    careerTrack = "Customer Experience & BPO Operations",
                    rating = 4.6f,
                    enrolledStudents = 980,
                    colorHex = 0xFFEA580C,
                    iconName = "storefront"
                )
            )
            database.courseDao().insertCourses(courses)

            // Seed Lessons for Excel Course
            val excelLessons = listOf(
                LessonEntity(
                    id = "ex_1",
                    courseId = "excel_workplace",
                    title = "1. Excel Fundamentals & Navigation",
                    durationMin = 15,
                    moduleTitle = "Module 1: Getting Started with Worksheets",
                    orderIndex = 1,
                    contentType = "interactive_guide",
                    contentBody = """
                        ### Welcome to MS Excel for Workplace
                        In modern offices, spreadsheet literacy is the #1 foundational skill across all operations.

                        #### Core Components:
                        • **Ribbon Menu**: Houses tabs (Home, Insert, Page Layout, Formulas, Data).
                        • **Formula Bar**: Shows the formula or raw value behind the active cell.
                        • **Active Cell & Name Box**: Displays column letter and row number (e.g., A1, C14).

                        #### Essential Keyboard Shortcuts:
                        • `Ctrl + Z`: Undo last action
                        • `Ctrl + Y`: Redo action
                        • `Ctrl + Home`: Move to cell A1 immediately
                        • `Ctrl + Arrow Keys`: Jump to edge of continuous data region
                        • `Shift + Space`: Select entire row
                        • `Ctrl + Space`: Select entire column

                        #### Practical Tip for Anudip Students:
                        Always name your worksheet tabs clearly (e.g., `Jan_Sales_MIS` instead of `Sheet1`) when submitting work to employers.
                    """.trimIndent(),
                    keyTakeaways = "Mastered cell referencing, navigation shortcuts, and clean worksheet naming standards.",
                    practicalTask = "Open a blank spreadsheet. Fill columns with Employee ID, Name, Department, and Basic Pay for 10 records.",
                    quizQuestionsJson = """
                        [
                          {
                            "id": 1,
                            "question": "Which keyboard shortcut selects an entire column in Microsoft Excel?",
                            "options": ["Ctrl + Space", "Shift + Space", "Alt + C", "Ctrl + Shift + Down"],
                            "correctIndex": 0,
                            "explanation": "Ctrl + Space selects the active column immediately, while Shift + Space selects the entire row."
                          },
                          {
                            "id": 2,
                            "question": "What is the primary function of the Name Box in Excel?",
                            "options": ["Displays the name of the file", "Shows the address of the currently selected active cell", "Sorts names alphabetically", "Calculates payroll formulas"],
                            "correctIndex": 1,
                            "explanation": "The Name Box located to the left of the formula bar displays the cell reference (e.g., B4) of the selected cell."
                          }
                        ]
                    """.trimIndent(),
                    isCompleted = true,
                    isDownloaded = true,
                    userScore = 100
                ),
                LessonEntity(
                    id = "ex_2",
                    courseId = "excel_workplace",
                    title = "2. Formulas: SUM, AVERAGE, COUNTIF & IF",
                    durationMin = 25,
                    moduleTitle = "Module 2: Essential Formulas & Logic",
                    orderIndex = 2,
                    contentType = "practical_lab",
                    contentBody = """
                        ### Working with Logical & Statistical Functions
                        Formulas are expressions that operate on values in a range of cells.

                        #### 1. The SUM & AVERAGE Functions
                        `=SUM(C2:C50)` : Adds all values from cell C2 to C50.
                        `=AVERAGE(D2:D50)` : Computes the arithmetic mean.

                        #### 2. The COUNTIF Function
                        Count cells based on conditions:
                        `=COUNTIF(E2:E50, "Present")`
                        This is universally used in HR attendance tracking and daily dispatch logs.

                        #### 3. The Logical IF Function
                        `=IF(logical_test, value_if_true, value_if_false)`
                        Example:
                        `=IF(F2>=50, "Qualified", "Needs Retest")`

                        #### Nested IF & AND Conditions:
                        `=IF(AND(F2>=80, G2="Yes"), "Eligible for Bonus", "Standard Pay")`
                    """.trimIndent(),
                    keyTakeaways = "Constructed reliable conditional formulas for attendance, grading, and payroll computation.",
                    practicalTask = "Create an attendance sheet with 15 employees and use COUNTIF to calculate total present days.",
                    quizQuestionsJson = """
                        [
                          {
                            "id": 1,
                            "question": "What will the formula =IF(10 > 5, 'Pass', 'Fail') output?",
                            "options": ["Fail", "Pass", "Error", "#VALUE!"],
                            "correctIndex": 1,
                            "explanation": "Since 10 is indeed greater than 5, the condition evaluates to TRUE and outputs 'Pass'."
                          },
                          {
                            "id": 2,
                            "question": "Which function would you use to count how many employees scored above 75 marks?",
                            "options": ["SUMIF", "COUNTIF", "COUNT", "COUNTA"],
                            "correctIndex": 1,
                            "explanation": "COUNTIF evaluates a criterion (like '>75') and counts the matching cell count."
                          }
                        ]
                    """.trimIndent(),
                    isCompleted = true,
                    isDownloaded = true,
                    userScore = 100
                ),
                LessonEntity(
                    id = "ex_3",
                    courseId = "excel_workplace",
                    title = "3. VLOOKUP & Modern XLOOKUP for MIS",
                    durationMin = 30,
                    moduleTitle = "Module 3: Data Retrieval & Lookups",
                    orderIndex = 3,
                    contentType = "practical_lab",
                    contentBody = """
                        ### Lookup Mastery for Real-world Datasets
                        In back-office operations, matching customer records against inventory or payment databases is done using Lookup functions.

                        #### VLOOKUP Syntax:
                        `=VLOOKUP(lookup_value, table_array, col_index_num, [range_lookup])`
                        • `lookup_value`: What you are searching for (e.g., Emp ID in A2).
                        • `table_array`: The reference master table (e.g., MasterSheet!A1:F200).
                        • `col_index_num`: Which column from the table to return (e.g., 3 for Department).
                        • `[range_lookup]`: Always put `0` or `FALSE` for exact match!

                        #### The Superior XLOOKUP:
                        `=XLOOKUP(lookup_value, lookup_array, return_array, [if_not_found])`
                        • Does not break when columns are inserted!
                        • Can look up leftwards or rightwards.
                    """.trimIndent(),
                    keyTakeaways = "Eliminated manual search errors using exact match VLOOKUP and resilient XLOOKUP formulas.",
                    practicalTask = "Link a 20-row customer invoice sheet to a master product catalogue using VLOOKUP to auto-fetch prices.",
                    quizQuestionsJson = """
                        [
                          {
                            "id": 1,
                            "question": "In =VLOOKUP(A2, MasterData!A1:D100, 3, FALSE), what does FALSE specify?",
                            "options": ["Approximate match", "Exact match", "Case-sensitive lookup", "Ignore blank cells"],
                            "correctIndex": 1,
                            "explanation": "Setting range_lookup to FALSE or 0 forces VLOOKUP to seek an exact match only."
                          }
                        ]
                    """.trimIndent(),
                    isCompleted = false,
                    isDownloaded = true,
                    userScore = 0
                ),
                LessonEntity(
                    id = "ex_4",
                    courseId = "excel_workplace",
                    title = "4. Pivot Tables & Executive Dashboards",
                    durationMin = 35,
                    moduleTitle = "Module 4: Data Summarization",
                    orderIndex = 4,
                    contentType = "interactive_guide",
                    contentBody = """
                        ### Instant Aggregation with Pivot Tables
                        Pivot Tables allow you to summarize thousands of rows of raw data in seconds without writing a single complex formula.

                        #### 4 Quadrants of Pivot Table Builder:
                        1. **Filters**: Top-level slicing (e.g., Region, Year).
                        2. **Columns**: Horizontal breakdown (e.g., Quarter 1, Quarter 2).
                        3. **Rows**: Vertical grouping (e.g., Branch Manager, Product Category).
                        4. **Values**: Numeric metric to aggregate (e.g., Sum of Sales, Count of Orders).

                        #### Adding Visual Slicers:
                        Click PivotTable Analyze > Insert Slicer to create one-click interactive filter buttons.
                    """.trimIndent(),
                    keyTakeaways = "Built interactive multi-dimensional reports and executive summaries using Pivot Tables.",
                    practicalTask = "Import a 50-row sales dataset and generate a Pivot Table showing total revenue per sales rep.",
                    quizQuestionsJson = "[]",
                    isCompleted = false,
                    isDownloaded = true,
                    userScore = 0
                )
            )
            database.lessonDao().insertLessons(excelLessons)

            // Seed Lessons for Spoken English & BPO
            val englishLessons = listOf(
                LessonEntity(
                    id = "eng_1",
                    courseId = "spoken_english_pro",
                    title = "1. Customer Empathy & Professional Tone",
                    durationMin = 20,
                    moduleTitle = "Module 1: Workplace Communication Foundations",
                    orderIndex = 1,
                    contentType = "interactive_guide",
                    contentBody = """
                        ### The Art of Customer Empathy in BPO & Support
                        When handling clients, your tone, vocabulary, and active listening determine customer satisfaction.

                        #### Replacing Negative Triggers with Empathy Phrases:
                        • ❌ "You didn't do this right." 
                        →  "Allow me to guide you through the process step-by-step."
                        • ❌ "That's not my department."
                        →  "I will connect you right away with our specialist who can resolve this for you."
                        • ❌ "Calm down, sir."
                        →  "I completely understand your frustration and I am here to help get this sorted."

                        #### The 4-Step Call Handling Protocol:
                        1. **Warm Professional Greeting**: "Thank you for calling Anudip Support. My name is Priya, how may I assist you today?"
                        2. **Active Acknowledgment & Paraphrasing**: "I understand you are having an issue accessing your portal, let me check that."
                        3. **Action & Resolution**: State the exact step you are taking.
                        4. **Pleasant Closing**: "Is there anything else I may assist you with today? Thank you for choosing us."
                    """.trimIndent(),
                    keyTakeaways = "Adopted de-escalation vocabulary and professional 4-step call opening and closing framework.",
                    practicalTask = "Record a 60-second voice note practicing the 4-step customer greeting for mentor review.",
                    quizQuestionsJson = """
                        [
                          {
                            "id": 1,
                            "question": "Which phrase is the best example of empathetic customer de-escalation?",
                            "options": ["Please read the instructions carefully.", "I completely understand why this is inconvenient, let me resolve this for you right now.", "It is company policy, we cannot change it.", "You should wait 24 hours."],
                            "correctIndex": 1,
                            "explanation": "Validating the user's emotion and immediately offering proactive assistance resolves conflict effectively."
                          }
                        ]
                    """.trimIndent(),
                    isCompleted = true,
                    isDownloaded = true,
                    userScore = 100
                ),
                LessonEntity(
                    id = "eng_2",
                    courseId = "spoken_english_pro",
                    title = "2. Formal Email Writing & Business Etiquette",
                    durationMin = 25,
                    moduleTitle = "Module 2: Written Business Communication",
                    orderIndex = 2,
                    contentType = "practical_lab",
                    contentBody = """
                        ### Anatomy of a High-Impact Professional Email
                        Every workplace email should be concise, courteous, and actionable.

                        #### Standard Structure:
                        1. **Clear Subject Line**: `[Urgent] Monthly MIS Report Submission - Branch Salt Lake`
                        2. **Salutation**: `Dear Mr. Bannerjee / Hello Rajesh,`
                        3. **Opening Context**: `I hope this email finds you well. I am writing to submit...`
                        4. **Bulleted Details**: Keep key information in bullet points for easy scanning.
                        5. **Call to Action (CTA)**: `Please let me know if you would like any revisions by Friday 3 PM.`
                        6. **Sign-off**: `Warm regards, Priya Sharma | Data Associate`.
                    """.trimIndent(),
                    keyTakeaways = "Structured professional emails with crisp subject lines, bulleted summaries, and clear calls to action.",
                    practicalTask = "Draft an email requesting a 1-day leave for personal reasons adhering to corporate etiquette.",
                    quizQuestionsJson = "[]",
                    isCompleted = false,
                    isDownloaded = true,
                    userScore = 0
                )
            )
            database.lessonDao().insertLessons(englishLessons)

            // Seed Lessons for Generative AI for Workplace
            val aiLessons = listOf(
                LessonEntity(
                    id = "ai_1",
                    courseId = "generative_ai_workplace",
                    title = "1. Prompt Engineering for Office Productivity",
                    durationMin = 15,
                    moduleTitle = "Module 1: AI Prompting Basics",
                    orderIndex = 1,
                    contentType = "interactive_guide",
                    contentBody = """
                        ### Prompting for Real-World Jobs
                        Generative AI is a co-pilot that helps entry-level professionals work 3x faster when given clear contextual instructions.

                        #### The C.R.E.A.T.E Prompt Framework:
                        • **C**ontext: Give background (e.g. "I work as an operations coordinator in a logistics firm").
                        • **R**ole: Assign AI a role (e.g. "Act as a senior customer relationship manager").
                        • **E**xplicit Task: What to produce (e.g. "Draft an apology email for a delayed shipment").
                        • **A**udience: Who it is for (e.g. "For an enterprise B2B retail client").
                        • **T**one: Style (e.g. "Professional, reassuring, concise").
                        • **E**xamples/Constraints: (e.g. "Keep it under 150 words, include tracking link placeholder").
                    """.trimIndent(),
                    keyTakeaways = "Applied the CREATE framework to generate clean reports and customer correspondence.",
                    practicalTask = "Use the CREATE framework to generate a 5-question mock interview questionnaire for a data entry role.",
                    quizQuestionsJson = """
                        [
                          {
                            "id": 1,
                            "question": "What is the purpose of specifying a 'Role' in your AI prompt?",
                            "options": ["To make the AI answer in poetry", "To constrain the AI's perspective and vocabulary to match industry standards", "To reduce internet bandwidth", "To change the language to Hindi"],
                            "correctIndex": 1,
                            "explanation": "Role prompting grounds the AI in a specific professional domain, improving answer relevance and tone."
                          }
                        ]
                    """.trimIndent(),
                    isCompleted = true,
                    isDownloaded = true,
                    userScore = 100
                )
            )
            database.lessonDao().insertLessons(aiLessons)

            // Seed Achievements
            val achievements = listOf(
                AchievementEntity(
                    id = "ach_first_lesson",
                    title = "Ignite the Flame",
                    description = "Complete your very first skilling lesson on DIYA",
                    category = "Mastery",
                    iconType = "star",
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 86400000 * 7,
                    progress = 1,
                    maxProgress = 1,
                    xpReward = 100
                ),
                AchievementEntity(
                    id = "ach_streak_7",
                    title = "7-Day Dedication",
                    description = "Maintain a 7-day uninterrupted learning streak",
                    category = "Streak",
                    iconType = "local_fire_department",
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 86400000 * 2,
                    progress = 7,
                    maxProgress = 7,
                    xpReward = 250
                ),
                AchievementEntity(
                    id = "ach_excel_pro",
                    title = "Spreadsheet Wizard",
                    description = "Score 100% on 3 consecutive Excel formula quizzes",
                    category = "Mastery",
                    iconType = "table_chart",
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 86400000,
                    progress = 3,
                    maxProgress = 3,
                    xpReward = 200
                ),
                AchievementEntity(
                    id = "ach_feedback_hero",
                    title = "Mentor Enthusiast",
                    description = "Submit 3 assignments and review mentor evaluations",
                    category = "Feedback",
                    iconType = "rate_review",
                    isUnlocked = false,
                    unlockedAt = 0,
                    progress = 2,
                    maxProgress = 3,
                    xpReward = 300
                ),
                AchievementEntity(
                    id = "ach_speed_learner",
                    title = "Vocational Star",
                    description = "Spend 300 minutes actively skilling this month",
                    category = "Speed",
                    iconType = "speed",
                    isUnlocked = false,
                    unlockedAt = 0,
                    progress = 140,
                    maxProgress = 300,
                    xpReward = 500
                )
            )
            database.achievementDao().insertAchievements(achievements)

            // Seed Real-time Teacher Feedback Threads
            val feedbackList = listOf(
                FeedbackThreadEntity(
                    id = "fb_1",
                    courseId = "excel_workplace",
                    assignmentTitle = "Practical Lab: VLOOKUP & Salary MIS Sheet",
                    studentSubmission = "Completed the employee payroll workbook with VLOOKUP formulas linking EmpID to Basic Pay, DA, and HRA columns. Calculated net salary with IF tax brackets.",
                    teacherName = "Prof. Rajesh Bannerjee",
                    teacherRole = "Lead Operations Trainer, Anudip Foundation",
                    teacherAvatarInitial = "R",
                    teacherComment = "Outstanding work on the nested IF logic for tax slabs, Priya! Your sheet layout is exceptionally tidy and corporate-ready.",
                    strengths = "Accurate exact-match lookups; clean column headers; zero cell reference formula errors.",
                    actionableTips = "For larger datasets with 10k+ rows, try using XLOOKUP instead of VLOOKUP to prevent column shifting issues.",
                    score = 95,
                    maxScore = 100,
                    status = "Graded",
                    timestamp = System.currentTimeMillis() - 3600000 * 12,
                    studentReply = "Thank you sir! I practiced XLOOKUP as suggested in the next exercise."
                ),
                FeedbackThreadEntity(
                    id = "fb_2",
                    courseId = "spoken_english_pro",
                    assignmentTitle = "Mock Call Simulation: Customer De-escalation",
                    studentSubmission = "Recorded a 90-second mock customer call handling an angry subscriber whose broadband was disconnected prematurely.",
                    teacherName = "Ananya Mukherjee",
                    teacherRole = "Soft Skills & Voice Specialist",
                    teacherAvatarInitial = "A",
                    teacherComment = "Great control of pace and pitch. You maintained an empathetic posture throughout the interaction without sounding defensive.",
                    strengths = "Polite opening greeting, clear pronunciation of key technical terms, steady breathing.",
                    actionableTips = "Acknowledge the customer's emotional frustration slightly earlier (within the first 8 seconds) before explaining technical details.",
                    score = 88,
                    maxScore = 100,
                    status = "Graded",
                    timestamp = System.currentTimeMillis() - 3600000 * 36,
                    studentReply = null
                ),
                FeedbackThreadEntity(
                    id = "fb_3",
                    courseId = "generative_ai_workplace",
                    assignmentTitle = "AI Prompting: B2B Procurement Summary",
                    studentSubmission = "Drafted 3 prompt variations using the CREATE framework to extract itemized costs from vendor quotation PDFs.",
                    teacherName = "Vikram Sengupta",
                    teacherRole = "Tech Skills & AI Lead Mentor",
                    teacherAvatarInitial = "V",
                    teacherComment = "Your constraint specifications (requesting JSON output and markdown tables) are spot on. Ready for placement evaluations.",
                    strengths = "Clear role definition, high accuracy constraints, structured outputs.",
                    actionableTips = "Include zero-shot and few-shot examples when prompting for complex non-standard vendor invoice layouts.",
                    score = 92,
                    maxScore = 100,
                    status = "Graded",
                    timestamp = System.currentTimeMillis() - 3600000 * 72,
                    studentReply = null
                )
            )
            database.feedbackDao().insertFeedbackList(feedbackList)

            // Seed Goal Milestones
            val milestones = listOf(
                GoalMilestoneEntity(
                    id = "g_1",
                    title = "Complete Module 3: VLOOKUP & XLOOKUP Lab",
                    category = "Course Milestone",
                    dueDate = "Today, 6:00 PM",
                    isCompleted = false,
                    rewardXp = 80
                ),
                GoalMilestoneEntity(
                    id = "g_2",
                    title = "Submit Spoken English Accent Assessment",
                    category = "Mentor Task",
                    dueDate = "Tomorrow, 2:00 PM",
                    isCompleted = false,
                    rewardXp = 100
                ),
                GoalMilestoneEntity(
                    id = "g_3",
                    title = "Weekly 180-Minute Skilling Target",
                    category = "Habit Goal",
                    dueDate = "Sunday, 11:59 PM",
                    isCompleted = false,
                    rewardXp = 150
                ),
                GoalMilestoneEntity(
                    id = "g_4",
                    title = "Complete Diagnostic Career Assessment",
                    category = "Career Path",
                    dueDate = "Completed",
                    isCompleted = true,
                    rewardXp = 50
                )
            )
            database.goalDao().insertMilestones(milestones)
        }
    }

    suspend fun completeLesson(lessonId: String, score: Int, xpEarned: Int, courseId: String) = withContext(Dispatchers.IO) {
        database.lessonDao().markLessonCompleted(lessonId, true, score)
        database.profileDao().addXpAndProgress(xpEarned, 15)
        database.profileDao().incrementStreak()

        // Calculate course progress
        val lessons = database.lessonDao().getLessonsForCourse(courseId).firstOrNull() ?: emptyList()
        if (lessons.isNotEmpty()) {
            val completed = lessons.count { it.isCompleted || it.id == lessonId }
            val pct = (completed * 100) / lessons.size
            database.courseDao().updateProgress(courseId, pct)
        }
    }

    suspend fun toggleCourseDownload(courseId: String, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        database.courseDao().setDownloaded(courseId, !currentStatus)
    }

    suspend fun toggleLessonDownload(lessonId: String, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        database.lessonDao().setLessonDownloaded(lessonId, !currentStatus)
    }

    suspend fun enrollCourse(courseId: String) = withContext(Dispatchers.IO) {
        val course = database.courseDao().getCourseById(courseId).firstOrNull()
        if (course != null) {
            database.courseDao().updateCourse(course.copy(enrolled = true))
        }
    }

    suspend fun toggleMilestone(milestoneId: String, completed: Boolean, rewardXp: Int) = withContext(Dispatchers.IO) {
        database.goalDao().setMilestoneCompleted(milestoneId, completed)
        if (completed) {
            database.profileDao().addXpAndProgress(rewardXp, 5)
        }
    }

    suspend fun submitAssignmentForFeedback(
        courseId: String,
        assignmentTitle: String,
        studentSubmission: String
    ) = withContext(Dispatchers.IO) {
        val feedbackId = "fb_live_${System.currentTimeMillis()}"
        val newFeedback = FeedbackThreadEntity(
            id = feedbackId,
            courseId = courseId,
            assignmentTitle = assignmentTitle,
            studentSubmission = studentSubmission,
            teacherName = "Dr. Sourav Ganguly",
            teacherRole = "Master Trainer & Vocational Assessment Lead",
            teacherAvatarInitial = "S",
            teacherComment = "Reviewing your submission against Anudip industry standards...",
            strengths = "Proactive attempt with well-structured answers.",
            actionableTips = "Review the grading rubric and practical checklist.",
            score = 0,
            maxScore = 100,
            status = "Pending Review",
            timestamp = System.currentTimeMillis()
        )
        database.feedbackDao().insertFeedback(newFeedback)

        // Simulate Teacher Evaluation after brief real-time loop delay
        CoroutineScope(Dispatchers.IO).launch {
            delay(3500)
            val evaluated = newFeedback.copy(
                status = "Graded",
                score = (85..98).random(),
                teacherComment = "Excellent work on this practical submission! Your approach reflects solid workplace readiness.",
                strengths = "Good attention to detail, adherence to professional formatting guidelines, and clear clarity of expression.",
                actionableTips = "Keep practicing time-constrained problem solving to ace company placement rounds."
            )
            database.feedbackDao().insertFeedback(evaluated)
            database.profileDao().addXpAndProgress(75, 10)
        }
    }

    suspend fun replyToTeacherFeedback(feedbackId: String, reply: String) = withContext(Dispatchers.IO) {
        database.feedbackDao().addStudentReply(feedbackId, reply)
    }

    suspend fun updateCareerTrack(newTrack: String, jobRole: String) = withContext(Dispatchers.IO) {
        database.profileDao().updateCareerTrack(newTrack, jobRole)
    }

    suspend fun performSync(): Long = withContext(Dispatchers.IO) {
        database.profileDao().updateSyncState(true, System.currentTimeMillis())
        delay(1200) // Realistic cloud synchronization pulse
        val syncTime = System.currentTimeMillis()
        database.profileDao().updateSyncState(false, syncTime)
        syncTime
    }

    companion object {
        @Volatile
        private var INSTANCE: AppRepository? = null

        fun getInstance(context: Context): AppRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppRepository(context).also { INSTANCE = it }
            }
        }
    }
}
