package com.example.qrdolgozat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class listaAdatok extends AppCompatActivity {
    public ListView listViewID;
    public  EditText idID;
    public String Url;
    private Button modifyID;
    private Button backID;
    private EditText gradeID;
    private EditText nameID;
    private LinearLayout linForm;
    private List<Person> people = new ArrayList<>();
    private String url = "https://retoolapi.dev/5edtfW/dolgozat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_adatok);
        init();
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
        modifyID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emberModositas();
            }
        });
        backID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(listaAdatok.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void urlapAlaphelyzetbe() {
        nameID.setText("");
        gradeID.setText("");
        linForm.setVisibility(View.GONE);
        modifyID.setVisibility(View.VISIBLE);
        backID.setVisibility(View.VISIBLE);
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
    }

    private boolean validacio() {
        if (nameID.getText().toString().isEmpty() || gradeID.getText().toString().isEmpty())
            return true;
        else return false;
    }

    private void emberModositas() {
        String name = nameID.getText().toString();
        String grade = gradeID.getText().toString();
        String idText = idID.getText().toString();
        boolean valid = validacio();
        if (valid) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni", Toast.LENGTH_SHORT).show();
        } else {
            int grade1 = Integer.parseInt(grade);
            int id = Integer.parseInt(idText);
            Person person = new Person(id, name, grade1);
            Gson jsonConverter = new Gson();
            RequestTask task = new RequestTask(url + "/" + id, "PUT", jsonConverter.toJson(person));
            task.execute();
        }
    }

    public void init() {
        listViewID = findViewById(R.id.listViewID);
        modifyID = findViewById(R.id.modifyID);
        backID = findViewById(R.id.backID);
        gradeID = findViewById(R.id.gradeID);
        nameID = findViewById(R.id.nameID);
        listViewID.setAdapter(new PersonAdapter());

    }
    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }


        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl);
                        break;
                    case "POST":
                        response = RequestHandler.post(requestUrl, requestParams);
                        break;
                    case "PUT":
                        response = RequestHandler.put(requestUrl, requestParams);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(listaAdatok.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }
        @Override
        protected void onPostExecute(Response response) {
            Gson converter = new Gson();
            if (response.getResponseCode() >= 400) {
                Toast.makeText(listaAdatok.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError: ", response.getResponseMessage());
            }
            switch (requestType) {
                case "GET":
                    Person[] peopleArray = converter.fromJson(response.getResponseMessage(), Person[].class);
                    people.clear();
                    people.addAll(Arrays.asList(peopleArray));
                    break;
                case "PUT":
                    Person updatePerson = converter.fromJson(response.getResponseMessage(), Person.class);
                    people.replaceAll(person1 -> person1.getId() == updatePerson.getId() ? updatePerson : person1);
                    urlapAlaphelyzetbe();
                    break;
            }
        }

        }
    private class PersonAdapter extends ArrayAdapter<Person> {
        public PersonAdapter() {
            super(listaAdatok.this, R.layout.person_list_adapter, people);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.person_list_adapter, null, false);

            Person actualPerson = people.get(position);
            TextView name = view.findViewById(R.id.nevID);
            TextView age = view.findViewById(R.id.jegyID);
            TextView textViewModify = view.findViewById(R.id.modositID);

            name.setText(actualPerson.getName());
            age.setText(String.valueOf(actualPerson.getJegy()));

            textViewModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linForm.setVisibility(View.VISIBLE);
                    idID.setText(String.valueOf(actualPerson.getId()));
                    nameID.setText(actualPerson.getName());
                    gradeID.setText(actualPerson.getJegy());
                    modifyID.setVisibility(View.VISIBLE);
                    backID.setVisibility(View.VISIBLE);
                }
            });
            return view;
        }
    private void urlapAlaphelyzetbe() {
        nameID.setText("");
        modifyID.setText("");
        linForm.setVisibility(View.GONE);
        listViewID.setVisibility(View.VISIBLE);
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
    }}}
