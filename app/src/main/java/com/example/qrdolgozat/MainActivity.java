package com.example.qrdolgozat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
private Button listazasID;
private Button scanID;
private TextView textViewID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        scanID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setPrompt("QRCode Scanner by Szabó Béla");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(false);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.initiateScan();
            }
        });
        listazasID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textViewID.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this,"Nincs QR kód beolasva", Toast.LENGTH_SHORT);
                    return;
                }
                SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editro = sharedPreferences.edit();
                editro.putString("url",textViewID.getText().toString());
                Intent intent = new Intent(MainActivity.this,listaAdatok.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Vissza léptél az alkalmazásban", Toast.LENGTH_SHORT).show();
            } else {
                textViewID.setText(result.getContents());
                Uri uri = Uri.parse(result.getContents());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
    public void init()
    {
        textViewID= findViewById(R.id.textViewID);
        scanID= findViewById(R.id.scanID);
        listazasID= findViewById(R.id.listazasID);

    }
}