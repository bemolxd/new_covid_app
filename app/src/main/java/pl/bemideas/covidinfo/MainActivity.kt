package pl.bemideas.covidinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        posts_recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        fetchStatistics()
        fetchArticles()
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
class CovidData(val deaths: Int, val recovered: Int, val active: Int, val critical: Int)

//Gov Articles
class Data(val data: Collection)

class Collection(val collection: List<Post>)

class Post(val thumbnail: String, val articleDate: String, val articleName: String)