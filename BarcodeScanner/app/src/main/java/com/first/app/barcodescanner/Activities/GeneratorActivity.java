package com.first.app.barcodescanner.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.first.app.barcodescanner.Save_Imges_In_Storge.CapturePhotoUtils;
import com.first.app.barcodescanner.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.File;
import java.io.FileOutputStream;

public class GeneratorActivity extends AppCompatActivity {

    private AdView mAdView;                 // banner

    EditText text;
    Button gen_btn;
    ImageView image;
    String convert;
    TextView  note;
    GeneratorActivity context = this ;
    private int position;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);
        getSupportActionBar().setTitle("QR Generator");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));

        bannerAdverts();

        note = (TextView) findViewById(R.id.note_text);
        text = (EditText) findViewById(R.id.editText);
        gen_btn = (Button) findViewById(R.id.button);
        image = (ImageView) findViewById(R.id.imageView2);




                      gen_btn.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {

                              convert = text.getText().toString().trim();

                              MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                              if (convert.equals("")){

                                  text.setError("Please Enter Your Data First");


                              } else {
                                  image.setVisibility(View.VISIBLE);
                                  note.setText("Note : IF You Want To Do Some Thing With This Image , Press Long Click On It");
                                  try{
                                      BitMatrix bitMatrix = multiFormatWriter.encode(convert, BarcodeFormat.QR_CODE,250,250);
                                      BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                      Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                      image.setImageBitmap(bitmap);
                                      text.setText("");
                                  }
                                  catch (WriterException e){
                                      e.printStackTrace();
                                  }
                              }

                          }
                      });


                      image.setOnLongClickListener(new View.OnLongClickListener() {
                          @Override
                          public boolean onLongClick(View v) {


                              final String items [] = {"Save At Gallery" , "Share"};
                              AlertDialog.Builder builder = new AlertDialog.Builder(context);
                              builder.setTitle("Choose Your Option");
                              builder.setIcon(R.drawable.ic_touch_app_black_48dp);
                              builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {

                                      position = which ;
                                  }
                              });
                              builder.setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {

                                      if (items[position].equals("Save At Gallery")){

                                          saveImageToGallery();


                                      } else {

                                          View content = findViewById(R.id.imageView2);
                                          content.setDrawingCacheEnabled(true);

                                          Bitmap bitmap = content.getDrawingCache();
                                          File root = Environment.getExternalStorageDirectory();
                                          File cachePath = new File(root.getAbsolutePath() + "/DCIM/Camera/generatedImage.jpg");
                                          try {
                                              cachePath.createNewFile();
                                              FileOutputStream ostream = new FileOutputStream(cachePath);
                                              bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                              ostream.close();
                                          } catch (Exception e) {
                                              e.printStackTrace();
                                          }


                                          Intent share = new Intent(Intent.ACTION_SEND);
                                          share.setType("image/*");
                                          share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cachePath));
                                          startActivity(Intent.createChooser(share,"Share via"));
                                      }

                                  }
                              });
                              builder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      alert.dismiss();
                                  }
                              });

                              alert = builder.create();
                              alert.setCanceledOnTouchOutside(false);
                              alert.show();
                              return false;
                          }
                      });








    }


    private void saveImageToGallery(){
        image.setDrawingCacheEnabled(true);
        Bitmap b = image.getDrawingCache();
        CapturePhotoUtils.insertImage(getContentResolver(), b ,"BacodeScannerImage" , "gneratedImage");
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    public void bannerAdverts() {

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }






}
