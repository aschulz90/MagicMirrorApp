package com.blublabs.magicmirror.modules;

/**
 * Created by andrs on 30.08.2016.
 *
 */
public abstract class MagicMirrorModule {

    protected enum PositionRegion {
        top_bar,
        bottom_bar,
        top_left,
        bottom_left,
        top_center,
        bottom_center,
        top_right,
        bottom_right,
        upper_third,
        middle_center,
        lower_third
    }

    private String name;
    private boolean active;
    private PositionRegion position;

    public MagicMirrorModule(String name, boolean active, PositionRegion position) {
        this.name = name;
        this.active = active;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public PositionRegion getPosition() {
        return position;
    }

    public void setPosition(PositionRegion position) {
        this.position = position;
    }

    public abstract ModuleSettingsFragment getAddtionalSettingsFragment();
}
