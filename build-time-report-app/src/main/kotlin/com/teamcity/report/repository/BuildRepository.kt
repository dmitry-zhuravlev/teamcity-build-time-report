package com.teamcity.report.repository

import com.teamcity.report.repository.entity.BuildEntity
import org.springframework.data.repository.Repository

/**
 * @author Dmitry Zhuravlev
 *         Date:  20.10.2017
 */
interface BuildRepository : Repository<BuildEntity, Long> {
    fun save(build: BuildEntity): BuildEntity
}