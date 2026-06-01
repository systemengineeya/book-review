package jp.systemengineeya.bookreview.api.testutil;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.nio.file.Paths;
import java.util.UUID;

public class ImageUrlAssert {

    public static void assertImageUrlIsUuidPng(String imageUrl) {
        URI uri = URI.create(imageUrl);
        String path = uri.getPath();
        String fileName = Paths.get(path).getFileName().toString();

        assertTrue(fileName.endsWith(".png"));
        String uuidPart = fileName.replace(".png", "");

        assertDoesNotThrow(() -> UUID.fromString(uuidPart));
    }
}