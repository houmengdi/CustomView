package com.hmd.address.selector.entry;


import com.hmd.address.selector.annotation.Name;
import com.hmd.address.selector.annotation.Selected;

import java.util.List;

public class ProvinceEntry {
    @Name
    private String pName;
    @Selected
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private List<CityEntry> cList;

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public List<CityEntry> getcList() {
        return cList;
    }

    public void setcList(List<CityEntry> cList) {
        this.cList = cList;
    }
}
