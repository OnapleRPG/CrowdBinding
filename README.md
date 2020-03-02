# CrowdBinding  [![Build Status](https://travis-ci.org/OnapleRPG/CrowdBinding.svg?branch=master)](https://travis-ci.org/OnapleRPG/CrowdBinding) ![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=CrowdBinding&metric=reliability_rating) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

CrowdBinding is a Sponge Minecraft plugin that allows group management between players.  

## Minecraft commands

* */group invite [player]* : Invite a player into your group, create one if needed. The player will receive an invitation within the chat.    
Permission : *crowdbinding.commands.invite*  

* */group accept [UUID]* : Accept a pending invitation. This command is issued when clicking an invitation, you're not really supposed to type it yourself.  
Permission : *crowdbinding.commands.accept*  

* */group deny [UUID]* : Deny a pending invitation. This command is issued when clicking an invitation, you're not really supposed to type it yourself.  
Permission : *crowdbinding.commands.deny*  

* */group list* : List the members of your group.  
Permission : *crowdbinding.commands.list*  

* */group leave* : Leave your current group.  
Permission : *crowdbinding.commands.leave*  

* */group promote [player]* : Promote a player of your group to group leader.  
Permission : *crowdbinding.commands.promote*  

* */gr [message]* : Send a message to your group.  
Permission : *crowdbinding.commands.chat*

## For developer

If you are willing to use **CrowdBinding** in your plugin development, we provide services to ease interactions.  

### Services

* **GroupService** : Give access to groups
    * `Optional<Player> getGroupLeader(UUID groupId)` : Try to get group leader from an UUID.  
    * `Optional<UUID> getGroupId(Player player)` : Try to get UUID of group player is in.  
    * `List<Player> getMembers(UUID groupId)` : List members of group with given UUID.  

### Installation with Gradle

* Add [Jitpack](https://jitpack.io/) into your repositories
 ```
   repositories {
     mavenCentral()
     maven {
         name = 'jitpack'
         url = 'https://jitpack.io'
     }
 }  
 ```
 * Add **CrowdBinding** to your dependencies
 ```
 dependencies {
      compile 'org.spongepowered:spongeapi:7.0.0'
      implementation 'com.github.OnapleRPG:CrowdBinding:v0.4.1'
  }
 ```
 * Use services 
 ```java
Optional<GroupService> optionalGroupService = Sponge.getServiceManager().provide(GroupService.class);
            if (optionalGroupService.isPresent()) {
                GroupService groupService = optionalGroupService.get();
                players = groupService.getMembers(groupUuid);
            }
```
 

