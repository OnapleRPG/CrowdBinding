package com.onaple.crowdbinding.service;

import com.onaple.crowdbinding.CrowdBinding;
import com.onaple.crowdbinding.data.Group;
import org.spongepowered.api.entity.living.player.Player;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class SimpleGroupService implements GroupService {

    public SimpleGroupService() {}

    @Override
    public Optional<Player> getGroupLeader(UUID groupId){
        Optional<Group> groupOptional = CrowdBinding.getGroupManager().getGroup(groupId);
        return groupOptional.map(Group::getLeader);
    }

    @Override
    public Optional<UUID> getGroupId(Player player){
      Optional<Group> groupOptional = CrowdBinding.getGroupManager().getPlayerGroup(player);
        return groupOptional.map(Group::getUuid);
    }

    @Override
    public List<Player> getMembers(UUID groupId){
        Optional<Group> groupOptional = CrowdBinding.getGroupManager().getGroup(groupId);
        return groupOptional.map(group -> new ArrayList<>(group.getPlayers())).orElseGet(ArrayList::new);
    }
}
