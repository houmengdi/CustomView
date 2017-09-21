package com.hmd.address.selector;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.hmd.address.selector.entry.CityEntry;
import com.hmd.address.selector.entry.DistrictEntry;
import com.hmd.address.selector.entry.ProvinceEntry;
import com.hmd.address.selector.listener.OnSelectedFinishListener;
import com.hmd.address.selector.listener.OnSelectorClickListener;
import com.hmd.address.selector.widget.SelectorView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectAddressWindow extends PopupWindow implements OnSelectorClickListener {

    private final SelectorView mSelectorView;
    private List<ProvinceEntry> mData;
    private List<CityEntry> mCityEntryList;
    private List<DistrictEntry> mDistrictEntries;
    private ProvinceEntry mProvinceEntry;
    private CityEntry mCityEntry;
    private DistrictEntry mDistrictEntry;
    private OnSelectedFinishListener mSelectedFinishListener;

    public SelectAddressWindow(Context context) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mSelectorView = new SelectorView(context);
        setContentView(mSelectorView.getContentView());
        init();
        initData();
    }

    private void initData() {
        mData = generateData();
        mSelectorView.setData(mData);
    }

    private void init() {
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(false); // 设置是否允许在外点击使其消失
        mSelectorView.setTitle("选择所属地区");
        mSelectorView.setTabs("所属省", "所属城市", "所属区/县");
        mSelectorView.setOnSelectorClickListener(this);
    }

    //解析xml数据
    private List<ProvinceEntry> generateData() {
        try {

            InputStream inputStream = getContentView().getContext().getAssets().open("province_data.xml");
            XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlPullParser.setInput(inputStream, "utf-8");
            int eventType = xmlPullParser.getEventType();
            List<ProvinceEntry> pList = new ArrayList<>();
            List<CityEntry> cList = null;
            List<DistrictEntry> dList = null;
            ProvinceEntry provinceEntry = null;
            CityEntry cityEntry = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("province".equals(name)) {
                            cList = new ArrayList<>();
                            provinceEntry = new ProvinceEntry();
                            String pName = getAttributeValue(xmlPullParser, "name");
                            provinceEntry.setpName(pName);
                        } else if ("city".equals(name)) {
                            dList = new ArrayList<>();
                            cityEntry = new CityEntry();
                            String cName = getAttributeValue(xmlPullParser, "name");
                            cityEntry.setcName(cName);
                        } else if ("district".equals(name)) {
                            DistrictEntry districtEntry = new DistrictEntry();
                            String dName = getAttributeValue(xmlPullParser, "name");
                            districtEntry.setdName(dName);
                            if (dList == null) {
                                dList = new ArrayList<>();
                            }
                            dList.add(districtEntry);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("province".equals(name)) {
                            if (provinceEntry != null) {
                                provinceEntry.setcList(cList);
                                pList.add(provinceEntry);
                            }
                        } else if ("city".equals(name)) {
                            if (cityEntry != null) {
                                cityEntry.setdList(dList);
                            }
                            if (cList != null) {
                                cList.add(cityEntry);
                            }
                        }
                        break;
                }

                eventType = xmlPullParser.next();

            }
            return pList;

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
    }

    //获取xml标签对应属性值
    private String getAttributeValue(XmlPullParser xmlPullParser, String attrName) {
        String name = null;
        int attributeCount = xmlPullParser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String attributeName = xmlPullParser.getAttributeName(i);
            if (attrName != null && attrName.equals(attributeName)) {
                name = xmlPullParser.getAttributeValue(i);
            }
        }
        return name;
    }

    @Override
    public void onListItemClick(int position, int tabItem) {
        if (tabItem == 0) {//点击省联动市
            if (mProvinceEntry != null && mProvinceEntry.isSelected()) {
                mProvinceEntry.setSelected(false);
            }
            if (mCityEntry != null) {
                mCityEntry.setSelected(false);
            }
            if (mDistrictEntry !=null) {
                mDistrictEntry.setSelected(false);
            }
            mProvinceEntry = mData.get(position);
            mProvinceEntry.setSelected(true);
            mCityEntryList = mProvinceEntry.getcList();
            if (mCityEntryList != null) {
                mSelectorView.setData(mCityEntryList);
            }
        } else if (tabItem == 1) {//点击市联动区/县
            if (mCityEntry != null && mCityEntry.isSelected()) {
                mCityEntry.setSelected(false);
            }
            if (mDistrictEntry !=null) {
                mDistrictEntry.setSelected(false);
            }
            mCityEntry = mCityEntryList.get(position);
            mCityEntry.setSelected(true);
            mDistrictEntries = mCityEntry.getdList();
            if (mDistrictEntries != null) {
                mSelectorView.setData(mDistrictEntries);
            }
        }else if(tabItem == 2){//点区/县选择完成
            if (mDistrictEntry != null && mDistrictEntry.isSelected()) {
                mDistrictEntry.setSelected(false);
            }
            mDistrictEntry = mDistrictEntries.get(position);
            mDistrictEntry.setSelected(true);
            mSelectorView.notifyDataSetChanged();
            //选择完成
            if (mSelectedFinishListener != null) {
                mSelectedFinishListener.onSelectedFinish(mProvinceEntry.getpName(),mCityEntry.getcName(),mDistrictEntry.getdName());
            }
            dismiss();
        }
    }

    @Override
    public void onTabItemClick(int tabItem) {
        if (tabItem == 0) {//重选省
            if (mData != null) {
                mSelectorView.setData(mData);
            }
        }else if(tabItem == 1){//重选市
            if (mCityEntryList != null) {
                mSelectorView.setData(mCityEntryList);
            }
        }else if (tabItem == 2){
            if (mDistrictEntries != null) {
                mSelectorView.setData(mDistrictEntries);
            }
        }
    }

    @Override
    public void onCloseClick() {
        dismiss();
    }

    public void setSelectedFinishListener(OnSelectedFinishListener selectedFinishListener) {
        mSelectedFinishListener = selectedFinishListener;
    }
}
