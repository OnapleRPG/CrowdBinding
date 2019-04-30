package com.onaple.crowdbinding;

import com.onaple.crowdbinding.commands.AcceptCommand;
import com.onaple.crowdbinding.commands.DenyCommand;
import com.onaple.crowdbinding.commands.InviteCommand;
import com.onaple.crowdbinding.commands.ListCommand;
import com.onaple.crowdbinding.data.Group;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Plugin(id = "crowdbinding", name = "CrowdBinding", version = "0.1.3")
@Singleton
/**
 * CrowdBinding main class, a plugin that allows players to create and manage groups of players.
 */
public class CrowdBinding {
    @Inject
    private Game game;

    @Inject
    private GroupManager groupManager;

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

        CommandSpec groupSpec = CommandSpec.builder()
                .description(Text.of("Group view and management"))
                .permission("crowdbinding.commands.*")
                .child(inviteSpec, "invite")
                .child(acceptSpec, "accept")
                .child(denySpec, "deny")
                .child(listSpec, "list")
                .build();

        commandManager.register(this, groupSpec, "group");
    }

    @Listener
    public void onClientDisconnect(ClientConnectionEvent.Disconnect clientDisconnectEvent) {

    }
}