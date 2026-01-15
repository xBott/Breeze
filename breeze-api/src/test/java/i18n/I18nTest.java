package i18n;

import me.bottdev.breezeapi.i18n.*;
import me.bottdev.breezeapi.i18n.translations.SimpleTranslation;
import me.bottdev.breezeapi.i18n.types.SimpleI18n;
import me.bottdev.breezeapi.log.platforms.SLF4JLogPlatform;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class I18nTest {

    static final SimpleLogger logger = SLF4JLogPlatform.getFactory().simple("I18nTest");
    static TranslationModuleManager translationModuleManager;

    @BeforeAll
    static void setup() {
        translationModuleManager = new TranslationModuleManager();

        TranslationModule module = new TranslationModule("test")
                .addTranslation(
                        Locale.ENGLISH,
                        new SimpleTranslation()
                                .put("greeting", "Hello world!")
                                .put("farewell", "Bye!")
                                .put("fallback", "Fallback message in english.")
                                .put("reference", "Hello, {}! We are glad to see you!")
                )
                .addTranslation(
                        Locale.FRENCH,
                        new SimpleTranslation()
                                .put("greeting", "Bonjour tout le monde!")
                                .put("farewell", "À bientôt!")
                                .put("reference", "Bonjour, {}! Nous sommes ravis de vous voir!")
                );

        translationModuleManager.register(module);
    }

    @Test
    void shouldCreateI18n() {

        logger.info("Test create I18n container:");

        I18n i18n = new SimpleI18n(Locale.ENGLISH, Locale.ENGLISH, translationModuleManager);

        String message = i18n.get("greeting");
        logger.info(message);

        assertEquals("Hello world!", message);
    }

    @Test
    void shouldCreateI18nWithMultipleLanguages() {

        logger.info("Test create several I18n containers:");

        String greeting;
        String farewell;
        String fallback;

        I18n eng = new SimpleI18n(Locale.ENGLISH, Locale.ENGLISH, translationModuleManager);
        greeting = eng.get("greeting");
        farewell = eng.get("farewell");

        logger.info("Greeting English: {}", greeting);
        logger.info("Farewell English: {}", farewell);

        assertEquals("Hello world!", greeting);
        assertEquals("Bye!", farewell);

        I18n fr = new SimpleI18n(Locale.FRENCH, Locale.ENGLISH, translationModuleManager);
        greeting = fr.get("greeting");
        farewell = fr.get("farewell");
        fallback = fr.get("fallback");
        logger.info("Greeting French: {}", greeting);
        logger.info("Farewell French: {}", farewell);
        logger.info("Fallback French: {}", fallback);

        assertEquals("Bonjour tout le monde!", greeting);
        assertEquals("À bientôt!", farewell);
        assertEquals("Fallback message in english.", fallback);

    }

    @Test
    void shouldCreateI18nWithMultipleLanguages_And_UseArguments() {

        logger.info("Test create several I18n containers and use arguments:");

        String name = "Bob";
        String reference;

        I18n eng = new SimpleI18n(Locale.ENGLISH, Locale.ENGLISH, translationModuleManager);
        reference = eng.get("reference", name);
        logger.info("Reference English: {}", reference);

        assertEquals("Hello, Bob! We are glad to see you!", reference);

        I18n fr = new SimpleI18n(Locale.FRENCH, Locale.ENGLISH, translationModuleManager);
        reference = fr.get("reference", name);
        logger.info("Reference French: {}", reference);

        assertEquals("Bonjour, Bob! Nous sommes ravis de vous voir!", reference);

    }

}
