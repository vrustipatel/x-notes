package model;

public class ModifyLabelModeL {

    String labelCreated, key;

    public ModifyLabelModeL() {

    }

    public ModifyLabelModeL(String labelCreated, String key) {
        this.labelCreated = labelCreated;
        this.key = key;
    }

    public String getLabelCreated() {
        return labelCreated;
    }

    public void setLabelCreated(String labelCreated) {
        this.labelCreated = labelCreated;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
