package com.ceibal.clients.adapter.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


//LIBRERIA QUE SE LLAMA OPENFEIGN -> ME PERMITE HACER LLAMADAS HTTP A CLIENTES EXTERNOS.
//SE ESCRIBE EN UNA INTERFAZ.
@Component("CancilleriaClient")
@FeignClient(name="${feign.client.cancilleria.url}", url="${feign.client.cancilleria.url}")
public interface CancilleriaClient{

    //PEGADA PARA OBTENER LAS TABLAS PAGINADAS POR CADA CLIENTE
    @PostMapping(value = "/tabla_directorios_exportadores_poncho/1", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
    String getCancilleriaHtml(@RequestBody String body, @RequestHeader(name = "Accept") String accept);

    //PARA OBTENER LA INFO CON EL ID DE CADA CLIENTE
    @GetMapping(value = "/ficha_directorio2_poncho/{id}")
    String getClientInformation(@PathVariable("id") String id);

}
