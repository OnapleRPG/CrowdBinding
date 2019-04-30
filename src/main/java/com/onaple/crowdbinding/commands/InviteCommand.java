package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.GroupManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;

public class InviteCommand implements CommandExecutor {
    @Inject
    private GroupManager groupManager;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player sender = (Player) src;
        Player recipient = args.<Player>getOne("recipient").get();
        if (sender.equals(recipient)) {
            sender.sendMessage(Text.of("Are you sure you want to invite yourself into your group?"));
            return CommandResult.empty();
        }

        this.groupManager.processInvitationSent(sender, recipient);

        return CommandResult.success();
    }
}
