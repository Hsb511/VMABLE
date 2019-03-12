package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Pattern;

import java.util.ArrayList;

public class OfflineActivity extends Activity {
    private LinearLayout timerLayout;
    private ImageButton addButton;
    private int index = 0;
    private int screenWidth;
    private boolean displayAddButton = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        /* Initialize the windows layout and screenWidth*/
        timerLayout = findViewById(R.id.main_timer_layout);
        //displayAddButton();

        addButton = findViewById(R.id.add_runner_button);
        addButton.setVisibility(View.VISIBLE);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        screenWidth = size.x;

        /* Display a first Runner Layout */
        displayNewRunnerLayout();

        /* set OnClickListener for AddButton */
        findViewById(R.id.add_runner_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayNewRunnerLayout();
            }
        });

        /* setOnClickListener for StartForAllButton */
        findViewById(R.id.start_everyone_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < timerLayout.getChildCount(); i++) {
                    if (timerLayout.getChildAt(i) instanceof LinearLayout) {
                        LinearLayout childLayout = (LinearLayout) timerLayout.getChildAt(i);
                        LinearLayout childOfChildLayout = (LinearLayout) childLayout.getChildAt(1);
                        Chronometer chronometer = (Chronometer) childOfChildLayout.getChildAt(0);
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();

                    }
                }
            }
        });

        /* setOnClickListener for sendResultsButton */
        findViewById(R.id.send_results_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendResultsIntent = new Intent(OfflineActivity.this, SendResultsActivity.class);
                sendResults(sendResultsIntent);
            }
        });
    }

    /**
     * Hide the button if we are in automatic timing mode
     */
    protected void displayAddButton() {
        addButton = findViewById(R.id.add_runner_button);
        if (displayAddButton) addButton.setVisibility(View.VISIBLE);
        else addButton.setVisibility(View.GONE);
    }

    /**
     * Create LayoutParams for the different graphical object
     */
    protected void displayNewRunnerLayout() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16);

        LinearLayout runnerLayout = createRunnerLayout(layoutParams);
        runnerLayout.addView(createNameEditText());
        runnerLayout.addView(createTimerLayout(layoutParams));

        timerLayout.addView(runnerLayout, index);
        index ++;
    }

    /**
     * Create the LinearLayout which is the basis of the timer for one runner
     * @param layoutParams
     * @return the empty LinearLayout correctly set up
     */
    protected LinearLayout createRunnerLayout (LinearLayout.LayoutParams layoutParams) {
        LinearLayout runnerLayout = new LinearLayout(getApplicationContext());
        runnerLayout.setLayoutParams(layoutParams);
        runnerLayout.setBackgroundColor(getResources().getColor(R.color.lightGrey));
        runnerLayout.setOrientation(LinearLayout.VERTICAL);
        return runnerLayout;
    }

    /**
     * Create and add the EditText for the runner's name
     * @return the EditText correctly set up
     */
    protected EditText createNameEditText () {
        EditText nameEditText = new EditText(getApplicationContext());
        nameEditText.setHint(getResources().getString(R.string.input_runner));
        nameEditText.setWidth(screenWidth - Math.round(convertDpToPixel(2*12, getApplicationContext())));
        nameEditText.setHeight(Math.round(convertDpToPixel(40, getApplicationContext())));
        nameEditText.setPadding(16,-6,16, -2);
        return nameEditText;
    }


    /**
     * Create and add the LinearLayout for the timer
     * @param layoutParams
     * @return the LinearLayout correctly filled and set up
     */
    protected LinearLayout createTimerLayout (LinearLayout.LayoutParams layoutParams) {
        final LinearLayout timerLayout = new LinearLayout(getApplicationContext());
        timerLayout.setLayoutParams(layoutParams);
        timerLayout.setOrientation(LinearLayout.HORIZONTAL);

        /* Adding the timer with layoutParams */
        final LinearLayout.LayoutParams timerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        timerParams.setMargins(32, 8, 16, 0);

        final Chronometer chronometer = createChronometer(timerParams);
        timerLayout.addView(chronometer);

        if (displayAddButton) {
            /* Creating layoutParams for the ImageButtons */
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    Math.round(convertDpToPixel(42, getApplicationContext())),
                    Math.round(convertDpToPixel(34, getApplicationContext()))
            );

            /* Adding startAndPauseButton */
            imageParams.setMargins(48, 8, 0, 8);
            ImageButton startAndPauseButton = createTimerButton(imageParams, R.drawable.start_and_pause);
            startAndPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getContentDescription() != null && view.getContentDescription().toString().equals("start")) {
                        chronometer.stop();
                        view.setContentDescription(String.valueOf(SystemClock.elapsedRealtime() - chronometer.getBase()));
                    } else {
                        if (view.getContentDescription() != null) {
                            chronometer.setBase(SystemClock.elapsedRealtime() - Integer.parseInt(view.getContentDescription().toString()));
                        }
                        chronometer.start();
                        view.setContentDescription("start");
                    }

                }
            });
            timerLayout.addView(startAndPauseButton);

            /* Adding resetTimerButton */
            imageParams.setMargins(16, 8, 0, 8);
            ImageButton resetTimerButton = createTimerButton(imageParams, R.drawable.cancel);
            resetTimerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //timerTextView.setText("00:00:00.000");
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.stop();

                }
            });
            timerLayout.addView(resetTimerButton);

            /* Adding validateTimeButton */
            ImageButton validateTimeButton = createTimerButton(imageParams, R.drawable.validate);
            validateTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //String time = timerTextView.getText().toString();
                    LinearLayout parentLayout = (LinearLayout)timerLayout.getParent();
                    int index = 1;
                    if (view.getContentDescription() == null) {
                        view.setContentDescription("1");
                        index = 1;
                    } else {
                        index = Integer.parseInt(view.getContentDescription().toString());
                        index ++;
                        view.setContentDescription(String.valueOf(index));
                    }
                    (parentLayout).addView(createNewRace(chronometer.getText().toString(), parentLayout, timerParams));
                }
            });
            timerLayout.addView(validateTimeButton);

        }
        return timerLayout;
    }

    /**
     * Create the Chronometer
     * @param layoutParams to set up graphically the View
     * @return the set up Chronometer (with milliseconds) as found on the net based on the Chronometer
     */
    protected Chronometer createChronometer(LinearLayout.LayoutParams layoutParams) {
        Chronometer chronometer = new Chronometer(getApplicationContext());
        chronometer.setWidth(Math.round(screenWidth/2 - convertDpToPixel(12*2, getApplicationContext())));
        chronometer.setTextSize(23);
        chronometer.setLayoutParams(layoutParams);
        chronometer.setBackgroundColor(getResources().getColor(R.color.grey));
        chronometer.setPadding(16,0, 16, 0);
        return chronometer;
    }

    /**
     * Create the different buttons that will be used to manage the timer
     * @param imageParams to create the Button with the good format
     * @param resourceDrawable to fill with the good picture
     * @return the ImageButton set up but without onClickListener
     */
    protected ImageButton createTimerButton(LinearLayout.LayoutParams imageParams, int resourceDrawable) {
        ImageButton startAndPauseButton = new ImageButton(getApplicationContext());
        startAndPauseButton.setBackgroundColor(getResources().getColor(R.color.grey));
        startAndPauseButton.setImageResource(resourceDrawable);

        startAndPauseButton.setLayoutParams(imageParams);
        startAndPauseButton.setScaleType(ImageView.ScaleType.FIT_XY);
        return startAndPauseButton;
    }

    /**
     * Create a new race layout set up with the race label (index) and time
     * @param time the time to display
     * @param parentLayout to find the race index
     * @param params
     * @return the LinearLayout filled
     */
    protected LinearLayout createNewRace(String time, LinearLayout parentLayout, LinearLayout.LayoutParams params) {
        LinearLayout raceLayout = new LinearLayout(getApplicationContext());
        int ind = findRaceNumber(parentLayout) + 1;
        raceLayout.addView(createRaceTextView(ind));
        raceLayout.addView(createTimerTextView(params, time));
        raceLayout.addView(createDeleteRaceButton());
        return raceLayout;
    }

    /**
     * Method to find the amount of races to set the label of the new race to create
     * @param parentLayout the runnerLayout
     * @return the amount of visible race
     */
    protected int findRaceNumber(LinearLayout parentLayout) {
        int amount = 0;
        for (int i = 2; i < parentLayout.getChildCount(); i++) {
            LinearLayout childLayout = (LinearLayout) parentLayout.getChildAt(i);
            if (childLayout.getVisibility() == View.VISIBLE) {amount++;}
        }
        return amount;
    }

    /**
     * Create the TextView to show the Race's label
     * @param index the number of the race
     * @return the TextView correctly set up
     */
    protected TextView createRaceTextView(int index) {
        TextView raceTextView = new TextView(getApplicationContext());
        raceTextView.setContentDescription(String.valueOf(index));
        raceTextView.setText(getResources().getString(R.string.race) + " " + String.valueOf(index));
        raceTextView.setTextSize(23);
        raceTextView.setWidth(Math.round(screenWidth/2 - convertDpToPixel(36, getApplicationContext())));
        raceTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        raceTextView.setPadding(64, 0, 0, 16);
        return raceTextView;
    }

    /**
     * Create the TextView when the Race is finished with the time inside
     * @param layoutParams
     * @param text the time for the race
     * @return the TextView set up
     */
    protected TextView createTimerTextView(LinearLayout.LayoutParams layoutParams, String text) {
        TextView timerTextView = new TextView(getApplicationContext());
        timerTextView.setLayoutParams(layoutParams);
        //timerTextView.setWidth(Math.round(screenWidth/2 - convertDpToPixel(12*2, getApplicationContext())));
        timerTextView.setBackgroundColor(getResources().getColor(R.color.grey));
        timerTextView.setText(text);
        timerTextView.setTextSize(23);
        timerTextView.setPadding(16, 0, 16, 0);
        return timerTextView;
    }


    /**
     * Create the deleteButton to remove the race from the runnerLayout
     * @return the ImageButton correctly set up
     */
    protected ImageButton createDeleteRaceButton() {
        ImageButton imageButton = new ImageButton(getApplicationContext());
        imageButton.setBackgroundColor(getResources().getColor(R.color.grey));
        imageButton.setImageResource(R.drawable.delete);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                Math.round(convertDpToPixel(23, getApplicationContext())),
                Math.round(convertDpToPixel(34, getApplicationContext()))
        );
        imageParams.setMargins(8, 8, 0, 0);
        imageButton.setLayoutParams(imageParams);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout raceLayout = (LinearLayout) view.getParent();
                raceLayout.setVisibility(View.GONE);
                //int index = Integer.parseInt((raceLayout.getChildAt(0)).getContentDescription().toString());
                LinearLayout runnerLayout = (LinearLayout) raceLayout.getParent();
                int indexNb = 1;
                    for (int i = 2; i < runnerLayout.getChildCount(); i++) {
                        LinearLayout childLayout = (LinearLayout) runnerLayout.getChildAt(i);
                        if (childLayout.getVisibility() == View.VISIBLE) {
                            TextView raceTV = (TextView) childLayout.getChildAt(0);
                            raceTV.setText(getResources().getString(R.string.race) + " " + indexNb++);
                        }
                    }

                //runnerLayout.removeViewAt(index + 2);


            }
        });
        return imageButton;
    }

    /**
     * method triggered when the Button for sending results is pressed.
     * It will check whether the names matches a certain pattern and add the names and the times in an ArrayList of Strings
     * @param intent
     */
    protected void sendResults(Intent intent) {
        boolean error = false;
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < timerLayout.getChildCount(); i++) {
            if (timerLayout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout childLayout = (LinearLayout) timerLayout.getChildAt(i);
                TextView nameTextView = (TextView) childLayout.getChildAt(0);
                String name = nameTextView.getText().toString();
                Log.i("samere", name + " : " + String.valueOf(Pattern.matches("^[a-zA-Z ]$", name)));
                if (Pattern.matches("^[a-zA-Zéèëàùöôê]+[a-zA-Zéèëàùöôê ]*$", name)) {
                    names.add(name);
                    Log.i("name", name);
                    ArrayList<String> races = new ArrayList<>();
                    for (int j = 2; j < childLayout.getChildCount(); j++) {
                        LinearLayout raceLayout = (LinearLayout)childLayout.getChildAt(j);
                        if (raceLayout.getVisibility() == View.VISIBLE) {
                            TextView timeTextView = (TextView) raceLayout.getChildAt(1);
                            races.add(timeTextView.getText().toString());
                        }
                    }

                    intent.putStringArrayListExtra(name, races);
                } else {
                    error = true;
                }



            }
            intent.putStringArrayListExtra("names", names);
        }

        if (error) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.missing_name), Toast.LENGTH_LONG).show();
        } else {
            startActivity(intent);
        }


    }


    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
