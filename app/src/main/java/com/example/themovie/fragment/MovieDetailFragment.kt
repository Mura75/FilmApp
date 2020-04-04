package com.example.themovie.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.themovie.BuildConfig
import com.example.themovie.Fav.FavMovieInfo
import com.example.themovie.Fav.FavResponse
import com.example.themovie.R
import com.example.themovie.api.MovieApi
import com.example.themovie.api.RetrofitService
import com.example.themovie.model.MovieDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MovieDetailFragment : Fragment() {
    private val API_KEY = "50433cbb6c47b22aabd51bf88ddd11c0"
    private var movieTitle: TextView? = null
    private var movieJanre: TextView? = null
    private var movieDate: TextView? = null
    private var movieDescription: TextView? = null
    private var movie_id:Int?=null
    private var poster: ImageView? = null
    private var likeBtn: ImageView? = null
    var sessionId: String ?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = LayoutInflater.from(container?.context)
            .inflate(R.layout.fragment_movie_detail, container, false)
        movieTitle = v.findViewById(R.id.m_movie_title)
        movieJanre = v.findViewById(R.id.m_movie_genre)
        movieDate = v.findViewById(R.id.m_movie_date)
        movieDescription = v.findViewById(R.id.m_movie_overview)
        poster = v.findViewById(R.id.m_avatar_detail)
        likeBtn = v.findViewById(R.id.fav_btn)
        val pref =
            activity!!.getSharedPreferences("tkn",Context.MODE_PRIVATE)
        sessionId = pref.getString("sessionID", "empty")
        return v
    }

    fun getMovieDetail(id: Int) {
        val api: MovieApi? = RetrofitService.getClient()?.create(MovieApi::class.java)
        api?.getMovieDetail(id, BuildConfig.THE_MOVIE_DB_API_TOKEN)
            ?.enqueue(object : Callback<MovieDetailResponse> {
                override fun onResponse(
                    call: Call<MovieDetailResponse>,
                    response: Response<MovieDetailResponse>
                ) {
                    movieTitle?.setText(response.body()?.title)
                    movieDescription?.setText(response.body()?.overview)
                    movieJanre?.setText((response.body()?.genres?.first()?.name))
                    Glide.with(view?.context!!)
                        .load(response?.body()?.getPosterPath())
                        .into(this@MovieDetailFragment!!.poster!!)
                    movie_id=response.body()?.id
                    likeBtn?.setOnClickListener(View.OnClickListener {
                        markAsFav(FavMovieInfo(true,movie_id,"movie"), sessionId )
                    })
                }

                override fun onFailure(call: Call<MovieDetailResponse>, t: Throwable) {}
            })
    }



    fun markAsFav(info: FavMovieInfo, sessionId: String?) {
        Log.d("pusk","mfav" + sessionId)
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                return
            }
            RetrofitService.getApi()
                ?.addFavList(info, sessionId)
                ?.enqueue(object : Callback<FavResponse> {
                    override fun onFailure(call: Call<FavResponse>, t: Throwable) {
                        Log.d("fav", "lol")
                    }

                    override fun onResponse(
                        call: Call<FavResponse>,
                        response: Response<FavResponse>
                    ) {
                        Log.d("pusk", response.toString())
                        Log.d("pusk", response.body().toString())
                        if (response.body()?.status_code==12)
                            Toast.makeText(activity,"Film added to favList",Toast.LENGTH_LONG).show()
                    }

                })
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show()
            Log.d("mark",e.toString())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }
}