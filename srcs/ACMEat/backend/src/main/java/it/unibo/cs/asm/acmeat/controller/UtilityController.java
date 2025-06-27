package it.unibo.cs.asm.acmeat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.ZoneId;

@RestController
public class UtilityController {

    @GetMapping("/time")
    public String getCurrentTime() {
        LocalTime currentTime = LocalTime.now(ZoneId.of("Europe/Rome"));
        return "Current server time: " + currentTime;
    }
}
