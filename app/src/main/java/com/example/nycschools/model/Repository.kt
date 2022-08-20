package com.example.nycschools.model

import com.example.nycschools.model.local.NYCDao
import com.example.nycschools.model.local.SatEntity
import com.example.nycschools.model.local.SchoolEntity
import com.example.nycschools.model.local.SchoolSatEntity
import com.example.nycschools.model.remote.NYCSatScore
import com.example.nycschools.model.remote.NYCSchool
import com.example.nycschools.model.remote.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class Repository(
    private val nycDao: NYCDao,
    private val network: Network,
    private val scope: CoroutineScope
): IRepository {

    // fetch school list
    // fetch school sat
    // merge both list
    // insert school entity
    // insert school sat
    // insert the merge
    private val merge = mutableListOf<SchoolSatEntity>()

    /**
     * Coroutine builders
     * CoroutineScope.launch.- Will return a JOB, the task that
     * will be executed. Similar to "runnable".
     * Example: Network call (independent), DB Transaction,
     * upload data to the server.
     * CoroutineScope.async.- Return Deffered<T>, to unwrapped
     * the value, use await().
     * Example: Network call (sequential/parallel)
     */

    override fun getData(): Flow<UIState> {
        return flow {
            emit(UIState.Loading())
            delay(500)

            val remoteSchoolList = network.service
                .getRemoteSchoolList()
            val remoteSat = network.service
                .getRemoteSat()

            if (remoteSchoolList.isSuccessful && remoteSat.isSuccessful){
                remoteSchoolList.body()?.let { schoolList ->
                    remoteSat.body()?.let { satList ->

                        insertToLocal(schoolList, satList)

                        schoolList.forEach { school ->
                            satList.forEach { sat ->
                                if (school.dbn == sat.dbn)
                                    SchoolSatEntity(
                                        school.dbn,
                                        sat.school_name,
                                        sat.satTakers,
                                        sat.reading,
                                        sat.math,
                                        sat.writing,
                                        school.location ?: "N/A",
                                        school.phone_number ?: "N/A",
                                        school.fax_number ?: "N/A",
                                        school.city ?: "N/A",
                                        school.latitude ?: "N/A",
                                        school.longitude ?: "N/A"
                                    ).run {
                                        nycDao.insertSchoolSat(
                                            this
                                        )
                                    }
                            }
                        }
                        emit(
                            UIState.Response(
                                nycDao.getPresentationData()
                            )
                        )
                    } ?: emit(UIState.Empty)
                } ?: emit(UIState.Empty)

            } else {
                emit(UIState.Error( remoteSat.message() ))
            }
        }
    }

    private fun insertToLocal(
        schoolList: List<NYCSchool>,
        satScore: List<NYCSatScore>
    ) {
        scope.launch {
            satScore.forEach {
                nycDao.fetchSatList(it.toSatEntity())
            }
            schoolList.forEach {
                nycDao.fetchSchoolList(it.toSchoolEntity())
            }
        }
    }
}

private fun NYCSatScore.toSatEntity(): SatEntity {
    return SatEntity(
        this.dbn,
        this.school_name,
        this.satTakers,
        this.reading,
        this.math,
        this.writing
    )
}

private fun NYCSchool.toSchoolEntity(): SchoolEntity {
    return SchoolEntity(
        1,
        this.dbn,
        this.school_name ?: "N/A",
        this.location ?: "N/A",
        this.phone_number ?: "N/A",
        this.fax_number ?: "N/A",
        this.city ?: "N/A",
        this.latitude ?: "N/A",
        this.longitude ?: "N/A"
    )
}