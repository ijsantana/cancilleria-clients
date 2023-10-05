package com.ceibal.clients.core.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class Client {

    public Client(){};

    public Client(String id,Integer page){
        this.id = id;
        this.page = page;
    }

    @Id
    private String id;
    private Integer page;
    private String name;
    private List<String> mails;
    private Map<String,String> attributes;

}
