package com.alexjlockwood.twentyfortyeight.di

import android.content.Context
import com.alexjlockwood.twentyfortyeight.repository.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainApplicationModule {

    @Singleton
    @Provides
    fun provideGameRepository(@ApplicationContext context: Context): GameRepository {
        return GameRepository(context)
    }
}
