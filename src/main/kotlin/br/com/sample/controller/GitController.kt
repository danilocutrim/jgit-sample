package br.com.sample.controller

import br.com.sample.service.GitService
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/git")
class GitController(private val gitService: GitService) {

    @GetMapping("/start")
    suspend fun cloneRepositry(){
        gitService.start()
    }

}