package univpm.iot_for_emergency.View.Funzionali;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import univpm.iot_for_emergency.R;

/**
 * Created by Giuseppe on 13/05/2017.
 */

public class Mappa  extends ImageView{

    public Mappa(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    public void init(){
        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q145_color);
        Bitmap mutableBitmap = bMap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bMap1 = BitmapFactory.decodeResource(getResources(),R.drawable.sensortag);
        Bitmap mutableBitmap1 = bMap1.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap=overlay(mutableBitmap,mutableBitmap1);
        this.setImageBitmap(mutableBitmap);
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        int maxw=bmp1.getWidth();
        int maxh=bmp1.getHeight();
        int dens=bmp1.getDensity();
        Log.e("ciaone",maxh+ " "+maxw+" "+dens);
        Rect rect=new Rect();
        rect.set(130,250,170,290);
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2,null,rect,null);
        return bmOverlay;
    }

}
