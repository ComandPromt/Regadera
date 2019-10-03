
package com.theartofdev.edmodo.cropper.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.croppersample.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

  DrawerLayout mDrawerLayout;

  private ActionBarDrawerToggle mDrawerToggle;

  private MainFragment mCurrentFragment;

  private Uri mCropImageUri;

  private CropImageViewOptions mCropImageViewOptions = new CropImageViewOptions();


  public static int paso=0;

  public static int posicion=1;

  static LinkedList<String> listaImagenes = new LinkedList<>();

  public static LinkedList<String> getListaImagenes() {
    return listaImagenes;
  }

  public static void setListaImagenes(LinkedList<String> listaImagenes) {
    MainActivity.listaImagenes = listaImagenes;
  }

  public void setCurrentFragment(MainFragment fragment) {
    mCurrentFragment = fragment;
  }

  public void setCurrentOptions(CropImageViewOptions options) {
    mCropImageViewOptions = options;
    updateDrawerTogglesByOptions(options);
  }

  public void modo_multiple(MenuItem item) {

    CropResultActivity.setMultiple(true);
  }

  public void modo_simple(MenuItem item) {

    CropResultActivity.setMultiple(false);
  }

  public void resetear(MenuItem item) {

    paso=0;

    posicion=1;

    CropResultActivity.setMultiple(false);

    MainFragment.mCropImageView.setImageUriAsync(Uri.fromFile(new File("/mnt/sdcard/imagenes/" +
            listaImagenes.get(paso))));

  }

  public void back(MenuItem item) {

    try {
      if (paso <= 0) {
        paso = 1;

      }

      MainFragment.mCropImageView.setImageUriAsync(Uri.fromFile(new File("/mnt/sdcard/imagenes/" +
              listaImagenes.get(--paso))));

      posicion = ++paso;

      Toast.makeText(this, "(" + posicion + " / " + listaImagenes.size() + ")", Toast.LENGTH_SHORT)
              .show();

      --paso;
    }

    catch (Exception e){

    }

  }

  public void next(MenuItem item) {

    try {
      if (paso >= listaImagenes.size() - 1) {
        --paso;

      }

      MainFragment.mCropImageView.setImageUriAsync(Uri.fromFile(new File("/mnt/sdcard/imagenes/" +
              listaImagenes.get(++paso))));

      posicion = ++paso;

      Toast.makeText(this, "(" + posicion + " / " + listaImagenes.size() + ")", Toast.LENGTH_LONG)
              .show();

      --paso;
    }

    catch (Exception e){

    }

  }

  public static void copyFile(File sourceFile, File destFile) throws IOException {

    if(!destFile.exists()) {
      destFile.createNewFile();
    }

    FileChannel origen = null;
    FileChannel destino = null;

    try {

      origen = new FileInputStream(sourceFile).getChannel();
      destino = new FileOutputStream(destFile).getChannel();

      long count = 0;
      long size = origen.size();

      while((count += destino.transferFrom(origen, count, size-count))<size);

    }

    finally {

      if(origen != null) {
        origen.close();
      }

      if(destino != null) {
        destino.close();
      }

      if (sourceFile.exists()) {
        sourceFile.delete();
      }

    }

    listaImagenes.remove(paso);

    ++posicion;

  }

  public void bn(MenuItem item) throws IOException {

try {

  File f1 = new File("/mnt/sdcard/imagenes/" + listaImagenes.get(paso));

  File f2 = new File("/mnt/sdcard/imagenes/bn/" + listaImagenes.get(paso));

  copyFile(f1, f2);

  listaImagenes.clear();

  listaImagenes = directorio("/mnt/sdcard/imagenes", ".");

  if (listaImagenes.size() == 0) {
    MainFragment.mCropImageView.setImageResource(R.drawable.cat);
  }

  else {

    if (listaImagenes.size() == 1) {
      paso = 0;
    }

    MainFragment.mCropImageView.setImageUriAsync(Uri.fromFile(new File("/mnt/sdcard/imagenes/" +
            listaImagenes.get(paso))));
  }

}

catch (Exception e){

}

  }

  public static void renombrar(String ruta1, String ruta2) {

    File f1 = new File(ruta1);
    File f2 = new File(ruta2);

    f1.renameTo(f2);

  }

  public static String extraerExtension(String nombreArchivo) {

    String extension ="";

    if(nombreArchivo.length()>=3) {

      extension = nombreArchivo.substring(nombreArchivo.length() - 3, nombreArchivo.length());

      if (extension.equals("peg")) {
        extension = "jpeg";
      }

      if (extension.equals("PEG")) {
        extension = "JPEG";
      }

    }

    return extension;

  }

  public static String eliminarPuntos(String cadena) {

    String cadena2 = cadena.substring(0, cadena.length() - 4);

    cadena = cadena2.replace(".", "_") + "." + extraerExtension(cadena);

    return cadena;

  }

  public static LinkedList<String> directorio(String ruta, String extension) {

    LinkedList<String> lista = new LinkedList<>();

    File f = new File(ruta);

    if (f.exists()) {

      File[] ficheros = f.listFiles();

      String fichero = "";

      String extensionArchivo;

      File folder;

      boolean comprobacion;

      for (int x = 0; x < ficheros.length; x++) {

        fichero = ficheros[x].getName();

        folder = new File(ruta + "/" + fichero);

        extensionArchivo = extraerExtension(fichero);

        comprobacion=!folder.isDirectory();

        if (comprobacion && extension.equals(".") && ( extensionArchivo.equals("jpg") || extensionArchivo.equals("png") || extensionArchivo.equals("gif") ) ||
                comprobacion && extensionArchivo.equals(extension) ) {

          if (fichero.substring(0, fichero.length() - 5).contains(".")) {

            renombrar(
                    ruta+"/"+ fichero,
                    ruta+"/"+ eliminarPuntos(fichero));

          }

          if (extension.equals(".") || extension.equals(extraerExtension(fichero))) {
            lista.add(fichero);
          }

        }

      }
    }

    return lista;

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    crearCarpeta("/mnt/sdcard/imagenes");

    crearCarpeta("/mnt/sdcard/imagenes/bn");

    listaImagenes= directorio("/mnt/sdcard/imagenes", ".");

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    getSupportActionBar().setHomeButtonEnabled(true);

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    mDrawerToggle =
            new ActionBarDrawerToggle(
                    this, mDrawerLayout, R.string.main_drawer_open, R.string.main_drawer_close);

    mDrawerToggle.setDrawerIndicatorEnabled(true);

    mDrawerLayout.addDrawerListener(mDrawerToggle);

    if (savedInstanceState == null) {
      setMainFragmentByPreset(CropDemoPreset.RECT);
    }

  }

  private void crearCarpeta(String ruta) {

    File carpeta = new File(ruta);

    if (!carpeta.exists()) {
      carpeta.mkdir();
    }

  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {

    super.onPostCreate(savedInstanceState);

    mDrawerToggle.syncState();

    mCurrentFragment.updateCurrentCropViewOptions();

  }

  @Override

  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override

  public boolean onOptionsItemSelected(MenuItem item) {

    if (mDrawerToggle.onOptionsItemSelected(item)) {
      return true;
    }

    if (mCurrentFragment != null && mCurrentFragment.onOptionsItemSelected(item)) {
      return true;
    }

    return super.onOptionsItemSelected(item);

  }

  @Override

  @SuppressLint("NewApi")

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
            && resultCode == AppCompatActivity.RESULT_OK) {

      Uri imageUri = CropImage.getPickImageResultUri(this, data);

      boolean requirePermissions = false;

      if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {

        requirePermissions = true;

        mCropImageUri = imageUri;

        requestPermissions(
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
      }

      else {

        mCurrentFragment.setImageUri(imageUri);
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(
          int requestCode, String permissions[], int[] grantResults) {

    if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {

      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        CropImage.startPickImageActivity(this);
      }

      else {
        Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
                .show();
      }
    }

    if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {

      if (mCropImageUri != null
              && grantResults.length > 0
              && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mCurrentFragment.setImageUri(mCropImageUri);
      }

      else {
        Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
                .show();
      }

    }

  }

  @SuppressLint("NewApi")
  public void onDrawerOptionClicked(View view) {

    switch (view.getId()) {

      case R.id.drawer_option_load:

        if (CropImage.isExplicitCameraPermissionRequired(this)) {
          requestPermissions(
                  new String[] {Manifest.permission.CAMERA},
                  CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        }

        else {
          CropImage.startPickImageActivity(this);
        }

        mDrawerLayout.closeDrawers();

        break;

      case R.id.drawer_option_oval:

        setMainFragmentByPreset(CropDemoPreset.CIRCULAR);
        mDrawerLayout.closeDrawers();

        break;

      case R.id.drawer_option_rect:

        setMainFragmentByPreset(CropDemoPreset.RECT);
        mDrawerLayout.closeDrawers();

        break;

      case R.id.drawer_option_customized_overlay:

        setMainFragmentByPreset(CropDemoPreset.CUSTOMIZED_OVERLAY);
        mDrawerLayout.closeDrawers();

        break;

      case R.id.drawer_option_toggle_scale:

        mCropImageViewOptions.scaleType =
                mCropImageViewOptions.scaleType == CropImageView.ScaleType.FIT_CENTER
                        ? CropImageView.ScaleType.CENTER_INSIDE
                        : mCropImageViewOptions.scaleType == CropImageView.ScaleType.CENTER_INSIDE
                        ? CropImageView.ScaleType.CENTER
                        : mCropImageViewOptions.scaleType == CropImageView.ScaleType.CENTER
                        ? CropImageView.ScaleType.CENTER_CROP
                        : CropImageView.ScaleType.FIT_CENTER;
        mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
        updateDrawerTogglesByOptions(mCropImageViewOptions);

        break;

      case R.id.drawer_option_toggle_shape:

        mCropImageViewOptions.cropShape =
                mCropImageViewOptions.cropShape == CropImageView.CropShape.RECTANGLE
                        ? CropImageView.CropShape.OVAL
                        : CropImageView.CropShape.RECTANGLE;
        mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
        updateDrawerTogglesByOptions(mCropImageViewOptions);

        break;

      case R.id.drawer_option_toggle_guidelines:

        mCropImageViewOptions.guidelines =
                mCropImageViewOptions.guidelines == CropImageView.Guidelines.OFF
                        ? CropImageView.Guidelines.ON
                        : mCropImageViewOptions.guidelines == CropImageView.Guidelines.ON
                        ? CropImageView.Guidelines.ON_TOUCH
                        : CropImageView.Guidelines.OFF;
        mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
        updateDrawerTogglesByOptions(mCropImageViewOptions);

        break;

      case R.id.drawer_option_toggle_aspect_ratio:

        if (!mCropImageViewOptions.fixAspectRatio) {
          mCropImageViewOptions.fixAspectRatio = true;
          mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
        }

        else {

          if (mCropImageViewOptions.aspectRatio.first == 1
                  && mCropImageViewOptions.aspectRatio.second == 1) {
            mCropImageViewOptions.aspectRatio = new Pair<>(4, 3);
          } else if (mCropImageViewOptions.aspectRatio.first == 4
                  && mCropImageViewOptions.aspectRatio.second == 3) {
            mCropImageViewOptions.aspectRatio = new Pair<>(16, 9);
          } else if (mCropImageViewOptions.aspectRatio.first == 16
                  && mCropImageViewOptions.aspectRatio.second == 9) {
            mCropImageViewOptions.aspectRatio = new Pair<>(9, 16);
          } else {
            mCropImageViewOptions.fixAspectRatio = false;
          }
        }

        mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
        updateDrawerTogglesByOptions(mCropImageViewOptions);

        break;

      case R.id.drawer_option_toggle_auto_zoom:

        mCropImageViewOptions.autoZoomEnabled = !mCropImageViewOptions.autoZoomEnabled;
        mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
        updateDrawerTogglesByOptions(mCropImageViewOptions);

        break;

      case R.id.drawer_option_toggle_max_zoom:

        mCropImageViewOptions.maxZoomLevel =
                mCropImageViewOptions.maxZoomLevel == 4
                        ? 8
                        : mCropImageViewOptions.maxZoomLevel == 8 ? 2 : 4;
        mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
        updateDrawerTogglesByOptions(mCropImageViewOptions);

        break;

      case R.id.drawer_option_set_initial_crop_rect:

        mCurrentFragment.setInitialCropRect();
        mDrawerLayout.closeDrawers();

        break;

      case R.id.drawer_option_reset_crop_rect:

        mCurrentFragment.resetCropRect();
        mDrawerLayout.closeDrawers();

        break;

      case R.id.drawer_option_toggle_show_overlay:

        mCropImageViewOptions.showCropOverlay = !mCropImageViewOptions.showCropOverlay;
        mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
        updateDrawerTogglesByOptions(mCropImageViewOptions);

        break;

      default:

        Toast.makeText(this, "Unknown drawer option clicked", Toast.LENGTH_LONG).show();

        break;
    }
  }

  private void setMainFragmentByPreset(CropDemoPreset demoPreset) {

    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager
            .beginTransaction()
            .replace(R.id.container, MainFragment.newInstance(demoPreset))
            .commit();
  }

  @SuppressLint("StringFormatMatches")
  private void updateDrawerTogglesByOptions(CropImageViewOptions options) {

    ((TextView) findViewById(R.id.drawer_option_toggle_scale))
            .setText(
                    getResources()
                            .getString(R.string.drawer_option_toggle_scale, options.scaleType.name()));

    ((TextView) findViewById(R.id.drawer_option_toggle_shape))
            .setText(
                    getResources()
                            .getString(R.string.drawer_option_toggle_shape, options.cropShape.name()));

    ((TextView) findViewById(R.id.drawer_option_toggle_guidelines))
            .setText(
                    getResources()
                            .getString(R.string.drawer_option_toggle_guidelines, options.guidelines.name()));

    ((TextView) findViewById(R.id.drawer_option_toggle_show_overlay))
            .setText(
                    getResources()
                            .getString(
                                    R.string.drawer_option_toggle_show_overlay,
                                    Boolean.toString(options.showCropOverlay)));

    String aspectRatio = "FREE";

    if (options.fixAspectRatio) {
      aspectRatio = options.aspectRatio.first + ":" + options.aspectRatio.second;
    }

    ((TextView) findViewById(R.id.drawer_option_toggle_aspect_ratio))
            .setText(getResources().getString(R.string.drawer_option_toggle_aspect_ratio, aspectRatio));

    ((TextView) findViewById(R.id.drawer_option_toggle_auto_zoom))
            .setText(
                    getResources()
                            .getString(
                                    R.string.drawer_option_toggle_auto_zoom,
                                    options.autoZoomEnabled ? "Enabled" : "Disabled"));
    ((TextView) findViewById(R.id.drawer_option_toggle_max_zoom))
            .setText(
                    getResources().getString(R.string.drawer_option_toggle_max_zoom, options.maxZoomLevel));
  }

  public static int getPaso() {
    return paso;
  }

  public static void setPaso(int paso) {
    MainActivity.paso = paso;
  }

}
