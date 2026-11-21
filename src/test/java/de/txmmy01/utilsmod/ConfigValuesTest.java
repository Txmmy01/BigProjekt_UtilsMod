package de.txmmy01.utilsmod;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ConfigValuesTest {
    @Test
    void clampsRenderDistanceAndUsesFallbackForInvalidValues() {
        assertEquals(8, ConfigValues.parseDistance("2", 16));
        assertEquals(32, ConfigValues.parseDistance("80", 16));
        assertEquals(16, ConfigValues.parseDistance("invalid", 16));
    }

    @Test
    void clampsIntensityAndRejectsNonFiniteValues() {
        assertEquals(0.5f, ConfigValues.parseIntensity("0.1", 1.0f));
        assertEquals(2.0f, ConfigValues.parseIntensity("9.0", 1.0f));
        assertEquals(1.0f, ConfigValues.parseIntensity("NaN", 1.0f));
        assertEquals(1.0f, ConfigValues.parseIntensity("bad", 1.0f));
    }

    @Test
    void parsesRgbAndArgbColorsAndUsesFallbackForMalformedValues() {
        assertEquals(0xFF12AB34, ConfigValues.parseColor("#12AB34", 0));
        assertEquals(0x8012AB34, ConfigValues.parseColor("8012AB34", 0));
        assertEquals(0xFF000000, ConfigValues.parseColor("broken", 0xFF000000));
    }

    @Test
    void base64DimensionKeysDoNotCollideOnSeparators() {
        assertNotEquals(ConfigValues.dimensionKey("example:a.b"), BlockGlowConfig.dimensionKey("example.a:b"));
    }
}
