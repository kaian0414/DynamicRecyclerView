package com.kaianchan.staticrecyclerview;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static androidx.core.app.ActivityCompat.startActivityForResult;

/*
* RecyclerView.Adapter
* RecyclerView.ViewHolder
*/

public class VocabAdapter extends RecyclerView.Adapter<VocabAdapter.VocabViewHolder> implements Filterable {

    private Context vocabContext;
    private List<Vocabulary> vocabularyList; // From Project.java
    private List<Vocabulary> vocabularyListFull; // A copy

    private final int REQ_CODE_SPEECH_INPUT = 100;

    // Constructor
    VocabAdapter(MainActivity mainActivity, List<Vocabulary> vocabularyList) {
        this.vocabularyList = vocabularyList;
        vocabularyListFull = new ArrayList<>(vocabularyList);
    }

    @NonNull
    @Override
    public VocabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // (cardview.xml, null)
        View view = inflater.inflate(R.layout.vocab_cardview, parent, false);
        VocabViewHolder vocabViewHolder = new VocabViewHolder(view);
        return vocabViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VocabViewHolder holder, final int position) {
        // Get position
        Vocabulary vocabularyPos = vocabularyList.get(position);

        // 定義每格card view的text來自getTitle()
        holder.vPort.setText(vocabularyPos.getvPort());
        holder.vChin.setText(vocabularyPos.getvChin());
    }

    @Override
    public int getItemCount() {
        return vocabularyList.size();
    }



    public class VocabViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Elements in CardView
        private TextView vPort, vChin;
        private ImageView vMic;
        private RecyclerView vocabRecyclerView;

        public RelativeLayout vocabRelativeLayout;

        public VocabViewHolder(@NonNull View itemView) {
            super(itemView);

            vPort = (TextView) itemView.findViewById(R.id.vPort);
            vChin = (TextView) itemView.findViewById(R.id.vChin);
            vMic = (ImageView) itemView.findViewById(R.id.vMic);

            vocabRecyclerView = (RecyclerView) itemView.findViewById(R.id.vocab_recyclerView);
            vocabRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.vocab_relativeLayout);

            List<Vocabulary> vocabularyList;
            vocabularyList = new ArrayList<>();


            // 成功Toast, 但TextToSpeech不ok
//            vMic.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(v.getContext(), "vMic", Toast.LENGTH_SHORT).show();
//                }
//            });

            vMic.setOnClickListener(this);
        }

        // 僅限按ImageView
        @Override
        public void onClick(View v) {
            if (v instanceof ImageView) {
//                Toast.makeText(v.getContext(), "vMic: " + (getAdapterPosition() + 1), Toast.LENGTH_SHORT).show();
                startSpeech();
            }
        }
    }

    public void startSpeech() {
        Log.d("startSpeech", "Click the Mic"); // Successfully clicked
    }


    @Override
    public Filter getFilter() {
        return vocabularyFilter;
    }

    private Filter vocabularyFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Vocabulary> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(vocabularyListFull);
            } else {
                // something typed
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Vocabulary vocab : vocabularyListFull) {
                    if (vocab.getvPort().toLowerCase().contains(filterPattern)) {
                        filteredList.add(vocab);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            vocabularyList.clear(); // Remove every item here
            vocabularyList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
