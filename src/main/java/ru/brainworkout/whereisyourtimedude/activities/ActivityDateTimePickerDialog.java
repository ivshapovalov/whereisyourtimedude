package ru.brainworkout.whereisyourtimedude.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RelativeLayout;

import ru.brainworkout.whereisyourtimedude.R;

import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;

/**
 * Created by Ivan on 10.09.2016.
 */
public class ActivityDateTimePickerDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout layout = new RelativeLayout(this);
        Button button = new Button(this);
        button.setText("Button!");
        layout.addView(button);

        setContentView(layout);

    }
}
