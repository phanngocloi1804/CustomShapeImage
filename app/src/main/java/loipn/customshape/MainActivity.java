package loipn.customshape;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private Button btnExe;
    private ImageView imv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnExe = (Button) findViewById(R.id.btnExe);
        imv1 = (ImageView) findViewById(R.id.imv1);

        btnExe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomActivity.class);
                startActivityForResult(intent, CustomActivity.REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CustomActivity.REQUEST_CODE) {
            String image = data.getStringExtra("image");
            Bitmap bitmap = BitmapFactory.decodeFile(image);
            imv1.setImageBitmap(bitmap);
        }
    }
}
