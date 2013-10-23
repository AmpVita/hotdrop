package com.gethotdrop.android;

import java.util.ArrayList;
import java.util.List;

import com.gethotdrop.hotdrop.R;
import com.gethotdrop.service.SyncService;
//import com.gethotdrop.service.SyncService;
import com.gethotdrop.api.*;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class Feed extends Activity {
	DropAdapter adapter;
	ListView list;
	List<Drop> drops;

	String noteText;
	FitToWidthView postImage;
	Bitmap chosenImage;

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
		postImage = (FitToWidthView) findViewById(R.id.postImage);

		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		final RelativeLayout postButtons = (RelativeLayout) findViewById(R.id.postButtons);
		final EditText postNote = (EditText) findViewById(R.id.postNote);

		postNote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				postButtons.setBackgroundResource(R.color.blue);
			}
		});

		final ImageButton postButton = (ImageButton) findViewById(R.id.postButton);
		final ImageButton uploadButton = (ImageButton) findViewById(R.id.uploadButton);
		final ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraButton);

		postButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				noteText = postNote.getText().toString();
				Drop newDrop = new Drop(noteText, chosenImage);
				drops.add(newDrop);
				adapter.notifyDataSetChanged();

				postNote.setText("");
				postImage.setVisibility(View.GONE);
			}
		});

		uploadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, 1);
			}
		});

		cameraButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				startActivityForResult(intent, 0);
			}
		});

		// list setup
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				imm.hideSoftInputFromWindow(list.getWindowToken(), 0);
				postButtons.setBackgroundResource(R.color.grayLight);
			}
		});
		list.setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);
		list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				imm.hideSoftInputFromWindow(list.getWindowToken(), 0);
				postButtons.setBackgroundResource(R.color.grayLight);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				RelativeLayout postButtons = (RelativeLayout) findViewById(R.id.postButtons);
				if (visibleItemCount > 0) {
					View firstView = view.getChildAt(0);
					if ((firstVisibleItem == 0) && (firstView.getTop() >= 0)) {
						postButtons.setVisibility(View.VISIBLE);
					} else {
						postButtons.setVisibility(View.GONE);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent buttonIntent) {
		super.onActivityResult(requestCode, resultCode, buttonIntent);
		switch (requestCode) {
		case 0:
			if (resultCode == Activity.RESULT_OK) {
				chosenImage = (Bitmap) buttonIntent.getExtras().get("data");
			}
			break;

		case 1:
			if (resultCode == Activity.RESULT_OK) {
				if (resultCode == RESULT_OK) {
					Uri selectedImage = buttonIntent.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();

					chosenImage = BitmapFactory.decodeFile(filePath);
				}
			}
			break;
		}
		postImage = (FitToWidthView) findViewById(R.id.postImage);
		postImage.setImageBitmap(chosenImage);
		postImage.setVisibility(View.VISIBLE);
	}

}
