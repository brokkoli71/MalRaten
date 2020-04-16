package com.example.malraten;

import android.os.CountDownTimer;
import android.util.Log;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    public static DatabaseReference myRef;
    public static FirebaseDatabase database;
    public static volatile String currentValue = "";
    private static volatile boolean timeOut = false;
    private static volatile boolean currentValueProsessed = false;
    private static PaintView mPaintView;
    public static String databaseAttribut = "mitfarbe";
    public static FirebaseFirestore firestore;


    public static void saveState(){
        Map<String, Object> saveSet = new HashMap<>();
        saveSet.put("save", currentValue);

        firestore.collection("saves")
                .add(saveSet)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("firebase", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebase", "Error adding document", e);
                    }
                });

    }

    private static void updateCurrentValue(final DataSnapshot dataSnapshot){
        while(currentValueProsessed)
        {
            try {
                Log.w("wait", "waiting... "+timeOut+currentValueProsessed);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e("wait", "InterruptedWaiting");
            }
        }
        currentValueProsessed = true;

        final String value = dataSnapshot.getValue(String.class);
        Log.d("Firebase", " got Value : " + value);
        if (value.equals("")){
            clear();
            mPaintView.clear();
            currentValueProsessed = false;
            return;
        }
        if (!currentValue.equals(value)){
            //there are currently still problems when the firebase gets accessed from several different devices and the app will crash
            try {
                int i = currentValue.length();
                String newValue = value.substring(i); //index out of bounds
                Log.d("Firebase", "add Value: " + newValue);
                mPaintView.addData(StringTransformator.getFingerPathList(newValue)); //java.lang.NumberFormatException: empty String, java.lang.ArrayIndexOutOfBoundsException: length=60; index=60
                currentValueProsessed = false;
            }catch (IndexOutOfBoundsException e) {
                Log.e("errrr", "oldValue: " + currentValue);
                Log.e("errrr", "newValue: " + value);
                Log.e("errrr", e.getLocalizedMessage());
                overwritePaintView(dataSnapshot);
            }catch (NumberFormatException e1){
                Log.e("errrd", "oldValue: " + currentValue);
                Log.e("errrd", "newValue: " + value);
                Log.e("errrd", e1.getLocalizedMessage());
                overwritePaintView(dataSnapshot);
            }
        }else
            currentValueProsessed = false;
    }
    public static void overwritePaintView(final DataSnapshot dataSnapshot){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("errrr", "overwriting PaintView");
                String value = dataSnapshot.getValue(String.class);
                mPaintView.clear();
                mPaintView.addData(StringTransformator.getFingerPathList(value));
                currentValue = value;
                currentValueProsessed = false;
            }
        }).start();
    }
    public static void init(PaintView paintView){
        mPaintView = paintView;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(databaseAttribut);
        firestore = FirebaseFirestore.getInstance();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateCurrentValue(dataSnapshot);
                    }
                }).start();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }
    public static void addData(final String value){
        Log.d("database", "try to add: " + value);
        while(currentValueProsessed)
        {
            try {
                Log.w("wait", "waiting... ");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e("wait", "InterruptedWaiting");
            }
        }
        if (!timeOut) {
            timeOut = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    myRef.setValue(currentValue+value);
                    currentValue += value;
                    Log.d("Firebase", "set Value " + currentValue);
                }
            }).start();

            int millisTimeOut = 100;
            new CountDownTimer(millisTimeOut, millisTimeOut) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    timeOut = false;
                }

            }.start();
        }
    }

    public static void setDatabaseAttribut(String s){
        databaseAttribut = s;
        mPaintView.clear();
        currentValue = "";
        init(mPaintView);
    }


    public static void clear(){
        if (currentValue!="")
            saveState();
        mPaintView.clear();
        currentValue = "";
        myRef.setValue(currentValue);
    }
}
