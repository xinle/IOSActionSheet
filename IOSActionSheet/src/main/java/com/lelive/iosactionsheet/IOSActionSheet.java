package com.lelive.iosactionsheet;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinle on 16/12/24.
 */

public class IOSActionSheet extends Dialog implements View.OnClickListener {
    public interface IActionSheetListener {
        void onActionSheetItemClick(IOSActionSheet actionSheet, int itemPosition ,ItemModel itemModel);
    }

    private static final int TRANSLATE_DURATION = 100;
    private static final int TAG_KEY = 1000;

    private Attributes mAttrs;

    private Context mContext;

    private int mAttributesId;

    private LinearLayout mView;

    private String mTitleStr;
    private String mSubTitleStr;
    private List<ItemModel> mOtherButtonTitles;
    private String mCancelButtonTitle = "取消";
    private IActionSheetListener mListener;
    private DrawableSelector mDrawableSelector;

    private boolean mDismissed = true;
    private boolean mCancelableOnTouchOutside = true;

    public IOSActionSheet(@NonNull Activity activity) {
        this(activity ,null);
    }

    public IOSActionSheet(@NonNull Activity activity ,@AttrRes int attributesId) {
        this(activity ,null);
    }

    private IOSActionSheet(@NonNull Activity activity ,Attributes attributes) {
        super(activity);
        this.mContext = activity;
        this.mAttrs = attributes;
        init();
    }


    private IOSActionSheet(@NonNull Builder builder) {
        this(builder.mActivity ,builder.attributesId);

        if(builder.mAttributes != null) {
            mAttrs = builder.mAttributes;
        }

        mTitleStr = builder.mTitleStr;
        mSubTitleStr = builder.mSubTitleStr;

        mOtherButtonTitles = builder.mOtherButtonTitles;
        mCancelButtonTitle = builder.mCancelButtonTitle;
        mListener = builder.mListener;
        mCancelableOnTouchOutside = builder.mCancelableOnTouchOutside;
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(mCancelableOnTouchOutside);

        mAttrs = readAttribute();

        LinearLayout linearLay = new LinearLayout(mContext);
        linearLay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLay.setOrientation(LinearLayout.VERTICAL);

        mView = linearLay;

        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation animation = new TranslateAnimation(type, 0, type, 0, type, 1, type, 0);
        animation.setDuration(TRANSLATE_DURATION);
        mView.startAnimation(animation);

    }

    private Attributes readAttribute() {
        Attributes attrs = new Attributes(mContext);
        if(mAttributesId <= 0) {
            return mAttrs != null ? mAttrs : attrs;
        }

        TypedArray a = mContext.obtainStyledAttributes(mAttributesId, R.styleable.IOSActionSheet);

        attrs.background = a.getColor(R.styleable.IOSActionSheet_ias_background ,attrs.background);
        attrs.chooseBackground = a.getColor(R.styleable.IOSActionSheet_ias_chooseBackground ,attrs.chooseBackground);

        attrs.titleTextColor = a.getColor(R.styleable.IOSActionSheet_ias_titleTextColor ,attrs.titleTextColor);
        attrs.cancelButtonTextColor = a.getColor(R.styleable.IOSActionSheet_ias_cancelButtonTextColor ,attrs.cancelButtonTextColor);
        attrs.otherButtonTextColor = a.getColor(R.styleable.IOSActionSheet_ias_otherButtonTextColor ,attrs.otherButtonTextColor);
        attrs.warningButtonTextColor = a.getColor(R.styleable.IOSActionSheet_ias_warningButtonTextColor ,attrs.warningButtonTextColor);
        attrs.checkButtonTextColor = a.getColor(R.styleable.IOSActionSheet_ias_checkButtonTextColor ,attrs.checkButtonTextColor);

        attrs.titleTextSize = a.getDimensionPixelSize(R.styleable.IOSActionSheet_ias_titleTextSize, attrs.titleTextSize);
        attrs.subTitleTextSize = a.getDimensionPixelSize(R.styleable.IOSActionSheet_ias_subTitleTextSize, attrs.subTitleTextSize);
        attrs.cancleButtonTextSize = a.getDimensionPixelSize(R.styleable.IOSActionSheet_ias_cancleButtonTextSize, attrs.cancleButtonTextSize);
        attrs.otherButtonTextSize = a.getDimensionPixelSize(R.styleable.IOSActionSheet_ias_otherButtonTextSize, attrs.otherButtonTextSize);
        attrs.warningButtonTextSize = a.getDimensionPixelSize(R.styleable.IOSActionSheet_ias_warningButtonTextSize ,attrs.warningButtonTextSize);

        attrs.lineHeight = (int) a.getDimension(R.styleable.IOSActionSheet_ias_lineHeight ,attrs.lineHeight);
        attrs.cancelButtonMarginTop = (int) a.getDimension(R.styleable.IOSActionSheet_ias_cancelButtonMarginTop ,attrs.cancelButtonMarginTop);
        attrs.radius = (int) a.getDimension(R.styleable.IOSActionSheet_ias_radius , attrs.radius);
        attrs.padding = (int) a.getDimension(R.styleable.IOSActionSheet_ias_padding , attrs.padding);

        a.recycle();
        return attrs;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = ((Activity) mContext).getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void show() {
        if (!mDismissed)
            return;

        createItems();

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(mView , params);

        if (getWindow() != null) {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(layoutParams);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        mDismissed = false;
        super.show();
    }

    private void createItems() {
        mView.removeAllViews();
        mView.setPadding(mAttrs.padding ,mAttrs.padding ,mAttrs.padding ,mAttrs.padding);

        mDrawableSelector = new DrawableSelector(mAttrs.radius);

        int childCount = 0;
        if(!TextUtils.isEmpty(mTitleStr)) {
            childCount ++;
        }

        if(mOtherButtonTitles != null) {
            childCount += mOtherButtonTitles.size();
        }

        if(!TextUtils.isEmpty(mCancelButtonTitle)) {
            if(mOtherButtonTitles == null) {
                mOtherButtonTitles = new ArrayList<>();
            }
            mOtherButtonTitles.add(new ItemModel(mCancelButtonTitle , ItemModel.ITEM_TYPE_CANCLE));
        }

        if(!TextUtils.isEmpty(mTitleStr)) {
            LinearLayout titleLay = new LinearLayout(mContext);
            titleLay.setOrientation(LinearLayout.VERTICAL);
            titleLay.setMinimumHeight(mAttrs.lineHeight);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                titleLay.setBackground(mDrawableSelector.getTopBg(childCount , mView.getChildCount()));
            } else {
                titleLay.setBackgroundDrawable(mDrawableSelector.getTopBg(childCount, mView.getChildCount()));
            }

            TextView titleTextView = new TextView(mContext);
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX , mAttrs.titleTextSize);
            titleTextView.setTextColor(mAttrs.titleTextColor);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.weight = 1;
            params1.setMargins(dp2px(8) , dp2px(4) , dp2px(8) , 0);
            titleTextView.setGravity(Gravity.CENTER);
            titleTextView.setLayoutParams(params1);
            titleTextView.setText(mTitleStr);
            titleLay.addView(titleTextView);

            if(!TextUtils.isEmpty(mSubTitleStr)) {
                TextView subTitleTextView = new TextView(mContext);
                subTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX , mAttrs.subTitleTextSize);
                subTitleTextView.setTextColor(mAttrs.titleTextColor);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.weight = 1;
                params2.setMargins(dp2px(8) , dp2px(10) ,dp2px(8) ,dp2px(10));
                subTitleTextView.setGravity(Gravity.CENTER);
                subTitleTextView.setLayoutParams(params2);
                subTitleTextView.setText(mSubTitleStr);

                titleLay.addView(subTitleTextView);
            }

            mView.addView(titleLay);
        }

        int topChildCount = mView.getChildCount();
        if(topChildCount > 0) {
            mView.addView(createLineView(mContext));
        }

        if (mOtherButtonTitles != null && mOtherButtonTitles.size() > 0) {

            for (int i = 0; i < mOtherButtonTitles.size(); i++) {
                Button button = new Button(mContext);
                button.setId(i);
                button.setOnClickListener(this);
                ItemModel itemModel = mOtherButtonTitles.get(i);

                button.setTag(itemModel);

                Drawable bg = ItemModel.ITEM_TYPE_CANCLE == itemModel.getItemType() ?
                        mDrawableSelector.createDrawable(mDrawableSelector.rDefault) :
                        mDrawableSelector.getTopBg(childCount, i + topChildCount);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    button.setBackground(bg);
                } else {
                    button.setBackgroundDrawable(bg);
                }

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , mAttrs.lineHeight);
                button.setText(itemModel.getItemTitle());
                if(itemModel.getItemType() == ItemModel.ITEM_TYPE_DEFUALT) {
                    ColorStateList colorStateList = new ColorStateList(new int[][]{
                            {-android.R.attr.state_pressed},
                            {android.R.attr.state_pressed}},
                            new int[]{mAttrs.otherButtonTextColor, mAttrs.checkButtonTextColor});
                    button.setTextColor(colorStateList);
                    button.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.otherButtonTextSize);
                } else if(itemModel.getItemType() == ItemModel.ITEM_TYPE_WARNING) {
                    ColorStateList colorStateList = new ColorStateList(new int[][]{
                            {-android.R.attr.state_pressed},
                            {android.R.attr.state_pressed}},
                            new int[]{mAttrs.warningButtonTextColor, mAttrs.checkButtonTextColor});
                    button.setTextColor(colorStateList);
                    button.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.warningButtonTextSize);
                } else if(itemModel.getItemType() == ItemModel.ITEM_TYPE_CANCLE) {
                    ColorStateList colorStateList = new ColorStateList(new int[][]{
                            {-android.R.attr.state_pressed},
                            {android.R.attr.state_pressed}},
                            new int[]{mAttrs.cancelButtonTextColor, mAttrs.checkButtonTextColor});
                    button.setTextColor(colorStateList);
                    button.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.cancleButtonTextSize);
                    layoutParams.setMargins(0 , mAttrs.cancelButtonMarginTop , 0 , 0);
                    button.getPaint().setFakeBoldText(true);
                }

                button.setLayoutParams(layoutParams);
                mView.addView(button);

                if(i != mOtherButtonTitles.size() - 1 - (TextUtils.isEmpty(mCancelButtonTitle) ? 0 : 1)
                        && itemModel.getItemType() != ItemModel.ITEM_TYPE_CANCLE) {
                    mView.addView(createLineView(mContext));
                }
            }
        }
    }

    private View createLineView(Context context) {
        View line = new View(mContext);
        line.setBackgroundColor(Color.LTGRAY);
        line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT ,1));
        return line;
    }

    @Override
    public void onClick(View v) {
        if(v.getTag() instanceof ItemModel) {
            ItemModel itemModel = (ItemModel) v.getTag();

            if(ItemModel.ITEM_TYPE_CANCLE == itemModel.getItemType()) {
                dismiss();
                return;
            }

            if(mListener != null) {
                mListener.onActionSheetItemClick(this ,v.getId() , itemModel);
            }

            dismiss();
        }
    }

    @Override
    public void dismiss() {
        if(mDismissed) {
            return;
        }
        mDismissed = true;

        if (mView != null) {
            int type = TranslateAnimation.RELATIVE_TO_SELF;
            TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type, 0, type, 1);
            an.setDuration(TRANSLATE_DURATION);
            an.setFillAfter(true);
            an.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    IOSActionSheet.super.dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mView.startAnimation(an);
        }
    }

    public void setTitleStr(String mTitleStr) {
        this.mTitleStr = mTitleStr;
    }

    public void setSubTitleStr(String mSubTitleStr) {
        this.mSubTitleStr = mSubTitleStr;
    }

    public List<ItemModel> getOtherButtonTitles() {
        return mOtherButtonTitles;
    }

    public void setOtherButtonTitles(List<ItemModel> mOtherButtonTitles) {
        this.mOtherButtonTitles = mOtherButtonTitles;
    }

    public String getCancelButtonTitle() {
        return mCancelButtonTitle;
    }

    public void setCancelButtonTitle(String mCancelButtonTitle) {
        this.mCancelButtonTitle = mCancelButtonTitle;
    }

    public IActionSheetListener getItemClickListener() {
        return mListener;
    }

    public void setItemClickListener(IActionSheetListener mListener) {
        this.mListener = mListener;
    }

    public void setAttributesId(int mAttributesId) {
        this.mAttributesId = mAttributesId;
        mAttrs = readAttribute();
    }

    public void setAttrs(Attributes attrs) {
        this.mAttrs = attrs;
    }

    private class DrawableSelector {
        private float r;
        private final float r1 = 0f;

        private final float[] rMiddle;
        private final float[] rDefault;
        private final float[] rTop;
        private final float[] rBottom;

        public DrawableSelector(float cornerRadius) {
            r = cornerRadius;

            rMiddle = new float[]{r1, r1, r1, r1, r1, r1, r1, r1};
            rDefault = new float[]{r, r, r, r, r, r, r, r};
            rTop = new float[]{r, r, r, r, r1, r1, r1, r1};
            rBottom = new float[]{r1, r1, r1, r1, r, r, r, r};
        }

        private Drawable createDrawable(float [] r) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(mAttrs.background);
            drawable.setCornerRadii(r);

            GradientDrawable checkDrawable = new GradientDrawable();
            checkDrawable.setShape(GradientDrawable.RECTANGLE);
            checkDrawable.setColor(mAttrs.chooseBackground);
            checkDrawable.setCornerRadii(r);

            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[] {android.R.attr.state_pressed}, checkDrawable);
            stateListDrawable.addState(StateSet.WILD_CARD, drawable);

            return stateListDrawable;
        }

        private Drawable getTopBg(int childCount, int childIndex) {
            if(childCount == 1) {
                return createDrawable(rDefault);
            } else {
                if(childIndex == 0) {
                    return createDrawable(rTop);
                } else if(childIndex == childCount - 1) {
                    return createDrawable(rBottom);
                } else {
                    return createDrawable(rMiddle);
                }
            }
        }
    }

    public static class ItemModel {

        public static final int ITEM_TYPE_DEFUALT = 0;
        public static final int ITEM_TYPE_WARNING = 1;
        public static final int ITEM_TYPE_CANCLE = 2;
        @IntDef({ITEM_TYPE_DEFUALT, ITEM_TYPE_WARNING, ITEM_TYPE_CANCLE})
        @interface ItemType{};

        private String itemTitle;
        @ItemType
        private int itemType = ITEM_TYPE_DEFUALT;

        public ItemModel(String itemTitle ,int itemType) {
            this.itemType = itemType;
            this.itemTitle = itemTitle;
        }

        public ItemModel(String itemTitle) {
            this.itemTitle = itemTitle;
        }

        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        public String getItemTitle() {
            return itemTitle;
        }

        public void setItemTitle(String itemTitle) {
            this.itemTitle = itemTitle;
        }
    }

    public static class Attributes {
        private Context mContext;

        public int background;            // 正常情况下的背景色
        public int chooseBackground;      // 选择状态下的背景色

        public int titleTextColor;             // 头部的文字的颜色
        public int cancelButtonTextColor;      // 取消按钮的颜色
        public int otherButtonTextColor;       // 其他按钮的颜色
        public int warningButtonTextColor;     // 警告按钮的颜色
        public int checkButtonTextColor;       // 选中状态下的文字的颜色

        public int titleTextSize;            // 头部文字的大小
        public int subTitleTextSize;         // 二级头部文字的大小
        public int cancleButtonTextSize;     // 取消按钮的大小
        public int otherButtonTextSize;      // 其他按钮的大小
        public int warningButtonTextSize;    // 警告按钮的大小

        public int lineHeight;                // 每一行的高度
        public int cancelButtonMarginTop;     // 取消按钮其他按钮之间的间距
        public int radius;                    // 圆角的半径
        public int padding;                   // 周围的padding值

        Attributes(Context context) {
            mContext = context;

            background = Color.argb(214 , 255 , 255 ,255);
            chooseBackground = Color.argb(214 ,218 ,218 ,218);

            titleTextColor = Color.GRAY;
            cancelButtonTextColor = Color.BLUE;
            otherButtonTextColor = Color.BLUE;
            warningButtonTextColor = Color.RED;
            checkButtonTextColor = Color.WHITE;

            titleTextSize = sp2px(16);
            subTitleTextSize = sp2px(14);
            cancleButtonTextSize = sp2px(16);
            otherButtonTextSize = sp2px(16);
            warningButtonTextSize = sp2px(16);

            lineHeight = dp2px(55);
            cancelButtonMarginTop = dp2px(10);
            radius = dp2px(8);
            padding = dp2px(10);
        }

        private int dp2px(int dp) {
            return (int) (dp * mContext.getResources().getDisplayMetrics().density + 0.5f);
        }

        private int sp2px(float sp) {
            return (int) (sp * mContext.getResources().getDisplayMetrics().scaledDensity + 0.5f);
        }
    }

    private int dp2px(int dp) {
        return (int) (dp * mContext.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static final class Builder {
        private Activity mActivity;

        private int attributesId;
        private Attributes mAttributes;

        private String mTitleStr;
        private String mSubTitleStr;

        private List<ItemModel> mOtherButtonTitles;
        private String mCancelButtonTitle;
        private IActionSheetListener mListener;

        private boolean mCancelableOnTouchOutside = true;

        public Builder(Activity activity) {
            mActivity = activity;
        }

        public Builder cancleTitle(String title) {
            mCancelButtonTitle = title;
            return this;
        }

        /**
         * 设置属性的id(不能和setAttribute一起设置)
         * @param attributesId
         * @return
         */
        public Builder attributesId(int attributesId) {
            this.attributesId = attributesId;
            return this;
        }

        /**
         * 设置属性 (不能和setAttributesId一起设置)
         * @param attributes
         * @return
         */
        public Builder attributes(Attributes attributes) {
            mAttributes = attributes;
            return this;
        }

        public Builder titleStr(String titleStr) {
            mTitleStr = titleStr;
            return this;
        }

        public Builder subTitleStr(String subTitleStr) {
            mSubTitleStr = subTitleStr;
            return this;
        }

        public Builder otherButtonTitles(List<ItemModel> otherButtonTitles) {
            mOtherButtonTitles = otherButtonTitles;
            return this;
        }

        public Builder itemClickListener(IActionSheetListener listener) {
            this.mListener = listener;
            return this;
        }

        public Builder cancleAbleOnTouchOutside(boolean cancelable) {
            mCancelableOnTouchOutside = cancelable;
            return this;
        }

        public void show() {
            IOSActionSheet actionSheet = new IOSActionSheet(this);
            actionSheet.show();
        }
    }

}
