package com.ganaljigi.kubf.feature.building.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ganaljigi.kubf.data.repository.BuildingInfoRepository
import com.ganaljigi.kubf.feature.building.model.Room
import com.ganaljigi.kubf.feature.building.model.RoomSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuildingViewModel @Inject constructor( private val repo: BuildingInfoRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<BuildingUIState> = MutableStateFlow(BuildingUIState())
    val uiState = _uiState.asStateFlow()

    private var sourceRoom: List<Room> = emptyList()

    fun init(buildingId: Long){
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = false) }
            runCatching { repo.fetchBuildingSpaces(buildingId) }
                .onSuccess { (info, total) ->
                    sourceRoom = total.floorList.flatMap { it.rooms }
                    _uiState.update {
                        it.copy(
                            currentBuildingId = buildingId,
                            currentBuildingName = info.name,
                            buildingInfo = info,
                            totalFloor = total
                        )
                    }
                }
        }
    }

    fun onQueryChange(v: TextFieldValue){
        _uiState.update { it.copy(query = v) }
        refreshResults()
    }
    fun clearQuery(){
        _uiState.update { it.copy(query = TextFieldValue(), result = persistentListOf()) }
    }

    private fun roomNumberKey(num: String?):Int{
        val digits = num?.filter { it.isDigit() }
        return digits?.toIntOrNull()?:Int.MAX_VALUE
    }

    private fun refreshResults() {
        val q = _uiState.value.query.text.trim()
        if(q.isBlank()){
            _uiState.update { it.copy(result = persistentListOf()) }
            return
        }
        val roomSearchResults:List<RoomSearchResult> =
            sourceRoom.asSequence()
                .filter { r ->
                    val name = r.name.orEmpty()
                    val num = r.number.orEmpty()
                    name.contains(q, ignoreCase = true) || num.contains(q, ignoreCase = true)
                }
                .sortedWith(compareBy<Room>{roomNumberKey(it.number)}
                    .thenBy { it.name.orEmpty() })
                .map { r ->
                    val display = when{
                        !r.name.isNullOrBlank() -> r.name!!
                        !r.number.isNullOrBlank() -> r.number!!
                        else -> "(이름 없음)"
                    }
                    RoomSearchResult(
                        id = r.id ?:0L,
                        name = display,
                        building = _uiState.value.currentBuildingName,
                        room = r
                    )
                }
                .take(50)
                .toPersistentList()
        _uiState.update { it.copy(result = roomSearchResults) }
    }
}

