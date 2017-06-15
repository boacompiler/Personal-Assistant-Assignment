package com.example.robert.opennlptest;

import android.text.TextUtils;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by robert on 17/05/17.
 * class that prepares a string for Expression.java, runs it, and returns the result.
 */

public class StringCalc {
    /**
     * method that calculates a plain text expression
     * @param sum the expression to calculate
     * @return the solution to the expression as a string
     */
    public String Calc(String sum)
    {
        Log.d("calc", "Calc: " + sum);
        String[] words = sum.split("\\s+");
        Log.d("calc", "Calc: " + Arrays.toString(words));
        boolean error = false;

        //checks for common words and replaces with correct symbols
        for(int i = 0; i<words.length;i++)
        {

            if(TextUtils.isDigitsOnly(words[i].replace(".","")))
            {
                //if the word is a number, do nothing
                //we replace the '.' first to allow decimal numbers to be checked
                Log.d("calc", "Calc: digits: "+ words[i]);
            }
            else if(words[i].equals("times")||words[i].equals("X")||words[i].equals("x")||words[i].equals("*"))
            {
                Log.d("calc", "Calc: *: "+ words[i]);
                words[i] = "*";

            }
            else if(words[i].equals("divide")||words[i].equals("divided")||words[i].equals("/")||words[i].equals("÷"))
            {
                Log.d("calc", "Calc: /: "+ words[i]);
                words[i] = "/";

            }
            else if(words[i].equals("plus")||words[i].equals("add")||words[i].equals("+"))
            {
                Log.d("calc", "Calc: +: "+ words[i]);
                words[i] = "+";

            }
            else if(words[i].equals("minus")||words[i].equals("subtract")||words[i].equals("-")||words[i].equals("takeaway")||words[i].equals("take"))
            {
                Log.d("calc", "Calc: -: "+ words[i]);
                words[i] = "-";

            }
            else if(words[i].equals("million"))
            {
                Log.d("calc", "Calc: 000000: "+ words[i]);
                words[i] = "000000";

            }
            else if(words[i].equals("billion"))
            {
                Log.d("calc", "Calc: 000000000: "+ words[i]);
                words[i] = "000000000";

            }
            else if(words[i].equals("away")||words[i].equals("by")||words[i].equals(" "))
            {
                //'divided by' and 'take away' are 2 words, we handle the first and throw away the second
                Log.d("calc", "Calc: waste: "+ words[i]);
                words[i] = "";
            }
            else
            {
                //if the word isn't recognised, the expression isn't solvable
                Log.d("calc", "Calc: error: "+ words[i]);
                error = true;
                break;
            }
            System.out.print("["+words[i]+"]");
            Log.d("calc", "Calc: " + error);
        }
        if(!error)
        {
            String sumformat = "";

            for(int i = 0;i<words.length;i++)
            {
                sumformat += words[i];
            }
            Log.d("calc", "Calc: " + sumformat);
            try {
                BigDecimal result = null;
                Expression expression = new Expression(sumformat);
                result = expression.eval();
                System.out.println(result);
                return result.toPlainString();
            }catch (Exception e){
                Log.d("calc", "Calc: ARITHMETIC ERROR");
                return "";
            }
        }
        else
        {
            Log.d("calc", "Calc: ERROR");
            return "";
        }
    }

}
