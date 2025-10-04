package com.example.flixer2

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class ActorDetailActivity : AppCompatActivity() {

    private lateinit var actor: Actor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actor_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val passedActor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("ACTOR_EXTRA", Actor::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("ACTOR_EXTRA") as? Actor
        }

        if (passedActor == null) {
            Log.e("ActorDetailActivity", "Actor data was not passed correctly.")
            finish()
            return
        }
        actor = passedActor

        val nameTextView = findViewById<TextView>(R.id.actor_name_detail)
        val profileImageView = findViewById<ImageView>(R.id.actor_profile_detail)

        nameTextView.text = actor.name
        actor.profileUrl?.let {
            Glide.with(this).load(it).transform(RoundedCorners(25)).into(profileImageView)
        }

        fetchActorMovieCredits()
    }

    private fun fetchActorMovieCredits() {
        val client = AsyncHttpClient()
        val knownForTextView = findViewById<TextView>(R.id.known_for_textview)
        val overviewTextView = findViewById<TextView>(R.id.movie_overview_detail)
        val moviePosterImageView = findViewById<ImageView>(R.id.movie_poster_detail)

        client.get("https://api.themoviedb.org/3/person/${actor.id}/movie_credits?api_key=${MainActivity.API_KEY}", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                try {
                    val movies = json.jsonObject.getJSONArray("cast")
                    if (movies.length() > 0) {
                        // Find the best-known movie (often the first in the list)
                        val firstMovie = movies.getJSONObject(0)
                        val movieTitle = firstMovie.getString("title")
                        val movieOverview = firstMovie.getString("overview")
                        val posterPath = firstMovie.getString("poster_path")
                        val posterUrl = "https://image.tmdb.org/t/p/w500/$posterPath"

                        knownForTextView.text = "Known For: $movieTitle"
                        overviewTextView.text = movieOverview

                        Glide.with(this@ActorDetailActivity)
                            .load(posterUrl)
                            .transform(RoundedCorners(25))
                            .into(moviePosterImageView)
                    }
                } catch (e: JSONException) {
                    Log.e("ActorDetailActivity", "Failed to parse movie credits: $e")
                }
            }

            override fun onFailure(statusCode: Int, h: Headers?, r: String?, t: Throwable?) {
                Log.e("ActorDetailActivity", "API call for movie credits failed.")
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
