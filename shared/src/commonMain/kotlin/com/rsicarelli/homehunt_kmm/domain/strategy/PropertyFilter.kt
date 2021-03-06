package com.rsicarelli.homehunt_kmm.domain.strategy

import com.rsicarelli.homehunt_kmm.domain.model.Property
import com.rsicarelli.homehunt_kmm.domain.model.Property.Tag
import com.rsicarelli.homehunt_kmm.domain.model.SearchOption
import com.rsicarelli.homehunt_kmm.domain.model.toTag

interface PropertyFilter {
    fun apply(searchOption: SearchOption, property: Property): Boolean
}

val allFilters = listOf(
    Price,
    Surface,
    Dorm,
    Bath,
    LongTermOnly,
    Availability
)

private object Price : PropertyFilter {
    private const val UNLIMITED_PRICE = 99999.0
    private const val MAX_PRICE = 2000.0

    override fun apply(searchOption: SearchOption, property: Property): Boolean {
        val (min, max) = searchOption.priceRange

        val range = if (max == MAX_PRICE) min..UNLIMITED_PRICE else min..max

        return property.price in range
    }
}

private object Surface : PropertyFilter {
    private const val UNLIMITED_SURFACE = 99999
    private const val MAX_SURFACE = 180

    override fun apply(searchOption: SearchOption, property: Property): Boolean {
        val (min, max) = searchOption.surfaceRange

        val range = if (max == MAX_SURFACE) min..UNLIMITED_SURFACE else min..max

        return property.surface in range
    }
}

private object Dorm : PropertyFilter {
    override fun apply(searchOption: SearchOption, property: Property): Boolean {
        return property.dormCount >= searchOption.dormCount
    }
}

private object Bath : PropertyFilter {
    override fun apply(searchOption: SearchOption, property: Property): Boolean {
        return property.bathCount >= searchOption.bathCount
    }
}

private object Visibility : PropertyFilter {
    override fun apply(searchOption: SearchOption, property: Property): Boolean {
        if (searchOption.showSeen) return true

        return !property.isViewed
    }
}

private object LongTermOnly : PropertyFilter {
    override fun apply(searchOption: SearchOption, property: Property): Boolean {
        if (!searchOption.longTermOnly) return true

        return property.fullDescription.lowercase().contains("short term").not()
    }
}

private object Availability : PropertyFilter {
    override fun apply(searchOption: SearchOption, property: Property): Boolean {
        if (searchOption.availableOnly) {
            return property.tag.toTag() != Tag.RESERVED && property.tag.toTag() != Tag.RENTED
        }

        return true
    }
}

private object UpVoted : PropertyFilter {
    override fun apply(searchOption: SearchOption, property: Property): Boolean {
        if (searchOption.upVotedOnly) {
            return !property.isDownVoted
        }

        return true
    }

}