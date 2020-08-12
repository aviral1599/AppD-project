package com.aviral.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OrderList")
data class OrderEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "dish_name") val DishName: String,
    @ColumnInfo(name = "dish_price") val DishPrice: String
)
