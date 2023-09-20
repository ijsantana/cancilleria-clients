package com.ceibal.clients.adapter.service;

import com.ceibal.clients.adapter.client.CancilleriaClient;
import com.ceibal.clients.core.model.Client;
import com.ceibal.clients.infra.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final CancilleriaClient cancilleriaClient;
    private final ClientRepository clientRepository;

    public List<Client> getClientInformation(Integer pageTot){
        List<Client> clients = new ArrayList<>();
        List<Integer> pages = new ArrayList<>();

        //LE CARGA TODAS LAS PAGINAS A LA LISTA DE PAGINAS.
        for(int page = 45; page<=pageTot; page++){
            pages.add(page);
        }

        //POR CADA PAGINA VA A HACER LA CREACION DE LOS CLIENTES
        //FUNCION LAMBDA (a) -> {f(a)}
        pages.forEach(page -> {

            //CAPTURAR ERRORES TRY-CATCH
            try {
                String html = cancilleriaClient.getCancilleriaHtml("pageNo=" + page,
                        "application/x-www-form-urlencoded; charset=UTF-8");

                log.info("Page " + page);


                //JAVA 8: STREAMS -> PROGRAMACION FUNCIONAL -> RECORRER LISTAS DE OBJETOS.
                //A LA LISTA DE CLIENTES HAGO UN ADD ALL.
                //EL SPLIT CONVIERTE UN STRING EN UN ARRAY DE STRINGS SEPARADOS POR !-- (USADO PARA COMENTAR EL HTML)
                //AL ARRAY LE APLICO UN STREAM PARA RECORRERLO
                //MAP -> SIRVE PARA TRANSFORMAR UN ELEMENTO A OTRO
                clients.addAll(Arrays.stream(html.split("!--"))
                        .map(e -> e.substring(e.indexOf("poncho") + 8, e.indexOf("Entidad") - 3)) //MAP-> ACORTA EL STRING AL ID
                        .map(id -> new Client(id,page)) //MAP-> CREA UN CLIENTE SOLO CON ID Y PAGINA POR CADA ID OBTENIDO
                        .map(this::completeClient) //MAP-> COMPLETA EL CLIENTE CON EL METODO COMPLETECLIENT. POR CADA CLIENT CORRE ESE METODO.
                        .filter(obj -> true)
                        .toList());

            } catch (Exception e){
                log.error("Problem detected on page " + page + ". " + e.getMessage());
            }
        });

        return clientRepository.saveAll(clients);
    }

    private Client completeClient(Client client) {
        try {
            //HACE LA PEGADA CON EL ID PARA OBTENER EL HTML DEL CLIENTE
            String html = cancilleriaClient.getClientInformation(client.getId());

            Map<String, String> map = new HashMap<>();
            List<String> mails = new ArrayList<>();

            //LIBRERIA QUE SE LLAMA JSOUP QUE PERMITE TRABAJAR CON HTML Y PARSEARLO (SEPARARLO) EN ETIQUETAS. <div> </div>
            //CONVERTIR EL STRING DE HTML A UN OBJETO DOCUMENT QUE CONTIENE EL HTML
            Document document = Jsoup.parse(html);

            //EL SELECT ME DEVUELVE UNA LISTA CON LOS STRINGS DENTRO DE CADA ETIQUETA <p>STRING</p>
            //APLICO UN STREAM PARA RECORRER LA LISTA
            //APLICO UN FILTER -> TODO LO QUE TIENE : ES DATO.
            List<Element> elements = document.select("p")
                    .stream()
                    .filter(e -> e.toString().contains(":"))
                    .toList();

            //VOY A RECORRER LA LISTA DE ELEMENTOS CON LOS ATRIBUTOS E IR AGREGANDOLOS AL MAPA (CLAVE - VALOR)
            elements.forEach(e -> this.convertElementToMap(e, map, mails));

            //BUSCO EL NOMBRE ESTA EN LA ETIQUETA <h2>NOMBRE</h2>
            String name = document.select("h2").toString();
            map.put("Razón Social",name);

            //LE SETEO LOS PARAMETROS FALTANTES AL OBJETO CLIENTE
            client.setName(name.substring(4, name.length() - 5).replace(".","-"));
            client.setMails(mails);
            client.setAttributes(map);
            return client;

        } catch(Exception e){
            log.error("Fatal Error detected while trying to get client information: " + e.getMessage());
            return null;
        }
    }

    //METODO PARA OBTENER EL MAPA (KEY-VALUE) Y LA LISTA DE MAILS
    private Map<String,String> convertElementToMap(Element element, Map<String,String> map, List<String> mails){

        String key = element.select("b").toString();

        if(key.contains("Email")) {
            mails.add(element.childNodes().get(1).toString());
        }

        //UNA VARIABLE QUE SE GUARDA EN UNA BD MONGO NO PUEDE TENER PUNTO ".". SE CONFUNDE CON PARTE DE LA CONSULTA.
        map.put(key.substring(3,key.length()-5).replace(".","-"), element.childNodes().get(1).toString());
        return map;
    }


    public Client updateClient(String idClient) {
        Client client = new Client(idClient,0);
        completeClient(client);
        return clientRepository.save(client);
    }
}
