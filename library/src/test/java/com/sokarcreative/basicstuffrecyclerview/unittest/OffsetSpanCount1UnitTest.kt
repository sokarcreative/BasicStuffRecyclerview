package com.sokarcreative.basicstuffrecyclerview.unittest

import android.graphics.Rect
import com.sokarcreative.basicstuffrecyclerview.*
import com.sokarcreative.basicstuffrecyclerview.assertRectEquals
import com.sokarcreative.basicstuffrecyclerview.divider.LinearDividersListener
import org.junit.Test
import java.lang.IllegalStateException


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */

class OffsetSpanCount1UnitTest {

    private val emptyExpectedRect = ExpectedRect()

    @Test
    @Throws(Exception::class)
    fun itemOffsetsSpanCount1WithOneViewTypeAndOnlyDividerDecoration() {
        val dividerDecoration = Decoration.Space(10)
        val linearDividersListener = object : LinearDividersListener {
            override fun getDividerDecoration(viewType: Int): Decoration? = dividerDecoration
        }
        for (itemsCount in 1..ARBITRARY_ITEMS_COUNT) {
            with(Situation(
                    spanCount = 1,
                    items = arrayOfNulls<Any>(itemsCount).toList(),
                    getItemViewType = { position: Int -> 0 },
                    getSpanSizeOnViewType = { viewType -> 1 },
                    linearDividersListener = linearDividersListener,
            )) {
                OffsetTestBuilder.build(this) { resultsInVerticalLtr: Array<Rect>, resultsInHorizontalLtr: Array<Rect>, resultsInVerticalRtl: Array<Rect>, resultsInHorizontalRtl: Array<Rect> ->
                    val verticalChecker: (Array<Rect>) -> Unit = {
                        with(it) {
                            for (position in 0 until items.count() - 1) {
                                assertRectEquals(ExpectedRect(bottom = dividerDecoration.value), this[position])
                            }
                            assertRectEquals(emptyExpectedRect, this[items.count() - 1])
                        }
                    }
                    with(resultsInVerticalLtr) {
                        verticalChecker(this)
                    }
                    with(resultsInHorizontalLtr) {
                        for (position in 0 until items.count() - 1) {
                            assertRectEquals(ExpectedRect(right = dividerDecoration.value), this[position])
                        }
                        assertRectEquals(emptyExpectedRect, this[items.count() - 1])
                    }
                    with(resultsInVerticalRtl) {
                        verticalChecker(this)
                    }
                    with(resultsInHorizontalRtl) {
                        for (position in 0 until items.count() - 1) {
                            assertRectEquals(ExpectedRect(left = dividerDecoration.value), this[position])
                        }
                        assertRectEquals(emptyExpectedRect, this[items.count() - 1])
                    }
                }
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun itemOffsetsSpanCount1WithOneViewTypeAndOnlyFirstDecoration() {
        val firstDecoration = Decoration.Space(10)
        val linearDividersListener = object : LinearDividersListener {
            override fun getFirstDecoration(viewType: Int): Decoration? = firstDecoration
        }
        for (itemsCount in 1..ARBITRARY_ITEMS_COUNT) {
            with(Situation(
                    spanCount = 1,
                    items = arrayOfNulls<Any>(itemsCount).toList(),
                    getItemViewType = { position: Int -> 0 },
                    getSpanSizeOnViewType = { viewType -> 1 },
                    linearDividersListener = linearDividersListener,
            )) {
                OffsetTestBuilder.build(this) { resultsInVerticalLtr: Array<Rect>, resultsInHorizontalLtr: Array<Rect>, resultsInVerticalRtl: Array<Rect>, resultsInHorizontalRtl: Array<Rect> ->
                    val verticalChecker: (Array<Rect>) -> Unit = {
                        with(it) {
                            assertRectEquals(ExpectedRect(top = firstDecoration.value), this[0])
                            for (position in 1 until items.count()) {
                                assertRectEquals(emptyExpectedRect, this[position])
                            }
                        }
                    }
                    with(resultsInVerticalLtr) {
                        verticalChecker(this)
                    }
                    with(resultsInHorizontalLtr) {
                        assertRectEquals(ExpectedRect(left = firstDecoration.value), this[0])
                        for (position in 1 until items.count()) {
                            assertRectEquals(emptyExpectedRect, this[position])
                        }
                    }
                    with(resultsInVerticalRtl) {
                        with(resultsInVerticalLtr) {
                            verticalChecker(this)
                        }
                    }
                    with(resultsInHorizontalRtl) {
                        assertRectEquals(ExpectedRect(right = firstDecoration.value), this[0])
                        for (position in 1 until items.count()) {
                            assertRectEquals(emptyExpectedRect, this[position])
                        }
                    }
                }
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun itemOffsetsSpanCount1WithOneViewTypeAndOnlyLastDecoration() {
        val lastDecoration = Decoration.Space(10)
        val linearDividersListener = object : LinearDividersListener {
            override fun getLastDecoration(viewType: Int): Decoration? = lastDecoration
        }
        for (itemsCount in 1..ARBITRARY_ITEMS_COUNT) {
            with(Situation(
                    spanCount = 1,
                    items = arrayOfNulls<Any>(itemsCount).toList(),
                    getItemViewType = { position: Int -> 0 },
                    getSpanSizeOnViewType = { viewType -> 1 },
                    linearDividersListener = linearDividersListener,
            )) {
                OffsetTestBuilder.build(this) { resultsInVerticalLtr: Array<Rect>, resultsInHorizontalLtr: Array<Rect>, resultsInVerticalRtl: Array<Rect>, resultsInHorizontalRtl: Array<Rect> ->
                    val verticalChecker: (Array<Rect>) -> Unit = {
                        with(it) {
                            for (position in 0 until items.count() - 1) {
                                assertRectEquals(emptyExpectedRect, this[position])
                            }
                            assertRectEquals(ExpectedRect(bottom = lastDecoration.value), this[items.count() - 1])
                        }
                    }
                    with(resultsInVerticalLtr) {
                        verticalChecker(this)
                    }
                    with(resultsInHorizontalLtr) {
                        for (position in 0 until items.count() - 1) {
                            assertRectEquals(emptyExpectedRect, this[position])
                        }
                        assertRectEquals(ExpectedRect(right = lastDecoration.value), this[items.count() - 1])
                    }
                    with(resultsInVerticalRtl) {
                        verticalChecker(this)
                    }
                    with(resultsInHorizontalRtl) {
                        for (position in 0 until items.count() - 1) {
                            assertRectEquals(emptyExpectedRect, this[position])
                        }
                        assertRectEquals(ExpectedRect(left = lastDecoration.value), this[items.count() - 1])
                    }
                }
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun itemOffsetsSpanCount1WithAllDecorationsExceptGridBorderDecoration() {
        val firstDecoration = Decoration.Space(10)
        val firstDividerDecoration = Decoration.Space(20)
        val dividerDecoration = Decoration.Space(30)
        val lastDividerDecoration = Decoration.Space(50)
        val lastDecoration = Decoration.Space(80)
        val linearDividersListener = object : LinearDividersListener {
            override fun getFirstDecoration(viewType: Int): Decoration? = firstDecoration
            override fun getFirstDividerDecoration(viewType: Int, previousViewType: Int): Decoration? = firstDividerDecoration
            override fun getDividerDecoration(viewType: Int): Decoration? = dividerDecoration
            override fun getLastDividerDecoration(viewType: Int, nextViewType: Int): Decoration? = lastDividerDecoration
            override fun getLastDecoration(viewType: Int): Decoration? = lastDecoration
        }
        for (itemsCount in 1..ARBITRARY_ITEMS_COUNT) {
            with(Situation(
                    spanCount = 1,
                    items = arrayOfNulls<Any>(itemsCount).toList(),
                    getItemViewType = { position: Int -> 0 },
                    getSpanSizeOnViewType = { viewType -> 1 },
                    linearDividersListener = linearDividersListener,
            )) {
                OffsetTestBuilder.build(this) { resultsInVerticalLtr: Array<Rect>, resultsInHorizontalLtr: Array<Rect>, resultsInVerticalRtl: Array<Rect>, resultsInHorizontalRtl: Array<Rect> ->
                    val verticalChecker: (Array<Rect>) -> Unit = {
                        with(it) {
                            if (items.count() == 1) {
                                assertRectEquals(ExpectedRect(top = firstDecoration.value, bottom = lastDecoration.value), this[0])
                            } else {
                                assertRectEquals(ExpectedRect(top = firstDecoration.value, bottom = dividerDecoration.value), this[0])
                                for (position in 1 until items.count() - 1) {
                                    assertRectEquals(ExpectedRect(bottom = dividerDecoration.value), this[position])
                                }
                                assertRectEquals(ExpectedRect(bottom = lastDecoration.value), this[items.count() - 1])
                            }
                        }
                    }
                    with(resultsInVerticalLtr) {
                        verticalChecker(this)
                    }
                    with(resultsInHorizontalLtr) {
                        if (items.count() == 1) {
                            assertRectEquals(ExpectedRect(left = firstDecoration.value, right = lastDecoration.value), this[0])
                        } else {
                            assertRectEquals(ExpectedRect(left = firstDecoration.value, right = dividerDecoration.value), this[0])
                            for (position in 1 until items.count() - 1) {
                                assertRectEquals(ExpectedRect(right = dividerDecoration.value), this[position])
                            }
                            assertRectEquals(ExpectedRect(right = lastDecoration.value), this[items.count() - 1])
                        }
                    }
                    with(resultsInVerticalRtl) {
                        verticalChecker(this)
                    }
                    with(resultsInHorizontalRtl) {
                        if (items.count() == 1) {
                            assertRectEquals(ExpectedRect(right = firstDecoration.value, left = lastDecoration.value), this[0])
                        } else {
                            assertRectEquals(ExpectedRect(right = firstDecoration.value, left = dividerDecoration.value), this[0])
                            for (position in 1 until items.count() - 1) {
                                assertRectEquals(ExpectedRect(left = dividerDecoration.value), this[position])
                            }
                            assertRectEquals(ExpectedRect(left = lastDecoration.value), this[items.count() - 1])
                        }
                    }
                }
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun itemOffsetsSpanCount1WithMultipleViewTypesWithAllDecorationsExceptGridBorderDecoration() {
        val firstDecoration = Decoration.Space(10)
        val firstDividerDecoration = Decoration.Space(20)
        val dividerDecoration = Decoration.Space(30)
        val lastDividerDecoration = Decoration.Space(50)
        val lastDecoration = Decoration.Space(80)
        val VIEWTYPE_INT = 1
        val VIEWTYPE_STRING = 2
        val VIEWTYPE_DOUBLE = 3
        val VIEWTYPE_LONG = 4
        val fakeItems = arrayListOf<Any>(1, "my", "life", "is potato", 3, 7, 0.0, 5L)
        val linearDividersListener = object : LinearDividersListener {
            override fun getFirstDecoration(viewType: Int): Decoration? = if (viewType == VIEWTYPE_INT) firstDecoration else null
            override fun getFirstDividerDecoration(viewType: Int, previousViewType: Int): Decoration? = if (viewType == VIEWTYPE_STRING || viewType == VIEWTYPE_LONG) firstDividerDecoration else null
            override fun getDividerDecoration(viewType: Int): Decoration? = if (viewType == VIEWTYPE_STRING) dividerDecoration else null
            override fun getLastDividerDecoration(viewType: Int, nextViewType: Int): Decoration? = if (viewType == VIEWTYPE_STRING || viewType == VIEWTYPE_INT) lastDividerDecoration else null
            override fun getLastDecoration(viewType: Int): Decoration? = if (viewType != VIEWTYPE_LONG) lastDecoration else null
        }

        with(Situation(
                spanCount = 1,
                items = fakeItems,
                getItemViewType = { position: Int ->
                    when (fakeItems[position]) {
                        is Int -> VIEWTYPE_INT
                        is String -> VIEWTYPE_STRING
                        is Double -> VIEWTYPE_DOUBLE
                        is Long -> VIEWTYPE_LONG
                        else -> throw IllegalStateException()
                    }
                },
                getSpanSizeOnViewType = { viewType -> 1 },
                linearDividersListener = linearDividersListener,
        )) {
            OffsetTestBuilder.build(this) { resultsInVerticalLtr: Array<Rect>, resultsInHorizontalLtr: Array<Rect>, resultsInVerticalRtl: Array<Rect>, resultsInHorizontalRtl: Array<Rect> ->
                val verticalChecker: (Array<Rect>) -> Unit = {
                    with(it) {
                        assertRectEquals(ExpectedRect(top = firstDecoration.value, bottom = lastDividerDecoration.value), this[0])
                        assertRectEquals(ExpectedRect(top = firstDividerDecoration.value, bottom = dividerDecoration.value), this[1])
                        assertRectEquals(ExpectedRect(bottom = dividerDecoration.value), this[2])
                        assertRectEquals(ExpectedRect(bottom = lastDividerDecoration.value), this[3])
                        assertRectEquals(emptyExpectedRect, this[4])
                        assertRectEquals(ExpectedRect(bottom = lastDividerDecoration.value), this[5])
                        assertRectEquals(emptyExpectedRect, this[6])
                        assertRectEquals(ExpectedRect(top = firstDividerDecoration.value), this[7])
                    }
                }
                with(resultsInVerticalLtr) {
                    verticalChecker(this)
                }
                with(resultsInHorizontalLtr) {
                    assertRectEquals(ExpectedRect(left = firstDecoration.value, right = lastDividerDecoration.value), this[0])
                    assertRectEquals(ExpectedRect(left = firstDividerDecoration.value, right = dividerDecoration.value), this[1])
                    assertRectEquals(ExpectedRect(right = dividerDecoration.value), this[2])
                    assertRectEquals(ExpectedRect(right = lastDividerDecoration.value), this[3])
                    assertRectEquals(emptyExpectedRect, this[4])
                    assertRectEquals(ExpectedRect(right = lastDividerDecoration.value), this[5])
                    assertRectEquals(emptyExpectedRect, this[6])
                    assertRectEquals(ExpectedRect(left = firstDividerDecoration.value), this[7])
                }
                with(resultsInVerticalRtl) {
                    verticalChecker(this)
                }
                with(resultsInHorizontalRtl) {
                    assertRectEquals(ExpectedRect(right = firstDecoration.value, left = lastDividerDecoration.value), this[0])
                    assertRectEquals(ExpectedRect(right = firstDividerDecoration.value, left = dividerDecoration.value), this[1])
                    assertRectEquals(ExpectedRect(left = dividerDecoration.value), this[2])
                    assertRectEquals(ExpectedRect(left = lastDividerDecoration.value), this[3])
                    assertRectEquals(emptyExpectedRect, this[4])
                    assertRectEquals(ExpectedRect(left = lastDividerDecoration.value), this[5])
                    assertRectEquals(emptyExpectedRect, this[6])
                    assertRectEquals(ExpectedRect(right = firstDividerDecoration.value), this[7])
                }
            }
        }
    }

    companion object {
        const val ARBITRARY_ITEMS_COUNT = 4
    }

}
