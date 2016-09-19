package com.way.mat.opentokchat.items;

/**
 * Created by matviy on 08.09.16.
 */
public class PopupItem {

    int icon;
    String title;
    Type type;

    public enum Type {
        USERNAME,
        ABOUT
    }

    public PopupItem(int icon, String title, Type type) {
        this.icon = icon;
        this.title = title;
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
