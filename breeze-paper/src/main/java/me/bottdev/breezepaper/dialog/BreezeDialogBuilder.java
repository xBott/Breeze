package me.bottdev.breezepaper.dialog;

public class BreezeDialogBuilder {

    private String title = "BreezeDialog";

    public BreezeDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public BreezeDialog build() {
        return new BreezeDialog(title);
    }

}
