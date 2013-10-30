package com.gethotdrop.android;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gethotdrop.api.Drop;
import com.gethotdrop.hotdrop.R;
import com.gethotdrop.service.SyncService;
//import com.gethotdrop.service.SyncService;

public class Feed extends Activity {
	DropAdapter adapter;
	ListView list;
	List<Drop> drops;

	String noteText = null;
	FitToWidthView postImage = null;
	Bitmap chosenImage = null;
	EditText postNote = null;
	Uri imageUri;
	InputMethodManager imm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

		//Get drops and start service
		drops = SyncService.getDrops();
		startService(new Intent(this, SyncService.class));

		//Create DropAdapter / ListView
		adapter = new DropAdapter(this, R.layout.card, drops);
		list = (ListView) findViewById(R.id.list);
		
		//Create view for image / edittext for note
		postImage = (FitToWidthView) findViewById(R.id.postImage);
		postNote = (EditText) findViewById(R.id.postNote);
		
		//Set-up listener for what is selected
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		//Create layout that wraps buttons buttons
		final RelativeLayout postButtons = (RelativeLayout) findViewById(R.id.postButtons);
		
		//START LISTENER FOR NOTE EDITTEXT
		postNote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				postButtons.setBackgroundResource(R.color.blue);
			}
		});
		//END LISTENER FOR NOTE EDITTEXT
		
		//START LIST CONFIGURATION
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
				if (visibleItemCount > 0) {
					View firstView = view.getChildAt(0);
					if ((firstVisibleItem == 0) && (firstView.getTop() >= 0)) {
						postButtons.setVisibility(View.VISIBLE);
					} else {
						//slideToTop(postButtons);
						postButtons.setVisibility(View.GONE);
					}
				}
			}
		});
		list.setAdapter(adapter);
		//END LIST CONFIGURATION
		
		//START BUTTON LISTENERS		
		final ImageButton postButton = (ImageButton) findViewById(R.id.postButton);
		final ImageButton uploadButton = (ImageButton) findViewById(R.id.uploadButton);
		final ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraButton);

		postButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				noteText = postNote.getText().toString();
				Drop newDrop;
				if (chosenImage != null && !(noteText.equals(""))) {
					list = (ListView) findViewById(R.id.list);
					newDrop = new Drop(noteText, chosenImage);
				} else if (!(noteText.equals(""))) {
					newDrop = new Drop(noteText);		
				} else if (chosenImage != null && noteText.equals("")) {
					newDrop = new Drop(chosenImage);		
				} else {
					return;
				}
				
				drops.add(0, newDrop);
				adapter.notifyDataSetChanged();
					
				postNote.setText("");
				chosenImage = null;
				
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
//				
//				ContentValues values = new ContentValues();
//		            values.put(MediaStore.Images.Media.TITLE, "New Picture");
//		            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//		            imageUri = getContentResolver().insert(
//		                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//		            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//		            startActivityForResult(intent, 0);
			}
		});
		//END BUTTON LISTENERS		
	}
	
//	// To animate view slide out from bottom to top
//	public void slideToTop(View view){
//	TranslateAnimation animate = new TranslateAnimation(0,0,0,-view.getHeight());
//	animate.setDuration(500);
//	animate.setFillAfter(true);
//	view.startAnimation(animate);
//	view.setVisibility(View.GONE);
//	}
//	
//	// To animate view slide out from top to bottom
//	public void slideToBottom(View view){
//	TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
//	animate.setDuration(500);
//	animate.setFillAfter(true);
//	view.startAnimation(animate);
//	view.setVisibility(View.VISIBLE);
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feed, menu);
		return true;
	} 
	
	//Handles results for camera and image upload intents
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent buttonIntent) {
		super.onActivityResult(requestCode, resultCode, buttonIntent);
		switch (requestCode) {
		case 0:
			if (resultCode == Activity.RESULT_OK) {
		        chosenImage = (Bitmap) buttonIntent.getExtras().get("data");
                				
//				try {
//	                        Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
//	                                getContentResolver(), imageUri);
//	        				chosenImage = (Bitmap) thumbnail;
//	                      } catch (Exception e) {
//	                        e.printStackTrace();
//	                    }
//				        imm.hideSoftInputFromWindow(list.getWindowToken(), 0);
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

					BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
					optsDownSample.inSampleSize = 8;

					chosenImage = BitmapFactory.decodeFile(filePath, optsDownSample);
				}
			}
			break;
		}
		postImage.setImageBitmap(chosenImage);
		postImage.setVisibility(View.VISIBLE);
	}
	
}
