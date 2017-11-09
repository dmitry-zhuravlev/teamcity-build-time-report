package com.teamcity.report.repository

import com.teamcity.report.repository.entity.BuildTypeEntity
import com.teamcity.report.repository.entity.BuildTypeEntityKey
import com.teamcity.report.repository.entity.BuildTypeViewEntity
import com.teamcity.report.repository.entity.BuildTypeViewEntityKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  26.10.2017
 */

interface BuildTypeViewRepository : CassandraRepository<BuildTypeViewEntity, BuildTypeViewEntityKey> {

    @Query("select * from report.teamcity_build_type_view where projectId in :projectIds and serverName=:serverName")
    fun getBuildTypesByProjectIdsAndServerNames(@Param("projectIds") projectIds: List<String>,
                                                @Param("serverName") serverName: String): List<BuildTypeViewEntity>

    @Query("select * from report.teamcity_build_type_view where projectId in :projectIds and serverName=:serverName")
    fun getBuildTypesByProjectIdsAndServerNames(@Param("projectIds") projectIds: List<String>,
                                                @Param("serverName") serverName: String,
                                                pageable: Pageable): Slice<BuildTypeViewEntity>

    @Query("select count(*) from report.teamcity_build_type_view where serverName=:serverName")
    fun count(@Param("serverName") serverName: String): Int
}

interface BuildTypeRepository : CassandraRepository<BuildTypeEntity, BuildTypeEntityKey>

@Component
class PageableBuildTypeViewRepository {
    @Autowired
    lateinit var buildTypeRepository: BuildTypeViewRepository

    fun getBuildTypesByProjectIdsAndServerNames(projectIds: List<String>, serverName: String, page: Int?, size: Int?): List<BuildTypeViewEntity> {
        if (page == null || size == null) {
            return buildTypeRepository.getBuildTypesByProjectIdsAndServerNames(projectIds, serverName)
        }
        var buildTypesSlice = buildTypeRepository.getBuildTypesByProjectIdsAndServerNames(projectIds, serverName, CassandraPageRequest.of(0, size))
        for (i in 1..page) {
            if (buildTypesSlice.hasNext()) {
                buildTypesSlice = buildTypeRepository.getBuildTypesByProjectIdsAndServerNames(projectIds, serverName, buildTypesSlice.nextPageable())
            } else return emptyList()
        }
        return buildTypesSlice.content
    }

    fun count(serverName: String) = buildTypeRepository.count(serverName)
}