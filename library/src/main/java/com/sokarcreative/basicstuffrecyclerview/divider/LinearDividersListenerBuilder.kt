package com.sokarcreative.basicstuffrecyclerview.divider

import com.sokarcreative.basicstuffrecyclerview.Decoration

class LinearDividersListenerBuilder {

    private var defaultFirstDecoration: Decoration? = null
    private var firstDecorations: HashMap<Int, Decoration?>? = null
    private var defaultFirstDividerDecoration: Decoration? = null
    private var defaultFirstDividerDecorations: HashMap<Int, Decoration?>? = null
    private var firstDividerDecorations: HashMap<Pair<Int, Int>, Decoration?>? = null
    private var defaultDividerDecoration: Decoration? = null
    private var dividerDecorations: HashMap<Int, Decoration?>? = null
    private var defaultGridSideBorderDecoration: Decoration? = null
    private var gridSideBorderDecoration: HashMap<Int, Decoration?>? = null
    private var defaultLastDividerDecoration: Decoration? = null
    private var defaultLastDividerDecorations: HashMap<Int, Decoration?>? = null
    private var lastDividerDecorations: HashMap<Pair<Int, Int>, Decoration?>? = null
    private var defaultLastDecoration: Decoration? = null
    private var lastDecorations: HashMap<Int, Decoration?>? = null


    /**
     *
     * FIRST LAST DECORATION
     *
     */
    fun defaultFirstLastDecoration(decoration: Decoration?): LinearDividersListenerBuilder = apply {
        defaultFirstDecoration = decoration
        defaultLastDecoration = decoration
    }

    fun firstLastDecoration(viewType: Int, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        firstDecoration(viewType, decoration)
        lastDecoration(viewType, decoration)
    }

    fun firstLastDecoration(viewTypes: Set<Int>, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        firstDecoration(viewTypes, decoration)
        lastDecoration(viewTypes, decoration)
    }

    /**
     *
     * FIRST DECORATION
     *
     */
    fun defaultFirstDecoration(decoration: Decoration?): LinearDividersListenerBuilder = apply {
        defaultFirstDecoration = decoration
    }

    fun firstDecoration(viewType: Int, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        (firstDecorations ?: HashMap()).let { firstDecorations ->
            this.firstDecorations = firstDecorations.apply {
                put(viewType, decoration)
            }
        }
    }

    fun firstDecoration(viewTypes: Set<Int>, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        if (viewTypes.isNotEmpty()) {
            (firstDecorations ?: HashMap()).let { firstDecorations ->
                this.firstDecorations = firstDecorations.apply {
                    viewTypes.forEach {
                        put(it, decoration)
                    }
                }
            }
        }
    }

    /**
     *
     * FIRST DIVIDER DECORATION
     *
     */

    fun defaultFirstDividerDecoration(decoration: Decoration?): LinearDividersListenerBuilder = apply {
        defaultFirstDividerDecoration = decoration
    }

    fun defaultFirstDividerDecoration(viewType: Int, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        (defaultFirstDividerDecorations ?: HashMap()).let { defaultFirstDividerDecorations ->
            this.defaultFirstDividerDecorations = defaultFirstDividerDecorations.apply {
                put(viewType, decoration)
            }
        }
    }

    fun defaultFirstDividerDecoration(viewTypes: Set<Int>, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        if (viewTypes.isNotEmpty()) {
            (defaultFirstDividerDecorations ?: HashMap()).let { defaultFirstDividerDecorations ->
                this.defaultFirstDividerDecorations = defaultFirstDividerDecorations.apply {
                    viewTypes.forEach {
                        put(it, decoration)
                    }
                }
            }
        }
    }

    fun firstDividerDecoration(viewType: Int, previousViewType: Int, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        if (viewType != previousViewType) {
            (firstDividerDecorations ?: HashMap()).let { firstDividerDecorations ->
                this.firstDividerDecorations = firstDividerDecorations.apply {
                    put(viewType to previousViewType, decoration)
                }
            }
        }
    }

    fun firstDividerDecoration(viewTypeToPreviousViewTypeSet: Set<Pair<Int, Int>>, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        if (viewTypeToPreviousViewTypeSet.isNotEmpty()) {
            (firstDividerDecorations ?: HashMap()).let { firstDividerDecorations ->
                this.firstDividerDecorations = firstDividerDecorations.apply {
                    viewTypeToPreviousViewTypeSet.forEach {
                        if (it.first != it.second) {
                            put(it, decoration)
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * DIVIDER DECORATION
     *
     */

    fun defaultDividerDecoration(decoration: Decoration?): LinearDividersListenerBuilder = apply {
        defaultDividerDecoration = decoration
    }

    fun dividerDecoration(viewType: Int, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        (dividerDecorations ?: HashMap()).let { dividerDecorations ->
            this.dividerDecorations = dividerDecorations.apply {
                put(viewType, decoration)
            }
        }
    }

    fun dividerDecoration(viewTypes: Set<Int>, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        if (viewTypes.isNotEmpty()) {
            (dividerDecorations ?: HashMap()).let { dividerDecorations ->
                this.dividerDecorations = dividerDecorations.apply {
                    viewTypes.forEach {
                        put(it, decoration)
                    }
                }
            }
        }
    }

    /**
     *
     * GRID SIDE DECORATION
     *
     */

    fun defaultGridSideBorderDecoration(decoration: Decoration?): LinearDividersListenerBuilder = apply {
        defaultGridSideBorderDecoration = decoration
    }

    fun gridSideBorderDecoration(viewType: Int, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        (gridSideBorderDecoration ?: HashMap()).let { gridSideBorderDecoration ->
            this.gridSideBorderDecoration = gridSideBorderDecoration.apply {
                put(viewType, decoration)
            }
        }
    }

    fun gridSideBorderDecoration(viewTypes: Set<Int>, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        if (viewTypes.isNotEmpty()) {
            (gridSideBorderDecoration ?: HashMap()).let { gridSideBorderDecoration ->
                this.gridSideBorderDecoration = gridSideBorderDecoration.apply {
                    viewTypes.forEach {
                        put(it, decoration)
                    }
                }
            }
        }
    }

    /**
     *
     * LAST DIVIDER DECORATION
     *
     */

    fun defaultLastDividerDecoration(decoration: Decoration?): LinearDividersListenerBuilder = apply {
        defaultLastDividerDecoration = decoration
    }

    fun defaultLastDividerDecoration(viewType: Int, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        (defaultLastDividerDecorations ?: HashMap()).let { defaultLastDividerDecorations ->
            this.defaultLastDividerDecorations = defaultLastDividerDecorations.apply {
                put(viewType, decoration)
            }
        }
    }

    fun defaultLastDividerDecoration(viewTypes: Set<Int>, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        if (viewTypes.isNotEmpty()) {
            (defaultLastDividerDecorations ?: HashMap()).let { defaultLastDividerDecorations ->
                this.defaultLastDividerDecorations = defaultLastDividerDecorations.apply {
                    viewTypes.forEach {
                        put(it, decoration)
                    }
                }
            }
        }
    }

    fun lastDividerDecoration(viewType: Int, nextViewType: Int, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        if (viewType != nextViewType) {
            (lastDividerDecorations ?: HashMap()).let { lastDividerDecorations ->
                this.lastDividerDecorations = lastDividerDecorations.apply {
                    put(viewType to nextViewType, decoration)
                }
            }
        }
    }

    fun lastDividerDecoration(viewTypeToNextViewTypeSet: Set<Pair<Int, Int>>, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        if (viewTypeToNextViewTypeSet.isNotEmpty()) {
            (lastDividerDecorations ?: HashMap()).let { lastDividerDecorations ->
                this.lastDividerDecorations = lastDividerDecorations.apply {
                    viewTypeToNextViewTypeSet.forEach {
                        if (it.first != it.second) {
                            put(it, decoration)
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * LAST DECORATION
     *
     */

    fun defaultLastDecoration(decoration: Decoration?): LinearDividersListenerBuilder = apply {
        defaultLastDecoration = decoration
    }

    fun lastDecoration(viewType: Int, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        (lastDecorations ?: HashMap()).let { lastDecorations ->
            this.lastDecorations = lastDecorations.apply {
                put(viewType, decoration)
            }
        }
    }

    fun lastDecoration(viewTypes: Set<Int>, decoration: Decoration?): LinearDividersListenerBuilder = apply {
        if (viewTypes.isNotEmpty()) {
            (lastDecorations ?: HashMap()).let { lastDecorations ->
                this.lastDecorations = lastDecorations.apply {
                    viewTypes.forEach {
                        put(it, decoration)
                    }
                }
            }
        }
    }

    fun build(): LinearDividersListener =
            object : LinearDividersListener {
                private val defaultFirstDecoration: Decoration? = this@LinearDividersListenerBuilder.defaultFirstDecoration
                private val firstDecorations: Map<Int, Decoration?>? = this@LinearDividersListenerBuilder.firstDecorations?.toMap()
                private val defaultFirstDividerDecoration: Decoration? = this@LinearDividersListenerBuilder.defaultFirstDividerDecoration
                private val defaultFirstDividerDecorations: Map<Int, Decoration?>? = this@LinearDividersListenerBuilder.defaultFirstDividerDecorations?.toMap()
                private val firstDividerDecorations: Map<Pair<Int, Int>, Decoration?>? = this@LinearDividersListenerBuilder.firstDividerDecorations?.toMap()
                private val defaultDividerDecoration: Decoration? = this@LinearDividersListenerBuilder.defaultDividerDecoration
                private val dividerDecorations: Map<Int, Decoration?>? = this@LinearDividersListenerBuilder.dividerDecorations?.toMap()
                private val defaultGridSideBorderDecoration: Decoration? = this@LinearDividersListenerBuilder.defaultGridSideBorderDecoration
                private val gridSideBorderDecoration: Map<Int, Decoration?>? = this@LinearDividersListenerBuilder.gridSideBorderDecoration?.toMap()
                private val defaultLastDividerDecoration: Decoration? = this@LinearDividersListenerBuilder.defaultLastDividerDecoration
                private val defaultLastDividerDecorations: Map<Int, Decoration?>? = this@LinearDividersListenerBuilder.defaultLastDividerDecorations?.toMap()
                private val lastDividerDecorations: Map<Pair<Int, Int>, Decoration?>? = this@LinearDividersListenerBuilder.lastDividerDecorations?.toMap()
                private val defaultLastDecoration: Decoration? = this@LinearDividersListenerBuilder.defaultLastDecoration
                private val lastDecorations: Map<Int, Decoration?>? = this@LinearDividersListenerBuilder.lastDecorations?.toMap()

                override fun getFirstDecoration(viewType: Int): Decoration? {
                    return firstDecorations?.takeIf { it.containsKey(viewType) }?.get(viewType)
                            ?: defaultFirstDecoration
                }

                override fun getFirstDividerDecoration(viewType: Int, previousViewType: Int): Decoration? {
                    return firstDividerDecorations?.takeIf { it.containsKey(viewType to previousViewType) }?.get(viewType to previousViewType)
                            ?: defaultFirstDividerDecorations?.takeIf { it.containsKey(viewType) }?.get(viewType)
                            ?: defaultFirstDividerDecoration
                }

                override fun getDividerDecoration(viewType: Int): Decoration? {
                    return dividerDecorations?.takeIf { it.containsKey(viewType) }?.get(viewType)
                            ?: defaultDividerDecoration
                }

                override fun getGridSideBorderDecoration(viewType: Int): Decoration? {
                    return gridSideBorderDecoration?.takeIf { it.containsKey(viewType) }?.get(viewType)
                            ?: defaultGridSideBorderDecoration
                }

                override fun getLastDividerDecoration(viewType: Int, nextViewType: Int): Decoration? {
                    return lastDividerDecorations?.takeIf { it.containsKey(viewType to nextViewType) }?.get(viewType to nextViewType)
                            ?: defaultLastDividerDecorations?.takeIf { it.containsKey(viewType) }?.get(viewType)
                            ?: defaultLastDividerDecoration
                }

                override fun getLastDecoration(viewType: Int): Decoration? {
                    return lastDecorations?.takeIf { it.containsKey(viewType) }?.get(viewType)
                            ?: defaultLastDecoration
                }
            }
    fun buildAndWrapped(): LinearItemDecoration = LinearItemDecoration(build())
}