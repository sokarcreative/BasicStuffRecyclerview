package com.sokarcreative.basicstuffrecyclerview.models

import androidx.annotation.StringRes
import com.sokarcreative.basicstuffrecyclerview.R

enum class Category {
    HORROR,
    THRILLER,
}

@StringRes
fun Category.titleStringRes(): Int = when (this) {
    Category.HORROR -> R.string.horror
    Category.THRILLER -> R.string.thriller
}