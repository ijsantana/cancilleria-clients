package com.ceibal.clients.infra.controller;

import com.ceibal.clients.adapter.service.ClientService;
import com.ceibal.clients.core.model.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/client")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping()
    public List<Client> updateClientsInformation(@RequestParam("pages") Integer page){
        return clientService.getClientInformation(page);
    }

    @PostMapping("/{idClient}")
    public Client updateInformationOfOneClient(@PathVariable("idClient") String idClient){
        return clientService.updateClient(idClient);
    }

}
