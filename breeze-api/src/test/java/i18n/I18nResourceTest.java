package i18n;

import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.di.proxy.ProxyFactoryRegistry;
import me.bottdev.breezeapi.i18n.I18n;
import me.bottdev.breezeapi.i18n.TranslationLoader;
import me.bottdev.breezeapi.i18n.TranslationModule;
import me.bottdev.breezeapi.i18n.TranslationModuleManager;
import me.bottdev.breezeapi.i18n.types.SimpleI18n;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.annotations.ProvideResource;
import me.bottdev.breezeapi.resource.annotations.sources.JarSource;
import me.bottdev.breezeapi.resource.proxy.ResourceProvider;
import me.bottdev.breezeapi.resource.proxy.ResourceProxyHandlerFactory;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.source.types.DummyResourceSource;
import me.bottdev.breezeapi.resource.source.types.JarResourceSource;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class I18nResourceTest {

    @Proxy
    public interface TranslationProvider extends ResourceProvider {

        @ProvideResource(isTree = true)
        @JarSource(path = "translations")
        ResourceTree<SingleFileResource> getTranslationsTree();

    }

    static final SimpleLogger logger = new SimpleLogger("I18nResourceTest");
    static TranslationModuleManager translationModuleManager;
    static ProxyFactoryRegistry proxyFactory;
    static TranslationProvider translationProvider;
    static JsonMapper jsonMapper;
    static TranslationLoader translationLoader;


    @BeforeAll
    static void setup() {

        jsonMapper = new JsonMapper();
        translationLoader = new TranslationLoader(jsonMapper);

        ResourceSourceRegistry resourceSourceRegistry = new ResourceSourceRegistry()
                .register(SourceType.JAR, new JarResourceSource())
                .register(SourceType.DUMMY, new DummyResourceSource());

        proxyFactory = new ProxyFactoryRegistry()
                .register(
                        new ResourceProxyHandlerFactory(resourceSourceRegistry),
                        1
                );

        translationModuleManager = new TranslationModuleManager();

    }

    @BeforeEach
    void setupProvider() {
        translationProvider = proxyFactory.createObject(TranslationProvider.class).orElse(null);
    }

    @Test
    void shouldLoadTranslationsFromResourceTree() {

        logger.info("Test load translations from resource tree:");
        ResourceTree<SingleFileResource> translationsTree = translationProvider.getTranslationsTree();
        TranslationModule translationModule = translationLoader.loadModule("Test", translationsTree);

        logger.info("Loaded translation module with {}x translations:", translationModule.getSize());

        assertEquals(3, translationModule.getSize());

        translationModuleManager.register(translationModule);

        I18n enUs = new SimpleI18n(
                Locale.forLanguageTag("en-US"),
                Locale.forLanguageTag("en-US"),
                translationModuleManager
        );

        logger.info("Greeting en-US: {}", enUs.get("greeting"));
        logger.info("Farewell en-US: {}", enUs.get("farewell"));

        assertEquals("Hello world!", enUs.get("greeting"));
        assertEquals("Bye!", enUs.get("farewell"));

        I18n ruRu = new SimpleI18n(
                Locale.forLanguageTag("ru-RU"),
                Locale.forLanguageTag("en-US"),
                translationModuleManager
        );

        logger.info("Greeting ru-RU: {}", ruRu.get("greeting"));
        logger.info("Farewell ru-RU: {}", ruRu.get("farewell"));

        assertEquals("Привет мир!", ruRu.get("greeting"));
        assertEquals("Пока!", ruRu.get("farewell"));

        I18n deDe = new SimpleI18n(
                Locale.forLanguageTag("de-DE"),
                Locale.forLanguageTag("en-US"),
                translationModuleManager
        );

        logger.info("Greeting de-DE: {}", deDe.get("greeting"));
        logger.info("Farewell de-DE: {}", deDe.get("farewell"));

        assertEquals("Hallo Welt!", deDe.get("greeting"));
        assertEquals("Bis jetzt!", deDe.get("farewell"));

    }

}
