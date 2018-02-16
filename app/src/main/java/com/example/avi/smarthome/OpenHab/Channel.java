package com.example.avi.smarthome.OpenHab;

import java.util.ArrayList;

/**
 * Created by Avi on 31/12/2017.
 */

public class Channel {
    private String id;
    private String uid;
    private ArrayList<String> linkedItems = new ArrayList<>();
    private String channelTypeUID;
    private String kind;
    private String description;
    private String label;
    private String itemType;

    public Channel(String id, String uid, ArrayList<String> linkedItems, String channelTypeUID, String configuration, String kind,
                   String description, String label, String itemType) {
        this.id = id;
        this.uid = uid;
        this.linkedItems = linkedItems;
        this.channelTypeUID = channelTypeUID;
        this.kind = kind;
        this.description = description;
        this.label = label;
        this.itemType = itemType;
    }

    public Channel() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getLinkedItems() {
        return linkedItems;
    }

    public void setLinkedItems(ArrayList<String> linkedItems) {
        this.linkedItems = linkedItems;
    }

    public String getChannelTypeUID() {
        return channelTypeUID;
    }

    public void setChannelTypeUID(String channelTypeUID) {
        this.channelTypeUID = channelTypeUID;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public String toString() {
        return "Channel [id=" + id + ", uid=" + uid + ", linkedItems=" + linkedItems + ", channelTypeUID="
                + channelTypeUID + ", kind=" + kind + ", description=" + description + ", "
                + "label=" + label + ", itemType=" + itemType + "]\n";
    }
}
