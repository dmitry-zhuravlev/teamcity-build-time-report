package com.teamcity.report.config

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
object ConfigDefault {
    const val WORKER_REQUEST_TIMEOUT_MS = 30000L
    const val WORKER_CHUNK_SIZE = 100
    const val WORKER_START_PAGE = 0
    const val WORKER_ACTUALIZATION_DAYS = 1L

    const val DATE_PATTERN = "yyyyMMdd'T'HHmmssZ"
}

object DefaultPath {
    val BUILDS_PATH = "/app/rest/builds"
    val PROJECTS_PATH = "/app/rest/projects"

    val BUILDS_PATH_TEMPLATE = "$BUILDS_PATH/?affectedProject:(id:_Root)=&fields=count,nextHref,build(id,number,status,buildType(id,name,projectName,parentProjectId),statistics(\$locator(name:BuildDuration),property(name,value)))"
}
