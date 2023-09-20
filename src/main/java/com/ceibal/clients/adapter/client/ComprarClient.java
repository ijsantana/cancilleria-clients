package com.ceibal.clients.adapter.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Component("ComprarClient")
@FeignClient(name="${feign.client.comprar.url}", url="${feign.client.comprar.url}")
public interface ComprarClient {

    @PostMapping(value = "/PLIEGO/BuscarProveedorCiudadano.aspx", consumes = "application/x-www-form-urlencoded; charset=UTF-8")
    String getComprarHtml(@RequestBody String body, @RequestHeader(name = "Accept") String accept);

}
