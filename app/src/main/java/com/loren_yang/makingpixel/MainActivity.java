package com.loren_yang.makingpixel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.loren_yang.listeners.BaseMakingPixelInterface;
import com.loren_yang.makingpixelview.MakingPixelView;

public class MainActivity extends AppCompatActivity {

    private MakingPixelView mp;
    private MakingPixelAdapter mpAdatper=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp= (MakingPixelView) findViewById(R.id.main_mpView);

        init();
    }

    private void init(){
        final String[][] arrays=new  String[30][30];
        for(int i=0; i < arrays.length; ++i){
            for(int j=0; j<arrays[i].length; ++j){
                arrays[i][j]="#FFFFFF";
            }
        }
        mpAdatper=new MakingPixelAdapter(arrays);
        mpAdatper.setOnItemClickListenerListener(new BaseMakingPixelInterface.OnItemClickListener() {
            @Override
            public void onItemClick(int row, int col) {
                if(arrays[row][col].equals("#FFB095")){
                    arrays[row][col] = "#FFFFFF";
                }else {
                    arrays[row][col] = "#FFB095";
                }
                Toast.makeText(MainActivity.this,
                        "("+row+","+col+")",
                        Toast.LENGTH_SHORT).show();
                mpAdatper.notifyDataSetChanged();
            }
        });
        mp.setAdapter(mpAdatper);
    }

    public void mOnClick(View v){
        switch (v.getId()){
            case R.id.main_rect:
                mp.setShapeCircle(false);
                mpAdatper.notifyDataSetChanged();
                break;
            case R.id.main_circle:
                mp.setShapeCircle(true);
                mpAdatper.notifyDataSetChanged();
                break;
        }
    }
}
