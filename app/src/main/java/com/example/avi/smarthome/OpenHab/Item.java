package com.example.avi.smarthome.OpenHab;

import java.util.Map;

/**
 * Created by Avi on 31/12/2017.
 */

public class Item {
    String link;
    String state;
    String type;
    String name;
    String label;
    String category;
    StateDescription stateDescription;

    public Item(){}

    public Item(String link, String state, String type, String name, String label, String category, StateDescription stateDescription) {
        this.link = link;
        this.state = state;
        this.type = type;
        this.name = name;
        this.label = label;
        this.category = category;
        this.stateDescription = stateDescription;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public StateDescription getStateDescription() {
        return stateDescription;
    }

    public void setStateDescription(StateDescription stateDescription) {
        this.stateDescription = stateDescription;
    }

    @Override
    public String toString() {
        return "Item{" +
                "link='" + link + '\'' +
                ", state='" + state + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", category='" + category + '\'' +
                ", stateDescription=" + stateDescription +
                '}';
    }
}
