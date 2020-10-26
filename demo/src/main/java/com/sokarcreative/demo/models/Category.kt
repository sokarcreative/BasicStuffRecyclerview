package com.sokarcreative.demo.models

import androidx.annotation.StringRes
import com.sokarcreative.demo.R

enum class Category {
    HORROR,
    THRILLER,
}

@StringRes
fun Category.titleStringRes(): Int = when (this) {
    Category.HORROR -> R.string.horror
    Category.THRILLER -> R.string.thriller
}