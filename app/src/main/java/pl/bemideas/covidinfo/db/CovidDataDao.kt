package pl.bemideas.covidinfo.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.bemideas.covidinfo.CovidData

@Dao
interface CovidDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(dataEntry: CovidData)

    @Query("select * from statystyki where id = 0")
    fun getStats(): LiveData<CovidData>
}