package me.bottdev.breezepaper.components;

import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;

@Component
public class PaperTestComponent {

    private final PaperTestComponent2 testComponent2;

    @Inject
    public PaperTestComponent(PaperTestComponent2 testComponent2) {
        this.testComponent2 = testComponent2;
    }

}
