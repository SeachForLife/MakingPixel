package com.loren_yang.makingpixelview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.loren_yang.adapter.BaseMakingPixelAdapter;

/**
 * Created by Loren Yang on 2017/12/21.
 */

public class MakingPixelView extends View  {

    private static final String TAG = "MakingPixelView";
    private final int MODE_DRAG=1;//拖动模式
    private final int MODE_ZOOM=2;//缩放模式

    private float patternDivider=3;//间距
    private float itemLength=10;//每个小方格的边长（正方形）
    private boolean isCircle=false;//是否用圆形来代替
    private float nowLeft=0;//计算首个矩形距离左边的位置
    private float nowTop=0;//计算首个矩形距离顶部的位置
//    private int clickNum=0;//双指触控的数量
    private int mMode=0;
    private boolean isDrag=false;//判断是否拖拽过，防止和点击同时触发
    private float oldSpace=0;//双指触控的上一次双指距离
    private float offsetX=0;//拖动时X轴偏移量
    private float offsetY=0;//拖动时Y轴偏移量
    private float canvasWidth=0;
    private float canvasHeight=0;

    protected BaseMakingPixelAdapter mAdapter;
    private RectF rectF;

    protected Paint mPaint = new Paint();

    PointF mStartPoint = new PointF();
    PointF mEndPoint = new PointF();

    public MakingPixelView(Context context) {
        super(context);
    }

    public MakingPixelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MakingPixelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MakingPixelView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            if(attr==R.styleable.MakingPixelView_PixelDivider){
                patternDivider= a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
            }
        }
        a.recycle();
    }

    public void setAdapter(BaseMakingPixelAdapter adapter) {
        this.mAdapter = adapter;
        if (this.mAdapter != null) {
            this.mAdapter.setView(this);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void setShapeCircle(boolean isCircle){
        this.isCircle=isCircle;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        rectF=new RectF(0,0,0,0);
        //获取父容器中给它设置的大小和模式
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int actualWidth=sizeWidth - getPaddingLeft() - getPaddingRight();
        int actualHeight=sizeHeight - getPaddingTop() - getPaddingBottom();

        int row = 0;
        int column = 0;
        if(mAdapter!=null){
            row = mAdapter.getRowCount();
            column = mAdapter.getColumnCount();
        }
        float itemWidth=(actualWidth-column*patternDivider+patternDivider)/column;
        float itemHeight=(actualHeight-row*patternDivider+patternDivider)/row;

        itemLength=itemWidth<itemHeight?itemWidth:itemHeight;

        float measureWidth = (column == 0 ? 0 : column * (itemLength + patternDivider) - patternDivider)
                + getPaddingLeft() + getPaddingRight();
        float measureHeight = (row == 0 ? 0 : row * (itemLength + patternDivider) - patternDivider)
                + getPaddingTop() + getPaddingBottom() ;

        float mWidth = modeWidth == MeasureSpec.EXACTLY ?
                sizeWidth: measureWidth;
        float mHeight = modeHeight == MeasureSpec.EXACTLY ?
                sizeHeight : measureHeight;
        setMeasuredDimension((int)mWidth, (int)mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(getPaddingLeft(), getPaddingTop(),
                getRight()- getPaddingRight(),
                getBottom() - getPaddingBottom());

        if (mAdapter != null) {
            final int columnCount = mAdapter.getColumnCount();
            final int rowCount = mAdapter.getRowCount();

            float useWidth=(columnCount == 0 ? 0 : columnCount * (itemLength + patternDivider) - patternDivider)
                    + getPaddingLeft() + getPaddingRight();
            float useHeight = (rowCount == 0 ? 0 : rowCount * (itemLength + patternDivider) - patternDivider)
                    + getPaddingTop() + getPaddingBottom() ;
            nowLeft=(canvas.getWidth()-useWidth)/2;
            nowTop=(canvas.getHeight()-useHeight)/2;
            canvasWidth=canvas.getWidth()-getPaddingLeft()-getPaddingRight();
            canvasHeight=canvas.getHeight()-getPaddingTop()-getPaddingBottom();
            for(int column=0;column<columnCount;column++){
                for(int row=0;row<rowCount;row++){
                    rectF.left = (column == 0 ? 0 : column * (itemLength + patternDivider)) + getPaddingLeft()+nowLeft+offsetX;
                    rectF.right = rectF.left + itemLength ;
                    rectF.top = (row == 0 ? 0 : row * (itemLength + patternDivider)) +  + getPaddingTop()+nowTop+offsetY;
                    rectF.bottom = rectF.top + itemLength;
                    drawItem(rectF,canvas,Color.parseColor(mAdapter.getColorValue(row,column)));
                }
            }
        }
    }

    protected void drawItem(RectF rect, Canvas canvas, int color) {
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        if (isCircle) {
            canvas.drawCircle((rectF.left + rectF.right) / 2,
                    (rectF.top + rectF.bottom) / 2,
                    Math.min(itemLength, itemLength) / 2,
                    mPaint);
        } else {
            canvas.drawRect(rect, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
//                System.out.println("click down");
                mMode=MODE_DRAG;
                isDrag=false;
                mStartPoint.x = event.getX();
                mStartPoint.y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
//                Log.i(TAG, "onTouchEvent: up"+(mAdapter==null)+"::"+isDrag);
                mEndPoint.x = event.getX();
                mEndPoint.y = event.getY();
                if(isDrag) break;
                int itemIndexX = getItemIndexX(mStartPoint.x);
                int itemIndexY = getItemIndexY(mStartPoint.y);
                if(mAdapter == null) break;
//                Log.i(TAG, "onTouchEvent: begin count X Y");
                if(itemIndexX == getItemIndexX(mEndPoint.x) && itemIndexY == getItemIndexY(mEndPoint.y)
                        &&itemIndexX<mAdapter.getColumnCount() &&itemIndexY<mAdapter.getRowCount()){
//                    Log.i(TAG, "onTouchEvent: enter 1");
                    if(itemIndexX >= 0 && itemIndexY >=0 && mAdapter != null){
//                        Log.i(TAG, "onTouchEvent: enter 2");
                        mAdapter.onReceiveItemClick(
                                itemIndexY,itemIndexX);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldSpace=countSpacing(event);
                mMode=MODE_ZOOM;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_MOVE:
                if(mMode==MODE_ZOOM){
                    float newSpace=countSpacing(event);
                    if(newSpace<10f) break;
                    float scale=newSpace/oldSpace;
                    if(itemLength*scale<5) break;
                    itemLength*=scale;
                    patternDivider*=scale;
                    oldSpace=newSpace;
                    invalidate();
                }else if(mMode==MODE_DRAG){
                    mEndPoint.x = event.getX();
                    mEndPoint.y = event.getY();
//                    System.out.println("-----"+Math.abs(offsetX+mEndPoint.x-mStartPoint.x));
                    if(Math.abs(mEndPoint.x-mStartPoint.x)<10) break;
//                    if(Math.abs(offsetX+mEndPoint.x-mStartPoint.x+nowLeft)>canvasWidth-10) break;
//                    if(Math.abs(offsetY+mEndPoint.y-mStartPoint.y+nowTop)>canvasHeight-10) break;
                    offsetX+=mEndPoint.x-mStartPoint.x;
                    offsetY+=mEndPoint.y-mStartPoint.y;
                    mStartPoint.x=mEndPoint.x;
                    mStartPoint.y=mEndPoint.y;
                    isDrag=true;
                    invalidate();
                }
                break;
        }
        return true;
    }

    /**
     * 计算双指之间的距离
     * @param event
     * @return
     */
    private float countSpacing(MotionEvent event){
        float x=0;
        float y=0;
        try {
            x = event.getX(0) - event.getX(1);
            y = event.getY(0) - event.getY(1);
        }catch (IllegalArgumentException i){
        }
        return (float) Math.sqrt(x*x+y*y);
    }

    /**
     * 获取当前点击坐标对应的X
     * @param touchX
     * @return
     */
    private int getItemIndexX(float touchX){
        if(touchX < 0 || touchX > getMeasuredWidth()){
            return -1;
        }
        Float itemIndex = (touchX-nowLeft-offsetX) / (itemLength + patternDivider);
        return (itemIndex - itemIndex.intValue() )   <= 1- patternDivider / ((float)itemLength +patternDivider) ?
                itemIndex.intValue()  : -1;
    }

    /**
     * 获取当前点击坐标对应的Y
     * @param touchY
     * @return
     */
    private int getItemIndexY(float touchY){
        if(touchY < 0 || touchY > getMeasuredHeight()){
            return -1;
        }
        Float itemIndex = (touchY-nowTop-offsetY) / (itemLength + patternDivider);
        return (itemIndex - itemIndex.intValue() ) <= 1- patternDivider / ((float)itemLength +patternDivider) ?
                itemIndex.intValue()  : -1;
    }
}
