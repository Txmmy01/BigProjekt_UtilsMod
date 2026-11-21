package de.txmmy01.utilsmod;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

final class ConfigStorage {
    private ConfigStorage() {
    }

    static void load(Path file, Properties values, String description) {
        if (!Files.exists(file)) return;
        try (Reader reader = Files.newBufferedReader(file)) {
            values.load(reader);
        } catch (IOException exception) {
            UtilsModClient.LOGGER.error("Could not load " + description + " configuration", exception);
        }
    }

    static void save(Path file, Properties values, String description) {
        Path temporaryFile = null;
        try {
            Files.createDirectories(file.getParent());
            temporaryFile = Files.createTempFile(file.getParent(), file.getFileName().toString(), ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporaryFile)) {
                values.store(writer, null);
            }
            try {
                Files.move(temporaryFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            UtilsModClient.LOGGER.error("Could not save " + description + " configuration", exception);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException exception) {
                    UtilsModClient.LOGGER.warn("Could not remove temporary configuration file", exception);
                }
            }
        }
    }
}