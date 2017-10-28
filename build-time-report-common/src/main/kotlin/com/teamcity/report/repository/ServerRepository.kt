package com.teamcity.report.repository

import com.teamcity.report.repository.entity.ServerEntity
import org.springframework.data.cassandra.repository.CassandraRepository

/**
 * @author Dmitry Zhuravlev
 *         Date: 28/10/2017
 */
interface ServerRepository : CassandraRepository<ServerEntity, String>
