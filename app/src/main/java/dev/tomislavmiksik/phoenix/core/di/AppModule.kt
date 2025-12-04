package dev.tomislavmiksik.phoenix.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.tomislavmiksik.phoenix.core.config.AppConfig
import dev.tomislavmiksik.phoenix.core.config.AppConfigImpl
import dev.tomislavmiksik.phoenix.core.data.remote.api.AuthApi
import dev.tomislavmiksik.phoenix.core.data.repository.AuthRepositoryImpl
import dev.tomislavmiksik.phoenix.core.domain.repository.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppConfig(): AppConfig {
        return AppConfigImpl()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi): AuthRepository {
        return AuthRepositoryImpl(authApi)
    }
}
