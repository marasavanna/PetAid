package com.halcyonmobile.adoption.model;

public class TraitConnection {
    public String id;
    public String targetId;
    public String traitId;

    public TraitConnection(String id, String targetId, String traitId) {
        this.id = id;
        this.targetId = targetId;
        this.traitId = traitId;
    }

    public TraitConnection() {
    }
}
