package com.hmd.address.selector.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hmd.address.R;
import com.hmd.address.selector.adapter.SelectorAdapter;
import com.hmd.address.selector.listener.OnSelectorClickListener;

import java.util.ArrayList;
import java.util.List;

public class SelectorView implements AdapterView.OnItemClickListener, View.OnClickListener {

    private Context mContext;
    private View mContentView;
    private TextView mTitle;
    private LinearLayout mTab;
    private View mIndicator;
    private ListView mListView;
    private List mData;
    private SelectorAdapter mAdapter;
    private int mCurrentItem;
    private int mItemCount;
    private OnSelectorClickListener mOnSelectorClickListener;
    private String[] mTabTexts;

    public SelectorView(Context context) {
        this.mContext = context;
        initView();
        initData();
    }

    private void initData() {
        mData = new ArrayList<>();
        mAdapter = new SelectorAdapter(mData, mContext);
        mListView.setAdapter(mAdapter);
    }

    private void initView() {
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.window_selector, null);//673    142
        mTitle = ((TextView) mContentView.findViewById(R.id.window_selector_title));
        mTab = ((LinearLayout) mContentView.findViewById(R.id.window_selector_tab));
        mIndicator = mContentView.findViewById(R.id.window_selector_indicator);
        mListView = ((ListView) mContentView.findViewById(R.id.window_selector_list));
        mContentView.findViewById(R.id.window_selector_close).setOnClickListener(this);
        Resources resources = mContext.getResources();
        ((LinearLayout.LayoutParams) mListView.getLayoutParams()).height = resources.getDimensionPixelSize(R.dimen.y673) - resources.getDimensionPixelSize(R.dimen.y142);
        mListView.setOnItemClickListener(this);
    }

    public View getContentView() {
        return mContentView;
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setTabs(String... text) {
        mTab.removeAllViews();
        mTabTexts = text;
        if (text != null) {
            mItemCount = text.length;
            for (int i = 0; i < mItemCount; i++) {
                TextView tab_text = (TextView) LayoutInflater.from(mContext).inflate(R.layout.window_selector_tab_text, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int margin_right = mContext.getResources().getDimensionPixelOffset(R.dimen.x87);
                if (i != mItemCount - 1) {
                    layoutParams.setMargins(0, 0, margin_right, 0);
                }
                tab_text.setLayoutParams(layoutParams);
                tab_text.setOnClickListener(this);
                tab_text.setText(text[i]);
                tab_text.setTag(String.valueOf(i));
                mTab.addView(tab_text);
            }
        }
        mCurrentItem = 0;

        setIndicatorPosition(mTab.findViewWithTag(String.valueOf(mCurrentItem)));
    }

    private void setIndicatorPosition(final View viewWithTag) {
        if (viewWithTag != null) {
            viewWithTag.post(new Runnable() {
                @Override
                public void run() {
                    mIndicator.setX(viewWithTag.getX());
                    mIndicator.getLayoutParams().width = viewWithTag.getWidth();
                }
            });
        }
    }

    private void updateIndicatorPosition(final View viewWithTag) {
        viewWithTag.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mIndicator, "X", mIndicator.getX(), viewWithTag.getX()).setDuration(250);

                ValueAnimator valueAnimator = new ValueAnimator();
                valueAnimator.setFloatValues(0, 1.0f);
                valueAnimator.setDuration(250);
                final int mIndicatorWidth = mIndicator.getWidth();
                final int mItemWidth = viewWithTag.getWidth();
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float animatedValue = (float) animation.getAnimatedValue();
                        float width = mIndicatorWidth + (mItemWidth - mIndicatorWidth) * animatedValue;
                        ViewGroup.LayoutParams layoutParams = mIndicator.getLayoutParams();
                        layoutParams.width = (int) width;
                        mIndicator.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimator, valueAnimator);
                animatorSet.start();
            }
        });
    }

    public void setData(List collection) {
        if (collection != null) {
            mData.clear();
            mData.addAll(collection);
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
        }
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView name = (TextView) view.findViewById(R.id.window_selector_name);
        TextView tabItem = (TextView) mTab.findViewWithTag(String.valueOf(mCurrentItem));
        if (tabItem != null && tabItem != name) {
            tabItem.setText(name.getText().toString());
        }
        if (mCurrentItem < mItemCount - 1) {
            if (mOnSelectorClickListener != null) {
                mOnSelectorClickListener.onListItemClick(position, mCurrentItem);
            }
            mCurrentItem++;
            for (int i = mCurrentItem; i < mItemCount; i++) {
                TextView tabText = (TextView) mTab.findViewWithTag(String.valueOf(i));
                tabText.setText(mTabTexts[i]);
            }
        } else if (mCurrentItem == mItemCount - 1) {
            if (mOnSelectorClickListener != null) {
                mOnSelectorClickListener.onListItemClick(position, mCurrentItem);
            }
        }
        updateIndicatorPosition(mTab.findViewWithTag(String.valueOf(mCurrentItem)));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.window_selector_close) {//点击关闭按钮
            if (mOnSelectorClickListener != null) {
                mOnSelectorClickListener.onCloseClick();
                return;
            }
        }
        TextView clickTextView = (TextView) v;
        int item = Integer.parseInt((String) v.getTag());
        String clickText = clickTextView.getText().toString();

        TextView lastTextView = (TextView) mTab.findViewWithTag(String.valueOf(item - 1));
        String lastText = lastTextView == null ? "" : lastTextView.getText().toString();

        if (item < mCurrentItem || !clickText.equals(mTabTexts[item]) || (item > 0 && !lastText.equals(mTabTexts[item - 1]))) {
            mCurrentItem = item;
            updateIndicatorPosition(mTab.findViewWithTag(String.valueOf(mCurrentItem)));
            if (mOnSelectorClickListener != null) {
                mOnSelectorClickListener.onTabItemClick(mCurrentItem);
            }
        }
    }

    public void setOnSelectorClickListener(OnSelectorClickListener onSelectorClickListener) {
        mOnSelectorClickListener = onSelectorClickListener;
    }
}
