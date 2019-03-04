package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class SendResultsActivity extends Activity {
    LinearLayout recapLayout;
    File path;
    File file;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> times = new ArrayList<>();
    int maxRaces = 0;
    Bundle bundle;
    String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        path = Environment.getExternalStoragePublicDirectory("/Document");
        file = new File(path, "data.csv");
        if(file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.i("name", e.toString());
        }

        setContentView(R.layout.activity_send_results);
        recapLayout = findViewById(R.id.recap_layout);
        showRecap();
        writeOnCsv();


        findViewById(R.id.send_email_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_TEXT, content);
                emailIntent.setType("*/*");
                EditText nameView = findViewById(R.id.name_edit_text);
                String error = "";
                String mail = "";
                try {
                    mail = nameView.getText().toString();
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                        error = getResources().getString(R.string.incorrect_address);
                    }
                } catch (Exception e) {
                    error = e.toString();
                }


                if (error.equals("")) {
                    String to[] = {mail};
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
                    Uri csvUri = getUriDataCsv();

                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));

                    emailIntent.putExtra(Intent.EXTRA_STREAM, csvUri);
                    startActivity(emailIntent);
                } else {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG);
                }
            }
        });
    }

    protected void showRecap() {
        if (bundle != null) {
            if (bundle.getStringArrayList("names") != null) {
                names = bundle.getStringArrayList("names");
                for (int i = 0; i < names.size(); i++) {
                    String name = names.get(i);
                    recapLayout.addView(makeNameTextView(name));
                    if (bundle.getStringArrayList(name) != null) {
                        times = bundle.getStringArrayList(name);
                        if (maxRaces < times.size()) {
                            maxRaces = times.size();
                        }
                        for (int j = 0; j < times.size(); j++) {
                            String time = times.get(j);
                            recapLayout.addView(makeTimeTextView(time));
                        }
                    }
                }

            }
        }
    }

    protected TextView makeNameTextView (String name) {
        TextView nameTV = new TextView(getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 16, 16, 0);
        nameTV.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        nameTV.setTextColor(getResources().getColor(R.color.white));
        nameTV.setText(name);
        nameTV.setTextSize(23);
        nameTV.setLayoutParams(layoutParams);
        nameTV.setPadding(16, 8, 16, 8);
        return nameTV;
    }

    protected TextView makeTimeTextView (String time) {
        TextView timeTV = new TextView(getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 0, 16, 0);
        timeTV.setBackgroundColor(getResources().getColor(R.color.lightGrey));
        timeTV.setTextColor(getResources().getColor(R.color.colorPrimary));
        timeTV.setText(time);
        timeTV.setTextSize(18);
        timeTV.setLayoutParams(layoutParams);
        timeTV.setPadding(64, 8, 16, 8);
        return timeTV;
    }

    protected void writeOnCsv() {
        /* Creating the objects to write on the csv file */
        //InputStream inputStream = getResources().openRawResource(R.raw.data);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(getFirstRow());
            content += getFirstRow();
            for (int j = 0; j < names.size(); j++) {
                String name = names.get(j);
                String rowRunner = name + ",";
                if (bundle.getStringArrayList(name) != null) {
                    ArrayList<String> times = bundle.getStringArrayList(name);
                    for (int k = 0; k < times.size(); k++) {
                        rowRunner += times.get(k);
                        if (k == times.size() - 1) {
                            rowRunner += "\\n";
                        } else {
                            rowRunner += ",";
                        }
                    }

                }
                writer.write(rowRunner);
                content += rowRunner;
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.i("name", e.toString());
        }
    }

    protected String getFirstRow() {
        String firstRow = getResources().getString(R.string.input_runner) + ",";
        for (int i = 1; i < maxRaces+1; i++) {
            firstRow += getResources().getString(R.string.race) + " " + String.valueOf(i);
            if (i != maxRaces) {
                firstRow += ",";
            } else {
                firstRow += "\\n";
            }
        }
        return firstRow;
    }

    protected Uri getUriDataCsv() {
        Uri uri;
        if (Build.VERSION.SDK_INT > 23) {
            uri = FileProvider.getUriForFile(
                getApplicationContext(),
                "com.example.android.bluetoothlegatt.provider",
                file
            );
        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

    public Intent createEmailOnlyChooserIntent(Intent source,
                                               CharSequence chooserTitle) {
        Stack<Intent> intents = new Stack<Intent>();
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
                "tests@vmable.fr", null));
        List<ResolveInfo> activities = getPackageManager().queryIntentActivities(i, 0);

        for(ResolveInfo ri : activities) {
            Intent target = new Intent(source);
            target.setPackage(ri.activityInfo.packageName);
            intents.add(target);
        }

        if(!intents.isEmpty()) {
            Intent chooserIntent = Intent.createChooser(intents.remove(0),
                    chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    intents.toArray(new Parcelable[intents.size()]));

            return chooserIntent;
        } else {
            return Intent.createChooser(source, chooserTitle);
        }
    }
}
