package com.example.shope.data.models

data class UniformItem(
    var id: String = "",
    var itemName: String = "",
    var itemImage: String = "", // Base64 string
    var category: String = "Regular", // Regular, PT, Winter, etc.
    var price: Double = 0.0,
    var quantity: Int = 0,
    var status: String = "active",
    var schoolId: String = "",
    var schoolName: String = ""
) {
    constructor() : this("", "", "", "Regular", 0.0, 0, "active", "", "")
}
