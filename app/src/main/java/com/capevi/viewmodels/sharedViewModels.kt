package com.capevi.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel
    @Inject
    constructor(
//        private val repository: MyRepository,
    ) : ViewModel() {
        private val _dataList = MutableStateFlow<List<Any>>(mutableListOf())
        val dataList: StateFlow<List<Any>> get() = _dataList

        fun updateList(newList: MutableList<Uri>) {
            _dataList.value = newList
        }

        fun addMedia(item: Any) {
            val updatedList = _dataList.value.toMutableList()
            updatedList.add(item)
            _dataList.value = updatedList
        }

        fun removeMedia(image: Any) {
            val updatedList = _dataList.value.toMutableList()
            updatedList.remove(image)
            _dataList.value = updatedList
        }

        fun clearList() {
            val updatedList = _dataList.value.toMutableList()
            updatedList.clear()
            _dataList.value = updatedList
        }

        fun addToExisitingList(newList: List<Any>) {
            _dataList.value = (_dataList.value + newList).toMutableList()
        }
    }
