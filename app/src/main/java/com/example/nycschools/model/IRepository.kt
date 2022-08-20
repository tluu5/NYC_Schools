package com.example.nycschools.model

import kotlinx.coroutines.flow.Flow

interface IRepository {
    fun getData(): Flow<UIState>
}