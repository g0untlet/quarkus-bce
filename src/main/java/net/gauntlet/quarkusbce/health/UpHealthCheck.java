//==============================================================================

// Copyright (c) 2026 net.gauntlet. All rights reserved.

//==============================================================================

package net.gauntlet.quarkusbce.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@ApplicationScoped
@Liveness
public class UpHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("up").up().build();
    }
}
