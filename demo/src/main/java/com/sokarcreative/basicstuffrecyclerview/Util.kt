package com.sokarcreative.basicstuffrecyclerview

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sokarcreative on 01/11/2017.
 */
internal object Util {

    val simpleDateFormatDayCustom = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault())
    val simpleDateFormatMonthCustom = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val simpleDateFormatMonth = SimpleDateFormat("MMMM", Locale.getDefault())
    val simpleDateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
    val simpleDateFormatDay = SimpleDateFormat("EEEE dd", Locale.getDefault())

    fun convertDpToPixel(context: Context, dp: Float): Int {
        return (dp * (context.resources.displayMetrics.densityDpi / 160f)).toInt()
    }

    fun randInt(min: Int, max: Int): Int {
        return Random().nextInt(max - min + 1)
    }
}
