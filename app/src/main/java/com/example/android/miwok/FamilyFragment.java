package com.example.android.miwok;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FamilyFragment extends Fragment {
    private final ArrayList<Word> mWords = new ArrayList<Word>();
    private MediaPlayer mMediaPlayer;
    private WordAdapter mAdapter;
    private AudioManager mAudioManager;

    /**
     * This listener gets triggered when the {@link MediaPlayer} has completed
     * playing the audio file.
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the sound file has finished playing, release the media player resources.
            releaseMediaPlayer();
        }
    };

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        mMediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        mMediaPlayer.stop();
                        releaseMediaPlayer();
                    }
                }
            };

    public FamilyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);


        // Create and setup the {@link AudioManager} to request audio focus
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        populateWordList();

        // Create an {@link WordAdapter}, whose data source is a list of {@link Word}s. The
        // mAdapter knows how to create list items for each item in the list.
        mAdapter = new WordAdapter(getActivity(), mWords, R.color.category_family);
        createListView(rootView);


        return rootView;
    }
    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    private void createListView(View rootView) {
        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml layout file.
        ListView listView = (ListView) rootView.findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        if (listView != null) {
            listView.setAdapter(mAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Word currentWord = mWords.get(i);
                    if (currentWord.hasSound()) {
                        releaseMediaPlayer();
                        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                            // We have audio focus now.

                            // Create and setup the {@link MediaPlayer} for the audio resource associated
                            // with the current word
                            mMediaPlayer = MediaPlayer.create(getActivity(),
                                    currentWord.getmSoundResourceId());
                            // Start the audio file
                            mMediaPlayer.start();

                            // Setup a listener on the media player, so that we can stop and release the
                            // media player once the sound has finished playing.
                            mMediaPlayer.setOnCompletionListener(mCompletionListener);
                        }
                    }
                }
            });
        }
    }

    private void populateWordList() {

        // Create a list of words
        mWords.add(new Word("father", "әpә", R.drawable.family_father, R.raw.family_father));
        mWords.add(new Word("mother", "әṭa", R.drawable.family_mother, R.raw.family_mother));
        mWords.add(new Word("son", "angsi", R.drawable.family_son, R.raw.family_son));
        mWords.add(new Word("daughter", "tune", R.drawable.family_daughter, R.raw.family_daughter));
        mWords.add(new Word("older brother", "taachi", R.drawable.family_older_brother,
                R.raw.family_older_brother));
        mWords.add(new Word("younger brother", "chalitti", R.drawable.family_younger_brother,
                R.raw.family_younger_brother));
        mWords.add(new Word("older sister", "teṭe", R.drawable.family_older_sister,
                R.raw.family_older_sister));
        mWords.add(new Word("younger sister", "kolliti", R.drawable.family_younger_sister,
                R.raw.family_younger_sister));
        mWords.add(new Word("grandmother ", "ama", R.drawable.family_grandmother,
                R.raw.family_grandmother));
        mWords.add(new Word("grandfather", "paapa", R.drawable.family_grandfather,
                R.raw.family_grandfather));

    }
}
