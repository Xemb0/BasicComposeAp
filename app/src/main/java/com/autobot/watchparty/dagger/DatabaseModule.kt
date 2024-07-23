package com.autobot.watchparty.dagger

import android.content.Context
import androidx.room.Room
import com.autobot.watchparty.database.room.MovieDao
import com.autobot.watchparty.database.room.WatchPartyDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): WatchPartyDataBase {
        return Room.databaseBuilder(
            context.applicationContext,
            WatchPartyDataBase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @ViewModelScoped
    fun provideUserDao(database: WatchPartyDataBase): MovieDao {
        return database.movieDao()
    }

}
