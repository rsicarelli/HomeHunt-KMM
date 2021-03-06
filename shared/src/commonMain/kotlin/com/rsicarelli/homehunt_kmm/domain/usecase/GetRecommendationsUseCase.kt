package com.rsicarelli.homehunt_kmm.domain.usecase

import com.rsicarelli.homehunt_kmm.core.model.UseCase
import com.rsicarelli.homehunt_kmm.domain.model.Property
import com.rsicarelli.homehunt_kmm.domain.repository.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class GetRecommendationsUseCase(
    private val propertyRepository: PropertyRepository,
    private val getFilterPreferences: GetSearchOptionSettings,
    private val filterProperties: FilterPropertiesUseCase,
) : UseCase<Unit, GetRecommendationsUseCase.Outcome> {

    data class Outcome(val properties: List<Property>)

    @OptIn(FlowPreview::class)
    override fun invoke(request: Unit): Flow<Outcome> =
        getFilterPreferences(request)
            .combine(propertyRepository.getDiscover()) { filterOutcome, properties ->
                Pair(filterOutcome.searchOption, properties)
            }
            .flatMapConcat {
                filterProperties(FilterPropertiesUseCase.Request(it.first, it.second))
            }
            .map {
                Outcome(it.properties)
            }.flowOn(Dispatchers.Default)

}