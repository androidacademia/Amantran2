package amantran.in.lavit.amantran;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewInvitationActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_CAMRA_CODE = 1;
    private static final int MY_PERMISSION_VIDEO_CODE = 2;
    private static final int MY_PERMISSION_SD_CODE = 3;
    private SimpleExoPlayerView playerView;

    private ArrayList<Contact> finalContactList;
    private RecyclerView recyclerViewNew;
    private RecyclerView.LayoutManager layoutManager;
    private NewListAdapter adapter;
    private CircleImageView circleImageInvite1;
    private LinearLayout linearLayoutNewInv;
    private SimpleExoPlayer player;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invitation);

        circleImageInvite1 = findViewById(R.id.circleImageInvite1);
        playerView = findViewById(R.id.video_view);
        playerView.requestFocus();
        //initializePlayer();


        linearLayoutNewInv = findViewById(R.id.linearLayoutNewInv);

        recyclerViewNew = findViewById(R.id.recyclerViewNewInv);
        layoutManager = new GridLayoutManager(this,4);
        //layoutManager = new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.HORIZONTAL);
        //layoutManager = new StaggeredGridLayoutManager()
        recyclerViewNew.setLayoutManager(layoutManager);
        recyclerViewNew.setHasFixedSize(true);
        Bundle bundle = getIntent().getExtras();
        finalContactList = (ArrayList<Contact>) bundle.get("finalList");

        adapter = new NewListAdapter(this,finalContactList);
        recyclerViewNew.setAdapter(adapter);
        //Add Listener for circleImageInvite1 and circleImageInvite2
        circleImageInvite1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(NewInvitationActivity.this
                        ,v, Gravity.BOTTOM);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_new_camera,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        uploadImage(item,circleImageInvite1);
                        return false;
                    }
                });
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializePlayer(Uri playUri) {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);

        //player.setPlayWhenReady(playWhenReady);
        //player.seekTo(currentWindow, playbackPosition);
        //Uri uri = Uri.parse("http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4");
        MediaSource mediaSource = buildMediaSource(playUri);
        player.prepare(mediaSource, true, false);

    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }


    private void uploadImage(MenuItem item, CircleImageView circleImageView) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.action_choose_photo:
                addImageWritePermission(this,MY_PERMISSION_SD_CODE);
                break;
            case R.id.action_take_photo:
                addImageViewPermission(this,MY_PERMISSION_CAMRA_CODE);
                break;
            case R.id.action_cancel:
                break;
        }
    }

    public void addImageWritePermission(Context context, final int MY_PERMISSION_SD_CODE){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (NewInvitationActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder mBuild = new AlertDialog.Builder(this);
                mBuild.setTitle("Invitation Image");
                mBuild.setMessage("Permission required");
                mBuild.setPositiveButton(R.string.access_sdcard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(NewInvitationActivity.this
                                ,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSION_SD_CODE);
                    }
                });
                AlertDialog dialog = mBuild.create();
                dialog.show();
            }else {
                ActivityCompat.requestPermissions(NewInvitationActivity.this
                        ,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_SD_CODE);
            }
        }else {
            actionForImageAccess(MY_PERMISSION_SD_CODE);
        }

    }

    private void actionForImageAccess(int my_permission_sd_code) {
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, MY_PERMISSION_SD_CODE);
        }
    }

    private void addImageViewPermission(Context context, final int MY_PERMISSION_CAMRA_CODE) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            //Permission not granted
            //should we show the explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (NewInvitationActivity.this,Manifest.permission.CAMERA)){
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder mBuild = new AlertDialog.Builder(this);
                mBuild.setTitle("Invitation Image");
                mBuild.setMessage("Permission required");
                mBuild.setPositiveButton(R.string.access_camera, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(NewInvitationActivity.this
                                ,new String[]{Manifest.permission.CAMERA},MY_PERMISSION_CAMRA_CODE);
                    }
                });
                AlertDialog dialog = mBuild.create();
                dialog.show();
            }else {
                ActivityCompat.requestPermissions(NewInvitationActivity.this
                        ,new String[]{Manifest.permission.CAMERA},MY_PERMISSION_CAMRA_CODE);
            }
        }else {
            actionForVideoOrImage(MY_PERMISSION_CAMRA_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_CAMRA_CODE:
                //If request is cancelled, the result array is empty
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission was granted,yay! Do the
                    //Camera related task
                    actionForVideoOrImage(requestCode);

                }else {
                    //Permission Denied , Boo! Disable the
                    //funtionality the depends this permission
                    Snackbar snackbar = Snackbar.make(linearLayoutNewInv, R.string.camera_permission_denied,Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case MY_PERMISSION_SD_CODE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission was granted,yay! Do the
                    //Camera related task
                    actionForImageAccess(requestCode);

                }else {
                    //Permission Denied , Boo! Disable the
                    //funtionality the depends this permission
                    Snackbar snackbar = Snackbar.make(linearLayoutNewInv, R.string.image_permission_denied,Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case MY_PERMISSION_CAMRA_CODE:
                if (resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    circleImageInvite1.setImageBitmap(imageBitmap);
                }
                break;
            case MY_PERMISSION_SD_CODE:
                if (resultCode == RESULT_OK){
                    Uri targetUri = data.getData();
                    Log.i("NEWINVI",targetUri.toString());
                    Bitmap bitmap;
                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),targetUri);
                        circleImageInvite1.setImageBitmap(bitmap);

                    }catch (FileNotFoundException e){
                        Snackbar.make(linearLayoutNewInv,R.string.image_not_found,Snackbar.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Snackbar.make(linearLayoutNewInv,R.string.image_not_found,Snackbar.LENGTH_LONG).show();
                    }
                }
                break;
            case MY_PERMISSION_VIDEO_CODE:
               // if (resultCode == RESULT_OK){
               //     Uri videoUri = data.getData();
                    //mVideoView.setVideoURI(videoUri);
               // }
                break;

        }
    }

    private void actionForVideoOrImage(int requestCode) {
        switch (requestCode){
            case MY_PERMISSION_CAMRA_CODE:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, MY_PERMISSION_CAMRA_CODE);
                }
                break;
            case MY_PERMISSION_VIDEO_CODE:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, MY_PERMISSION_VIDEO_CODE);
                }

                break;
        }
    }

}
