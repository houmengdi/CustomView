package com.hmd.address.selector.entry;


import com.hmd.address.selector.annotation.Name;
import com.hmd.address.selector.annotation.Selected;

import java.util.List;

public class CityEntry {

    @Name
    private String cName;
    @Selected
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private List<DistrictEntry> dList;

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public List<DistrictEntry> getdList() {
        return dList;
    }

    public void setdList(List<DistrictEntry> dList) {
        this.dList = dList;
    }
}
