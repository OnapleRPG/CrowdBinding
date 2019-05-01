package com.onaple.crowdbinding;

import com.onaple.crowdbinding.data.Group;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

public class GroupMessageChannel implements MutableMessageChannel {
    private Set<MessageReceiver> members;

    public GroupMessageChannel() {
        this(Collections.emptySet());
    }

    public GroupMessageChannel(Collection<MessageReceiver> members) {
        this.members = Collections.newSetFromMap(new WeakHashMap<>());
        this.members.addAll(members);
    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        return Collections.unmodifiableSet(this.members);
    }

    @Override
    public boolean addMember(MessageReceiver member) {
        return this.members.add(member);
    }

    @Override
    public boolean removeMember(MessageReceiver member) {
        return this.members.remove(member);
    }

    @Override
    public void clearMembers() {
        this.members.clear();
    }

    @Override
    public Optional<Text> transformMessage(Object sender, MessageReceiver recipient, Text original, ChatType type) {
        if (!(sender instanceof Player)) {
            return Optional.empty();
        }
        Player player = (Player)sender;
        Optional<Group> groupOptional = CrowdBinding.getGroupManager().getPlayerGroup(player);
        String playerName = ((Player)sender).getName();
        boolean isLeader = groupOptional.isPresent() && groupOptional.get().getLeader().equals(player);
        Text text = original;
        if(this.members.contains(recipient)) {
            text = Text.of(TextColors.DARK_AQUA).toBuilder()
                    .append(Text.of("[gr]").toBuilder().onClick(TextActions.suggestCommand("/gr ")).build())
                    .append(Text.of("<", isLeader ? TextStyles.BOLD : "", playerName, isLeader ? TextStyles.NONE : "" "> ", text))
                    .build();
        }
        return Optional.of(text);
    }
}
