package ru.ulstu.`is`.pmu.database.remotekeys.repository

import ru.ulstu.`is`.pmu.database.remotekeys.model.RemoteKeyType
import ru.ulstu.`is`.pmu.database.remotekeys.model.RemoteKeys

interface RemoteKeyRepository {
    suspend fun getAllRemoteKeys(id: Int, type: RemoteKeyType): RemoteKeys?
    suspend fun createRemoteKeys(remoteKeys: List<RemoteKeys>)
    suspend fun deleteRemoteKey(type: RemoteKeyType)
}