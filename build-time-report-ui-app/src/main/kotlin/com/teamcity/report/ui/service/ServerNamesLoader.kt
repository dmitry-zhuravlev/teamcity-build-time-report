package com.teamcity.report.ui.service

import com.teamcity.report.repository.ServerRepository
import com.vaadin.spring.annotation.SpringComponent
import org.springframework.beans.factory.annotation.Autowired
import java.io.Serializable

/**
 * @author Dmitry Zhuravlev
 *         Date: 28/10/2017
 */
@SpringComponent
class ServerNamesLoader : Serializable {
    @Autowired
    lateinit var serverRepository: ServerRepository

    fun loadServerNames() = serverRepository.findAll()

}