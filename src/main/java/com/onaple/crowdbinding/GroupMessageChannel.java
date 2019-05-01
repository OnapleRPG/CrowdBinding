package com.onaple.crowdbinding;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

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
        String playerName = (sender instanceof Player) ? ((Player)sender).getName() : "";
        Text text = original;
        if(this.members.contains(recipient)) {
            text = Text.of(TextColors.DARK_AQUA, "[gr]<", playerName, "> ", text);
        }
        return Optional.of(text);
    }
}
