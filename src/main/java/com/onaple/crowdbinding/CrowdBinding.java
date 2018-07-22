package com.onaple.crowdbinding;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Plugin(id = "crowdbinding", name = "CrowdBinding", version = "0.1")
@Singleton
/**
 * CrowdBinding main class, a plugin that allows players to create and manage groups of players.
 */
public class CrowdBinding {
    @Inject
    private Game game;

    @Inject
    private Logger logger;

    @Inject
    private GroupManager groupManager;

    @Listener
    public void onServerStart(GameInitializationEvent gameInitializationEvent) {
    }

    @Listener
    public void onClientDisconnect(ClientConnectionEvent.Disconnect clientDisconnectEvent) {

    }

}