package appeng.api.networking.pathing;

/**
 * Defines how AE2's channel capacities work.
 */
public enum ChannelMode {
    /**
     * Cables carry infinite channels, effectively disabling pathfinding and channel requirements.
     */
    INFINITE(Integer.MAX_VALUE, 0);

    private final int adHocNetworkChannels;

    private final int cableCapacityFactor;

    ChannelMode(int adHocNetworkChannels, int cableCapacityFactor) {
        this.adHocNetworkChannels = adHocNetworkChannels;
        this.cableCapacityFactor = cableCapacityFactor;
    }

    /**
     * @return The maximum number of channels supported by ad-hoc networks. 0 disables any requirements.
     */
    public int getAdHocNetworkChannels() {
        return adHocNetworkChannels;
    }

    /**
     * @return Multiplier for the default capacity of cables. Must be a power of two. 0 disables cable capacity
     *         requirements altogether.
     */
    public int getCableCapacityFactor() {
        return cableCapacityFactor;
    }
}
