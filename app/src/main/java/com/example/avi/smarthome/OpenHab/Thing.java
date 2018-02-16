package com.example.avi.smarthome.OpenHab;

import java.util.ArrayList;
/**
 * Created by Avi on 31/12/2017.
 */

public class Thing {
    private String UID;
    private String thingTypeUID;
    private String status;
    private String statusDetail;
    private ArrayList<Channel> channels = new ArrayList<>();
    private boolean editable;
    private String location;
    private String label;

    public Thing(String uID, String thingTypeUID, String status, String statusDetail,
                 ArrayList<Channel> channels, boolean editable, String location, String label) {
        UID = uID;
        this.thingTypeUID = thingTypeUID;
        this.status = status;
        this.statusDetail = statusDetail;
        this.channels = channels;
        this.editable = editable;
        this.location = location;
        this.label = label;
    }

    public Thing() {}

    public String getUID() {
        return UID;
    }

    public void setUID(String uID) {
        UID = uID;
    }

    public String getThingTypeUID() {
        return thingTypeUID;
    }

    public void setThingTypeUID(String thingTypeUID) {
        this.thingTypeUID = thingTypeUID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Thing [UID=" + UID + ",\n thingTypeUID=" + thingTypeUID + ",\n status=" +
                status + ",\n statusDetail=" + statusDetail + ",\n channels=" + channels
                + ",\n editable=" + editable + ",\n location=" + location + ",\n label=" + label + "]";
    }
}
