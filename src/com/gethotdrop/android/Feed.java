package com.gethotdrop.android;

import java.util.ArrayList;
import java.util.List;


import com.gethotdrop.hotdrop.R;
import com.gethotdrop.service.SyncService;
import com.gethotdrop.api.*;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class Feed extends Activity {
	DropAdapter adapter;
	ListView list;
	List<Drop> drops;

	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics = getBaseContext().getResources()
				.getDisplayMetrics();
		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
	 	Log.e("feed", "ok");

		drops = SyncService.getDrops();
        startService(new Intent(this, SyncService.class));

		adapter = new DropAdapter(this, R.layout.card, drops);

		
        
        list = (ListView) findViewById(R.id.list);
		list.setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);
		list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				LinearLayout post = (LinearLayout) findViewById(R.id.post);
				if (visibleItemCount > 0) {
					View firstView = view.getChildAt(0);
					if ((firstVisibleItem == 0) && (firstView.getTop() >= 0)) {
						post.setVisibility(View.VISIBLE);
					} else {
						post.setVisibility(View.GONE);
					}
				}
			}
		});
		list.setAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feed, menu);
		return true;
	}

}
