package com.github.theaellis.couponmaxxr

data class Retailer(
    val name: String,
    val domain: String,
    val loginUrl: String,
    val couponsUrl: String,
    val scriptFilename: String,
    val sessionIndicators: List<String> // e.g., listOf("ppdtk=")
) {
    // A clean helper function so the Retailer object checks its own cookies
    fun hasValidSession(cookieString: String?): Boolean {
        if (cookieString == null) return false
        return sessionIndicators.any { cookieString.contains(it, ignoreCase = true) }
    }
}