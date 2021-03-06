package com.rsicarelli.homehunt_kmm.domain.model

data class Property(
    val _id: String,
    val price: Double,
    val title: String,
    val location: Location,
    val surface: Int,
    val dormCount: Int,
    val bathCount: Int,
    val avatarUrl: String,
    val tag: String,
    val propertyUrl: String,
    val videoUrl: String?,
    val fullDescription: String,
    val locationDescription: String?,
    val characteristics: List<String>,
    val photoGalleryUrls: List<String>,
    val pdfUrl: String?,
    val origin: String,
    val isActive: Boolean,
    val isViewed: Boolean = false,
    val isUpVoted: Boolean = false,
    val isDownVoted: Boolean = false
) {

    sealed class Tag(val identifier: String) {
        object EMPTY : Tag("")
        object NEW : Tag("NEW")
        object RESERVED : Tag("RESERVED")
        object RENTED : Tag("RENTED")
    }
}

data class Location(
    val lat: Double,
    val lng: Double,
    val name: String,
    val isApproximated: Boolean,
    val isUnknown: Boolean
) {
    fun toStaticMap(
        key: String,
        zoom: Int = 13,
        width: Int = 2000,
        height: Int = 140
    ): String {
        return "https://maps.googleapis.com/maps/api/staticmap?center=$lat,$lng&zoom=$zoom&size=${width}x${height}&markers=%7C${lat},${lng}&scale=2&maptype=roadmap&key=${key}"
    }
}

fun String?.toTag(): Property.Tag = this?.let {
    return@let when (it) {
        Property.Tag.NEW.identifier -> Property.Tag.NEW
        Property.Tag.RESERVED.identifier -> Property.Tag.RESERVED
        Property.Tag.RENTED.identifier -> Property.Tag.RENTED
        else -> Property.Tag.EMPTY
    }
} ?: Property.Tag.EMPTY