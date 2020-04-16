package com.example.malraten;

import android.util.DisplayMetrics;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;


public class MainActivity extends AppCompatActivity {


    private PaintView paintView;

    Button button, buttonColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

        DatabaseManager.init(paintView);

        final HSLColorPicker colorPicker = (HSLColorPicker) findViewById(R.id.colorpick);
        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                paintView.setCurrentColor(color);
            }
        });

        buttonColor = findViewById(R.id.button);
        buttonColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (colorPicker.getVisibility()==View.VISIBLE)
                    colorPicker.setVisibility(View.INVISIBLE);
                else
                    colorPicker.setVisibility(View.VISIBLE);
            }
        });


        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View e) {
                DatabaseManager.clear();
            }
        });

        button = findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View e) {
                DatabaseManager.saveState();
            }
        });


        final AutoCompleteTextView autoCompl = findViewById(R.id.autoCompleteTextView);
        final String[] databaseattribute = new String[]{"familienmalen", "hanni", "message", "public_release", "mitfarbe", "familiefarbe", "hannifarbe"};
        ArrayAdapter adapterCountries = new ArrayAdapter(this,android.R.layout.simple_list_item_1,databaseattribute);
        autoCompl.setAdapter(adapterCountries);
        autoCompl.setThreshold(0);
        autoCompl.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String inp = autoCompl.getText().toString();
                for (String s: databaseattribute) {
                    if (s.equals(inp)){
                        DatabaseManager.setDatabaseAttribut(s);
                        break;
                    }else
                        DatabaseManager.setDatabaseAttribut("message");
                }


            }
        });
    }
}
