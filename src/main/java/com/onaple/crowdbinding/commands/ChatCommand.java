package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.data.Group;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class ChatCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player source = (Player) src;
        Optional<Group> groupOptional = CrowdBinding.getGroupManager().getPlayerGroup(source);
        if (!groupOptional.isPresent()) {
            src.sendMessage(Text.of("You don't have a group to talk to."));
            return CommandResult.empty();
        }
        Optional<Text> message = args.getOne("message");
        if (!message.isPresent()) {
            src.sendMessage(Text.of("Cannot send empty message."));
            return CommandResult.empty();
        }

        groupOptional.get().getMessageChannel().send(Text.of(message.get()));

        return CommandResult.success();
    }
}
