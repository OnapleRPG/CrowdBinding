package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.GroupManager;
import com.onaple.crowdbinding.data.Group;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListCommand implements CommandExecutor {
    @Inject
    private Game game;
    @Inject
    private GroupManager groupManager;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player source = (Player) src;
        Optional<Group> groupOptional = this.groupManager.getGroup(source);

        if (!groupOptional.isPresent()) {
            source.sendMessage(Text.of("You have no group."));
            return CommandResult.success();
        }

        Group group = groupOptional.get();
        Text.Builder builder = Text.builder("Your group's members are: ");

        builder.append(
                Text.of(
                        group.getPlayers().stream()
                                .map(this.game.getServer()::getPlayer)
                                .map(Optional::get)
                                .map(Player::getDisplayNameData)
                                .map(DisplayNameData::displayName)
                                .map(Value::get)
                                .map(Text::toPlain)
                                .collect(Collectors.joining(", "))
                )
        );

        source.sendMessage(builder.build());

        return CommandResult.success();
    }
}
