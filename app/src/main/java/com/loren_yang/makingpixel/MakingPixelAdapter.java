package com.loren_yang.makingpixel;

/**
 * Created by Loren Yang on 2017/12/22.
 */

import com.loren_yang.adapter.BaseMakingPixelAdapter;

    public class MakingPixelAdapter extends BaseMakingPixelAdapter {

    private String[][] mArrays;

    public MakingPixelAdapter() {
        super();
    }

    public MakingPixelAdapter(String[][] mArrays) {
        super();
        this.mArrays=mArrays;
    }

    @Override
    public int getRowCount() {
        return mArrays == null ? 0 : mArrays.length;
    }

    @Override
    public int getColumnCount() {
        int column=0;
        if (mArrays != null) {
            for (String[] sub : mArrays) {
                if (sub != null && column < sub.length) {
                    column = sub.length;
                }
            }
        } else {
            column = 0;
        }
        return column;
    }

    @Override
    public String getColorValue(int row,int col) {
        String colorValue="#FFFFFF";
        if (mArrays != null) {
            colorValue=mArrays[row][col];
        }
        return colorValue;
    }


}
