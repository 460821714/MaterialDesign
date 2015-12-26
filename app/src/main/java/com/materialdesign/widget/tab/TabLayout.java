/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.materialdesign.widget.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.materialdesign.R;

import java.util.ArrayList;
import java.util.List;

/**
 * tab
 */
public class TabLayout extends HorizontalScrollView {

    //正常文字颜色
    private int mTabTextColorNormal = android.R.color.darker_gray;
    //选中文字颜色
    private int mTabTextColorSelected = android.R.color.holo_blue_light;

    private int mTabViewBackgroundColorNormal = android.R.color.white;
    private int mTabViewBackgroundColorSelected = android.R.color.white;

    //tab字体颜色
    private ColorStateList mTabTextColorStateList = new ColorStateList(
            new int[][]{{android.R.attr.state_selected}, {}},
            new int[]{getResources().getColor(mTabTextColorSelected),
                    getResources().getColor(mTabTextColorNormal)});

    private List<Tab> mTabs = new ArrayList<>();

    /**
     * @param tab tab
     */
    public void addTab(Tab tab) {
        mTabs.add(tab);
    }

    public Tab newTab() {
        return new Tab();
    }

    /**
     * tab文字颜色
     *
     * @param tabTextColorNormal   normal
     * @param tabTextColorSelected selected
     */
    public void setTabTextColors(@ColorRes int tabTextColorNormal, @ColorRes int tabTextColorSelected) {
        int[][] stats = new int[][]{{android.R.attr.state_selected}, {}};
        int[] colors = new int[]{getResources().getColor(tabTextColorSelected),
                getResources().getColor(tabTextColorNormal)};
        mTabTextColorStateList = new ColorStateList(stats, colors);
    }

    /**
     * tabView背景色
     * @param tabViewBackgroundColorNormal normal
     * @param tabViewBackgroundColorSelected selected
     */
    public void setTabViewBackgroundColors(@ColorRes int tabViewBackgroundColorNormal,
                                           @ColorRes int tabViewBackgroundColorSelected) {
        mTabViewBackgroundColorNormal= tabViewBackgroundColorNormal;
        mTabViewBackgroundColorSelected = tabViewBackgroundColorSelected;
    }

    /**
     * 是否显示 tab 分割线
     *
     * @param b boolean
     */
    public void setDividerIndicator(boolean b) {
        mTabStrip.mDividerIndicator = b;
    }

    /**
     * 是否显示底部线
     *
     * @param b boolean
     */
    public void setUnderlineIndicator(boolean b) {
        mTabStrip.mUnderlineIndictor = b;
    }

    public class Tab {
        public int icon;
        public String tabText;

        public Tab() {
        }

        public Tab setIcon(@DrawableRes int icon) {
            this.icon = icon;
            return this;
        }

        public Tab setTabText(String tabText) {
            this.tabText = tabText;
            return this;
        }

        public Tab setTabText(@StringRes int stringId) {
            this.tabText = getResources().getString(stringId);
            return this;
        }
    }

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with.
     */
    public interface TabColorizer {

        /**
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

        /**
         * @return return the color of the divider drawn to the right of {@code position}.
         */
        int getDividerColor(int position);

    }

    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TAB_VIEW_PADDING_DIPS = 0;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;

    private int mTitleOffset;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private final TabStrip mTabStrip;

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        mTabStrip = new TabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    /**
     * Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setDividerColors(int... colors) {
        mTabStrip.setDividerColors(colors);
    }

    /**
     * Set the {@link ViewPager.OnPageChangeListener}. When using {@link TabLayout} you are
     * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * .
     */
    protected View createDefaultTabView(Context context) {
        View tabView = LayoutInflater.from(context).inflate(R.layout.tab, null);
        TextView textView = (TextView) tabView.findViewById(R.id.tv_tabText);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);
            textView.setBackgroundResource(outValue.resourceId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
            textView.setAllCaps(true);
        }

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return tabView;
    }

    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++) {

            View tabView = createDefaultTabView(getContext());

            TextView tabText = (TextView) tabView.findViewById(R.id.tv_tabText);

            ImageView tabIcon = (ImageView) tabView.findViewById(R.id.iv_tabIcon);

            if (mTabs.size() > i) {
                Tab tab = mTabs.get(i);
                if (tab.icon != 0) {
                    tabIcon.setImageResource(tab.icon);
                }

                if (tab.tabText != null) {
                    tabText.setText(tab.tabText);
                }
            }
            tabText.setTextColor(mTabTextColorStateList);

            tabView.setOnClickListener(tabClickListener);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            if (i == 0) {
                tabText.setSelected(true);
                tabIcon.setSelected(true);
                tabView.setBackgroundColor(
                        getResources().getColor(mTabViewBackgroundColorSelected));
            }
            mTabStrip.addView(tabView, lp);
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }

    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                View view = mTabStrip.getChildAt(i);
                tabViewSelector(view, false);
            }
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i, false);
                    tabViewSelector(v, true);
                    return;
                }
            }
        }
    }

    /**
     * 切换tab
     *
     * @param view     tabView
     * @param selector 是否选中
     */
    private void tabViewSelector(View view, boolean selector) {
        LinearLayout layout = null;
        if (LinearLayout.class.isInstance(view)) {
            layout = (LinearLayout) view;
        }

        if (selector) {
            view.setBackgroundColor(getResources().getColor(mTabViewBackgroundColorSelected));
        } else {
            view.setBackgroundColor(getResources().getColor(mTabViewBackgroundColorNormal));
        }

        for (int y = 0; y < layout.getChildCount(); y++) {
            layout.getChildAt(y).setSelected(selector);
        }
    }

}