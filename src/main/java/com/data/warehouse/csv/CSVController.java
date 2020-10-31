package com.data.warehouse.csv;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("csv")
public class CSVController {

    @Autowired
    private CSVService csvService;

    @PostMapping(value = "/upload", consumes = "text/csv")
    public ResponseEntity uploadData(HttpServletRequest request) {

        if (request.getContentLength() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            csvService.loadData(request.getInputStream());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
