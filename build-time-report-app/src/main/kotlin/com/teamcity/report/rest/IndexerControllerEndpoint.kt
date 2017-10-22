package com.teamcity.report.rest

import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.launch.JobOperator
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author Dmitry Zhuravlev
 *         Date: 22/10/2017
 */
@RestController
class IndexerControllerEndpoint {
    @Autowired
    lateinit var jobOperator: JobOperator

    @Autowired
    lateinit var jobRepository: JobRepository

    @RequestMapping(value = "startJob", method = arrayOf(RequestMethod.GET))
    fun startJob(@RequestParam("name") name: String) {
        val lastJobExecution = jobRepository.getLastJobExecution(name, JobParameters())
        if (lastJobExecution == null)
            jobOperator.start(name, "")
        else
            jobOperator.restart(lastJobExecution.id)
    }

    @RequestMapping(value = "stopJob", method = arrayOf(RequestMethod.GET))
    fun stopJob(@RequestParam("name") name: String) {
        val lastJobExecution = jobRepository.getLastJobExecution(name, JobParameters())
        if (lastJobExecution != null && lastJobExecution.isRunning) {
            jobOperator.stop(lastJobExecution.id)
        }
    }
}