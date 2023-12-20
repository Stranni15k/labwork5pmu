package ru.ulstu.`is`.pmu.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.ulstu.`is`.pmu.database.remotekeys.dao.RemoteKeysDao
import ru.ulstu.`is`.pmu.database.remotekeys.model.RemoteKeys
import ru.ulstu.`is`.pmu.database.task.dao.UserDao
import ru.ulstu.`is`.pmu.database.task.dao.TaskDao
import ru.ulstu.`is`.pmu.database.task.model.User
import ru.ulstu.`is`.pmu.database.task.model.Task

@Database(
    entities = [
        Task::class,
        User::class,
        RemoteKeys::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        private const val DB_NAME: String = "pmy-db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private suspend fun populateDatabase() {
            INSTANCE?.let { database ->
                // Users
                val userDao = database.userDao()
                val user1 = User(1, "Sergey", "brook.sergey@gmail.com")
                userDao.insert(user1)
                // Tasks
                val taskDao = database.taskDao()
                val task1 = Task("First1", "Last1","12.12.2023",false, user1)
                val task2 = Task("First2", "Last2","15.12.2023",false, user1)
                val task3 = Task("First3", "Last3","10.12.2023",false, user1)
                val task4 = Task("First4", "Last4","31.12.2023",false, user1)
                val task5 = Task("First5", "Last5","05.12.2023",false, user1)
                taskDao.insert(task1)
                taskDao.insert(task2)
                taskDao.insert(task3)
                taskDao.insert(task4)
                taskDao.insert(task5)
            }
        }

        fun getInstance(appContext: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
//                    .addCallback(object : Callback() {
//                        override fun onCreate(db: SupportSQLiteDatabase) {
//                            super.onCreate(db)
//                            CoroutineScope(Dispatchers.IO).launch {
//                                populateDatabase()
//                            }
//                        }
//                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}