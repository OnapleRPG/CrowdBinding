package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.exceptions.ExpiredInvitationException;
import com.onaple.crowdbinding.exceptions.UnknownInvitationException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class DenyCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Text.of("You must be in game to run this command."));
            return CommandResult.empty();
        }

        Player source = (Player) src;
        String invitation = args.<String>getOne("invitation").orElse("");
        if (invitation.isEmpty()) {
            src.sendMessage(Text.of("Unrecognized invitation code."));
            return CommandResult.empty();
        }

        UUID inviteUuid;
        try {
            inviteUuid = UUID.fromString(invitation);
        } catch (IllegalArgumentException e) {
            src.sendMessage(Text.of("Unrecognized invitation code."));
            return CommandResult.empty();
        }

        try {
            CrowdBinding.getGroupManager().denyInvitation(source, inviteUuid);
            return CommandResult.success();
        } catch (UnknownInvitationException | ExpiredInvitationException e) {
            src.sendMessage(Text.of(e.getMessage()));
            return CommandResult.empty();
        }
    }
}
