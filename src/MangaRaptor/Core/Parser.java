package MangaRaptor.Core;

import MangaRaptor.DataStructures.StringPair;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oduibhir on 24/09/16.
 */
public class Parser {

    // Perform Regex on String, and returns a String Pair of two groups
    public static ArrayList<StringPair> parse(String original, String expression, int indexOfFirst, int indexOfSecond) {

        Pattern pattern = Pattern.compile(expression);
        Matcher matches = pattern.matcher(original);
        ArrayList values = new ArrayList();
        while(matches.find()) {
            values.add(new StringPair(matches.group(indexOfFirst), indexOfSecond == -1 ? "-":matches.group(indexOfSecond)));
        }
        return values;
    }

    public static boolean matches(String original, String expression) {
        Pattern pattern = Pattern.compile(expression);
        Matcher matches = pattern.matcher(original);
        if(matches.find())return true;
        return false;
    }

}
