package com.idgen.controller;

import com.idgen.generator.IDGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class IDGeneratorRestController {

    private final IDGenerator idGenerator;

    public IDGeneratorRestController(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @GetMapping("/getId")
    public ResponseEntity<Long> idGenerateEndPoint(){
        return ResponseEntity.ok(idGenerator.generateId());
    }

}
