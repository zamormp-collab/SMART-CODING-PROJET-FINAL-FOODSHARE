package com.example.foodshare.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OffreDao {
    @Query("SELECT * FROM offres ORDER BY cachedAt DESC")
    suspend fun getAllOffres(): List<OffreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffres(offres: List<OffreEntity>)

    @Query("DELETE FROM offres")
    suspend fun clearAll()
}
