package dev.tomislavmiksik.phoenix.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.tomislavmiksik.phoenix.core.config.AppConfig
import dev.tomislavmiksik.phoenix.core.data.remote.api.MeasurementApi
import dev.tomislavmiksik.phoenix.core.data.remote.service.MeasurementService
import dev.tomislavmiksik.phoenix.core.data.remote.service.MeasurementServiceImpl
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        appConfig: AppConfig
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(provideLoggingInterceptor(appConfig))
            .addInterceptor(provideApiKeyInterceptor(appConfig))
            .connectTimeout(appConfig.networkTimeout.toLong(), TimeUnit.SECONDS)
            .readTimeout(appConfig.networkTimeout.toLong(), TimeUnit.SECONDS)
            .writeTimeout(appConfig.networkTimeout.toLong(), TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(appConfig: AppConfig): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (appConfig.isDebugMode) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(appConfig: AppConfig): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .addHeader("Authorization", "Bearer ${appConfig.apiKey}")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        appConfig: AppConfig
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(appConfig.apiBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

     @Provides
     @Singleton
     fun provideWorkoutApi(retrofit: Retrofit): MeasurementApi {
         return retrofit.create(MeasurementApi::class.java)
     }

    @Provides
    @Singleton
    fun provideMeasurementService(
        measurementApi: MeasurementApi
    ): MeasurementService {
        return MeasurementServiceImpl(measurementApi)
    }
}
