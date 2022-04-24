package com.example.melodimusic.Fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.example.melodimusic.Adapter.SongAdapter;
import com.example.melodimusic.Model.SongsList;
import com.example.melodimusic.R;
import com.example.melodimusic.Model.SongsList;

import java.util.ArrayList;


public class TabFragment extends ListFragment {

        private static ContentResolver contentResolver1;
        public ArrayList<SongsList> songsList;
    public ArrayList<String> pathList;
    public ArrayList<String> nameList;

        private ListView listView;
        private SongAdapter adapter;

        private createDataParse createDataParse;

        private ContentResolver contentResolver;

        public static Fragment getInstance(int position, ContentResolver mcontentResolver) {
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            TabFragment tabFragment = new TabFragment();
            tabFragment.setArguments(bundle);
            contentResolver1=mcontentResolver;
            return tabFragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParse = (createDataParse) context;
    }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_tab, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            //super.onViewCreated(view, savedInstanceState);
            listView = view.findViewById(R.id.list_playlist);

            contentResolver = contentResolver1;
            setContent();
        }

        public void  setContent(){
            songsList = new ArrayList<>();
            pathList = new ArrayList<>();
            nameList = new ArrayList<>();
            getMusic();
            adapter = new SongAdapter(getContext(), songsList);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    createDataParse.onDataPass(nameList.get(position), pathList.get(position));
                    createDataParse.fullSongList(songsList, position);
                    //Toast.makeText(getContext(), "You clicked :\n"+songsList.get(position), Toast.LENGTH_SHORT).show();
                }
            });
        }


    public void getMusic() {
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentLocation = songCursor.getString(songPath);
                pathList.add(currentLocation);
                nameList.add(currentTitle);
                songsList.add(new SongsList(songCursor.getString(songTitle), songCursor.getString(songArtist)));
            } while (songCursor.moveToNext());
        }
    }
    public interface createDataParse {
        public void onDataPass(String name, String path);
        public void fullSongList(ArrayList<SongsList> songList, int position);
    }

}

