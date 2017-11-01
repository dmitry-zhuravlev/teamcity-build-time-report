package com.teamcity.report.indexer.test.mock

import com.google.common.io.Files
import com.teamcity.report.indexer.test.client.TestConstants.TEST_ACCESS_COOKIE
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Dmitry Zhuravlev
 *         Date:  30.10.2017
 */
@RestController
class TeamCityServerMockController {

    @RequestMapping(value = "/app/rest/server", method = arrayOf(RequestMethod.GET))
    fun serverInfo(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        val body = "MockServer"
        if (isAuthRequest(request)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body)
        response.addHeader(HttpHeaders.SET_COOKIE, TEST_ACCESS_COOKIE)
        return ResponseEntity.ok(body)
    }

    @RequestMapping(value = "/httpAuth/app/rest/latest/builds", method = arrayOf(RequestMethod.GET), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun builds(@RequestParam("fields") name: String,
               @RequestParam("locator") locator: String,
               request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        if (isAuthRequest(request)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<String>()
        response.addHeader(HttpHeaders.SET_COOKIE, TEST_ACCESS_COOKIE)
        val buildsJSON = Files.toString(ClassPathResource("test_builds_response.json").file, StandardCharsets.UTF_8)
        val emptyBuildsJSON = Files.toString(ClassPathResource("test_empty_builds_response.json").file, StandardCharsets.UTF_8)
        val parsedLocator = parseLocator(locator)
        val start = parsedLocator["start"]
        if (start == null || start >= 13) {
            return ResponseEntity.ok(emptyBuildsJSON)
        }
        return ResponseEntity.ok(buildsJSON)
    }

    @RequestMapping(value = "/httpAuth/app/rest/latest/projects", method = arrayOf(RequestMethod.GET), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun projects(@RequestParam("fields") name: String,
                 @RequestParam("locator") locator: String,
                 request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<String> {
        if (isAuthRequest(request)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build<String>()
        response.addHeader(HttpHeaders.SET_COOKIE, TEST_ACCESS_COOKIE)
        val projectsJSON = Files.toString(ClassPathResource("test_projects_response.json").file, StandardCharsets.UTF_8)
        val emptyProjectsJSON = Files.toString(ClassPathResource("test_empty_projects_response.json").file, StandardCharsets.UTF_8)
        val parsedLocator = parseLocator(locator)
        val start = parsedLocator["start"]
        if (start == null || start >= 4) {
            return ResponseEntity.ok(emptyProjectsJSON)
        }
        return ResponseEntity.ok(projectsJSON)
    }

    private fun parseLocator(locator: String): Map<String, Int?> {
        val result = hashMapOf<String, Int?>()
        locator.split(",").forEach { locatorParam ->
            val split = locatorParam.split(":")
            result[split[0]] = split[1].toIntOrNull()
        }
        return result
    }

    private fun isAuthRequest(request: HttpServletRequest): Boolean {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        val cookieAuthHeader = request.getHeader(HttpHeaders.COOKIE)
        if (authHeader == null && cookieAuthHeader == null) {
            return true
        }
        return false
    }

}