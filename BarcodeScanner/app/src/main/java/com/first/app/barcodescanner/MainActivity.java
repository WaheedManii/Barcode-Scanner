package com.first.app.barcodescanner;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.first.app.barcodescanner.Activities.ContactUsActivity;
import com.first.app.barcodescanner.Activities.GeneratorActivity;
import com.first.app.barcodescanner.Activities.HistoryActivity;
import com.first.app.barcodescanner.Database.Data;
import com.first.app.barcodescanner.Database.DbHelper;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AdView mAdView;                 // banner


    ActionBarDrawerToggle toggle ;
    Button ScanButton , GenerateButton ;
    private AlertDialog alert;
    private MainActivity activity;
    public static SQLiteDatabase db;
    private String res;
    Switch FlashButton , FocusButton ;
    private Camera cam;
    private Boolean exit = false ;
    TextView app_name ;
    ImageView app_logo ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));

        bannerAdverts();

        String Permissions [] = {Manifest.permission.INTERNET , Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.CAMERA};
        ActivityCompat.requestPermissions(this , Permissions , 1);

        FlashButton = (Switch) findViewById(R.id.flashButton);
        FocusButton = (Switch) findViewById(R.id.focusButton);
        app_name = (TextView) findViewById(R.id.app_name);
        app_logo = (ImageView) findViewById(R.id.circleImageView);
        Animation anim1 = AnimationUtils.loadAnimation(this , R.anim.rotate);
        app_logo.setAnimation(anim1);

        FlashButton.setChecked(false);
        FlashButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean bChecked) {
                if (bChecked){
                    try {
                        if (getPackageManager().hasSystemFeature(
                                PackageManager.FEATURE_CAMERA_FLASH)) {
                            cam = Camera.open();
                            Camera.Parameters p = cam.getParameters();
                            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            cam.setParameters(p);
                            cam.startPreview();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getBaseContext(), "Exception flashLightOn()",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    try {
                        if (getPackageManager().hasSystemFeature(
                                PackageManager.FEATURE_CAMERA_FLASH)) {
                            cam.stopPreview();
                            cam.release();
                            cam = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getBaseContext(), "Exception flashLightOff",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        DbHelper helper = new DbHelper(this);
         db = helper.getWritableDatabase();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this , drawerLayout , R.string.open , R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view_id);
        navigationView.setNavigationItemSelectedListener(this);

        ScanButton = (Button) findViewById(R.id.scan_button);
           activity = this;

        ScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Switch On Flash {'Volume Up'} , Switch Off Flash {'Volume Down'}");
                integrator.setCameraId(0);
                integrator.setBarcodeImageEnabled(true);
                integrator.setBeepEnabled(true);
                integrator.initiateScan();
            }
        });

        GenerateButton = (Button) findViewById(R.id.generate_button);
        GenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, GeneratorActivity.class);
                startActivity(intent);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {

            if(result.getContents() == null) {

            } else {

                res =  result.getContents();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.ic_location_searching_black_48dp);
                builder.setTitle("Scan Result");
                builder.setMessage(""+res);
                builder.setPositiveButton("SCAN AGAIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentIntegrator integrator = new IntentIntegrator(activity);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        integrator.setPrompt("Switch On Flash {'Volume Up'} , Switch Off Flash {'Volume Down'}");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(true);
                        integrator.setBarcodeImageEnabled(true);
                        integrator.initiateScan();
                    }
                });
                builder.setNegativeButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long row = SaveData();
                        if (row > 0){
                            Toast.makeText(MainActivity.this, "Saved In History", Toast.LENGTH_SHORT).show();
                            Log.i("CKECK_DATA" , String.valueOf(row));
                        }
                    }
                });
                 alert = builder.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();


            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private long SaveData() {

        ContentValues values = new ContentValues();
        Log.i("BBBBBBB" , res);
        values.put(Data.KEY_RESULT , res);
        Log.i("AAAAAAA" , res);
        long row = db.insertOrThrow(Data.TABLE_NAME, null, values);
        return row ;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true ;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId){

            case R.id.home_item :
                // nothing
                break;
            case R.id.history_item :
             startActivity(new Intent(this , HistoryActivity.class));
                break;
            case R.id.share_item :
                try{
                 Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, "Download Barcode Scanner From This link : http://play.google.com/store/apps/details?id=" + getPackageName());
                    startActivity(Intent.createChooser(i, "Choose One!"));
                }
                catch(Exception e){
                    Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rate_item :
                 rateApplication();
                break;
            case R.id.contact_item :
                startActivity(new Intent(this , ContactUsActivity.class));
                break;
        }

        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (dl.isDrawerOpen(GravityCompat.START))
            dl.closeDrawer(GravityCompat.START);
        return false;
    }

    private void rateApplication() {

        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }


    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 4 * 1000);

        }
    }


    public void bannerAdverts() {

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }






}
