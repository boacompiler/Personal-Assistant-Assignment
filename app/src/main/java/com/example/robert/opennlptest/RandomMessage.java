package com.example.robert.opennlptest;

import java.util.Random;

/**
 * Created by robert on 18/05/17.
 */

public class RandomMessage {
    private String[] notsure = new String[]{
            "I'm not sure, but this could be it: ",
            "I could be mistaken, but here it is: ",
            "This is probably right: ",
            "I think this is correct: "};

    private String[] joke = new String[]{
            "Two hats are sitting on a hat rack. One says, you stay here, I'll go on a head.",
            "Parallel lines have so much in common but it’s a shame they’ll never meet.",
            "I would tell you a UDP joke, but you might not get it.",
            "How many computer programmers does it take to change a light bulb? None, that's a hardware problem.",
            "As a computer program, Sometimes i feel the call of the void. And I know I've reached the point of no return.",
            "why did the functions stop talking. they had too many arguments!",
            "why don't bachelors like git? because they are afraid to commit.",
            "Yo Momma's so fat. She can't handle files over 4 gigabytes."

    };

    public String GetNotSure()
    {
        return RandIndex(notsure);
    }

    public String GetJoke()
    {
        return RandIndex(joke);
    }

    private String RandIndex(String[] array)
    {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
