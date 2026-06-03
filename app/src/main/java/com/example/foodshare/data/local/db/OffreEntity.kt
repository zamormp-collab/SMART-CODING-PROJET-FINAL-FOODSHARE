package com.example.foodshare.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodshare.data.remote.dto.OffreDto

@Entity(tableName = "offres")
data class OffreEntity(
    @PrimaryKey val id: String,
    val title: String?,
    val description: String?,
    val quantity: Int?,
    val expirationDate: String?,
    val location: String?,
    val imageUrl: String?,
    val userId: String?,
    val cachedAt: Long = System.currentTimeMillis()
)

// Extension pour convertir du DTO (Réseau) vers l'Entité (Local)
fun OffreDto.toEntity(): OffreEntity? {
    val id = this.id ?: return null
    return OffreEntity(
        id = id,
        title = title,
        description = description,
        quantity = quantity,
        expirationDate = expirationDate,
        location = location,
        imageUrl = imageUrl,
        userId = userId
    )
}

// Extension pour convertir de l'Entité (Local) vers le DTO (UI)
fun OffreEntity.toDto(): OffreDto {
    return OffreDto(
        id = id,
        title = title,
        description = description,
        quantity = quantity,
        expirationDate = expirationDate,
        location = location,
        imageUrl = imageUrl,
        userId = userId
    )
}
