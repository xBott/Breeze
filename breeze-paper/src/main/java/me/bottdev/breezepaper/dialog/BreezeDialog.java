package me.bottdev.breezepaper.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bottdev.breezepaper.text.BreezeText;

import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public class BreezeDialog {

    private String title;

    public static BreezeDialog create(Consumer<BreezeDialogBuilder> consumer) {
        BreezeDialogBuilder builder = new BreezeDialogBuilder();
        consumer.accept(new BreezeDialogBuilder());
        return builder.build();
    }

    @SuppressWarnings("UnstableApiUsage")
    public Dialog toPaperDialog() {
        return Dialog.create(builder -> {
            builder.empty()
                    .base(
                            DialogBase.builder(BreezeText.format(title)).build()
                    );
        });
    }


}
