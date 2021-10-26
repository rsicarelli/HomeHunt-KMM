package com.rsicarelli.homehunt_kmm.data.repository

import com.rsicarelli.homehunt_kmm.data.cache.PropertyCache
import com.rsicarelli.homehunt_kmm.data.network.PropertyService
import com.rsicarelli.homehunt_kmm.domain.model.Property
import com.rsicarelli.homehunt_kmm.domain.repository.PropertyRepository
import com.rsicarelli.homehunt_kmm.type.DownVoteInput
import com.rsicarelli.homehunt_kmm.type.UpVoteInput
import com.rsicarelli.homehunt_kmm.type.ViewedPropertyInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PropertyRepositoryImpl(
    private val propertyCache: PropertyCache,
    private val propertyService: PropertyService
) : PropertyRepository {
    override fun getProperties(): Flow<List<Property>> {
        return flow {
            propertyService.getProperties()?.let {
                propertyCache.saveAll(it)
                emit(it)
            }
        }
    }

    override suspend fun getPropertyById(id: String): Property? = propertyCache.get(id)

    override suspend fun markAsViewed(viewedPropertyInput: ViewedPropertyInput) {
        propertyService.markAsViewed(viewedPropertyInput)
            .takeIf { success -> success }
            ?.let {
                propertyCache.updateVisibility(viewedPropertyInput.propertyId)
            }
    }

    override suspend fun upVote(upVoteInput: UpVoteInput) {
        propertyService.upVote(upVoteInput)
            .takeIf { success -> success }
            ?.let {
                propertyCache.upVote(upVoteInput.propertyId)
            }
    }

    override suspend fun downVote(downVoteInput: DownVoteInput) {
        propertyService.downVote(downVoteInput)
            .takeIf { success -> success }
            ?.let {
                propertyCache.downVote(downVoteInput.propertyId)
            }
    }
}