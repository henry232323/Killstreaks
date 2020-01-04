# Killstreaks
A killstreaks plugin for PaperMC, built for ResortCraft. It's easily configurable

# Sample Config - config.yml
```yaml
worlds:
  - world
title_messages:
  5:
    - "%1$s is doin mega killin (main title)"
    - "That means 5 whole kills! (subtitle)"
  10:
    - "$1$s is killbominable"
    - "(10 kills!)"
chat_messages:
  3: "%1$s is on 3 killstreak! wowie!"
commands:
  5:
    - "tell %1$s good job!"
```
This example configuration demonstrates all the possible options.
- At 5 consecutive kills, every player in the same world will see (using the /title command) `____ is doin mega killin (main title)` 
- There will also be a subtitle that says `That means 5 whole kills! (subtitle)`. If you don't know what I mean by titles, try playing with the /title command
- A command will also be executed at 5 kills, which will be a private message from console telling the player `good job!`
- At 3 consecutive kills, players will see a message in chat (as opposed to a title) announcing `_____ is on a 3 killstreak! wowie!`
- Anywhere you put `%1$s` will be replaced with the username of the player, `%2$s` will be the username of the player most recently killed, and `%3$s` will be the display name of the player, and `%4$s` is the display name of the player killed.
