package di;

import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.di.annotations.Named;

public class TestInject {

    private final int age;

    @Inject
    public TestInject(@Named("age") int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

}
