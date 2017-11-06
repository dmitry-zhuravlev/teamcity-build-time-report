package com.teamcity.report.ui.service

import com.teamcity.report.repository.ServerRepository
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.ViewScope
import org.springframework.beans.factory.annotation.Autowired
import java.io.Serializable

/**
 * @author Dmitry Zhuravlev
 *         Date: 28/10/2017
 */
@SpringComponent
@ViewScope
class ServerNamesLoader : Serializable {
    @Autowired
    lateinit var serverRepository: ServerRepository

    fun loadServerNames() = serverRepository.findAll()

}