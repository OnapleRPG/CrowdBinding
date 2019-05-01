# CrowdBinding  [![Build Status](https://travis-ci.org/OnapleRPG/CrowdBinding.svg?branch=master)](https://travis-ci.org/OnapleRPG/CrowdBinding) ![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=CrowdBinding&metric=alert_status)  [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
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

* */gr [message]* : Send a message to your group.  
Permission : *crowdbinding.commands.chat*
