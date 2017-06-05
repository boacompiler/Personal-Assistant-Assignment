package com.example.robert.opennlptest;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.content.Context;

import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import opennlp.tools.util.Span;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import java.net.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;

public class MainActivity extends AppCompatActivity {

    TextToSpeech tts;

    static Tokenizer questionTokenizer;
    private static Parser questionParser = null;

    static{
        questionTokenizer = SetupTokenizer();
        questionParser = SetupParser();
    }

    String previousTitle = "";
    int requestNo = 0;
    RandomMessage randomMessage = new RandomMessage();

    private static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int i){
                System.out.print("Start");
            }
        });
        tts.setLanguage(Locale.UK);

    }

    public static Tokenizer SetupTokenizer()
    {
        Tokenizer newTokenizer = null;
        InputStream inputStream = null;
        try{
            inputStream = App.getContext().getResources().getAssets().open("en-token.bin");
            final TokenizerModel tokenModel = new TokenizerModel(inputStream);
            inputStream.close();

            newTokenizer = new TokenizerME(tokenModel);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        return newTokenizer;
    }

    public static POSTagger SetupPOSTagger()
    {
        POSTagger newPosTagger = null;
        InputStream inputStream = null;
        try {
            inputStream = App.getContext().getResources().getAssets().open("en-pos-maxent.zip");
            final POSModel posModel = new POSModel(inputStream);
            newPosTagger = new POSTaggerME(posModel);
            inputStream.close();

        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        return newPosTagger;
    }

    private static final int SPEECH_REQUEST_CODE = 0;

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            String[] spokenwords = spokenText.split(" ", 2);

            if(spokenwords.length >= 2 && spokenwords[0].equals("calculate"))
            {
                StringCalc sc = new StringCalc();
                String ans = sc.Calc(spokenwords[1]);
                if(!ans.equals(""))
                {
                    tts.speak("The answer to "+spokenwords[1]+" is: " + ans, TextToSpeech.QUEUE_FLUSH, null);
                }
                else
                {
                    tts.speak("Sorry, I couldn't work this out", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
            else if(spokenText.equals("tell me more"))
            {
                if(!previousTitle.equals(""))
                {
                    String result = SearchWiki(previousTitle, GenerateProp());
                }
                else
                {
                    tts.speak("I haven't told you anything yet!", TextToSpeech.QUEUE_FLUSH, null);
                }

            }
            else if(spokenText.equals("tell me a joke"))
            {
                tts.speak(randomMessage.GetJoke(), TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                spokenText = spokenText  + ".";
                Parse myParse = parseSentence(spokenText, questionTokenizer);
                myParse.show();
                VoiceQuery vq = new VoiceQuery(myParse);
                if(!vq.IsError())
                {
                    Log.d("term", "onActivityResult: "+vq.GenerateTerm());
                    requestNo = 0;
                    String result = SearchWiki(vq.GenerateTerm(), GenerateProp());
                }
                else
                {
                    tts.speak("I'm a little confused by that, try phrasing it as a longer question or a more complete sentence", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String GenerateProp()
    {
        String prop = "";

        if(requestNo == 0)
        {
            prop = "&exsentences=1";
            requestNo++;
        }
        else if(requestNo == 1)
        {
            prop = "&exintro=1";
            requestNo = 0;
            //previousMessage = "";
        }
        return prop;
    }

    public void SpeechOnClick(View v)
    {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();
        displaySpeechRecognizer();
        Log.d("wiki", "SpeechOnClick: ");
    }

    public void ShutUpOnClick(View v)
    {
        //this flushes the current speech queue, halting current speech
        tts.speak("shut up", TextToSpeech.QUEUE_FLUSH, null);
    }

    public String SearchWiki(String title, String prop)
    {
        String result = "No Result";

        XMLGetter xg =(XMLGetter) new XMLGetter(new XMLGetter.AsyncResponse() {
            @Override
            public void processFinish(String title, Document output) {
                Log.d("wiki", "onPostExecute: done even more ");
                Log.d("wiki message", "processFinish:  started");
                Log.d("wiki message", "processFinish: " + (output == null));
                String message = "Sorry, somethings gone wrong";
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
                        message = randomMessage.GetNotSure() + strippedText;
                        previousTitle = output.getElementsByTagName("p").item(0).getAttributes().getNamedItem("title").getNodeValue();
                        //previousMessage = "";
                        Log.d("wiki", "processFinish: adds search title to previous");
                    }
                }
                else
                {
                    try {
                        message = output.getElementsByTagName("extract").item(0).getTextContent();
                        previousTitle = output.getElementsByTagName("page").item(0).getAttributes().getNamedItem("title").getNodeValue();
                        for (int i = 0; i < output.getElementsByTagName("cl").getLength(); i++) {
                            if (output.getElementsByTagName("cl").item(i).getAttributes().item(1).getTextContent().equals("Category:All article disambiguation pages")) {
                                message = "please be more specific, " + title + " is ambiguous.";
                                break;
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        message = "I'm sorry, I don't recognise that!";
                    }
                }

                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            }
        }).execute(title, prop);

        return result;
    }

    private static Parse parseSentence(final String text, Tokenizer tokenizer) {
        final Parse myParse = new Parse(text, new Span(0, text.length()), AbstractBottomUpParser.INC_NODE,1, 0);

        final Span[] spans = tokenizer.tokenizePos(text);

        for (int idx=0; idx < spans.length; idx++) {
            final Span span = spans[idx];
            myParse.insert(new Parse(text, span, AbstractBottomUpParser.TOK_NODE, 0, idx));
        }

        Parse newParse = parse(myParse);
        return newParse;
    }

    private static Parse parse(final Parse p) {
        return questionParser.parse(p);
    }

    private static Parser SetupParser() {
        if (questionParser == null) {
            InputStream inputStream = null;
            try {
                inputStream = App.getContext().getResources().getAssets().open("en-parser-chunking.bin");
                final ParserModel parseModel = new ParserModel(inputStream);
                inputStream.close();

                questionParser = ParserFactory.create(parseModel);
            } catch (final IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return questionParser;
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
        String prop = params[1];

        try {
            url = new URL("https://en.wikipedia.org/w/api.php?action=query&format=xml&prop=extracts%7Ccategories&titles="+urlformat+"&redirects=1"+prop+"&explaintext=1&list=search&utf8=1&srsearch="+urlformat);

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
        Log.d("wiki", "onPostExecute: done");
        delegate.processFinish(title, doc);
    }

}




