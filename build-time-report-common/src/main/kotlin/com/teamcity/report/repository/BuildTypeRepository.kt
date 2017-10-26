package com.teamcity.report.repository

import com.teamcity.report.repository.entity.BuildTypeEntity
import com.teamcity.report.repository.entity.BuildTypeEntityKey
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * @author Dmitry Zhuravlev
 *         Date:  26.10.2017
 */
interface BuildTypeRepository : Repository<BuildTypeEntity, BuildTypeEntityKey> {
    fun save(build: BuildTypeEntity): BuildTypeEntity

    @Query("select * from report.teamcity_build_type where projectId in (:projectIds) and serverName=:serverName limit :lim")
    fun getBuildTypesByProjectIdsAndServerNames(@Param("projectIds") projectIds: List<String>,
                                                @Param("serverName") serverName: String,
                                                @Param("lim") limit: Long): List<BuildTypeEntity>
}