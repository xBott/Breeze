package di;

import me.bottdev.breezecore.SimpleBreezeEngine;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

public class TestDI {

    @Test
    public void testSupplierCreation() {
        SimpleBreezeEngine breezeEngine = new SimpleBreezeEngine(Path.of(""), () -> {});
        breezeEngine.getContext().addSupplier(new TestSupplier());
        int age = breezeEngine.getContext().get(Integer.class, "age").orElse(-1);
        assert age == 10;
    }

    @Test
    public void testInject() {
        SimpleBreezeEngine breezeEngine = new SimpleBreezeEngine(Path.of(""), () -> {});
        breezeEngine.getContext().addSupplier(new TestSupplier());
        Optional<TestInject> injected = breezeEngine.getContext().injectConstructor(TestInject.class);
        assert injected.get().getAge() == 10;
    }

}
