package com.aviral.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface OrderDao {

    @Insert
    fun insertDish(orderEntity: OrderEntity)

    @Delete
    fun deleteDish(orderEntity: OrderEntity)

    @Query("SELECT * FROM orderlist")
    fun getAllDishes(): List<OrderEntity>

    @Query("SELECT * FROM orderlist WHERE id=:dishId")
    fun getDishById(dishId: String): OrderEntity


    @Query("DELETE FROM orderlist")
    fun deleteAll()

    @Query("SELECT SUM(dish_price) AS total FROM orderlist")
    fun totalCost(): Int

}