package com.gethotdrop.android;

import com.gethotdrop.api.Drop;
import com.gethotdrop.hotdrop.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DropActivity extends Activity {

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
		
	}

}
