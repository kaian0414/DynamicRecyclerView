/*
StaticRecyclerView
https://youtu.be/a4o9zFfyIM4
SearchView
https://youtu.be/sJ-Z9G0SDhc
*/

package com.kaianchan.staticrecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.widget.SearchView;

import android.view.View.OnClickListener;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String URL_VOCABULARY = "http://ian14.online/fyp_php/getVocab.php";

    RecyclerView vocabRecyclerView;
    VocabAdapter vocabAdapter;

    List<Vocabulary> vocabularyList;

    TextToSpeech textToSpeech;
    ImageView vMic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vMic = (ImageView) findViewById(R.id.vMic);

        vocabRecyclerView = (RecyclerView) findViewById(R.id.vocab_recyclerView);
        vocabRecyclerView.setHasFixedSize(true); // Fixed size, MUST SET!
        vocabRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        vocabularyList = new ArrayList<>();

        /*
        // Add data
        vocabularyList.add(new Vocabulary(R.drawable.ch1, "Chapter 1"));
        vocabularyList.add(new Vocabulary(R.drawable.ch2, "Chapter 2"));
        vocabularyList.add(new Vocabulary(R.drawable.ch3, "Chapter 3"));
        */

        loadVocab();

        vocabAdapter = new VocabAdapter(MainActivity.this, vocabularyList);
        vocabRecyclerView.setAdapter(vocabAdapter);

        // Text To Speech
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // If no error
                if (status != TextToSpeech.ERROR) {
                    // set language as Portuguese
                    textToSpeech.setLanguage(new Locale("pt"));

                    // set  language as Cantonese
                    // textToSpeech.setLanguage(new Locale("yue"));
                } else {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        vPlay.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    // Dynamic recyclerView
    public void loadVocab() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URL_VOCABULARY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray chapArray = new JSONArray(response);

                            for (int i = 0; i < chapArray.length(); i++) {
                                JSONObject chapObj = chapArray.getJSONObject(i); // index[i]

                                String vocabPort = chapObj.getString("vocabPort");
                                String vocabChin = chapObj.getString("vocabChin");

                                // Chinese, Portuguese
                                Vocabulary vocabulary = new Vocabulary(vocabChin, vocabPort); // Add into chapterList
                                vocabularyList.add(vocabulary);
                            }

                            vocabAdapter = new VocabAdapter(MainActivity.this, vocabularyList);
                            vocabRecyclerView.setAdapter(vocabAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // RequestQueue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Onclick CardView
    // vocab_cardview.xml中, 設定了android:onClick="vocabOnclick"
    public void vocabOnclick (View view) {
        int vocabPos = vocabRecyclerView.getChildAdapterPosition(view);

        // 顯示按下的position+1
        // Toast.makeText(MainActivity.this, "MainActiviity onClick " + (vocabRecyclerView.getChildAdapterPosition(view) + 1), Toast.LENGTH_SHORT).show();
        // 顯示按下的vPort
        Toast.makeText(MainActivity.this, vocabularyList.get(vocabPos).getvPort().trim(), Toast.LENGTH_SHORT).show();

        // get text from edit text
        String toSpeak = vocabularyList.get(vocabPos).getvPort().trim();

        // Speak the vPort
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    // Search
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chap_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                vocabAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}
