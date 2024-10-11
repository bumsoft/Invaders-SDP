package entity;

import entity.ship.*;

/**
 * Factory for creating ships.
 * It is used to create ships of different types.
 */
public class ShipFactory {
    /**
     * Creates a ship of the specified type.
     * @param type The type of ship to create.
     * @param positionX The initial position of the ship in the X axis.
     * @param positionY The initial position of the ship in the Y axis.
     * @return A new ship of the specified type.
     */
    public static Ship create(Ship.ShipType type, final int positionX, final int positionY) {
        return switch (type) {
            case StarDefender -> new StarDefender(positionX, positionY);
            case VoidReaper -> new VoidReaper(positionX, positionY);
            case GalacticGuardian -> new GalacticGuardian(positionX, positionY);
            case CosmicCruiser -> new CosmicCruiser(positionX, positionY);
        };
    }
}
