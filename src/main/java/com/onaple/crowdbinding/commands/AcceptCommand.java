package com.onaple.crowdbinding.commands;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.exceptions.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.UUID;

public class AcceptCommand implements CommandExecutor {
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
            CrowdBinding.getGroupManager().acceptInvitation(source, inviteUuid);
            return CommandResult.success();
        } catch (UnknownGroupException | UnknownInvitationException | SenderLeftGroupException | ExpiredInvitationException | SenderJoinedAnotherGroupException e) {
            src.sendMessage(Text.of(e.getMessage()));
            return CommandResult.empty();
        }
    }
}
