package com.teamcity.report.service

import com.teamcity.report.repository.ServerRepository
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.ViewScope
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author Dmitry Zhuravlev
 *         Date: 28/10/2017
 */
@SpringComponent
@ViewScope
class ServerNamesLoader {
    @Autowired
    lateinit var serverRepository: ServerRepository

    fun loadServerNames() = serverRepository.findAll()

}