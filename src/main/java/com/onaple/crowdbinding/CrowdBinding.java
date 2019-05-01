package com.onaple.crowdbinding;

import com.onaple.crowdbinding.commands.*;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;

/**
 * CrowdBinding main class, a plugin that allows players to create and manage groups of players.
 */
@Plugin(id = "crowdbinding", name = "CrowdBinding", version = "0.2.0")
public class CrowdBinding {
    @Inject
    private Logger logger;

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
    private CommandManager commandManager;

    @Listener
    public void onServerStart(GameInitializationEvent gameInitializationEvent) {

        CommandSpec inviteSpec = CommandSpec.builder()
                .description(Text.of("Invites a player to your group"))
                .permission("crowdbinding.commands.invite")
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("recipient"))))
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

        CommandSpec groupSpec = CommandSpec.builder()
                .description(Text.of("Group view and management"))
                .permission("crowdbinding.commands.*")
                .child(inviteSpec, "invite")
                .child(acceptSpec, "accept")
                .child(denySpec, "deny")
                .child(listSpec, "list")
                .child(leaveSpec, "leave")
                .build();

        commandManager.register(this, groupSpec, "group");

        logger.info("CROWDBINDING initialized.");
    }

    @Listener
    public void onClientDisconnect(ClientConnectionEvent.Disconnect clientDisconnectEvent) {
        groupManager.leaveGroup(clientDisconnectEvent.getTargetEntity());
    }
}