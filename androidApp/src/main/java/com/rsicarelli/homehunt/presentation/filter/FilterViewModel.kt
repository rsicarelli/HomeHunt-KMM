package com.rsicarelli.homehunt.presentation.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rsicarelli.homehunt.ui.navigation.Screen
import com.rsicarelli.homehunt_kmm.core.model.UiEvent
import com.rsicarelli.homehunt_kmm.domain.usecase.GetSearchOptionSettings
import com.rsicarelli.homehunt_kmm.domain.usecase.SaveSearchOptionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getFilter: GetSearchOptionSettings,
    private val saveFilter: SaveSearchOptionsUseCase
) : ViewModel() {

    private val state: MutableStateFlow<FilterState> = MutableStateFlow(FilterState())

    @OptIn(FlowPreview::class)
    fun init(): Flow<FilterState> = getFilter.invoke(Unit)
        .onEach { state.value = it.searchOption.toState() }
        .flatMapConcat { state }

    fun onAvailabilitySelectionChanged(newValue: Boolean) {
        state.value = state.value.copy(availableOnly = newValue)
    }

    fun onLongTermRentalSelectionChanged(newValue: Boolean) {
        state.value = state.value.copy(longTermOnly = newValue)
    }

    fun onVisibilitySelectionChanged(newValue: Boolean) {
        state.value = state.value.copy(showSeen = newValue)
    }

    fun onBathSelectionChanged(newValue: Int) {
        state.value = state.value.copy(bathCount = newValue)
    }

    fun onSurfaceRangeChanged(newRange: ClosedFloatingPointRange<Float>) {
        state.value = state.value.copy(surfaceRange = newRange)
    }

    fun onDormsSelectionChanged(newValue: Int) {
        state.value = state.value.copy(dormCount = newValue)
    }

    fun onPriceRangeChanged(newRange: ClosedFloatingPointRange<Float>) {
        state.value = state.value.copy(priceRange = newRange)
    }

    fun onSaveFilter() {
        viewModelScope.launch {
            saveFilter(request = SaveSearchOptionsUseCase.Request(state.value.toSearchOption()))
                .collect {
                    state.value = state.value.copy(uiEvent = UiEvent.Navigate(Screen.Home.route))
                }
        }
    }

}