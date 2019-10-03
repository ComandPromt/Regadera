// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.theartofdev.edmodo.cropper.sample;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.croppersample.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public final class CropResultActivity extends Activity {

  /** The image to show in the activity. */
  static Bitmap mImage;

  private ImageView imageView;

  public static boolean multiple=false;
  
    public static boolean isMultiple() {
    return multiple;
  }

  public static void setMultiple(boolean multiple) {
    CropResultActivity.multiple = multiple;
  }
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_crop_result);

    imageView = ((ImageView) findViewById(R.id.resultImageView));
    imageView.setBackgroundResource(R.drawable.backdrop);

    Intent intent = getIntent();

    if (mImage != null) {

      imageView.setImageBitmap(mImage);
      int byteCount = 0;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
        byteCount = mImage.getByteCount() / 1024;
      }

      String desc =
          "("
              + mImage.getWidth()
              + ", "
              + mImage.getHeight()
              + ") "
              + byteCount
              + "KB";

      ((TextView) findViewById(R.id.resultImageText)).setText(desc);

        Save savefile = new Save();
        savefile.SaveImage(this, mImage);

if(multiple){

    int paso=MainActivity.getPaso();

    if(MainActivity.getListaImagenes().size()>0 && paso<MainActivity.getListaImagenes().size()) {

      MainFragment.mCropImageView.setImageUriAsync(Uri.fromFile(new File("/mnt/sdcard/imagenes/" +
      MainActivity.listaImagenes.get(paso))));
	  
      MainActivity.setPaso(++paso);

   }
}

    } else {

      Uri imageUri = intent.getParcelableExtra("URI");

      if (imageUri != null) {
        imageView.setImageURI(imageUri);
      }

       else {
        MainActivity.setPaso(MainActivity.getPaso()+1);
        Toast.makeText(this, "No image is set to show", Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  public void onBackPressed() {
    releaseBitmap();
    super.onBackPressed();
  }


  public void onImageViewClicked(View view) {
    releaseBitmap();
    finish();
  }

  private void releaseBitmap() {
    if (mImage != null) {
      mImage.recycle();
      mImage = null;
    }
  }
}
