package pl.bemideas.covidinfo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        posts_recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        initFetching()

        swipe_layout.setOnRefreshListener {
            initFetching()
            Handler().postDelayed(Runnable {
                swipe_layout.isRefreshing = false
            }, 1500)
        }

        morePosts.setOnClickListener {
            val govURL = Intent(android.content.Intent.ACTION_VIEW)
            govURL.data = Uri.parse("https://www.gov.pl/web/koronawirus")
            startActivity(govURL)
        }
        moreStats.setOnClickListener {
            val statsURL = Intent(android.content.Intent.ACTION_VIEW)
            statsURL.data = Uri.parse("https://www.worldometers.info/coronavirus/")
            startActivity(statsURL)
        }
    }

    private fun initFetching() {
        if (!isConnected){
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Ups!")
            dialog.setMessage("Nie mogę załadować nowych danych! Upewnij się, że masz połączenie z internetem i odśwież dane.")
            dialog.show()
        } else {
            Toast.makeText(this, "Odświeżam dane...", Toast.LENGTH_SHORT).show()
            fetchStatistics()
            fetchArticles()
        }
    }

    private val Context.isConnected: Boolean
        get() {
            return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnected == true
        }

    private fun fetchArticles() {
        println("Fetching Articles...")

        val url = "https://wrapapi.com/use/bemideas/covidtracker/dzialania-rzadu/0.0.1?wrapAPIKey=ZiXGPHbJbKxCsjkjDBlF5ZJiTz7oa163"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to fetch!")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val homeFeed = gson.fromJson(body, Data::class.java)

                runOnUiThread { posts_recyclerview.adapter = MainAdapter(homeFeed) }
            }
        })
    }

    private fun fetchStatistics() {

        println("Fetching Data...")

        val dataUrl = "https://coronavirus-19-api.herokuapp.com/countries/poland"
        val request = Request.Builder().url(dataUrl).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to fetch!")
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response?.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val covidTiles = gson.fromJson(body, CovidData::class.java)

                runOnUiThread {

                    active_cases_text.text = covidTiles.active.toString()
                    deaths_text.text = covidTiles.deaths.toString()
                    recovered_text.text = covidTiles.recovered.toString()
                    critical_cases_text.text = covidTiles.critical.toString()
                }
            }

        })
    }
}

//COVID Data
@Entity(tableName = "statystyki")
class CovidData(val deaths: Int, val recovered: Int, val active: Int, val critical: Int){
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}

//Gov Articles
class Data(val data: Collection)

class Collection(val collection: List<Post>)

class Post(val thumbnail: String, val articleDate: String, val articleName: String)