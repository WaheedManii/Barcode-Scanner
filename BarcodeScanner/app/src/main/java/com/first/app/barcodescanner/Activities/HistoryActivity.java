package com.first.app.barcodescanner.Activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.first.app.barcodescanner.Adapter.MyAdapter;
import com.first.app.barcodescanner.Database.Data;
import com.first.app.barcodescanner.Database.DbHelper;
import com.first.app.barcodescanner.Model.Result;
import com.first.app.barcodescanner.R;
import java.util.ArrayList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;




public class HistoryActivity extends AppCompatActivity{


    private SQLiteDatabase db;
    private ArrayList<Result> resultArrayList;
    private AlertDialog alert;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setTitle("History");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));


        emptyView = (TextView)findViewById(R.id.empty_view);

        DbHelper helper = new DbHelper(this);
         db = helper.getWritableDatabase();


         layoutManager = new LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false);
         recyclerView = (RecyclerView) findViewById(R.id.my_Recyclerview);
         recyclerView.setLayoutManager(layoutManager);
         recyclerView.setHasFixedSize(true);


        viewAllData();
    }

    public void viewAllData() {

         resultArrayList = new ArrayList<>();
        String Columns [] = {Data.KEY_ID , Data.KEY_RESULT};
        Cursor c = db.query(false , Data.TABLE_NAME, Columns , null, null, null, null, Data.KEY_RESULT , null);
        if (c.moveToFirst()){

            do {
                int id = c.getInt(c.getColumnIndex(Data.KEY_ID));
                String res = c.getString(c.getColumnIndex(Data.KEY_RESULT));
                Log.i("ResultValue" , res);
                resultArrayList.add(new Result(id , res));

            } while (c.moveToNext());
        }


        if (resultArrayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }


        recyclerView.setAdapter(new MyAdapter(this,resultArrayList , db ));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_itmes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId){

            case R.id.clear_item :
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.ic_delete_forever_black_48dp);
                builder.setTitle("Attention");
                builder.setMessage("Are You Sure ? You Want To Delete All Items ?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         db.delete(Data.TABLE_NAME , null , null);
                            viewAllData();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                           alert.dismiss();
                    }
                });
                 alert = builder.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
                break;

            case R.id.refresh_item :
                Intent i = new Intent(HistoryActivity.this , HistoryActivity.class);
                startActivity(i);
                Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

}
