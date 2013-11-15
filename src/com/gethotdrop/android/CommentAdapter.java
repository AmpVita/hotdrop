package com.gethotdrop.android;

import java.util.List;

import com.gethotdrop.hotdrop.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<String> {
	   Context context;
	   int layoutResourceId;
	   List<String> data;
	   
	   public CommentAdapter(Context context, int layoutResourceId, List<String> commentArray) {
	       super(context, layoutResourceId, commentArray);
	       this.layoutResourceId = layoutResourceId;
	       this.context = context;
	       this.data = commentArray;
	   }

	   @Override
	   public View getView(int position, View convertView, ViewGroup parent) {
	       View row = convertView;
	       CommentHolder holder = null;

	       if(row == null)
	       {
	           LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	           row = inflater.inflate(layoutResourceId, parent, false);

	           holder = new CommentHolder(); 
	           holder.comment = (TextView)row.findViewById(R.id.comment_message);
	           //holder.timestamp = (TextView)row.findViewById(R.id.comment_timestamp);
	           
	           row.setTag(holder);
	       }
	       else
	       {
	           holder = (CommentHolder)row.getTag();
	       }

	       String message = data.get(position);

	       holder.comment.setText(message);
	       //holder.timestamp.setText(thisDrop.getCreatedAt());
	       
	      return row;

	   }

	   static class CommentHolder
	   {
	       TextView comment;
	       //TextView timestamp;
	   }
}