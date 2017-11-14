package com.teamcity.report.indexer.batch

import org.slf4j.LoggerFactory
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.launch.JobOperator
import org.springframework.batch.core.launch.NoSuchJobException
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PreDestroy


/**
 * @author Dmitry Zhuravlev
 *         Date: 21/10/2017
 */
@Service
class IndexerJobsCoordinatorService : JobExecutionListenerSupport() {

    @Autowired
    lateinit var jobOperator: JobOperator

    private val logger = LoggerFactory.getLogger(IndexerJobsCoordinatorService::class.java)

    private var terminatedIndexerJobsExecutions = ConcurrentHashMap<String, Long>()

    @Volatile
    private var isShuttingDown = false

    @Synchronized
    fun terminateIndexerJobs() {
        if (isShuttingDown) return
        logger.info("Stoping all indexer jobs if any...")
        isShuttingDown = true
        jobOperator.jobNames.forEach { jobName ->
            jobOperator.getRunningExecutionsSafe(jobName).forEach { jobExecutionId ->
                jobOperator.stopSafe(jobExecutionId)
            }
            //wait for jobs to stopped gracefully
            while (jobOperator.getRunningExecutionsSafe(jobName).isNotEmpty()) {
                Thread.sleep(100)
            }
        }
    }

    override fun afterJob(jobExecution: JobExecution?) {
        if (jobExecution == null) return
        val jobName = jobExecution.jobInstance.jobName
        terminatedIndexerJobsExecutions.put(jobName, jobExecution.id)
    }

    @PreDestroy
    fun shutdown() {
        terminateIndexerJobs()
    }

    @Scheduled(fixedRate = 5000)
    fun checkForActualizationIndexerRestart() {
        if (isShuttingDown) return
        terminatedIndexerJobsExecutions.forEach { (jobName, _) ->
            if (jobOperator.getRunningExecutionsSafe(jobName).isEmpty()) {
                terminatedIndexerJobsExecutions.remove(jobName)
                jobOperator.startNextInstanceSafe(jobName)
            }
        }
    }

    private fun JobOperator.getRunningExecutionsSafe(jobName: String) = try {
        getRunningExecutions(jobName)
    } catch (e: NoSuchJobException) {
        emptyList<Long>()
    }

    private fun JobOperator.startNextInstanceSafe(jobName: String) = try {
        startNextInstance(jobName)
    } catch (e: NoSuchJobException) {
        -1L
    }

    private fun JobOperator.stopSafe(executionId: Long) = try {
        stop(executionId)
    } catch (e: NoSuchJobException) {
        false
    }

}