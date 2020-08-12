package com.aviral.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Restaurants")
data class RestaurantEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "restaurant_name") val RestaurantName: String,
    @ColumnInfo(name = "restaurant_rating") val Rating: String,
    @ColumnInfo(name = "restaurant_cost") val Cost: String,
    @ColumnInfo(name = "restaurant_image") val image: String

)




