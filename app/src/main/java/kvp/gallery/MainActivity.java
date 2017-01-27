package kvp.gallery;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecylerClickIntegration {
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    private ArrayList<String> alPaths;
    private GalleryImageAdapter adapter;
    private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alPaths = new ArrayList<>();
        adapter = new GalleryImageAdapter(this, this);
        RecyclerView rvGallery = (RecyclerView) findViewById(R.id.rv_gallery);
        rvGallery.setLayoutManager(new GridLayoutManager(this, 4));
        rvGallery.setAdapter(adapter);
        imgPreview = (ImageView) findViewById(R.id.img_preview);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        } else {
            loadImageData();
        }


    }

    private void loadImageData() {
        try {
            String[] STAR = {"*"};
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STAR, MediaStore.MediaColumns.DATE_ADDED, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        Log.i("Path", path);
                        alPaths.add(path);  //add all image data into array list
                    } while (cursor.moveToNext());
                }

                cursor.close();
                adapter.addData(alPaths);  //assign image data to adapter
                setImgPreview(alPaths.get(0));  //display first image preview
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setImgPreview(String path) {
        Glide.with(this).load(new File(path)).thumbnail(0.2f).placeholder(R.drawable.default_profile_pic).
                transform(new CircleTransform(this)).into(imgPreview);

    }

    @Override
    public void onItemClick(int position) {//recyclerview item click
        String path = adapter.getImageSelectedImagePath(position);
        setImgPreview(path);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] +
                    grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                loadImageData();
            } else {
                Toast.makeText(getApplicationContext(), "Enable permission to access Glide Gallery", Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();

            }
        }

    }

    private static class CircleTransform extends BitmapTransformation {
        public CircleTransform(Context context) {
            super(context);
        }

        private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            // TODO this could be acquired from the pool too
            Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

            Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return result;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        @Override
        public String getId() {
            return getClass().getName();
        }
    }


}
