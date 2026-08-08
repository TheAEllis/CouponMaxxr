package com.github.theaellis.couponmaxxr

object RetailerRegistry {
    val stores = listOf(
        Retailer(
            name = "Publix",
            domain = "publix.com",
            loginUrl = "https://www.publix.com/login",
            couponsUrl = "https://www.publix.com/savings/digital-coupons",
            scriptFilename = "publix_clipper.js",
            sessionIndicators = listOf(".pblx.auth=")
        ),
        Retailer(
            name = "Food Lion",
            domain = "foodlion.com",
            loginUrl = "https://foodlion.com/",
            couponsUrl = "https://foodlion.com/savings/coupons",
            scriptFilename = "foodlion_clipper.js",
            sessionIndicators = listOf("ppdtk=")
        )
        // Adding a new store is now as simple as adding a block right here!
    )
}