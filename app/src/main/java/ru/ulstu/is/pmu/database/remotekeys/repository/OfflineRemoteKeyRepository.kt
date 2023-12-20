package ru.ulstu.`is`.pmu.database.remotekeys.repository

import ru.ulstu.`is`.pmu.database.remotekeys.dao.RemoteKeysDao
import ru.ulstu.`is`.pmu.database.remotekeys.model.RemoteKeyType
import ru.ulstu.`is`.pmu.database.remotekeys.model.RemoteKeys

class OfflineRemoteKeyRepository(private val remoteKeysDao: RemoteKeysDao) : RemoteKeyRepository {
    override suspend fun getAllRemoteKeys(id: Int, type: RemoteKeyType) =
        remoteKeysDao.getRemoteKeys(id, type)

    override suspend fun createRemoteKeys(remoteKeys: List<RemoteKeys>) =
        remoteKeysDao.insertAll(remoteKeys)

    override suspend fun deleteRemoteKey(type: RemoteKeyType) =
        remoteKeysDao.clearRemoteKeys(type)
}