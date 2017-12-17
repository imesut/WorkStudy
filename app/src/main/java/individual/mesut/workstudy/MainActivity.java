package individual.mesut.workstudy;

import android.content.Context;
import android.icu.text.DateTimePatternGenerator;
import android.icu.text.SimpleDateFormat;
import android.icu.util.DateInterval;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ListView liste;
    private Button button;
    private Button delete;
    private TextView display;
    private Spinner kind;
    private List<String> works = new ArrayList<>();
    private List<Integer> days = new ArrayList<>();
    private List<Integer> hours = new ArrayList<>();
    private List<Integer> minutes = new ArrayList<>();
    private List<Integer> seconds = new ArrayList<>();
    private List<Double> durations = new ArrayList<>();
    private List<String> items = new ArrayList<>();
    private String [] kinds = new String[]{
            "Bekleme", "Yürüme", "Metro",  "Otobüs", "Denizyolu"
    };

    private void writeToFile(String data, Context context, String file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(Context context, String file) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    @Override
    protected void onPause() {
        super.onPause();
        JSONObject json = new JSONObject();
        int state = 5000;
        try{
            json.put("state", state);

            JSONArray jsonItems = new JSONArray();
            JSONArray jsonDays = new JSONArray();
            JSONArray jsonHours = new JSONArray();
            JSONArray jsonMinutes = new JSONArray();
            JSONArray jsonSeconds = new JSONArray();
            JSONArray jsonDurations = new JSONArray();
            JSONArray jsonWorks = new JSONArray();
            for (int i=0; i < items.size(); i++ ) {
                jsonItems.put(items.get(i));
                jsonDurations.put(durations.get(i));
                jsonDays.put(days.get(i));
                jsonHours.put(hours.get(i));
                jsonMinutes.put(minutes.get(i));
                jsonSeconds.put(seconds.get(i));
                jsonWorks.put(works.get(i));
            }
            json.put("items", jsonItems);
            json.put("days", jsonDays);
            json.put("hours", jsonHours);
            json.put("minutes", jsonMinutes);
            json.put("seconds", jsonSeconds);
            json.put("durations", jsonDurations);
            json.put("works", jsonWorks);

        }catch(JSONException ex) {
        }
        writeToFile(String.valueOf(json), this, "current.json");

    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentFile = readFromFile(this, "current.json");
        Log.d("file", currentFile);
        try {
            JSONObject readJson = new JSONObject(currentFile);
            final int readresult = readJson.getInt("state");
            JSONArray jsonItems = readJson.getJSONArray("items");
            JSONArray jsonWorks = readJson.getJSONArray("works");
            JSONArray jsonDurations = readJson.getJSONArray("durations");
            JSONArray jsonDays = readJson.getJSONArray("days");
            JSONArray jsonHours = readJson.getJSONArray("hours");
            JSONArray jsonMinutes = readJson.getJSONArray("minutes");
            JSONArray jsonSeconds = readJson.getJSONArray("seconds");
            for (int i=0; i < jsonItems.length(); i++ ) {
                items.add(jsonItems.getString(i));
                works.add(jsonWorks.getString(i));
                durations.add(jsonDurations.getDouble(i));
                days.add(jsonDays.getInt(i));
                hours.add(jsonHours.getInt(i));
                minutes.add(jsonMinutes.getInt(i));
                seconds.add(jsonSeconds.getInt(i));
            }
        } catch (JSONException ex) {
        }
        if (days.size() > 0){
            display.setText(days.get(days.size()-1)
                    + "-" + hours.get(days.size()-1)
                    + "-" + minutes.get(days.size()-1)
                    + "-" + seconds.get(days.size()-1));
            kind.setSelection(Arrays.asList(kinds).indexOf(String.valueOf(items.get(items.size()-1).split(", ")[0])));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (new File("current.json").exists()) {
            String filetry = readFromFile(this, "current.json");

            try {
                JSONObject readJson = new JSONObject(filetry);
                final int readresult = readJson.getInt("result");
                Log.d("result", String.valueOf(readresult));
            } catch (JSONException ex) {
            }
        }

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        mTextMessage.setText(R.string.title_home);
                        display.setVisibility(View.VISIBLE);
                        liste.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.VISIBLE);
                        kind.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.navigation_dashboard:
                            for (int i=items.size(); i < works.size(); i++ ) {
                                items.add(works.get(i) + ", "
                                        + Double.toString(durations.get(i)) + ", "
                                        + Integer.toString(days.get(i)) + ", "
                                        + Integer.toString(hours.get(i)) + ", "
                                        + Integer.toString(minutes.get(i)) + ", "
                                        + Integer.toString(seconds.get(i)));
                            }
                        mTextMessage.setText(R.string.title_dashboard);
                        liste.setVisibility(View.VISIBLE);
                        button.setVisibility(View.INVISIBLE);
                        display.setVisibility(View.INVISIBLE);
                        kind.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.navigation_notifications:
                        mTextMessage.setText(R.string.title_notifications);
                        liste.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.INVISIBLE);
                        kind.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.VISIBLE);
                        return true;
                }
                return false;
            }
        };

        mTextMessage = (TextView) findViewById(R.id.message);
        liste = (ListView) findViewById(R.id.liste);
        button = (Button) findViewById(R.id.button);
        delete = (Button) findViewById(R.id.delete);
        display = (TextView) findViewById(R.id.display);
        kind = (Spinner) findViewById(R.id.kind);

        liste.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        button.setText("BAŞLADI");

        ArrayAdapter<String> listeAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, android.R.id.text1, items);

        liste.setAdapter(listeAdapter);

        ArrayAdapter<String> kindAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_text, kinds);
        kindAdapter.setDropDownViewResource(R.layout.spinner_text);
        kind.setAdapter(kindAdapter);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteFile("current.json");
                } catch (Exception FileNotFoundException){
                    Snackbar.make(view, "Dosya bulunamadı", 2);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_YEAR);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                int second = c.get(Calendar.SECOND);

                double duration;
                if (works.size() > 0){
                    duration = 24 * 60 * (day-days.get(days.size()-1))
                            + 60 * (hour - hours.get(hours.size()-1))
                            + (minute - minutes.get(minutes.size()-1))
                            + (second - seconds.get(seconds.size()-1)) / 60;
                } else{
                    duration = 0;
                }

                String currentWork = kind.getSelectedItem().toString();
                works.add(currentWork);
                durations.add(duration);
                days.add(day);
                hours.add(hour);
                minutes.add(minute);
                seconds.add(second);

                Snackbar sbar = Snackbar.make(view, currentWork + " işi için zaman noktası eklendi.", 600);
                sbar.show();
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
