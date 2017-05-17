package com.example.robert.opennlptest;

import android.text.TextUtils;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by robert on 17/05/17.
 */

public class StringCalc {

    public String Calc(String sum)
    {
        Log.d("calc", "Calc: " + sum);
        String[] words = sum.split("\\s+");
        Log.d("calc", "Calc: " + Arrays.toString(words));
        boolean error = false;
//		float total = 0;
//		float operand = 0;
        for(int i = 0; i<words.length;i++)
        {

            if(TextUtils.isDigitsOnly(words[i].replace(".","")))
            {
                //System.out.print("n");
                //operand = Float.parseFloat(words[i]);
                Log.d("calc", "Calc: digits: "+ words[i]);
            }
            else if(words[i].equals("times")||words[i].equals("X")||words[i].equals("x")||words[i].equals("*"))
            {
                Log.d("calc", "Calc: *: "+ words[i]);
                words[i] = "*";
                //total = total * operand;

            }
            else if(words[i].equals("divide")||words[i].equals("divided")||words[i].equals("/")||words[i].equals("÷"))
            {
                Log.d("calc", "Calc: /: "+ words[i]);
                words[i] = "/";
                //total = total / operand;

            }
            else if(words[i].equals("plus")||words[i].equals("add")||words[i].equals("+"))
            {
                Log.d("calc", "Calc: +: "+ words[i]);
                words[i] = "+";
                //total = total + operand;

            }
            else if(words[i].equals("minus")||words[i].equals("subtract")||words[i].equals("-")||words[i].equals("takeaway")||words[i].equals("take"))
            {
                Log.d("calc", "Calc: -: "+ words[i]);
                words[i] = "-";
                //total = total - operand;

            }
            else if(words[i].equals("million"))
            {
                Log.d("calc", "Calc: 000000: "+ words[i]);
                words[i] = "000000";
                //total = total - operand;

            }
            else if(words[i].equals("billion"))
            {
                Log.d("calc", "Calc: 000000000: "+ words[i]);
                words[i] = "000000000";
                //total = total - operand;

            }
            else if(words[i].equals("away")||words[i].equals("by")||words[i].equals(" "))
            {
                Log.d("calc", "Calc: waste: "+ words[i]);
                words[i] = "";

            }
            else
            {
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
