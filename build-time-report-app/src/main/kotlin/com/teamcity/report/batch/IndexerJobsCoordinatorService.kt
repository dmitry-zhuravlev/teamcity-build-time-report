package com.teamcity.report.batch

import com.teamcity.report.client.TeamCityApiClient
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.launch.JobOperator
import org.springframework.batch.core.listener.JobExecutionListenerSupport
import org.springframework.batch.core.repository.JobRepository
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

    @Autowired
    lateinit var jobRepository: JobRepository

    var terminatedIndexerJobsNames = mutableSetOf<String>()

    fun requestForActualizationIndexerJobTermination(jobName: String) = terminatedIndexerJobsNames.add(jobName)

    fun forceTerminateActualizationIndexerJob(jobName: String) = {
        jobOperator.getRunningExecutions(jobName).forEach { jobExecutionId ->
            jobOperator.stop(jobExecutionId)
        }
    }

    override fun afterJob(jobExecution: JobExecution?) {
        if (jobExecution == null) return
        val jobName = jobExecution.jobInstance.jobName
        terminatedIndexerJobsNames.add(jobName)
    }


    @Scheduled(fixedRate = 5000) //TODO make configurable
    fun checkForActualizationIndexerRestart() {
        terminatedIndexerJobsNames.forEach {jobName->
            if(jobOperator.getRunningExecutions(jobName).isEmpty()){
                val lastJobExecution = jobRepository.getLastJobExecution(jobName, JobParameters())
                if (lastJobExecution == null)
                    jobOperator.start(jobName, "")
                else
                    jobOperator.restart(lastJobExecution.id)
            }
        }
    }

}