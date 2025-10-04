package com.example.flixer2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Headers

class MainActivity : AppCompatActivity() {
    private val actors = mutableListOf<Actor>()
    private lateinit var actorsRecyclerView: RecyclerView
    private lateinit var actorAdapter: ActorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actorsRecyclerView = findViewById(R.id.actor_list)
        actorAdapter = ActorAdapter(this, actors)
        actorsRecyclerView.adapter = actorAdapter
        actorsRecyclerView.layoutManager = LinearLayoutManager(this)

        val client = AsyncHttpClient()
        client.get(API_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch actors: $statusCode | $response")
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "Successfully fetched actors.")
                try {
                    val jsonObject = json.jsonObject
                    if (jsonObject != null) {
                        val jsonString = jsonObject.toString()
                        val jsonParser = Json { ignoreUnknownKeys = true }
                        val actorResponse = jsonParser.decodeFromString(ActorResponse.serializer(), jsonString)

                        actorResponse.results?.let {
                            actors.addAll(it)
                            actorAdapter.notifyDataSetChanged()
                            Log.d(TAG, "Actor list updated with ${actors.size} actors")
                        } ?: Log.w(TAG, "No results found in response.")

                    } else {
                        Log.e(TAG, "API response was not a valid JSON object")
                    }
                } catch (e: SerializationException) {
                    Log.e(TAG, "Failed to parse JSON: $e")
                } catch (e: Exception) {
                    Log.e(TAG, "An unexpected error occurred: $e")
                }
            }
        })
    }

    companion object {
        private const val TAG = "MainActivity"
        const val API_KEY = "a07e22bc18f5cb106bfe4cc1f83ad8ed"
        const val API_URL = "https://api.themoviedb.org/3/person/popular?api_key=$API_KEY"
    }
}
