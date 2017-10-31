package com.teamcity.report.indexer.batch.reader

import com.teamcity.report.indexer.client.model.ElementCollection
import com.teamcity.report.indexer.properties.TeamCityConfigProperties
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemReader

/**
 * @author Dmitry Zhuravlev
 *         Date:  30.10.2017
 */
abstract class AbstractIndexerReader<E>(

        private val requestTimeoutMs: Long,

        private val chunkSize: Long,

        private val serverName: String,

        serverId: String,

        serverUrl: String,

        initialStart: Long,

        apiVersion: String,

        userName: String,

        userPassword: String

) : ItemReader<List<E>?> {
    private val logger = LoggerFactory.getLogger(javaClass)

    val serverConfig = TeamCityConfigProperties.ServerConfig(serverId, serverName, apiVersion, serverUrl, userName, userPassword)

    private var currentStart = initialStart

    abstract fun executeRequest(currentStart: Long): ElementCollection<E>

    override fun read(): List<E>? {
        val elementCollection = executeRequest(currentStart)
        val elementList = elementCollection.elements
        logger.info("Got the following data from server '$serverName' $elementList")
        currentStart += chunkSize
        pauseAfterRead(requestTimeoutMs)
        return if (isLastChunk(elementCollection) && elementList.isEmpty()) {
            null
        } else {
            elementList
        }
    }

    private fun pauseAfterRead(requestTimeoutMs: Long) = try {
        Thread.sleep(requestTimeoutMs)
    } catch (e: InterruptedException) {
        logger.warn("Indexer reader sleep interrupted")
    }

    private fun isLastChunk(elementCollection: ElementCollection<E>) = elementCollection.nextHref == null
}