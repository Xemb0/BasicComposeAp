package com.autobot.watchparty.database.room
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.autobot.watchparty.database.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class WatchPartyDataBase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: WatchPartyDataBase? = null

        fun getDatabase(context: Context): WatchPartyDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WatchPartyDataBase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
