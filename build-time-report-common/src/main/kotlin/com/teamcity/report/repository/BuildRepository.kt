package com.teamcity.report.repository

import com.teamcity.report.repository.entity.BuildEntity
import com.teamcity.report.repository.entity.BuildEntityKey
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */
interface BuildRepository : Repository<BuildEntity, BuildEntityKey> {

    fun save(build: BuildEntity): BuildEntity

    @Query("""SELECT sum(buildDuration) FROM report.teamcity_build WHERE
               buildTypeId=:buildTypeId
               and projectId=:projectId
               and serverName=:serverName
               and finishDate>=:afterFinishDate and finishDate<=:beforeFinishDate""")
    fun sumBuildDurations(@Param("buildTypeId") buildTypeId: String,
                          @Param("projectId") projectId: String,
                          @Param("serverName") serverName: String,
                          @Param("beforeFinishDate") beforeFinishDate: String,
                          @Param("afterFinishDate") afterFinishDate: String): Long
}