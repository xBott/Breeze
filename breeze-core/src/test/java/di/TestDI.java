package di;

import me.bottdev.breezecore.SimpleBreezeEngine;
import org.junit.jupiter.api.Test;

public class TestDI {

    @Test
    public void testSupplierCreation() {
        SimpleBreezeEngine breezeEngine = new SimpleBreezeEngine();
        breezeEngine.getContext().addSupplier(new TestSupplier());
        int age = breezeEngine.getContext().get(Integer.class, "age").orElse(-1);
        assert age == 10;
    }

    @Test
    public void testInject() {
        SimpleBreezeEngine breezeEngine = new SimpleBreezeEngine();
        breezeEngine.getContext().addSupplier(new TestSupplier());
        TestInject injected = breezeEngine.getContext().injectConstructor(TestInject.class);
        assert injected.getAge() == 10;
    }

}
