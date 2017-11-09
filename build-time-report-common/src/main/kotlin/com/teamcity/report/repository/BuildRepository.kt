package com.teamcity.report.repository

import com.teamcity.report.repository.entity.BuildEntity
import com.teamcity.report.repository.entity.BuildEntityKey
import com.teamcity.report.repository.entity.BuildViewEntity
import com.teamcity.report.repository.entity.BuildViewEntityKey
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.query.Param

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */
interface BuildViewRepository : CassandraRepository<BuildViewEntity, BuildViewEntityKey> {

    @Query("""SELECT sum(buildDuration) FROM report.teamcity_build_view WHERE
               buildTypeId=:buildTypeId
               and projectId=:projectId
               and serverName=:serverName
               and finishDate>=:afterFinishDate and finishDate<=:beforeFinishDate""")
    fun sumBuildDurations(@Param("buildTypeId") buildTypeId: String,
                          @Param("projectId") projectId: String,
                          @Param("serverName") serverName: String,
                          @Param("beforeFinishDate") beforeFinishDate: Long,
                          @Param("afterFinishDate") afterFinishDate: Long): Long
}

interface BuildRepository : CassandraRepository<BuildEntity, BuildEntityKey>