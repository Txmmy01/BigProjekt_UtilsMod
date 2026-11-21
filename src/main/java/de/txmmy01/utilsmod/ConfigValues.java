package de.txmmy01.utilsmod;

final class ConfigValues {
    private ConfigValues() {
    }

    static int parseDistance(String value, int fallback) {
        try {
            return Math.clamp(Integer.parseInt(value), 8, 32);
        } catch (RuntimeException exception) {
            return fallback;
        }
    }

    static float parseIntensity(String value, float fallback) {
        try {
            float parsed = Float.parseFloat(value);
            return Float.isFinite(parsed) ? Math.clamp(parsed, 0.5f, 2.0f) : fallback;
        } catch (RuntimeException exception) {
            return fallback;
        }
    }

    static int parseColor(String value, int fallback) {
        if (value == null) return fallback;
        String hex = value.startsWith("#") ? value.substring(1) : value;
        if (hex.length() != 6 && hex.length() != 8) return fallback;
        try {
            long parsed = Long.parseLong(hex, 16);
            return hex.length() == 6 ? (int) (0xFF000000L | parsed) : (int) parsed;
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }
}
