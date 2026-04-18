package com.example.kt6_1.data

import com.example.kt6_1.domain.Photo
import com.example.kt6_1.domain.PhotoRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// DTO для ответа от API
data class PicsumPhotoDto(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val download_url: String
)

// Retrofit API интерфейс
interface PicsumApi {
    @GET("v2/list")
    suspend fun getPhotos(
        @Query("limit") limit: Int = 20
    ): List<PicsumPhotoDto>
}

// Реализация репозитория
class PhotoRepositoryImpl : PhotoRepository {

    private val api: PicsumApi = Retrofit.Builder()
        .baseUrl("https://picsum.photos/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PicsumApi::class.java)

    override suspend fun getPhotos(limit: Int): List<Photo> {
        return try {
            val dtoList = api.getPhotos(limit)
            dtoList.map { dto ->
                Photo(
                    id = dto.id,
                    author = dto.author,
                    width = dto.width,
                    height = dto.height,
                    url = dto.url,
                    downloadUrl = dto.download_url
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}