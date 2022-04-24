package com.example.melodimusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.melodimusic.Adapter.ViewPagerAdapter;
import com.example.melodimusic.Fragments.TabFragment;
import com.example.melodimusic.Model.SongsList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, TabFragment.createDataParse {
    private DrawerLayout mDrawerLayout;
    private ImageButton imgBtnPlayPause, imgbtnReplay, imgBtnPrev, imgBtnNext;
    private FloatingActionButton refreshSongs;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SeekBar seekbarController;

    private TextView tvCurrentTime, tvTotalTime;

    private ArrayList<SongsList> songList;
    private int currentPosition;
    private boolean checkFlag = false, repeatFlag = false;
    private final int MY_PERMISSION_REQUEST = 100;


    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        grantedPermission();
    }

    /**
     *  Initialising the views
     */

    private void init() {
        imgBtnPrev = findViewById(R.id.img_btn_previous);
        imgBtnNext = findViewById(R.id.img_btn_next);
        imgbtnReplay = findViewById(R.id.img_btn_replay);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        refreshSongs = findViewById(R.id.btn_refresh);
        seekbarController = findViewById(R.id.seekbar_controller);
        viewPager = findViewById(R.id.songs_viewpager);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        imgBtnPlayPause = findViewById(R.id.img_btn_play);
        Toolbar toolbar = findViewById(R.id.toolbar);
        handler = new Handler();
        mediaPlayer = new MediaPlayer();

        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);

        imgBtnNext.setOnClickListener(this);
        imgBtnPrev.setOnClickListener(this);
        imgbtnReplay.setOnClickListener(this);

        refreshSongs.setOnClickListener(this);

        imgBtnPlayPause.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_about:
                        about();
                        break;
                }
                return true;
            }
        });
    }


    /**
     *  Function to ask user to grant the permission.
     */

    private void grantedPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);

            }  else {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar snackbar = Snackbar.make(mDrawerLayout, "Provide the Storage Permission", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }


        } else {
            setPagerLayout();
        }
    }

    /**
     * Checking if the persmission is granted or not.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        setPagerLayout();
                    } else {
                        Snackbar snackbar = Snackbar.make(mDrawerLayout, "Provide the StoragePermission", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        finish();
                    }
                }
        }
    }

    /**
     * Setting up the tab layout with the viewpager in it.
     */

    private void setPagerLayout() {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getContentResolver());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout = findViewById(R.id.tabs);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

        }

    /**
     * Function to show the dialog for about us.
     */
        private void about () {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.about))
                    .setMessage(getString(R.string.about_text))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.action_bar_menu, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected (@NonNull MenuItem item){
            switch (item.getItemId()) {
                case android.R.id.home:
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    return true;
                case R.id.menu_search:
                    Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.menu_favorites:
                    Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

    /**
     * Function to handle the click events.
     *
     * @param v
     */
        @Override
        public void onClick (View v){
            switch (v.getId()) {
                case R.id.img_btn_play:
                    if(checkFlag) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            imgBtnPlayPause.setImageResource(R.drawable.play_icon);
                        } else if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            imgBtnPlayPause.setImageResource(R.drawable.pause_icon);
                            playCycle();
                        }

                    }
                    break;
                case R.id.btn_refresh:
                    Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
                    setPagerLayout();
                    break;
                case R.id.img_btn_replay:
                    if (repeatFlag) {
                        Toast.makeText(this, "Looping Removed..", Toast.LENGTH_SHORT).show();
                        mediaPlayer.setLooping(false);
                        repeatFlag = false;
                    } else {
                        Toast.makeText(this, "Looping Added..", Toast.LENGTH_SHORT).show();
                        mediaPlayer.setLooping(true);
                        repeatFlag = true;
                    }
                    break;

                case R.id.img_btn_previous:
                    if (currentPosition - 1 > -1) {
                        attachMusic(songList.get(currentPosition - 1).getTitle(), songList.get(currentPosition - 1).getPath());
                        currentPosition = currentPosition - 1;
                    } else {
                        attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                    }
                    break;
                case R.id.img_btn_next:
                    if (currentPosition + 1 < songList.size()) {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getPath());
                        currentPosition += 1;
                    } else {
                        Toast.makeText(this, "Playlist Ended", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }





    /**
     * Function to attach the song to the music player
     *
     * @param name
     * @param path
     */
    private void attachMusic(String name, String path) {
        imgBtnPlayPause.setImageResource(R.drawable.play_icon);
        setTitle(name);
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            setControls();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgBtnPlayPause.setImageResource(R.drawable.play_icon);
            }
        });
    }

    /**
     * Function to set the controls according to the song
     */

    private void setControls() {

        seekbarController.setMax(mediaPlayer.getDuration());

        mediaPlayer.start();
        playCycle();
        checkFlag = true;
        if (mediaPlayer.isPlaying()) {
            imgBtnPlayPause.setImageResource(R.drawable.pause_icon);
            tvTotalTime.setText(getTimeFormatted(mediaPlayer.getDuration()));
        }

        seekbarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(getTimeFormatted(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Function to play the song using a thread
     */
    private void playCycle() {
        seekbarController.setProgress(mediaPlayer.getCurrentPosition());
        tvCurrentTime.setText(getTimeFormatted(mediaPlayer.getCurrentPosition()));

        if (mediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    seekbarController.setProgress(mediaPlayer.getCurrentPosition());
                    playCycle();

                }
            };
            handler.postDelayed(runnable, 100);
        }
    }
    private String getTimeFormatted(long milliSeconds) {
        String finalTimerString = "";
        String secondsString = "";

        //Converting total duration into time
        int hours = (int) (milliSeconds / 3600000);
        int minutes = (int) (milliSeconds % 3600000) / 60000;
        int seconds = (int) ((milliSeconds % 3600000) % 60000 / 1000);

        // Adding hours if any
        if (hours > 0)
            finalTimerString = hours + ":";

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // Return timer String;
        return finalTimerString;
    }


    /**
     * Function Overrided to receive the data from the fragment
     *
     * @param name
     * @param path
     */
    @Override
    public void onDataPass(String name, String path) {
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();
        attachMusic(name, path);
    }

    @Override
    public void fullSongList(ArrayList<SongsList> songList, int position) {
        this.songList = songList;
        this.currentPosition = position;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
    }
    }



