package com.example.nycschools.model

import com.example.nycschools.model.local.SchoolSatEntity

sealed class UIState{
    data class Response(val success: List<SchoolSatEntity>) : UIState()
    data class Error(val errorMessage: String) : UIState()
    data class Loading(val isLoading: Boolean = true) : UIState()
    object Empty : UIState()
}
