package com.sokarcreative.basicstuffrecyclerview

import android.graphics.Rect
import org.mockito.Mockito

fun Rect.withInjectedMockitoMethods(): Rect = apply {
    Mockito.`when`(height()).thenReturn(Math.abs(bottom - top))
    Mockito.`when`(width()).thenReturn(Math.abs(right - left))
}
fun Rect.absWidth() = Math.abs(right - left)
fun Rect.absHeight() = Math.abs(bottom - top)