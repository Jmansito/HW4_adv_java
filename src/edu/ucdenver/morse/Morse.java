package edu.ucdenver.morse;

import java.util.HashMap;

public class Morse {

    private HashMap<String, String> morseToNormal;
    private HashMap<String, String> normalToMorse;
    // morseCharacters array A -> Z
    private final String[] morseCharacters = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---",
            "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--",
            "--..", ".----", "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.", "-----"};
    private final String[] normalCharacters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
                            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2",
                            "3", "4", "5", "6", "7", "8", "9", "0"};

    // Constructor for initializing
    public Morse(){
        morseToNormal = new HashMap<>();
        normalToMorse = new HashMap<>();
        for(int i = 0; i < 36; i++){
            morseToNormal.put(morseCharacters[i], normalCharacters[i]);
            normalToMorse.put(normalCharacters[i], morseCharacters[i]);
        }
    }

    public String encode(String message){
        String encoded = "";
        message = message.toUpperCase();

        for(int i = 0; i < message.length(); i++){
            String key = String.valueOf((message.charAt(i)));

            if(message.charAt(i)==' '){encoded += " ";} // check for spaces in the string
            // Compare the key to the encoder HashMap
            else {
                if (normalToMorse.containsKey(key)){
                    encoded += normalToMorse.get(key) + "="; // Assign morse to encoded variable
                } else {encoded += " ";}
            }
        }
        return encoded;
    }

    public String decode(String message){
        String decoded = "";
        String morseString = "";
        for (int i = 0; i < message.length(); i++) {
            if(message.charAt(i)==' '){decoded += " ";} //check for spaces
            else {
                if(message.charAt(i)=='='){ // Search for delimiter
                    decoded += morseToNormal.get(morseString); // add the decoded morse to decoded
                    morseString = ""; // reset morse code string
                } else {morseString += message.charAt(i);} // Build the morse code string
            }
        }
        return decoded;
    }


    public static void main(String[] args) {
        Morse test = new Morse();
        String testString = "Hello World";
        String testMorse = "....=.=.-..=.-..=---= .--=---=.-.=.-..=-..=";
        System.out.println(testString + " to morse : " + test.encode(testString));
        System.out.println(testMorse + " to English : " + test.decode(testMorse));

    }
}
