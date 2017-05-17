package com.example.robert.opennlptest;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;

import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.parser.Parser;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import java.io.*;
import java.net.*;
import java.util.Queue;

import org.w3c.dom.*;
import javax.xml.parsers.*;

public class MainActivity extends AppCompatActivity {

    TextToSpeech tts;

//     static SentenceDetector mySentenceDetector;
     static Tokenizer myTokenizer;
//     static NameFinderME myNameFinderME;
//     static POSTagger myPOSTagger;

    static{
//        mySentenceDetector = SetupSentenceDetector();
        myTokenizer = SetupTokenizer();
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
                final String result = "lel";//process(query, mySentenceDetector, myTokenizer, myNameFinderME, myPOSTagger);
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
            String spokenText = results.get(0) + ".";
            // Do something with spokenText
            TextView t = (TextView)findViewById(R.id.myTextView);
            t.setText("test done " + spokenText);



            Parse myParse = parseSentence(spokenText, myTokenizer);
            myParse.show();
            String term = TreeTraverse(myParse);
            Log.d("term", "onActivityResult: "+term);
            String result = SearchWiki(term);//TODO what i going on with this return?

            //tts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
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

        //tts.speak("Why is this not working", TextToSpeech.QUEUE_FLUSH, null);

        TextView t = (TextView)findViewById(R.id.myTextView);
        t.setText("start speaking");
        displaySpeechRecognizer();
        //String result = SearchWiki("Donald+Trump");
        Log.d("wiki", "SpeechOnClick: ");
        //tts.speak(result, TextToSpeech.QUEUE_FLUSH, null);
    }

    public String SearchWiki(String title)
    {
        //String url = "https://en.wikipedia.org/w/api.php?action=query&format=xml&prop=extracts%7Ccategories&titles=LEGO&redirects=1&exsentences=1&explaintext=1";
        String result = "No Result";

        XMLGetter xg =(XMLGetter) new XMLGetter(new XMLGetter.AsyncResponse() {
            @Override
            public void processFinish(String title, Document output) {
                Log.d("wiki", "onPostExecute: done even more ");
                Log.d("wiki message", "processFinish:  started");
                Log.d("wiki message", "processFinish: " + (output == null));
                //Log.d("wiki message", "processFinish:  missing: "+ output.getElementsByTagName("page").item(0).getAttributes().item(3).getNodeName());
                String message = "Sorry, somethings gone wrong";
                //Log.d("message none", "processFinish: "+output.getElementsByTagName("page").item(0).getAttributes().item(1).toString());
                if(output == null)
                {
                    message = "I can't seem to access my data right now, try checking the network.";

                }
                else if(output.getElementsByTagName("page").item(0).getAttributes().item(3).getNodeName().equals("missing"))
                {
                    message = "I can't find anything on "+title;
                    if(output.getElementsByTagName("p").getLength() > 0)
                    {
                        String snip = output.getElementsByTagName("p").item(0).getAttributes().getNamedItem("snippet").getNodeValue();
                        String strippedText = snip.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
                        message = "I'm not sure, but this could be it: "+ strippedText;
                    }
                }
                else
                {
                    Log.d("wiki message", "processFinish:  message: "+output.getElementsByTagName("extract").item(0).getTextContent());
                    Log.d("wiki message", "processFinish:  ambiguous: " + output.getElementsByTagName("cl").getLength());
                    Log.d("wiki message", "processFinish:  ambiguous: " + output.getElementsByTagName("cl").item(0).getAttributes().getLength());
                    Log.d("wiki message", "processFinish:  ambiguous: " + output.getElementsByTagName("cl").item(0).getAttributes().item(0).getNodeValue());
                    message = output.getElementsByTagName("extract").item(0).getTextContent();
                    for(int i=0;i < output.getElementsByTagName("cl").getLength();i++)
                    {
                        if(output.getElementsByTagName("cl").item(i).getAttributes().item(1).getTextContent().equals("Category:All article disambiguation pages"))
                        {
                            message = "please be more specific, "+title+" is ambiguous." ;
                            break;
                        }
                    }
                    //Log.d("message ambiguous", "processFinish: "+output.getElementsByTagName("cl").item(0).getAttributes().item(1).getTextContent());
                }

                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            }
        }).execute(title);

        return result;
    }

    private static Parse parseSentence(final String text, Tokenizer _tokenizer) {
        final Parse p = new Parse(text,
                // a new span covering the entire text
                new Span(0, text.length()),
                // the label for the top if an incomplete node
                AbstractBottomUpParser.INC_NODE,
                // the probability of this parse...uhhh...?
                1,
                // the token index of the head of this parse
                0);

        // make sure to initialize the _tokenizer correctly
        final Span[] spans = _tokenizer.tokenizePos(text);

        for (int idx=0; idx < spans.length; idx++) {
            final Span span = spans[idx];
            // flesh out the parse with individual token sub-parses
            p.insert(new Parse(text,
                    span,
                    AbstractBottomUpParser.TOK_NODE,
                    0,
                    idx));
        }

        Parse actualParse = parse(p);
        return actualParse;
    }

    private static Parser _parser = null;

    private static Parse parse(final Parse p) {
        // lazy initializer
        if (_parser == null) {
            InputStream modelInP = null;
            try {
                // Loading the parser model
                modelInP = App.getContext().getResources().getAssets().open("en-parser-chunking.bin");
                final ParserModel parseModel = new ParserModel(modelInP);
                modelInP.close();

                _parser = ParserFactory.create(parseModel);
            } catch (final IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (modelInP != null) {
                    try {
                        modelInP.close();
                    } catch (final IOException e) {} // oh well!
                }
            }
        }
        return _parser.parse(p);
    }

    static String TreeTraverse(Parse p)
    {
        try {


            List<Parse> questions = new ArrayList<Parse>();
            List<Parse> wh = new ArrayList<Parse>();
            List<Parse> verbphrases = new ArrayList<Parse>();
            List<Parse> nounphrases = new ArrayList<Parse>();
            Parse primaryNP = null;

            Queue q = new LinkedList();
            q.add(p);
            //Traverse looking for basic chunks
            while (!q.isEmpty()) {
                Parse parse = (Parse) q.remove();
                for (int i = 0; i < parse.getChildCount(); i++) {
                    Parse child = parse.getChildren()[i];
                    System.out.println(child.toString() + " " + child.getType());
                    if (child.getType().equals("SQ") || child.getType().equals("SBARQ") || child.getType().equals("SBAR")) {
                        questions.add(child);
                    }
                    if (child.getType().equals("WRB") || child.getType().equals("WP")) {
                        wh.add(child);
                    }
                    if (child.getParent().getType().equals("VP") && (child.getType().equals("VB") || child.getType().equals("VBD") || child.getType().equals("VBN"))) {
                        verbphrases.add(child);
                    }
                    if (child.getType().equals("NP")) {
                        boolean rootNP = true;
                        for (int j = 0; j < child.getChildCount(); j++) {
                            if (child.getChildren()[j].getType().equals("NP")) {
                                rootNP = false;
                            }
                        }
                        if (rootNP) {
                            System.out.println("Adding np: " + child.toString());
                            nounphrases.add(child);
                        }

                    }
                    q.add(child);
                }
                System.out.println();
            }
            //Traverse final question chunk looking for most significant noun phrase
            Queue questionq = new LinkedList();
            if (questions.size() > 0) {
                questionq.add(questions.get(0));
            } else {
                questionq.add(p);
            }

            List<Parse> np = new ArrayList<Parse>();

            while (!questionq.isEmpty()) {
                Parse parse = (Parse) questionq.remove();
                for (int i = 0; i < parse.getChildCount(); i++) {
                    Parse child = parse.getChildren()[i];
                    System.out.println("questions: " + child.toString() + " " + child.getType());
                    if (child.getType().equals("NP")) {
                        boolean rootNP = true;
                        for (int j = 0; j < child.getChildCount(); j++) {
                            if (child.getChildren()[j].getType().equals("NP")) {
                                rootNP = false;
                            }
                        }
                        if (rootNP) {
                            System.out.println("Adding np: " + child.toString());
                            np.add(child);
                        }

                    }
                    questionq.add(child);
                }
            }
            if (!np.isEmpty()) {
                if (np.get(0).getChildren()[0].getType().equals("PRP") || np.get(0).getChildren()[0].getType().equals("PRP$")) {
                    primaryNP = nounphrases.get(0);
                } else {
                    primaryNP = np.get(0);
                    nounphrases.removeAll(Collections.singleton(primaryNP));
                }
            }


            for (int i = 0; i < questions.size(); i++) {
                System.out.println(questions.get(i));
            }
            for (int i = 0; i < wh.size(); i++) {
                System.out.println(wh.get(i));
            }
            for (int i = 0; i < verbphrases.size(); i++) {
                System.out.println(verbphrases.get(i));
            }
            for (int i = 0; i < nounphrases.size(); i++) {
                if (nounphrases.get(i).getChildren()[0].getType().equals("DT")) {
                    nounphrases.get(i).remove(0);
                }
                System.out.println(nounphrases.get(i));
            }
            if (primaryNP != null && primaryNP.getChildren()[0].getType().equals("DT")) {
                primaryNP.remove(0);
            }
            //System.out.println("primary term: "+.toString());
            return primaryNP.toString();//TODO return a class of stuff
        }
        catch (Exception e)
        {
            Log.d("parsing", "TreeTraverse: " + e.getMessage());
            return "error";
        }
    }

}

class XMLGetter extends AsyncTask<String,Integer,Void> {

    Document doc;
    String title;

    public interface AsyncResponse {
        void processFinish(String title, Document output);
    }

    public AsyncResponse delegate = null;

    public XMLGetter(AsyncResponse delegate)
    {
        this.delegate = delegate;
    }

    protected Void doInBackground(String...params){
        title = params[0];
        String urlformat = title.replace(" ","+");
        URL url;

        try {
            url = new URL("https://en.wikipedia.org/w/api.php?action=query&format=xml&prop=extracts%7Ccategories&titles="+urlformat+"&redirects=1&exsentences=1&explaintext=1&list=search&utf8=1&srsearch="+urlformat);
            //HttpURLConnection con=(HttpURLConnection)url.openConnection();
            //InputStream is=con.getInputStream();
            //text = is;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            doc = dbFactory.newDocumentBuilder().parse(url.openStream());
            Log.d("wiki", "onPostExecute: doing");
        }catch (Exception e) {
            Log.d("wiki", "onPostExecute: " + e.getMessage());
        }

        return null;
    }

    protected void onPostExecute(Void result){
        //use "text"
        Log.d("wiki", "onPostExecute: done");
        delegate.processFinish(title, doc);
    }

}




