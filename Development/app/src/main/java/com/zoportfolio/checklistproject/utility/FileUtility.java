package com.zoportfolio.checklistproject.utility;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtility {

    private static final String TAG = "FileUtility.TAG";

    //This method will save to the protected storage of the device, using names given for the folder and file, and the object to save.
    public static boolean saveToProtectedStorage(Context _context, String _fileName, String _folderName, Object _objectToSave) {

        //Create path to the folder
        File folder = _context.getExternalFilesDir(_folderName);

        File objectFile = new File(folder, _fileName);

        try{
            boolean isCreated = objectFile.createNewFile();
            Log.i(TAG, "saveToProtectedStorage: is file new: " + isCreated);

            FileOutputStream fos = _context.openFileOutput(_fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_objectToSave);
            oos.close();
            return true;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //This method will retrieve an object from the specified filename in storage.
    public static Object retrieveFromStorage(Context _context, String _fileName) {

        try{
            FileInputStream fis = _context.openFileInput(_fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            ois.close();
            return obj;
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    //This method will retrieve the count of the folder specified.
    //I will most likely convert this method into just returning the entire folder in the future.
    public static int getCountOfFolderFromProtectedStorage(Context _context, String _folderName) {
        File folder = _context.getExternalFilesDir(_folderName);
        if(folder != null) {
            String[] files = folder.list();
            return files.length;
        }
        return 0;
    }
}
