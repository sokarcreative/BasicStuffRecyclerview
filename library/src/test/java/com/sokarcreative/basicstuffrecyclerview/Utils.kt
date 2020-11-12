package com.sokarcreative.basicstuffrecyclerview

import android.graphics.Rect
import org.junit.Assert
import org.mockito.Mock


internal fun assertRectEquals(source: MyRect, @Mock target: Rect) {
    Assert.assertEquals(null, source, MyRect(target.left, target.top, target.right, target.bottom))
}