package com.gethotdrop.android;

import java.util.List;

import com.gethotdrop.hotdrop.R;
import com.gethotdrop.service.DropStore;
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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
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

public class FeedActivity extends Activity {
	public static DropAdapter adapter;
	ListView list;
	static List<Drop> drops;

	String noteText = null;
	FitToWidthView postImage = null;
	Bitmap chosenImage = null;
	EditText postNote = null;
	Uri imageUri;
	InputMethodManager imm;
	RelativeLayout postButtons;
	private static Context c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		c = this;

		//Get drops and start service
		startService(new Intent(this, SyncService.class));
		DropStore dStore = DropStore.initialize(this);
		drops = dStore.getDropList();
		Log.e("Drops", "Number of is " + drops.size());

		//Create DropAdapter / ListView
		//TODO: think about lifecycle of adapter and the app
		adapter = new DropAdapter(this, R.layout.listcard, drops);
		list = (ListView) findViewById(R.id.list);
		
		//Create view for image / edittext for note
		postImage = (FitToWidthView) findViewById(R.id.postImage);
		postNote = (EditText) findViewById(R.id.postNote);
		
		//Set-up listener for what is selected
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		//Create layout that wraps buttons buttons
		postButtons = (RelativeLayout) findViewById(R.id.postButtons);
		
		//TODO: think about listeners combining
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
				//postButtons.setBackgroundResource(R.color.grayLight);
				Intent intent = new Intent(getBaseContext(), DropActivity.class);
				intent.putExtra("index", arg3);
				startActivity(intent);
			}
		});
		list.setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);
		
		list.setOnScrollListener(new OnScrollListener() {
			boolean atTop = true;
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				imm.hideSoftInputFromWindow(list.getWindowToken(), 0);
				//postButtons.setBackgroundResource(R.color.grayLight);
			}
 
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (visibleItemCount > 0) {
					View firstView = view.getChildAt(0);
					if ((firstVisibleItem == 0) && (firstView.getTop() >= 0) && !atTop) {
					//	atTop = true;
					//	slideDown(postButtons);
					} else if (firstView.getTop() < 0){
					//	atTop = false;
					//	slideUp(postButtons);
					} 
				}
			}
		});
		Log.e("list count", "items" + adapter.getCount());
		list.setAdapter(adapter); // TODO: ADAPTER SET AT END OF LISTENER
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
				
				/*Log.e("newdrop is", newDrop.message);
				DropStore.addOutgoing(newDrop);
				Intent i = new Intent(c, Worker.class);
				i.putExtra("action", 2);
                c.startService(i);*/
				drops.add(newDrop);
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

                 //Code snippet for better quality images				
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
	
	// To animate view slide out from bottom to top
	public void slideUp(View view){
	TranslateAnimation animate = new TranslateAnimation(0,0,0,-view.getHeight());
	animate.setDuration(500);
	animate.setAnimationListener( new AnimationListener() {         
		public void onAnimationEnd(Animation arg0) {
			postButtons.bringToFront();
			postButtons.setVisibility(View.GONE);}
		public void onAnimationRepeat(Animation arg0) {}
		public void onAnimationStart(Animation arg0) {}
	});
	findViewById(R.id.postNote).bringToFront();
	view.startAnimation(animate);
	}
	
	// To animate view slide out from top to bottom
	public void slideDown(View view){
	findViewById(R.id.postButtons).bringToFront();
	TranslateAnimation animate = new TranslateAnimation(0,0,-view.getHeight(),0);
	animate.setDuration(500);
	animate.setAnimationListener( new AnimationListener() {         
		public void onAnimationEnd(Animation arg0) {}
		public void onAnimationRepeat(Animation arg0) {}
		public void onAnimationStart(Animation arg0) {postButtons.setVisibility(View.VISIBLE);}
	});
	view.startAnimation(animate);
	} //TODO: think about creating separate animations file
	
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
	public static void updateDrops() {
		if (adapter != null)
		adapter.notifyDataSetChanged();
		Log.d("adapter", "refresh");
	}
	
}
