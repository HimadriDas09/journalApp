package net.engineeringdigest.journalApp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckup {

    @GetMapping("/health-checkup")
    public String healthCheckup(){
        return "Ok";
    }
}
