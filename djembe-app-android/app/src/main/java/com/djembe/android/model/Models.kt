package com.djembe.android.model

data class Lesson(
    val id: String,
    val title: String,
    val description: String?,
    val difficulty: String,
    val sequenceOrder: Int,
    val contentUrl: String?,
    val prerequisiteLessonId: String?,
    val techniquesCovered: List<String>?
)

data class Rhythm(
    val id: String,
    val name: String,
    val region: String?,
    val description: String?,
    val defaultBpm: Int,
    val difficulty: String,
    val audioTrackUrl: String?,
    val partStemUrls: Map<String, String>?,
    val tags: List<String>?
)

data class Progress(
    val id: String,
    val userId: String,
    val completedLessonIds: Set<String>,
    val masteredRhythmIds: Set<String>,
    val totalPracticeMinutes: Long
)

data class AuthResponse(
    val token: String,
    val userId: String,
    val username: String,
    val email: String
)

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val username: String, val email: String, val password: String)
