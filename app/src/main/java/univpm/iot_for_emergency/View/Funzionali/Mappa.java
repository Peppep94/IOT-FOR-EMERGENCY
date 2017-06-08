package univpm.iot_for_emergency.View.Funzionali;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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


    public void init(Toolbar toolbar){
        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.q155_misure);
        Bitmap mutableBitmap = bMap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bMap1 = BitmapFactory.decodeResource(getResources(),R.drawable.sensortag);
        Bitmap mutableBitmap1 = bMap1.copy(Bitmap.Config.ARGB_8888, true);
        mutableBitmap=overlay(mutableBitmap,mutableBitmap1);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        params.addRule(RelativeLayout.BELOW, toolbar.getId());
        int dpValue = 43; // margin in dips
        float d = this.getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d); // margin in pixels
        params.setMargins(0,margin,0,0);
        this.setLayoutParams(params);
        this.setImageBitmap(mutableBitmap);

    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        Paint paint=new Paint();
        paint.setColor(Color.BLACK);

        InputStream inputStream;

        String[] ids;

        inputStream = getResources().openRawResource(R.raw.punti155csv);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {



                ids=csvLine.split(";");
                try{

                    canvas.drawCircle((float) ((Float.parseFloat(ids[1])-419.125)*8), (float) ((Float.parseFloat(ids[0])-42.375)*7.8889),10,paint);

                }catch (Exception e){
                    Log.e("Unknown ",e.toString());
                }
            }




        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }


        return bmOverlay;
    }

}
