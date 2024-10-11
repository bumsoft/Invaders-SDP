package entity;

public record ShipMultipliers(float speed, float bulletSpeed, float shootingInterval) {
    public ShipMultipliers {
        if (speed <= 0 || bulletSpeed <= 0 || shootingInterval <= 0) {
            throw new IllegalArgumentException("Ship multipliers must be positive.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShipMultipliers that = (ShipMultipliers) o;

        if (Float.compare(that.speed, speed) != 0) return false;
        if (Float.compare(that.bulletSpeed, bulletSpeed) != 0) return false;
        return Float.compare(that.shootingInterval, shootingInterval) == 0;
    }
}
