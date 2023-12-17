package com.example.finalproject

// Item.kt
data class Item(
    val id: Long,
    val item_name: String,
    val color: String,
    val description: String,
    val found_date: String,
    val found_location: String,
    val found_by: String,
    val contact_email: String,
    val contact_phone: String,
    val imageData: String? // Base64-encoded image data
)


