package com.teamcity.report.rest

import com.teamcity.report.batch.IndexerJobsCoordinatorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import kotlin.concurrent.thread

/**
 * @author Dmitry Zhuravlev
 *         Date: 22/10/2017
 */
@RestController
class IndexerControllerEndpoint {
//    @Autowired
//    lateinit var jobOperator: JobOperator

//    @Autowired
//    lateinit var jobRepository: JobRepository

    @Autowired
    lateinit var appContext: ApplicationContext

    @Autowired
    lateinit var indexerJobsCoordinatorService: IndexerJobsCoordinatorService

    @RequestMapping(value = "shutdown", method = arrayOf(RequestMethod.GET))
    fun shutdown() {
        indexerJobsCoordinatorService.terminateIndexerJobs()
        thread(start = true) {
            SpringApplication.exit(appContext, ExitCodeGenerator { 0 })
        }
    }

    /*@RequestMapping(value = "startJob", method = arrayOf(RequestMethod.GET))
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
    }*/
}