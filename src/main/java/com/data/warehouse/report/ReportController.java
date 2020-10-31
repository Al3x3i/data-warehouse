package com.data.warehouse.report;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReportController {


    @PostMapping("/report")
    public ResponseEntity report(){


        return ResponseEntity.ok().build();
    }
}
