package com.sokarcreative.basicstuffrecyclerview



sealed class Decoration {

    abstract fun width(): Int
    inline val width get() = width()
    abstract fun height(): Int
    inline val height get() = height()

    /**
     * @param drawable decoration
     */
    class Drawable(val drawable: android.graphics.drawable.Drawable): Decoration(){
        override fun width(): Int = drawable.intrinsicWidth
        override fun height(): Int = drawable.intrinsicHeight
    }

    /**
     * @param value space in pixel
     */
    class Space(val value: Int): Decoration(){
        override fun width(): Int = value
        override fun height(): Int = value
    }
}