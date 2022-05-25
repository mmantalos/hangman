package com.hangman;

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.util.Scanner;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Historian {
    private final List<Record> records;
    private final String save_file;

    Historian(String file) {
        records = new ArrayList<>();
        save_file = file;

        loadRecords();
    }

    public void addEntry(int new_score, String new_winner) {
        records.add(new Record(new_score, new_winner));

        while (records.size() > 5)
            records.remove(0);
    }

    public void loadRecords() {
        try {
            Scanner scanner = new Scanner((new File(save_file)));
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                records.add(new Record(Integer.parseInt(data.split(" ")[0]),data.split(" ")[1]));
            }
            scanner.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveRecords() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(save_file, false));
            for (Record r : records) {
                writer.write(r.getScore() + " " + r.getWinner());
                writer.newLine();
            }
            writer.flush();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public List<Record> getRecords() {
        return records;
    }
}