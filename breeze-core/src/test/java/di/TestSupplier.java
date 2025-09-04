package di;

import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.di.annotations.Supply;

public class TestSupplier {

    @Supply(type = SupplyType.PROTOTYPE)
    public int age() {
        return 10;
    }
    
}
