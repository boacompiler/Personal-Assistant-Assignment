package com.example.robert.opennlptest;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextToSpeech tts;

     static SentenceDetector mySentenceDetector;
     static Tokenizer myTokenizer;
     static NameFinderME myNameFinderME;
     static POSTagger myPOSTagger;

    static{
//        mySentenceDetector = SetupSentenceDetector();
//        myTokenizer = SetupTokenizer();
//        myNameFinderME = SetupNameFinder();
//        myPOSTagger = SetupPOSTagger();
    }

    private static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
//        mySentenceDetector = SetupSentenceDetector();
//        myTokenizer = SetupTokenizer();
//        myNameFinderME = SetupNameFinder();


        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int i){
                //tts.speak("Why is this not working", TextToSpeech.QUEUE_FLUSH, null);
                System.out.print("this print statement needs to be here");
            }
        });
        tts.setLanguage(Locale.UK);

        //Say("process completed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonOnClick(View v)
    {
        EditText e = (EditText)findViewById(R.id.editText3);
        String query = e.getText().toString();
        ThreadedProcess(query);

    }

    public static SentenceDetector SetupSentenceDetector()
    {
        SentenceDetector _sentenceDetector = null;
        InputStream modelIn = null;
        try{
            modelIn = App.getContext().getResources().getAssets().open("en-sent.bin");
            final SentenceModel sentenceModel = new SentenceModel(modelIn);
            modelIn.close();

            _sentenceDetector = new SentenceDetectorME(sentenceModel);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException e) {} // oh well!
            }
        }
        return _sentenceDetector;
    }

    public static Tokenizer SetupTokenizer()
    {
        Tokenizer _tokenizer = null;
        InputStream modelIn = null;
        try{
            modelIn = App.getContext().getResources().getAssets().open("en-token.bin");
            final TokenizerModel tokenModel = new TokenizerModel(modelIn);
            modelIn.close();

            _tokenizer = new TokenizerME(tokenModel);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException e) {} // oh well!
            }
        }
        return _tokenizer;
    }

    public static NameFinderME SetupNameFinder()
    {
        NameFinderME nameFinder = null;
        InputStream modelIn = null;
        try{
            modelIn = App.getContext().getResources().getAssets().open("en-ner-person.bin");
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            nameFinder = new NameFinderME(model);
            modelIn.close();

        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException e) {} // oh well!
            }
        }
        return nameFinder;
    }

    public static POSTagger SetupPOSTagger()
    {
        POSTagger _posTagger = null;

        InputStream modelIn = null;

        try {
            // Loading tokenizer model
            modelIn = App.getContext().getResources().getAssets().open("en-pos-maxent.zip");
            final POSModel posModel = new POSModel(modelIn);
            _posTagger = new POSTaggerME(posModel);
            modelIn.close();

        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException e) {} // oh well!
            }
        }
        return _posTagger;
    }

    public String process(String request, SentenceDetector sd, Tokenizer t, NameFinderME nf, POSTagger pos)
    {
//my stuff

        //String para = "Hello computer! I am Robert, i attend Farnborough College of Technology";
        String para = request;

        SentenceDetector _sentenceDetector = sd;
        Tokenizer _tokenizer = t;
        NameFinderME nameFinder = nf;
        POSTagger _posTagger = pos;

        String out = "";

        // Loading sentence detection model
        // Loading tokenizer model

        //loading name finder

        String[] allTokens = _tokenizer.tokenize(para);

        Span nameSpans[] = nameFinder.find(allTokens);
        String[] sentences = _sentenceDetector.sentDetect(para);
        //String[] tokens = _tokenizer.tokenize(sentences[0]);
        String[] parts = _posTagger.tag(allTokens);
        //String test = _posTagger.toString();
        //display




        for(int i = 0;i<sentences.length;i++)
        {
            out += "\n" + sentences[i];
        }

        out += "\n";

        for(int i = 0;i< allTokens.length;i++)
        {
            out += "["+allTokens[i]+"]";
        }
        out += "\n";
        for(int i =0;i<parts.length;i++)
        {
            out+="["+parts[i]+"]";
        }
        out += "\n";

        for(Span s: nameSpans)
        {
            for(int i = s.getStart(); i< s.getEnd(); i++)
            {
                out += allTokens[i] + " ";
            }
            out += "\n";
        }
        out += "\n";

        return out;
    }

    public void ThreadedProcess(String q)
    {
        final String query = q;
        //final SentenceDetector sd = mySentenceDetector;
        TextView t = (TextView)findViewById(R.id.myTextView);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final String result = process(query, mySentenceDetector, myTokenizer, myNameFinderME, myPOSTagger);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ProgressBar p = (ProgressBar)findViewById(R.id.progressBar);
                        p.setVisibility(View.INVISIBLE);
                        TextView t = (TextView)findViewById(R.id.myTextView);
                        t.setText("test done" + result);
                    }
                });
            }
        };

        t.append("\nTEST");
        ProgressBar p = (ProgressBar)findViewById(R.id.progressBar);
        p.setVisibility(View.VISIBLE);
        new Thread(runnable).start();
    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText
            TextView t = (TextView)findViewById(R.id.myTextView);
            t.setText("test done " + spokenText);
            ThreadedProcess(spokenText);
            //Say("process completed");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void SpeechOnClick(View v)
    {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        tts.speak("Why is this not working", TextToSpeech.QUEUE_FLUSH, null);

//        TextView t = (TextView)findViewById(R.id.myTextView);
//        t.setText("start speaking");
//        displaySpeechRecognizer();
    }

//    public void Say(String text)
//    {
//        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
//    }
}


