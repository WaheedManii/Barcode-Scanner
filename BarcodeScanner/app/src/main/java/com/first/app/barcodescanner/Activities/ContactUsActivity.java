package com.first.app.barcodescanner.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.first.app.barcodescanner.R;

public class ContactUsActivity extends AppCompatActivity {

    ImageView FacebookImage , LinkedinImage , GmailImage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));

        FacebookImage = (ImageView) findViewById(R.id.faceImage);
        LinkedinImage = (ImageView) findViewById(R.id.linkedinImage);
        GmailImage = (ImageView) findViewById(R.id.gmailImage);

        FacebookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.facebook.com/waheed.mani.5");
                Intent intent = new Intent(Intent.ACTION_VIEW , uri);
                startActivity(intent);
            }
        });

        LinkedinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.linkedin.com/in/waheedmanii?trk=nav_responsive_tab_profile_pic");
                Intent intent = new Intent(Intent.ACTION_VIEW , uri);
                startActivity(intent);
            }
        });

        GmailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://plus.google.com/u/0/115057228321460352213");
                Intent intent = new Intent(Intent.ACTION_VIEW , uri);
                startActivity(intent);

            }
        });
    }





}
