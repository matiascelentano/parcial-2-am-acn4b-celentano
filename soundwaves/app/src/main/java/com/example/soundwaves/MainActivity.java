package com.example.soundwaves;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.soundwaves.adapter.AlbumAdapter;
import com.example.soundwaves.adapter.ArtistAdapter;
import com.example.soundwaves.adapter.PlaylistAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PlaylistAdapterItemOnClickListener {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getTopArtists();
        initMainActivityRecyclerViewers();
    }
    private void initMainActivityRecyclerViewers(){
        //RecyclerView Seccion Playlists
        RecyclerView playlistSectionRecyclerView = findViewById(R.id.playlistRecyclerView);
        playlistSectionRecyclerView.setLayoutManager(new GridLayoutManager(this, 2,GridLayoutManager.HORIZONTAL,false));
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(Playlists.playlists, this);
        playlistSectionRecyclerView.setAdapter(playlistAdapter);

        //RecyclerView Seccion Artistas


        //RecyclerView Seccion Album
        RecyclerView albumSectionRecyclerView = findViewById(R.id.albumRecyclerView);
        albumSectionRecyclerView.setLayoutManager(new GridLayoutManager(this, 2,GridLayoutManager.HORIZONTAL,false));
        AlbumAdapter albumAdapter = new AlbumAdapter(Albums.getAlbums());
        albumSectionRecyclerView.setAdapter(albumAdapter);
    }

    @Override
    public void onItemClickListener(Playlist playlist, int position) {
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra("id",playlist.getId());
        startActivity(intent);
    }
    public void getTopArtists(){

        String urlTopArtists = "https://api.deezer.com/chart/0/artists";
        StringRequest request = new StringRequest(Request.Method.GET, urlTopArtists, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<Artist> topArtists = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("data");
                    for (int i = 0; i < result.length(); i++){
                        JSONObject item = result.getJSONObject(i);
                        Log.i("artista","" + item.getInt("id") + " " + item.getString("name") + " " + item.getString("picture_big"));
                        topArtists.add(new Artist(item.getInt("id"),item.getString("name"),item.getString("picture_big")));
                    }

                    RecyclerView artistSectionRecycleView = findViewById(R.id.artistsRecyclerView);
                    artistSectionRecycleView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    ArtistAdapter artistAdapter = new ArtistAdapter(topArtists, MainActivity.this);
                    artistSectionRecycleView.setAdapter(artistAdapter);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.getMessage());
            }
        });
        Volley.newRequestQueue(this).add(request);
    }
}