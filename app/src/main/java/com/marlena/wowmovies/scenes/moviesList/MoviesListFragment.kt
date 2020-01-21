package com.marlena.wowmovies.scenes.moviesList

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.marlena.wowmovies.R
import com.marlena.wowmovies.data.Constants
import com.marlena.wowmovies.model.domain.Genre
import com.marlena.wowmovies.model.domain.Movie
import com.marlena.wowmovies.scenes.adapters.movieadapter.MovieAdapter
import com.marlena.wowmovies.scenes.theMovie.TheMovieActivity
import kotlinx.android.synthetic.main.fragment_movie_list.*
import kotlinx.android.synthetic.main.fragment_page_genre.*

class MoviesListFragment: Fragment(), MoviesList.View, MovieAdapter.Listener {

    private val movieList = mutableListOf<Movie>()
    private lateinit var presenter: MoviesListPresenter
    private var sectionsPagerAdapter: SectionsPagerAdapter? = null
    private var genreId: Int = -1
    private var adapter: MovieAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = MoviesListPresenter(this)
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//aqui
        setupAdapters()
        setupViews()
        makeRequests()

    }

    private fun setupAdapters() {
        adapter =
            MovieAdapter(
                movieList,
                this
            )
    }

    private fun setupViews() {
        recyclerViewRV?.adapter = adapter
    }

    override fun makeRequests() {
        presenter.getMoviesList()
        presenter.getGenresList()
    }

    override fun setList(list: List<Movie>) {
        movieList.clear()
        movieList.addAll(list)
    }

    override fun setGenresList(genreList: MutableList<Genre>?) {
        presenter.getMoviesListByGenre(genreId, movieList, genreList)
    }

    override fun setMoviesListByGenre (list: List<Movie>) {
        sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager, this)
        container.adapter = sectionsPagerAdapter
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        setList(list)

        adapter?.notifyDataSetChanged()
    }


    override fun displayFailure(error: Int) {
        when (error) {
            1 -> Toast.makeText(context, getString(R.string.erro1), Toast.LENGTH_LONG).show()
            2 -> Toast.makeText(context,getString(R.string.erro2), Toast.LENGTH_LONG).show()
            else -> Toast.makeText(context,getString(R.string.erro0), Toast.LENGTH_LONG).show()
        }
    }

    override fun getViewContext(): Context? {
        return context
    }

    override fun openMovieFragment(movie: Movie, itemView: View) {

        val options = ActivityOptions.makeSceneTransitionAnimation(
            activity,
            Pair(itemView, TheMovieActivity.TRANSITION_IMAGE)
        )

        val intent = Intent(context, TheMovieActivity::class.java).apply {
            putExtra("imageTitle", movie.title)
            putExtra("imagePosterPath", (Constants.imageUrlMovie + movie.poster_path))
            putExtra("imageBackdropPath", (Constants.imageUrlMovie + movie.backdrop_path))
            putExtra("imageOverview", "")
        }
        activity?.startActivity(intent, options.toBundle())
        adapter?.notifyDataSetChanged()
    }

    companion object {
        const val GENRE_ID = "GENRE_ID"
        fun newInstance(genreId: Int) = MoviesListFragment().apply {
            arguments = Bundle().apply {
                putInt(GENRE_ID, genreId)
            }
            return MoviesListFragment()
        }
    }
}