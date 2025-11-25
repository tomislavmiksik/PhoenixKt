package dev.tomislavmiksik.phoenix.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.tomislavmiksik.phoenix.core.config.AppConfig
import dev.tomislavmiksik.phoenix.core.config.AppConfigImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppConfig(): AppConfig {
        return AppConfigImpl()
    }
}
