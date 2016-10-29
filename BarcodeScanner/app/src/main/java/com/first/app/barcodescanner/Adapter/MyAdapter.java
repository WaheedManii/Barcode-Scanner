package com.first.app.barcodescanner.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.first.app.barcodescanner.Database.Data;
import com.first.app.barcodescanner.Model.Result;
import com.first.app.barcodescanner.R;

import java.util.ArrayList;

/**
 * Created by Waheed Manii on 15/10/2016.
 */
public class MyAdapter extends RecyclerView.Adapter {

    Context context ;
    ArrayList<Result> resultArrayList ;
    SQLiteDatabase db ;
    private AlertDialog alert1;

    public MyAdapter(Context context, ArrayList<Result> resultArrayList, SQLiteDatabase db) {
        this.context = context ;
        this.resultArrayList = resultArrayList ;
        this.db = db ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

         final Result item = resultArrayList.get(position);
         MyHolder myHolder = (MyHolder) holder;
         myHolder.Supplay.setText(item.getResult());
        myHolder.Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.google.com.eg/?gfe_rd=cr&ei=A2_gV73QOsWT8QflkIWABA#q="+item.getResult());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
        myHolder.Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                { Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT,item.getResult());
                    context.startActivity(Intent.createChooser(i, "Choose One!"));
                }
                catch(Exception e){
                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        myHolder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setIcon(R.drawable.ic_delete_forever_black_48dp);
                builder1.setTitle("Attention");
                builder1.setMessage("Are You Sure ? You Want To Delete This Item ?");
                builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String [] where = {String.valueOf(item.getId())};
                        db.delete(Data.TABLE_NAME , Data.KEY_ID+"= ?" , where);
                        resultArrayList.remove(position);
                        notifyDataSetChanged();

                    }
                });
                builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert1.dismiss();
                    }
                });

                alert1 = builder1.create();
                alert1.setCanceledOnTouchOutside(false);
                alert1.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return resultArrayList.size() ;
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView Supplay ;
        ImageView Search ;
        ImageView Share ;
        ImageView Delete ;

        public MyHolder(View itemView) {
            super(itemView);

            Supplay = (TextView) itemView.findViewById(R.id.textView2);
            Search = (ImageView) itemView.findViewById(R.id.searchImage);
            Share = (ImageView) itemView.findViewById(R.id.share_icon);
            Delete = (ImageView) itemView.findViewById(R.id.deleteImage);
        }
    }
}
