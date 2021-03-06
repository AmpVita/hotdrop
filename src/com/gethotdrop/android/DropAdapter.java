package com.gethotdrop.android;

import java.util.List;

import com.gethotdrop.api.Drop;
import com.gethotdrop.hotdrop.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DropAdapter extends ArrayAdapter<Drop> {
   Context context;
   int layoutResourceId;
   List<Drop> data;
   
   public DropAdapter(Context context, int layoutResourceId, List<Drop> dropArray) {
       super(context, layoutResourceId, dropArray);
       this.layoutResourceId = layoutResourceId;
       this.context = context;
       this.data = dropArray;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
       View row = convertView;
       CardHolder holder = null;

       if(row == null)
       {
           LayoutInflater inflater = ((Activity)context).getLayoutInflater();
           row = inflater.inflate(layoutResourceId, parent, false);

           holder = new CardHolder(); 
           holder.note = (TextView)row.findViewById(R.id.note);
           holder.image = (ImageView)row.findViewById(R.id.image);
           holder.grabs = (TextView)row.findViewById(R.id.score);
           holder.timestamp = (TextView)row.findViewById(R.id.timestamp);
           
           row.setTag(holder);
       }
       else
       {
           holder = (CardHolder)row.getTag();
       }

       Drop thisDrop = data.get(position);

       holder.note.setText(thisDrop.getMessage());
       holder.grabs.setText("+" + thisDrop.getGrabs());
       holder.timestamp.setText(thisDrop.getCreatedAt());
       //int outImage=R.drawable.ic_camera;
       holder.image.setImageBitmap(thisDrop.getImage());
      return row;

   }

   static class CardHolder
   {
       ImageView image;
       TextView note;
       TextView timestamp;
       TextView grabs;
   }
}