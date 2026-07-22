package com.example.data.local

import kotlinx.coroutines.flow.Flow

class KundaliRepository(private val kundaliDao: KundaliDao) {
    val allProfiles: Flow<List<KundaliEntity>> = kundaliDao.getAllSavedProfiles()

    suspend fun saveProfile(profile: KundaliEntity): Long {
        return kundaliDao.insertProfile(profile)
    }

    suspend fun deleteProfile(profile: KundaliEntity) {
        kundaliDao.deleteProfile(profile)
    }

    suspend fun deleteProfileById(id: Long) {
        kundaliDao.deleteProfileById(id)
    }
}
