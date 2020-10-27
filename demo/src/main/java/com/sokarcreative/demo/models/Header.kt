package com.sokarcreative.demo.models

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sokarcreative.demo.R

enum class Header {
    ACTORS,
    FAVORITES,
    MOVIES
}

@StringRes
fun Header.getNameStringRes(): Int = when (this) {
    Header.ACTORS -> R.string.actors
    Header.FAVORITES -> R.string.favorites
    Header.MOVIES -> R.string.movies
}

@DrawableRes
fun Header.getDrawableRes(): Int = when (this) {
    Header.ACTORS -> R.drawable.face
    Header.FAVORITES -> R.drawable.favorite
    Header.MOVIES -> R.drawable.movie
}

@ColorRes
fun Header.getColorRes(): Int = when (this) {
    Header.ACTORS -> R.color.colorHeaderActors
    Header.FAVORITES -> R.color.colorHeaderFavorites
    Header.MOVIES -> R.color.colorHeaderMovies
}