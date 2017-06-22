package univpm.iot_for_emergency.View.Funzionali;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import univpm.iot_for_emergency.R;

/**
 * Created by Giuseppe on 13/05/2017.
 */

public class Mappa  extends ImageView{

    public Mappa(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    public void init(Toolbar toolbar,int x,int y,int quota){
        Bitmap bMap=null;
        if(quota==145)
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q145_misure);
        if(quota==150)
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q150_misure);
        if(quota==155)
            bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q155_misure);
        Bitmap mutableBitmap = bMap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bMap1 = BitmapFactory.decodeResource(getResources(),R.drawable.sensortag);
        Bitmap mutableBitmap1 = bMap1.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap=overlay(mutableBitmap,mutableBitmap1,x,y,quota);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        params.addRule(RelativeLayout.BELOW, toolbar.getId());
        int dpValue = 43; // margin in dips
        float d = this.getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d); // margin in pixels
        params.setMargins(0,margin,0,0);
        this.setLayoutParams(params);
        this.setImageBitmap(mutableBitmap);

    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2,int x, int y,int quota) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        Rect rect=null;
        if(quota==145)
            rect= new Rect((int) ((y-419.125)*8-50),(int) ((x-42.375)*7.8889+50),(int) ((y-419.125)*8-10),(int) ((x-42.375)*7.8889+90));
        if(quota==150)
            rect= new Rect((int) ((y-419.125)*8-30),(int) ((x-42.375)*7.8889+5),(int) ((y-419.125)*8+10),(int) ((x-42.375)*7.8889+45));
        if(quota==155)
            rect= new Rect((int) ((y-419.125)*8-20),(int) ((x-42.375)*7.8889-20),(int) ((y-419.125)*8+20),(int) ((x-42.375)*7.8889+25));
        //canvas.drawCircle((float) ((Float.parseFloat(ids[1])-419.125)*8), (float) ((Float.parseFloat(ids[0])-42.375)*7.8889),10,paint);
        canvas.drawBitmap(bmp2,null,rect,null);
        //canvas.drawCircle((float) ((y-419.125)*8-30), (float) ((x-42.375)*7.8889+70),10,paint);
        return bmOverlay;
    }

}
