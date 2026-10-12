
package com.wifilab.controller;

import com.wifilab.model.SimulationResult;
import com.wifilab.service.SimulationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LabController {

    private final SimulationService simulationService;

    public LabController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }

    @PostMapping("/simulate")
    public String simulate(
            @RequestParam String protocol,
            @RequestParam String scenario,
            Model model
    ) {
        SimulationResult result;

        if ("SAE".equals(protocol)) {
            result = simulationService.simulateSae(scenario);
        } else if ("EAP".equals(protocol)) {
            result = simulationService.simulateEap(scenario);
        } else {
            throw new IllegalArgumentException(
                "Giao thức không hợp lệ"
            );
        }

        model.addAttribute("result", result);

        return "result";
    }
}
