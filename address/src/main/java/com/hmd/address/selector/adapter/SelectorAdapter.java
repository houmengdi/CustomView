package com.hmd.address.selector.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmd.address.R;
import com.hmd.address.selector.annotation.Name;
import com.hmd.address.selector.annotation.Selected;

import java.lang.reflect.Field;
import java.util.List;

public class SelectorAdapter<T> extends BaseAdapter {
    private List<T> mData;
    private Context mContext;

    public SelectorAdapter(List<T> data, Context context) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T t = mData.get(position);
        ViewHolder holder;
        if (convertView != null) {
            holder = ((ViewHolder) convertView.getTag());
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.window_selector_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        Resources resources = mContext.getResources();
        if (position == 0) {
            holder.view.setPadding(resources.getDimensionPixelSize(R.dimen.x28), resources.getDimensionPixelSize(R.dimen.x50), 0, resources.getDimensionPixelSize(R.dimen.x25));
        } else {
            holder.view.setPadding(resources.getDimensionPixelSize(R.dimen.x28), resources.getDimensionPixelSize(R.dimen.x25), 0, resources.getDimensionPixelSize(R.dimen.x25));
        }
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Name name = field.getAnnotation(Name.class);
            Selected selected = field.getAnnotation(Selected.class);
            if (name != null || selected != null) {
                try {
                    if (name != null) {
                        field.setAccessible(true);
                        String text = (String) field.get(t);
                        holder.mName.setText(text);
                    }
                    if (selected != null) {
                        if (selected.isBoolean()) {
                            field.setAccessible(true);
                            if (field.getBoolean(t)) {
                                holder.mImg.setVisibility(View.VISIBLE);
                            } else {
                                holder.mImg.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return holder.getView();
    }

    private class ViewHolder {
        private final TextView mName;
        private final ImageView mImg;
        private View view;

        private ViewHolder(View view) {
            this.view = view;
            mName = ((TextView) view.findViewById(R.id.window_selector_name));
            mImg = ((ImageView) view.findViewById(R.id.window_selector_img));
        }

        public View getView() {
            return view;
        }
    }
}
