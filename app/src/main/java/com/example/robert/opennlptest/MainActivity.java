package com.example.robert.opennlptest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        //my stuff

        String para = "Hello computer! I am Robert, i attend Farnborough College of Technology";

        SentenceDetector _sentenceDetector = null;
        Tokenizer _tokenizer = null;

        InputStream modelIn = null;
        InputStream modelInT = null;
        InputStream modelInN = null;
        try {
            // Loading sentence detection model
            modelIn = this.getAssets().open("en-sent.bin");
            final SentenceModel sentenceModel = new SentenceModel(modelIn);
            modelIn.close();

            _sentenceDetector = new SentenceDetectorME(sentenceModel);

            // Loading tokenizer model
            modelInT = this.getAssets().open("en-token.bin");
            final TokenizerModel tokenModel = new TokenizerModel(modelInT);
            modelInT.close();

            _tokenizer = new TokenizerME(tokenModel);

            //loading name finder
            modelInN = getAssets().open("en-ner-organization.bin");
            TokenNameFinderModel model = new TokenNameFinderModel(modelInN);
            NameFinderME nameFinder = new NameFinderME(model);

            String[] allTokens = _tokenizer.tokenize(para);

            Span nameSpans[] = nameFinder.find(allTokens);
            //display

            TextView t = (TextView)findViewById(R.id.myTextBox);
            t.append("\nTEST");

            String[] sentences = _sentenceDetector.sentDetect(para);

            for(int i = 0;i<sentences.length;i++)
            {
                t.append("\n" + sentences[i]);
            }

            t.append("\n");

            for(Span s: nameSpans)
            {
                for(int i = s.getStart(); i< s.getEnd(); i++)
                {
                    t.append(allTokens[i] + " ");
                }
                t.append("\n");
            }


        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException e) {} // oh well!
            }
        }
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
}
