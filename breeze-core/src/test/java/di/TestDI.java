package di;

import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezecore.di.SimpleBreezeContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDI {

    BreezeContext context;

    @BeforeEach
    void setup() {
        context = new SimpleBreezeContext(new SimpleLogger("Context"));
    }

    @Test
    public void testSupplierCreation() {
        context.addSupplier(new TestSupplier());
        int age = context.get(Integer.class, "age").orElse(-1);
        assertEquals(10, age);
    }

    @Test
    public void testInject() {
        context.addSupplier(new TestSupplier());
        Optional<TestInject> injected = context.injectConstructor(TestInject.class);
        assertTrue(injected.isPresent());
        assertEquals(10, injected.get().getAge());
    }

}
