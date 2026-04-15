package net.danygames2014.logisticspipes.util;

public enum ParticleColor {
    WHITE(0xFFFFFF),
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF),
    GOLD(0xEECC5C),
    VIOLET(0x820ABA),
    ORANGE(0xF77530);

    private final float r;
    private final float g;
    private final float b;

    ParticleColor(int hex) {
        this.r = ((hex >> 16) & 0xFF) / 255.0f;
        this.g = ((hex >> 8) & 0xFF) / 255.0f;
        this.b = (hex & 0xFF) / 255.0f;
    }

    public float getR() {
        return r;
    }
    public float getG() {
        return g;
    }
    public float getB() {
        return b;
    }

    /**
     * General color arangement:
     * SinkReply: blue
     * Extract: orange
     * Provide/request: violet
     * Use power: gold
     * Render update: green
     * Power status change: red
     * Special cases: white
     */
}
