package com.teamcity.report

import com.teamcity.report.config.TeamCityServerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * @author Dmitry Zhuravlev
 *         Date:  19.10.2017
 */
@Component
class TestComponent {

    @Autowired
    lateinit var myServerConfigs: TeamCityServerConfig

//    @Autowired
//    lateinit var myTeamCityApiClient: TeamCityApiClientImpl

    /* @PostConstruct
     fun init(){
         for (server in myServerConfigs.servers) {
             println("url=${server.url}, username=${server.username}, password=${server.password}")
         }
         val builds = myTeamCityApiClient.getBuilds()
         val builds2 = myTeamCityApiClient.getBuilds()
         println(builds)
     }*/
}