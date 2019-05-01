package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.exceptions.PlayerAlreadyInAGroupException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class InviteCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player sender = (Player) src;
        Optional<Player> recipient = args.<Player>getOne("recipient");
        if (!recipient.isPresent()) {
            sender.sendMessage(Text.of("Recipient not found."));
            return CommandResult.empty();
        }

        if (sender.equals(recipient.get())) {
            sender.sendMessage(Text.of("You cannot invite yourself into a group."));
            return CommandResult.empty();
        }

        try {
            CrowdBinding.getGroupManager().createInvitation(sender, recipient.get());
        } catch (PlayerAlreadyInAGroupException e) {
            sender.sendMessage(Text.of(e.toString()));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
