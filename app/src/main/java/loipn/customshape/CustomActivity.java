package loipn.customshape;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import com.christophesmet.android.views.maskableframelayout.MaskableFrameLayout;
import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 8/2/2015.
 */
public class CustomActivity extends Activity {

    public static int REQUEST_CODE = 123;
    private int PICK_IMAGE = 11;

    private TwoWayView twoWayView;
    private Toast toast;
    private MaskableFrameLayout mask;
    private SeekBar seekBar;
    int old_progress;
    private SquareLayout btnDone;
    private ImageView imv1;

    ImageView imageDetail;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF startPoint = new PointF();
    PointF midPoint = new PointF();
    float oldDist = 1f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        twoWayView = (TwoWayView) findViewById(R.id.twoWayView);
        mask = (MaskableFrameLayout) findViewById(R.id.mask);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnDone = (SquareLayout) findViewById(R.id.btnDone);
        imv1 = (ImageView) findViewById(R.id.imv1);

        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.ic_shape_1);
        list.add(R.drawable.ic_shape_2);
        list.add(R.drawable.ic_shape_3);

        ShapeAdapter shapeAdapter = new ShapeAdapter(CustomActivity.this, twoWayView, list);
        twoWayView.setAdapter(shapeAdapter);

        ItemClickSupport itemClick = ItemClickSupport.addTo(twoWayView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                switch (position) {
                    case 0:
                        mask.setMask(R.drawable.shape_1);
                        break;
                    case 1:
                        mask.setMask(R.drawable.shape_2);
                        break;
                    case 2:
                        mask.setMask(R.drawable.shape_3);
                        break;
                }
            }
        });

        imageDetail = (ImageView) findViewById(R.id.imv1); /** * set on touch listner on image */
        imageDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                System.out.println("matrix=" + savedMatrix.toString());
//                toast.setText("matrix=" + savedMatrix.toString());
//                toast.show();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(midPoint, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oldDist;
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            }
                        }
                        break;
                }
                view.setImageMatrix(matrix);
                return true;
            }

            @SuppressLint("FloatMath")
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return FloatMath.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }
        });

        seekBar.setMax(20);
        seekBar.setProgress(10);
        old_progress = 10;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                toast.setText(progress + "");
//                toast.show();
                float scale;
                if (old_progress > progress) {
                    scale = (float) Math.pow((9f / 10f), (old_progress - progress));
                } else if (old_progress < progress) {
                    scale = (float) Math.pow((10f / 9f), (progress - old_progress));
                } else {
                    scale = 1f;
                }
                old_progress = progress;
                matrix.postScale(scale, scale);
                imageDetail.setImageMatrix(matrix);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = mask.getDrawingCache(true);
                String image = SaveImage(bitmap);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("image", image);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                imv1.setImageBitmap(bitmap);
            } catch (Exception e) {

            }
        }
    }

    private String SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/VLB_Noti");
        myDir.mkdirs();
        long n = System.currentTimeMillis();
        String fname = "vlb_" + n + ".png";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }
}
