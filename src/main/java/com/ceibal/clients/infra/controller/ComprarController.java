package com.ceibal.clients.infra.controller;

import com.ceibal.clients.adapter.service.ComprarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/comprar")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class ComprarController {

    private final ComprarService comprarService;

    @PostMapping("")
    public String updateComprarClientList(){
        return comprarService.updateComprarClients();
    }

}
