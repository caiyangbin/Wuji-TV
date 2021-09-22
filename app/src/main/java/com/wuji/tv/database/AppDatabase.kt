package com.wuji.tv.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.wuji.tv.model.*

const val DATABASE_NAME = "sdvn-db"

@Database(
    entities = [DownloadDBModel::class, MediaInfoModel::class, TopTabModel::class,
        LeftTabModel::class,DeviceInfoModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getDownloadListDao(): DownloadListDao
    abstract fun getMovieListDao(): MediaListDao
    abstract fun getTopTabDao(): TopTabDao
    abstract fun getLeftTabDao(): LeftTabDao
    abstract fun getTopWithLeftTabDao(): TopWithLeftTabDao
    abstract fun getDeviceInfoDao(): DeviceInfoDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                    }
                })
                .build()
        }
    }
}