package com.example.themovie.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.example.themovie.R
import com.example.themovie.adapter.FavListAdapter
import com.example.themovie.api.MovieApi
import com.example.themovie.api.RetrofitService
import com.example.themovie.model.Movie
import com.example.themovie.model.MovieResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class FavouritesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var movieListAdapter: FavListAdapter? = null
    private var movies: ArrayList<Movie>? = null
    var sessionId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = LayoutInflater.from(container?.context)
            .inflate(R.layout.favourites_fragment, container, false)
        val pref =
            requireActivity().getSharedPreferences("tkn", Context.MODE_PRIVATE)
        sessionId = pref.getString("sessionID", "empty")
        bindViews(view)
        getFavList(sessionId)
        swipeRefreshLayout.setOnRefreshListener {
            recyclerView.layoutManager = GridLayoutManager(activity, 1)
            recyclerView.itemAnimator = DefaultItemAnimator()
            movies = ArrayList<Movie>()
            movieListAdapter = FavListAdapter(movies)
            movieListAdapter?.notifyDataSetChanged()
            getFavList(sessionId)
        }
        return view
    }

    private fun bindViews(view: View) {
        recyclerView = view.findViewById(R.id.Frecycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.itemAnimator = DefaultItemAnimator()
        movies = ArrayList()
        movieListAdapter = FavListAdapter(movies)
        recyclerView.adapter = movieListAdapter
        movieListAdapter?.notifyDataSetChanged()
        swipeRefreshLayout = view.findViewById(R.id.main_content)
    }

    private fun getFavList(sessionId: String?) {

        swipeRefreshLayout.isRefreshing = true
        val api: MovieApi? = RetrofitService.getClient()?.create(MovieApi::class.java)
        api?.getFavList(sessionId)
            ?.enqueue(object : Callback<MovieResponse> {
                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Log.d("FavouritesFragment", "OnFailure")
                }

                override fun onResponse(
                    call: Call<MovieResponse>,
                    response: Response<MovieResponse>
                ) {
                    Log.d("FavouritesFragment", response.toString())
                    if (response.isSuccessful()) {
                        val movies = response.body()
                        movieListAdapter?.moviesList = movies?.results
                        movieListAdapter?.notifyDataSetChanged()
                    }
                    swipeRefreshLayout.isRefreshing = false

                }

            })
    }

}
