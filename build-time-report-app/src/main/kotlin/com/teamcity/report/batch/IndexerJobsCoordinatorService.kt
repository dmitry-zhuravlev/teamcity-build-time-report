package com.teamcity.report.batch

import com.teamcity.report.client.TeamCityApiClient
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.launch.JobOperator
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


/**
 * @author Dmitry Zhuravlev
 *         Date: 21/10/2017
 */
@Service
@EnableBatchProcessing
class IndexerJobsCoordinatorService : JobExecutionListenerSupport() {
    @Autowired
    lateinit var client: TeamCityApiClient

    @Autowired
    lateinit var jobOperator: JobOperator

    var terminatedIndexerJobsExecutions = mutableMapOf<String, Long>()

    fun forceTerminateIndexerJob(jobName: String) = {
        jobOperator.getRunningExecutions(jobName).forEach { jobExecutionId ->
            jobOperator.stop(jobExecutionId)
        }
    }

    override fun afterJob(jobExecution: JobExecution?) {
        if (jobExecution == null) return
        val jobName = jobExecution.jobInstance.jobName
        terminatedIndexerJobsExecutions.put(jobName, jobExecution.id)
    }


    @Scheduled(fixedRate = 5000) //TODO make configurable
    fun checkForActualizationIndexerRestart() {
        terminatedIndexerJobsExecutions.forEach { (jobName, executionId) ->
            if (jobOperator.getRunningExecutions(jobName).isEmpty()) {
//                val lastJobExecution = jobRepository.getLastJobExecution(jobName, JobParameters())
//                if (lastJobExecution == null)
//                    jobOperator.start(jobName, "")
//                else
//                jobOperator.restart(executionId)
                terminatedIndexerJobsExecutions.remove(jobName)
                jobOperator.startNextInstance(jobName)
            }
        }
    }

}