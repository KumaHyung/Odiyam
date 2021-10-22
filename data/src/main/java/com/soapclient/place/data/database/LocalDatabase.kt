package com.soapclient.place.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.soapclient.place.data.local.dao.PlaceHistoryDao
import com.soapclient.place.data.common.model.CommonPlace

@Database(entities = [CommonPlace::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): PlaceHistoryDao

    companion object {

        @Volatile private var instance: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): LocalDatabase {
            return Room.databaseBuilder(context, LocalDatabase::class.java, "sop-db")
                    .build()
        }
    }
}
