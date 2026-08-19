package com.djembe.android.network

import com.djembe.android.model.*
import retrofit2.Response
import retrofit2.http.*

interface DjembeApi {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/lessons")
    suspend fun getLessons(): Response<List<Lesson>>

    @GET("api/lessons/difficulty/{level}")
    suspend fun getLessonsByDifficulty(@Path("level") level: String): Response<List<Lesson>>

    @GET("api/rhythms")
    suspend fun getRhythms(): Response<List<Rhythm>>

    @GET("api/rhythms/{id}")
    suspend fun getRhythm(@Path("id") id: String): Response<Rhythm>

    @GET("api/progress/me")
    suspend fun getMyProgress(@Header("Authorization") bearerToken: String): Response<Progress>

    @POST("api/progress/lessons/{lessonId}/complete")
    suspend fun completeLesson(
        @Header("Authorization") bearerToken: String,
        @Path("lessonId") lessonId: String
    ): Response<Progress>

    @POST("api/progress/rhythms/{rhythmId}/master")
    suspend fun masterRhythm(
        @Header("Authorization") bearerToken: String,
        @Path("rhythmId") rhythmId: String
    ): Response<Progress>
}
