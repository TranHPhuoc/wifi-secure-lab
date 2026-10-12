package com.wifilab.model;

import java.util.List;

public record SimulationResult(
        String protocol,
        String mode,
        boolean success,
        String summary,
        List<ProtocolEvent> events
) {}