package com.angcyo.bodypointdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/11/21 13:31
 * 修改人员：Robi
 * 修改时间：2017/11/21 13:31
 * 修改备注：
 * Version: 1.0.0
 */
public class BodyPointLayout extends FrameLayout {

    public static final String TAG = "robi";

    public static final int bodyWidth = 490;//人体完整图的宽度
    public static final int bodyHeight = 1026;//人体完整图的高度

    /**
     * 重写padding属性, 效果就是 既具有padd的效果, 视图onDraw又不会受到padd的限制
     */
    private int drawPaddingLeft = 20;
    private int drawPaddingRight = 120;
    private int drawPaddingTop = 20;
    private int drawPaddingBottom = 20;

    /**
     * 是否是反面
     */
    private boolean isReverseSide = false;

    public BodyPointLayout(@NonNull Context context) {
        this(context, null);
    }

    public BodyPointLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    private List<BodyStructure> mBodyStructureList = new ArrayList<>();
    private List<BodyStructure> mBodyStructureListRS = new ArrayList<>();//反面部位

    void initLayout() {
        setWillNotDraw(false);
        bodyAllDrawable = ContextCompat.getDrawable(getContext(), R.drawable.body_all);

        initAllBodyStructure();
    }

    private void l(String log) {
        Log.i(TAG, log);
    }

    /**
     * 添加身体的所有部位结构
     */
    private void initAllBodyStructure() {
        //右手
        BodyStructure rHand = new BodyStructure();
        rHand.x = 330;
        rHand.y = 180;
        rHand.w = 156;
        rHand.h = 493;
        rHand.pressDrawable = ContextCompat.getDrawable(getContext(), R.drawable.youshou_p);

        //穴位
        AcuPoint acuPoint = new AcuPoint();
        acuPoint.x = 350;
        acuPoint.y = 240;
        acuPoint.drawable = ContextCompat.getDrawable(getContext(), R.drawable.tianchi);
        acuPoint.mListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                l("点击天池穴");
            }
        };
        rHand.mAcuPointList.add(acuPoint);

        acuPoint = new AcuPoint();
        acuPoint.x = 370;
        acuPoint.y = 330;
        acuPoint.drawable = ContextCompat.getDrawable(getContext(), R.drawable.quze);
        acuPoint.mListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                l("点击曲泽");
            }
        };
        rHand.mAcuPointList.add(acuPoint);

        acuPoint = new AcuPoint();
        acuPoint.drawable = ContextCompat.getDrawable(getContext(), R.drawable.neiguan);
        acuPoint.x = 520 - acuPoint.drawable.getIntrinsicWidth();
        acuPoint.isShowInLeft = true;
        acuPoint.y = 450;
        acuPoint.mListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                l("点击内关穴");
            }
        };
        rHand.mAcuPointList.add(acuPoint);

        mBodyStructureList.add(rHand);

        //左手
        BodyStructure lHand = new BodyStructure();
        lHand.x = 7;
        lHand.y = 180;
        lHand.w = 156;
        lHand.h = 493;
        lHand.pressDrawable = ContextCompat.getDrawable(getContext(), R.drawable.zuoshou_p);
        mBodyStructureList.add(lHand);

        //左脚
        BodyStructure lFoot = new BodyStructure();
        lFoot.x = 165;
        lFoot.y = 520;
        lFoot.w = 78;
        lFoot.h = 504;
        lFoot.pressDrawable = ContextCompat.getDrawable(getContext(), R.drawable.zuojiao_p);
        mBodyStructureList.add(lFoot);

        //右脚
        BodyStructure rFoot = new BodyStructure();
        rFoot.x = 250;
        rFoot.y = 520;
        rFoot.w = 80;
        rFoot.h = 504;
        rFoot.pressDrawable = ContextCompat.getDrawable(getContext(), R.drawable.youjiao_p);
        mBodyStructureList.add(rFoot);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            int x = (int) childAt.getTag(R.id.layout_x);
            int y = (int) childAt.getTag(R.id.layout_y);
            childAt.layout(x, y, x + childAt.getMeasuredWidth(), y + childAt.getMeasuredHeight());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bodyAllDrawable.setBounds(drawPaddingLeft, drawPaddingTop, getMeasuredWidth() - drawPaddingRight, getMeasuredHeight() - drawPaddingBottom);
        resetAllBodyStructure();
    }

    private int x(int x) {
        float sx = (getMeasuredWidth() - drawPaddingLeft - drawPaddingRight) * 1f / bodyWidth;
        return (int) (x * sx);
    }

    private int y(int y) {
        float sy = (getMeasuredHeight() - drawPaddingTop - drawPaddingBottom) * 1f / bodyHeight;
        return (int) (y * sy);
    }

    /**
     * 将所有结构的坐标, 进行映射(这样绘制出来的才会吻合)
     */
    private void resetAllBodyStructure() {

        for (BodyStructure bs : mBodyStructureList) {
            bs.x = drawPaddingLeft + x(bs.x);
            bs.y = drawPaddingTop + y(bs.y);
            bs.w = x(bs.w);
            bs.h = y(bs.h);
            bs.pressDrawable.setBounds(bs.getRect());

            for (AcuPoint ap : bs.mAcuPointList) {
                ap.x = drawPaddingLeft + x(ap.x);
                ap.y = drawPaddingTop + y(ap.y);
            }

            l(bs.getRect().toString());
        }
    }

    private Drawable bodyAllDrawable;
    /**
     * 当前按下的部位
     */
    private BodyStructure touchOnStruct;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bodyAllDrawable.draw(canvas);
        if (touchOnStruct != null) {
            touchOnStruct.pressDrawable.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                BodyStructure touchOnStruct = getTouchOnStruct(event.getX(), event.getY());
                onTouchStructChange(touchOnStruct);
                break;
        }
        return true;
    }

    private void onTouchStructChange(BodyStructure structure) {
        if (structure == null) {
            //点击在非人体部位外
            this.touchOnStruct = null;
            removeAllViews();
            postInvalidate();
        } else if (this.touchOnStruct == structure) {
            //重复点击
        } else {
            this.touchOnStruct = structure;
            addAllAcuPoint(touchOnStruct.mAcuPointList);
            postInvalidate();
        }
    }

    /**
     * 将所有穴位添加到视图中
     */
    private void addAllAcuPoint(List<AcuPoint> pointList) {
        removeAllViews();
        for (AcuPoint ap : pointList) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(ap.drawable);
            imageView.setTag(R.id.layout_x, ap.x);
            imageView.setTag(R.id.layout_y, ap.y);
            imageView.setOnClickListener(ap.mListener);
            addView(imageView, new ViewGroup.LayoutParams(-2, -2));

            if (ap.isShowInLeft) {
                imageView.setTranslationX(-ap.drawable.getIntrinsicWidth());
                imageView.animate()
                        .translationX(0)
                        .setDuration(500)
                        .start();
            } else {
                imageView.setTranslationX(ap.drawable.getIntrinsicWidth());
                imageView.animate()
                        .translationX(0)
                        .setDuration(500)
                        .start();
            }

        }
    }

    /**
     * 通过touch坐标, 返回身体部位
     */
    private BodyStructure getTouchOnStruct(float x, float y) {
        if (isReverseSide) {
            for (BodyStructure bs : mBodyStructureListRS) {
                if (bs.getRect().contains((int) x, ((int) y))) {
                    return bs;
                }
            }
        } else {
            for (BodyStructure bs : mBodyStructureList) {
                if (bs.getRect().contains((int) x, ((int) y))) {
                    return bs;
                }
            }
        }
        return null;
    }

    /**
     * 每个部位的结构体
     */
    static class BodyStructure {
        /**
         * 部位相对于整体的x和y坐标(px)
         */
        int x, y;

        /**
         * 部位的宽高(px)
         */
        int w, h;

        /**
         * 此部位按下时的状态图
         */
        Drawable pressDrawable;

        List<AcuPoint> mAcuPointList = new ArrayList<>();

        private Rect mRect = new Rect();

        public Rect getRect() {
            mRect.set(x, y, x + w, y + h);
            return mRect;
        }
    }

    /**
     * 穴位
     */
    static class AcuPoint {
        Drawable drawable;
        /**
         * 穴位相对于人体的x和y坐标(px)
         */
        int x, y; //如果图片要在左边显示, x需要减去图片的宽度

        boolean isShowInLeft = false;//是否显示在左边, 用来控制执行动画的方向

        View.OnClickListener mListener;//点击事件
    }
}
