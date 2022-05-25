package com.hangman;

import java.io.IOException;
import java.io.FileWriter;

import java.nio.file.Files;
import java.nio.file.Path;

import java.net.URL;
import java.net.MalformedURLException;

import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * The Librarian class manages the medialab directory.
 * It handles the creation and loading of dictionaries.
 * @author Marios Mantalos
 */
public class Librarian {
    /**
     * The loadDictionary method searches the medialab directory for a dictionary with the requested DICTIONARY-ID and returns it as a Dictionary object.
     * @param ID the Dictionary-ID of the requested dictionary.
     * @return The dictionary in the form of Dictionary object.
     * @throws IOException in case of a reading error or if the dictionary does not exist.
     */
    public Dictionary loadDictionary(String ID) throws IOException {
        String str = Files.readString(Path.of("medialab/hangman_" + ID + ".txt"));

        List<String> word_list = Arrays.asList(str.split("\n"));

        return new Dictionary(word_list);
    }

    /**
     * The saveDictionary method saves a dictionary in the medialab directory and gives it the specified DICTIONARY-ID.
     * @param word_list the list of words contained in the dictionary to be saved.
     * @param ID the DICTIONARY-ID to be given to the saved dictionary.
     * @throws IOException in case of a writing error.
     */
    private void saveDictionary(List<String> word_list, String ID) throws IOException {
        FileWriter writer = new FileWriter("medialab/hangman_" + ID + ".txt");

        try {
            writer.write(String.join("\n", word_list));
            writer.flush();
        }
        finally {
            writer.close();
        }
    }

    /**
     * The createDictionary method creates a new dictionary from the openlibrary entry with the given Open Library ID, formats it and saves it with the specified DICTIONARY-ID.
     * This method removes all duplicate words and all words that are too small.
     * @param ID the DICTIONARY-ID given to the dictionary when saved.
     * @param OPEN_ID the Open Library ID to be fetched from the openlibrary api.
     * @throws UndersizeException in case the dictionary doesn't have enough words.
     * @throws UnbalancedException in case the dictionary doesn't contain enough large words.
     * @throws MalformedURLException in case there is something wrong with the URL or the provided Open Library ID
     * @throws IOException in case  something goes wrong during writing.
     */
    public void createDictionary(String ID, String OPEN_ID)
            throws UndersizeException, UnbalancedException, MalformedURLException, IOException {
        URL url = new URL("https://openlibrary.org/works/" + OPEN_ID + ".json");
        String raw = fetchRawText(url);

        if (raw == null)
            throw new IOException("No such json, or the json uses the description instead of the description.value format");

        List<String> formatted_list = formatDictionary(raw);

        if (formatted_list.size() < 20)
            throw new UndersizeException("Not enough words in dictionary");

        if (countLarge(formatted_list) / (double) formatted_list.size() < 0.2)
            throw new UnbalancedException("Dictionary is unbalanced");

        saveDictionary(formatted_list, ID);
    }

    /**
     * The fetchRawText method gets the description.value data from the Open Library URL and returns it in String form.
     * @param url the Open Lilbrary URL to be used
     * @return a String containing the value field of the description of the Open Library entry.
     */
    private String fetchRawText(URL url) {
        JSONReader reader = new JSONReader();

        try {
            JSONObject json = reader.readJSONFromURL(url);
            return json.getJSONObject("description").getString("value");
        }
        catch (Exception e) {
            System.out.print("Something went wrong:\n\t" + e);
            return null;
        }
    }

    /**
     * The formatDictionary method takes the raw text of the dictionary returned from the api in String form and turns it into a list of valid words.
     * It removes all special characters, splits the raw String into words and removes all words with less than 6 letters.
     * @param raw_text the dictionary in String format.
     * @return a lisst of the valid and formatted words in the dictionary.
     */
    private List<String> formatDictionary(String raw_text) {
        List<String> word_list = Arrays.asList(
                raw_text
                        .replaceAll("[^\\p{L}\\p{Z}]", " ")
                        .split(" "));

        List<String> formatted_list = new ArrayList<>();


        for (String s : word_list)
            if (s.length() >= 6)
                formatted_list.add(s.toUpperCase());

        return new ArrayList<>(new LinkedHashSet<>(formatted_list));
    }

    /**
     * The countLarge method counts the number of words in the dictionary that have at least 9 letters.
     * @param word_list the list of words in the dictionary.
     * @return the number of words with at least 9 letters in the dictionary.
     */
    private int countLarge(List<String> word_list) {
        int count = 0;

        for (String s : word_list)
            if (s.length() >= 9)
                ++count;

        return count;
    }
}