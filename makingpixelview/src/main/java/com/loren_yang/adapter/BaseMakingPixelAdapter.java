package com.loren_yang.adapter;


import com.loren_yang.listeners.BaseMakingPixelInterface;
import com.loren_yang.makingpixelview.MakingPixelView;

/**
 * Created by Loren Yang on 2017/12/22.
 */

public abstract class BaseMakingPixelAdapter {

    private BaseMakingPixelInterface.OnItemClickListener mOnItemClickListener;

    private MakingPixelView mView;

    public BaseMakingPixelAdapter(){
    }

    public void onReceiveItemClick(int row, int col){
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick( row,col);
        }
    }

    public abstract int getRowCount();

    public abstract int getColumnCount();

    public abstract String getColorValue(int row,int col);

    public void setView(MakingPixelView mView) {
        this.mView = mView;
    }

    public void notifyDataSetChanged() {
        mView.invalidate();
    }

    public void setOnItemClickListenerListener(BaseMakingPixelInterface.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public BaseMakingPixelInterface.OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

}
