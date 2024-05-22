package it.einjojo.jobs.handler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractJobHandler {

    protected final Set<UUID> trackingPlayers;

    public AbstractJobHandler() {
        this.trackingPlayers = new HashSet<>();
    }


}
