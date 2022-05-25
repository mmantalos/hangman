package com.hangman;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.io.IOException;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.json.JSONException;

public class JSONReader {
    public JSONObject readJSONFromURL(URL url) throws IOException, JSONException {
        InputStream input = url.openStream();

        try {
            BufferedReader re = new BufferedReader(
                    new InputStreamReader(input, StandardCharsets.UTF_8)
            );

            String Text = Read(re);
            return new JSONObject(Text);
        }
        catch (Exception e) {
            return null;
        }
        finally {
            input.close();
        }
    }

    public String Read(Reader re) throws IOException {
        StringBuilder str = new StringBuilder();
        int temp;
        do {
            temp = re.read();
            str.append((char) temp);

        } while (temp != -1);

        return str.toString();
    }
}