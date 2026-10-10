package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AdminUserEntity::class,
        DispatchOrderEntity::class,
        ChatMessageEntity::class,
        AppConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TrackMatrixDatabase : RoomDatabase() {
    abstract fun adminUserDao(): AdminUserDao
    abstract fun dispatchOrderDao(): DispatchOrderDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun appConfigDao(): AppConfigDao

    companion object {
        @Volatile
        private var INSTANCE: TrackMatrixDatabase? = null

        fun getDatabase(context: Context): TrackMatrixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackMatrixDatabase::class.java,
                    "trackmatrix_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
