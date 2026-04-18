package com.example.kt6_1.domain

// Модель данных для фотографии
data class Photo(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val downloadUrl: String
) {
    val previewUrl: String
        get() = "https://picsum.photos/id/${id}/200/200"

    val fullUrl: String
        get() = "https://picsum.photos/id/${id}/${width}/${height}"
}

// получение списка фотографий
class GetPhotosUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(limit: Int = 20): List<Photo> = repository.getPhotos(limit)
}

interface PhotoRepository {
    suspend fun getPhotos(limit: Int): List<Photo>
}