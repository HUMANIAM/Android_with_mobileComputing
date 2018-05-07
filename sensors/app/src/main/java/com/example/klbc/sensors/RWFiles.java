package com.example.klbc.sensors;


import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hp on 4/27/2018.
 */

public class RWFiles {
    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorData/";

    public void  write(String fileName, String data, Context ctx){

        FileOutputStream fos = null;
        try {
            File file = new File(path + fileName);
            fos = new FileOutputStream(file, true);
            fos.write(data.getBytes());
            fos.write("\n".getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public  void Read(String fileName, Context ctx){
        FileInputStream fis = null;

        try {
            fis = ctx.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String txt;

            try {
                while((txt = br.readLine()) != null){
                    sb.append(txt).append("/n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
