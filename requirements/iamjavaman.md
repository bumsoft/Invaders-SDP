# IamJAVAman

## Team Introduction

Through this SDP class, we aim to improve our development and collaboration skills.

- Team leader
    - [`Seochan Moon`](https://github.com/dev-moonsc) : Project Manager, GitHub Repository Management, Bomb item implementation
- Team members
    - [`Jongwon Lee`](https://github.com/javadocq) : Ghost item implementation
    - [`Seungmin Kim`](https://github.com/smeasylife) : Multi-Shot item implementation
    - [`Jimin Hwang`](https://github.com/specture258) : Line-Bomb item implementation
    - [`Dohun Lee`](https://github.com/D0hunLee) : Time-Stop item implementation
    - [`Sanghoon Eum`](https://github.com/bamcasa) : Barrier item implementation

## Team Objectives

Our goal is to develop and implement the following item features:

- Creation
- Acquisition
- Usage

## Detailed Requirements

Items in this game will be implemented as follows:

1. When an enemy is defeated, there is a certain probability that an item will drop.
2. When a dropped item is hit by a bullet, the item will be both acquired and used immediately.

### Item list (To be implemented)

- **Bomb**
    - Eliminates enemies within a 3*3 range
- **Line-Bomb**
    - Eliminates enemies in the bottom row of enemy formations.
      - (This may be changed to kill enemies in a single vertical column.)
- **Barrier**
    - Creates a shield in front of the player that blocks enemy projectiles.
    - The barrier is destroyed after blocking one projectile.
- **Ghost**
    - For a certain period, the player's ship becomes transparent, allowing it to ignore bullets.
- **Time-stop**
    - Freezes enemy ships and bullets for a certain duration.
    - The player can move and attack freely during this time.
- **Multi-shot**
    - Increases the number of bullets fired by the player's ship.
    - The number of bullets increases each time the player acquires a Multi-shot item.

## Dependency

- Sound effects
    - Sound effects will be played when items are dropped and used.