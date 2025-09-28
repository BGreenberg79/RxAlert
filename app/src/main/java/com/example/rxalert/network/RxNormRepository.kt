package com.example.rxalert.network

import com.example.rxalert.data.RxDrug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RxNormRepository private constructor(
    private val service: RxNormApiService
) {

    suspend fun searchMedications(query: String): List<RxDrug> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@withContext emptyList()
        val response = service.searchDrugs(trimmed)
        val conceptGroups = response.drugGroup?.conceptGroup.orEmpty()
        conceptGroups.flatMap { group ->
            val type = group.tty ?: ""
            group.conceptProperties.orEmpty().mapNotNull { property ->
                val name = property.name ?: return@mapNotNull null
                val rxcui = property.rxcui ?: return@mapNotNull null
                RxDrug(
                    rxcui = rxcui,
                    name = name,
                    synonym = property.synonym,
                    type = property.tty ?: type
                )
            }
        }.distinctBy { it.rxcui }
    }

    companion object {
        private const val BASE_URL = "https://rxnav.nlm.nih.gov/REST/"

        @Volatile
        private var instance: RxNormRepository? = null

        fun getInstance(): RxNormRepository {
            return instance ?: synchronized(this) {
                instance ?: RxNormRepository(createService()).also { instance = it }
            }
        }

        private fun createService(): RxNormApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(RxNormApiService::class.java)
        }
    }
}