package com.sokarcreative.demo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.sokarcreative.demo.models.Category
import com.sokarcreative.demo.models.Header
import com.sokarcreative.demo.models.Movie
import com.zhuinden.livedatacombinetuplekt.combineTuple

class MainViewModel : ViewModel() {

    private val isDividerFeatureEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData(true)
    private val isStickyHeaderFeatureEnabledLiveData: MutableLiveData<Boolean> = MutableLiveData(true)
    private val favoritesLiveData: MutableLiveData<Set<Movie>> = MutableLiveData(emptySet())
    private val moviesLiveData: MutableLiveData<Set<Movie>> = MutableLiveData(moviesByDefault)

    private val stickyHeadersEnabledLiveData: MutableLiveData<Triple<Boolean, Boolean, Boolean>> = MutableLiveData(Triple(true, true, false))
    private val dividersEnabledLiveData: MutableLiveData<DividersEnabled> = MutableLiveData(DividersEnabled(isFirstLastDecorationEnabled = true, isFirstDividerDecorationEnabled = true, isDividerDecorationEnabled = true, isLastDividerDecorationEnabled = true, ))

    private val itemsLiveData: LiveData<List<Any>> = combineTuple(moviesLiveData, favoritesLiveData).map { (movies, favorites) ->
        if (movies == null || favorites == null) emptyList()
        else {
            arrayListOf<Any>().apply {
                if (favorites.isNotEmpty()) {
                    add(Header.FAVORITES)
                    favorites.map { it.category }.distinct().forEach { category ->
                        add(HeaderCategory(Header.FAVORITES, category))
                        addAll(favorites.filter { it.category == category }.map { MovieState.Favorite(it) })
                    }
                }

                val moviesLeft = movies.subtract(favorites)
                if(moviesLeft.isNotEmpty()){
                    add(Header.MOVIES)
                    moviesLeft.map { it.category }.distinct().forEach { category ->
                        add(HeaderCategory(Header.MOVIES, category))
                        addAll(moviesLeft.filter { it.category == category }.map { MovieState.Default(it) })
                    }
                }
            }
        }
    }

    fun isDividerFeatureEnabledLiveData(): LiveData<Boolean> = isDividerFeatureEnabledLiveData
    fun setIsDividerFeatureEnabled(isEnabled: Boolean) {
        isDividerFeatureEnabledLiveData.value = isEnabled
    }

    fun isStickyHeaderFeatureEnabledLiveData(): LiveData<Boolean> = isStickyHeaderFeatureEnabledLiveData
    fun setIsStickyHeaderFeatureEnabledLiveData(isEnabled: Boolean){
        isStickyHeaderFeatureEnabledLiveData.value = isEnabled
    }

    fun getStickyHeadersEnabledLiveData(): LiveData<Triple<Boolean, Boolean, Boolean>> = stickyHeadersEnabledLiveData
    fun setStickyHeadersEnabled(headersEnabled: Triple<Boolean, Boolean, Boolean>){
        stickyHeadersEnabledLiveData.value = headersEnabled
    }

    fun getDividersEnabledLiveData(): LiveData<DividersEnabled> = dividersEnabledLiveData
    fun setDividersEnabled(headersEnabled: DividersEnabled){
        dividersEnabledLiveData.value = headersEnabled
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

    data class DividersEnabled(val isFirstLastDecorationEnabled: Boolean, val isFirstDividerDecorationEnabled: Boolean, val isDividerDecorationEnabled: Boolean, val isLastDividerDecorationEnabled: Boolean)

    data class HeaderCategory(val header: Header, val category: Category)
    sealed class MovieState {
        abstract fun movie(): Movie
        data class Default(val movie: Movie): MovieState() {
            override fun movie(): Movie {
                return movie
            }
        }

        data class Favorite(val movie: Movie): MovieState(){
            override fun movie(): Movie {
                return movie
            }
        }
    }
}