package com.hmd.address.selector.entry;

import com.hmd.address.selector.annotation.Name;
import com.hmd.address.selector.annotation.Selected;

public class DistrictEntry {

    @Name
    private String dName;
    @Selected
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }
}
