package com.fuchsia.foodieree.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fuchsia.foodieree.data.repo.RoomRepo
import com.fuchsia.foodieree.data.room.FoodEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepo: RoomRepo

) : ViewModel() {

    val _state = MutableStateFlow<AppState>(AppState.Loading)
    val state = _state.asStateFlow()


    fun getAllFoods() {
        viewModelScope.launch {
            _state.value = AppState.Data(roomRepo.getAllFood())
        }
    }

    fun insertFood(foodEntity: FoodEntity) {
        viewModelScope.launch {
            roomRepo.insertFood(foodEntity)
        }
    }

    fun deleteFood(foodEntity: FoodEntity) {
        Log.d("TAG", "deleteFood: $foodEntity")
        viewModelScope.launch {
            roomRepo.deleteFood(foodEntity)
        }
    }
}

sealed class AppState {
    data class Data(val data: List<FoodEntity>) : AppState()
    object Loading : AppState()

}