package com.gethotdrop.android;

import java.util.ArrayList;
import java.util.List;

import com.gethotdrop.api.Drop;
import com.gethotdrop.hotdrop.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DropActivity extends Activity {
	ArrayAdapter<String> convoAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fulldrop);
	
		long index = 0;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    index = extras.getLong("index");
		    Log.e("spot", Long.toString(index));
		}
		
       Drop thisDrop = FeedActivity.drops.get((int) index);

       ((TextView)findViewById(R.id.fullnote)).setText(thisDrop.getMessage());
       ((ImageView)findViewById(R.id.fullimage)).setImageBitmap(thisDrop.getImage());
	
       final List<String> comments = new ArrayList<String>();
       comments.add("one");
       convoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, comments);
       
       ListView convoList = (ListView) findViewById(R.id.comment_list);
       convoList.setAdapter(convoAdapter); // TODO: ADAPTER SET AT END OF LISTENER
       
       ImageButton comment = (ImageButton)findViewById(R.id.comment_submit);
       
       comment.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			comments.add(((EditText)findViewById(R.id.comment)).getText().toString());
			convoAdapter.notifyDataSetChanged();
		}
    	   
       });
	}

}
