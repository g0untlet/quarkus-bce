//==============================================================================

// Copyright (c) 2026 net.gauntlet. All rights reserved.

//==============================================================================

package net.gauntlet.quarkusbce.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.sql.DataSource;
import java.sql.Connection;

@ApplicationScoped
@Readiness
public class ReadinessHealthCheck implements HealthCheck {

    private static final System.Logger LOG = System.getLogger(ReadinessHealthCheck.class.getName());

    @Inject
    DataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return HealthCheckResponse.named("ready").up().build();
            }
        } catch (Exception e) {
            LOG.log(System.Logger.Level.ERROR, "Health check failed", e);
        }
        return HealthCheckResponse.named("ready").down().build();
    }
}
