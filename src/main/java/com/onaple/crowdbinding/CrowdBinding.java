package com.onaple.crowdbinding;

import com.onaple.crowdbinding.commands.*;
import com.onaple.crowdbinding.events.PlayerLeaveGroupEvent;
import com.onaple.crowdbinding.listeners.PlayerLeaveGroupEventListener;
import com.onaple.crowdbinding.service.GroupService;
import com.onaple.crowdbinding.service.SimpleGroupService;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;

/**
 * CrowdBinding main class, a plugin that allows players to create and manage groups of players.
 */
@Plugin(id = "crowdbinding", name = "CrowdBinding", version = "0.4.0",
        description = "Plugin to manage groups",
        authors = {"Zessirb", "Selki"})
public class CrowdBinding {
    @Inject
    private void setLogger(Logger logger) {
        CrowdBinding.logger = logger;
    }
    private static Logger logger;
    public static Logger getLogger() {
        return logger;
    }

    @Inject
    private void setGroupManager(GroupManager groupManager) {
        CrowdBinding.groupManager = groupManager;
    }
    private static GroupManager groupManager;
    public static GroupManager getGroupManager() {
        return groupManager;
    }

    @Inject
    private void setGame(Game game) {
        CrowdBinding.game = game;
    }
    private static Game game;
    public static Game getGame() {
        return game;
    }

    @Inject
    private void setCommandManager(CommandManager commandManager) {
        CrowdBinding.commandManager = commandManager;
    }
    private static CommandManager commandManager;
    public static CommandManager getCommandManager() {
        return commandManager;
    }

    @Inject
    private void setEventManager(EventManager eventManager) {
        CrowdBinding.eventManager = eventManager;
    }
    private static EventManager eventManager;
    public static EventManager getEventManager() {
        return eventManager;
    }

    public static PluginContainer getInstance() {
        return Sponge.getPluginManager().getPlugin("crowdbinding").orElse(null);
    }

    @Listener
    public void onServerStart(GameInitializationEvent gameInitializationEvent) {

        CommandSpec chatSpec = CommandSpec.builder()
                .description(Text.of("Send a message to your group"))
                .permission("crowdbinding.commands.chat")
                .arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
                .executor(new ChatCommand())
                .build();

        CommandSpec inviteSpec = CommandSpec.builder()
                .description(Text.of("Invites a player to your group"))
                .permission("crowdbinding.commands.invite")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
                .executor(new InviteCommand())
                .build();

        CommandSpec acceptSpec = CommandSpec.builder()
                .description(Text.of("Accepts an invitation"))
                .permission("crowdbinding.commands.accept")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("invitation"))))
                .executor(new AcceptCommand())
                .build();

        CommandSpec denySpec = CommandSpec.builder()
                .description(Text.of("Denies an invitation"))
                .permission("crowdbinding.commands.deny")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("invitation"))))
                .executor(new DenyCommand())
                .build();

        CommandSpec listSpec = CommandSpec.builder()
                .description(Text.of("Prints out the members of your group"))
                .permission("crowdbinding.commands.list")
                .executor(new ListCommand())
                .build();

        CommandSpec leaveSpec = CommandSpec.builder()
                .description(Text.of("Leave your current group"))
                .permission("crowdbinding.commands.leave")
                .executor(new LeaveCommand())
                .build();

        CommandSpec promoteSpec = CommandSpec.builder()
                .description(Text.of("Promote a group member as leader"))
                .permission("crowdbinding.commands.promote")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
                .executor(new PromoteCommand())
                .build();

        CommandSpec kickSpec = CommandSpec.builder()
                .description(Text.of("Kick a player from the group"))
                .permission("crowdbinding.commands.kick")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))))
                .executor(new KickCommand())
                .build();

        CommandSpec groupSpec = CommandSpec.builder()
                .description(Text.of("Group view and management"))
                .permission("crowdbinding.commands.*")
                .child(inviteSpec, "invite")
                .child(acceptSpec, "accept")
                .child(denySpec, "deny")
                .child(listSpec, "list")
                .child(leaveSpec, "leave")
                .child(promoteSpec, "promote")
                .child(kickSpec, "kick")
                .build();

        commandManager.register(this, chatSpec, "gr");
        commandManager.register(this, groupSpec, "group");

        eventManager.registerListeners(this, new PlayerLeaveGroupEventListener());

        logger.info("CROWDBINDING initialized.");
    }

    @Listener
    public void gameConstruct(GameConstructionEvent event) {
        Sponge.getServiceManager().setProvider(getInstance(), GroupService.class, new SimpleGroupService());
    }

    @Listener
    public void onClientDisconnect(ClientConnectionEvent.Disconnect clientDisconnectEvent) {
        groupManager.getPlayerGroup(clientDisconnectEvent.getTargetEntity()).ifPresent(g -> {
            EventContext context = EventContext.builder().build();
            Cause cause = Cause.builder().append(clientDisconnectEvent).build(context);
            CrowdBinding.getEventManager().post(new PlayerLeaveGroupEvent(cause, clientDisconnectEvent.getTargetEntity(), g));
        });
    }

}