package edu.sjsu.sase.android.spoleralert.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.sjsu.sase.android.spoleralert.R;

public class ProfilePickerActivity extends AppCompatActivity {

    int[] profileImages = {
            R.drawable.bird1_pink,
            R.drawable.bird2_pink,
            R.drawable.bird3_pink,
            R.drawable.bird1_orange,
            R.drawable.bird2_orange,
            R.drawable.bird3_orange,
            R.drawable.bird1_yellow,
            R.drawable.bird2_yellow,
            R.drawable.bird3_yellow,
            R.drawable.bird1_green,
            R.drawable.bird2_green,
            R.drawable.bird3_green,
            R.drawable.bird1_blue,
            R.drawable.bird2_blue,
            R.drawable.bird3_blue,
            R.drawable.bird1_indigo,
            R.drawable.bird2_indigo,
            R.drawable.bird3_indigo,
            R.drawable.bird1_purple,
            R.drawable.bird2_purple,
            R.drawable.bird3_purple,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picker);

        GridView gridView = findViewById(R.id.profileGrid);
        gridView.setAdapter(new ImageAdapter(this, profileImages));

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedImage = profileImages[position];
            new AlertDialog.Builder(ProfilePickerActivity.this)
                    .setTitle("Set Profile Picture")
                    .setMessage("Use this image as your profile picture?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("selectedImageRes", selectedImage);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}