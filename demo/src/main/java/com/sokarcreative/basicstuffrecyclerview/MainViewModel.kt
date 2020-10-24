package com.sokarcreative.basicstuffrecyclerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.sokarcreative.basicstuffrecyclerview.models.Category
import com.sokarcreative.basicstuffrecyclerview.models.Header
import com.sokarcreative.basicstuffrecyclerview.models.Movie
import com.zhuinden.livedatacombinetuplekt.combineTuple
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    private val favoritesLiveData: MutableLiveData<Set<Movie>> = MutableLiveData(emptySet())
    private val moviesLiveData: MutableLiveData<Set<Movie>> = MutableLiveData(moviesByDefault)
    private val itemsLiveData: LiveData<List<Any>> = combineTuple(moviesLiveData, favoritesLiveData).map { (movies, favorites) ->
        if (movies == null || favorites == null) emptyList()
        else {
            arrayListOf<Any>().apply {
                if (favorites.isNotEmpty()) {
                    add(Header.FAVORITES)
                    favorites.map { it.category }.distinct().forEach { category ->
                        add(HeaderCategory(Header.FAVORITES, category))
                        addAll(favorites.filter { it.category == category })
                    }
                }
                add(Header.MOVIES)
                val moviesLeft = movies.subtract(favorites)
                moviesLeft.map { it.category }.distinct().forEach { category ->
                    add(HeaderCategory(Header.MOVIES, category))
                    addAll(moviesLeft.filter { it.category == category })
                }
            }
        }
    }

    fun getItemsLiveData(): LiveData<List<Any>> = itemsLiveData

    fun addOrRemove(movie: Movie) {
        favoritesLiveData.value = favoritesLiveData.value!!.toMutableSet().apply {
            if (contains(movie)) {
                remove(movie)
            } else {
                add(movie)
            }
        }
    }

    fun addOrRemoveAllMovies(headerCategory: HeaderCategory) {
        when (headerCategory.header) {
            Header.FAVORITES -> {
                favoritesLiveData.value = favoritesLiveData.value!!.toMutableSet().apply {
                    removeAll(this.filter { it.category == headerCategory.category })
                }
            }
            Header.MOVIES -> {
                favoritesLiveData.value = favoritesLiveData.value!!.toMutableSet().apply {
                    addAll(moviesLiveData.value!!.filter { it.category == headerCategory.category })
                }
            }
        }

    }

    data class HeaderCategory(val header: Header, val category: Category)

    companion object {
        val moviesByDefault: Set<Movie> = setOf(
                Movie("I spit on your grave", Category.HORROR),
                Movie("I spit on your grave 1", Category.HORROR),
                Movie("I spit on your grave 2", Category.HORROR),
                Movie("I spit on your grave 3", Category.HORROR),
                Movie("I spit on your grave 4", Category.HORROR),
                Movie("I spit on your grave 5", Category.HORROR),
                Movie("I spit on your grave 6", Category.HORROR),
                Movie("I spit on your grave 7", Category.HORROR),
                Movie("I spit on your grave 8", Category.HORROR),
                Movie("Fight Club", Category.THRILLER),
                Movie("Fight Club 1", Category.THRILLER),
                Movie("Fight Club 2", Category.THRILLER),
                Movie("Fight Club 3", Category.THRILLER),
                Movie("Fight Club 4", Category.THRILLER),
                Movie("Fight Club 5", Category.THRILLER),
                Movie("Fight Club 6", Category.THRILLER),
                Movie("Fight Club 7", Category.THRILLER),
                Movie("Fight Club 8", Category.THRILLER),
                Movie("Fight Club 9", Category.THRILLER),
                Movie("Fight Club 10", Category.THRILLER),
        )
    }
}