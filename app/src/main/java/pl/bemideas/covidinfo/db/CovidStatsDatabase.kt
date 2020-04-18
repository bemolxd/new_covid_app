package pl.bemideas.covidinfo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.bemideas.covidinfo.CovidData

@Database(
    entities = [CovidData::class],
    version = 1
)
abstract class CovidStatsDatabase: RoomDatabase() {
    abstract fun currentDataEntry(): CovidDataDao

    companion object{
        @Volatile private var instance: CovidStatsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context)//.also { instance = it }
        }

        private fun buildDatabase(context: Context){
            Room.databaseBuilder(context.applicationContext, CovidStatsDatabase::class.java, "stats.db").build()
        }
    }
}