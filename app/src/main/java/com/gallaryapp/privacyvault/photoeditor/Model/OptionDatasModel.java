package com.gallaryapp.privacyvault.photoeditor.Model;


public class OptionDatasModel {
    boolean isOptionSelectable;
    boolean isSelected;
    int optionIcon;
    int optionIconSelected;
    String optionName;
    boolean withDevider;

    public OptionDatasModel(String optionName, int optionIcon, boolean isOptionSelectable, boolean isSelected, boolean withDevider) {
        this.optionName = optionName;
        this.optionIcon = optionIcon;
        this.isOptionSelectable = isOptionSelectable;
        this.isSelected = isSelected;
        this.withDevider = withDevider;
    }

    public OptionDatasModel(String optionName, int optionIcon, int optionIconSelected, boolean isOptionSelectable, boolean isSelected, boolean withDevider) {
        this.optionName = optionName;
        this.optionIcon = optionIcon;
        this.optionIconSelected = optionIconSelected;
        this.isOptionSelectable = isOptionSelectable;
        this.isSelected = isSelected;
        this.withDevider = withDevider;
    }

    public String getOptionName() {
        return this.optionName;
    }

    public int getOptionIcon() {
        return this.optionIcon;
    }

    public int getOptionIconSelected() {
        return this.optionIconSelected;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isOptionSelectable() {
        return this.isOptionSelectable;
    }

    public boolean withDevider() {
        return this.withDevider;
    }
}
