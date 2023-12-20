package ru.ulstu.`is`.pmu.api

import android.util.Log
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Interceptor.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import ru.ulstu.`is`.pmu.api.model.UserRemote
import ru.ulstu.`is`.pmu.api.model.TaskRemote


interface MyServerService {
    @GET("users")
    suspend fun getUsers(): List<UserRemote>

    @GET("tasks")
    suspend fun getTasks(
        @Query("_page") page: Int,
        @Query("_limit") limit: Int,
    ): List<TaskRemote>

    @GET("tasks/{id}")
    suspend fun getTask(
        @Path("id") id: Int,
    ): TaskRemote

    @POST("tasks")
    suspend fun createTask(
        @Body task: TaskRemote,
    ): TaskRemote

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: Int,
        @Body task: TaskRemote,
    ): TaskRemote

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") id: Int,
    ): TaskRemote

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8079/"

        @Volatile
        private var INSTANCE: MyServerService? = null

        fun getInstance(): MyServerService {
            return INSTANCE ?: synchronized(this) {
                val logger = HttpLoggingInterceptor()
                logger.level = HttpLoggingInterceptor.Level.BASIC
                val client = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .build()
                return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                    .build()
                    .create(MyServerService::class.java)
                    .also { INSTANCE = it }
            }
        }
    }
}