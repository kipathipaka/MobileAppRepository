package com.bpatech.trucktracking.Util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by Anita on 11/3/2015.
 */
public class Helper {
    void SaveAsFile(String ErrorContent, Context context) {
        try {
            System.out.println("++++++++++++++++++++exceptttt+++++++++++++++++++");
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vm/";
            System.out.println("++++++++++++++++++++exceptttt+++++++++++++++++++"+filePath);
            File dir = new File(filePath);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(filePath, ".errorTrace.txt");
            if (file.exists())
                file.delete();
            file.createNewFile();

            FileOutputStream trace = new FileOutputStream(file);
            PrintStream printStream;
            printStream = new PrintStream(trace);
            printStream.println(ErrorContent);
            printStream.close();
            trace.close();
        } catch (Exception ioe) {
            Log.v("Crash+++++++", ioe.getMessage());
        }
    }
}
