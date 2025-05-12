package edu.sjsu.sase.android.spoleralert.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

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

    String[] avatarNames = {
        "Peckson", "Ruby", "Chirpella",
        "McFly", "Amber", "Tangerweet",
        "Sunny", "Beakyoncé", "Yolka",
        "Chirplin", "Kiwi", "Quillson",
        "Beaky", "Bluebell", "Peakley",
        "Feathersby", "Indiara", "Wingrove",
        "Finchley", "Plummie", "Wattle",
    };

    private final static Map<String, String[]> birdPhrases = new HashMap<>();

    static {
        birdPhrases.put("Peckson", new String[]{
                "You're crushing it today!",
                "Take a snack break. You earned it!",
                "You've got this, featherfriend.",
                "Your energy is contagious!",
                "Let’s turn up the heat in the kitchen!",
                "Spice up the day!",
                "You’re on fire (in a good way)!"
        });

        birdPhrases.put("Ruby", new String[]{
                "Shine like a gemstone!",
                "Your spark lights up the pantry!",
                "Ruby, you’re radiant today!",
                "Red alert: you're adorable.",
                "You glow from the inside out.",
                "Let’s bring the heat, chef!",
                "Sweet like strawberry jam!"
        });

        birdPhrases.put("Chirpella", new String[]{
                "Sing your own tune today!",
                "You're a little spark of magic.",
                "Red feathers, big heart.",
                "Let's chirp up a storm!",
                "Today’s a good chirp day.",
                "Crispy crusts and cozy vibes!",
                "You light up the sky!"
        });

        birdPhrases.put("McFly", new String[]{
                "Back to the fridge we go!",
                "Time to wing it again!",
                "You're flying high today!",
                "Orange you glad we met?",
                "Zooming through life stylishly.",
                "You’re looking toasty!",
                "Rise, shine, and flap on!"
        });

        birdPhrases.put("Amber", new String[]{
                "Warm vibes only!",
                "Glow like honey toast!",
                "Golden moments are made today.",
                "You make autumn jealous.",
                "You're sweeter than marmalade.",
                "Let’s bake the world better!",
                "Amber days are the best days."
        });

        birdPhrases.put("Tangerweet", new String[]{
                "Zesty and sweet—like you!",
                "Let’s peel into action!",
                "Tanger-ific job today!",
                "You brighten up any shelf.",
                "Cooking with sunshine!",
                "Citrusy smiles all around.",
                "Keep it fresh, always!"
        });

        birdPhrases.put("Sunny", new String[]{
                "Rise and shine, sunshine!",
                "Your energy is pure daylight.",
                "You're the yolk to my egg.",
                "Time for some bright bites!",
                "You’re glowing today!",
                "Butter up your day!",
                "Let’s scramble something fun!"
        });

        birdPhrases.put("Beakyoncé", new String[]{
                "You’re flawless.",
                "Beak it till you make it!",
                "Queen of the coop.",
                "Run the nest, run the kitchen.",
                "All the single chickadees!",
                "Sing it loud, snack it proud!",
                "Too fierce to fry!"
        });

        birdPhrases.put("Yolka", new String[]{
                "Sunny side up, just like you!",
                "Eggstraordinary effort today!",
                "You crack me up!",
                "Yolk’s on anyone who doubts you.",
                "You’re eggsactly what we needed.",
                "Stay golden and gooey.",
                "Whisking you lots of love!"
        });

        birdPhrases.put("Chirplin", new String[]{
                "Keep your wings strong!",
                "Lettuce turnip the beet!",
                "Green is your color—fresh and bold!",
                "Peas and love!",
                "You’re sprouting joy!",
                "Grow at your own pace.",
                "Rooting for you, always."
        });

        birdPhrases.put("Kiwi", new String[]{
                "Sweet, tart, and unstoppable!",
                "Kiwi can do it!",
                "Slice up something sweet!",
                "Tiny but mighty.",
                "You're fresh from the orchard!",
                "Always a-peel-ing!",
                "Tropical vibes activated!"
        });

        birdPhrases.put("Quillson", new String[]{
                "Classy with a splash of silly.",
                "Quill it and skill it!",
                "Your style is evergreen.",
                "You’re one fine feather!",
                "Today’s forecast: cool and crisp.",
                "Time to pen a snack-tastic story!",
                "Your wisdom is ripe."
        });

        birdPhrases.put("Beaky", new String[]{
                "Lookin’ sharp, Beaky!",
                "Keep peckin’ at your goals!",
                "Chillin’ like a blueberry pie.",
                "You're cool as ever!",
                "Flap the doubt away!",
                "You’re making waves today!",
                "Blue skies ahead!"
        });

        birdPhrases.put("Bluebell", new String[]{
                "Delicate but fierce!",
                "You bloom where you're planted.",
                "Singing in soft tones today?",
                "You’re as lovely as your name.",
                "Tea and tranquility, please!",
                "Gentle hearts fly high.",
                "Bloom with grace!"
        });

        birdPhrases.put("Peakley", new String[]{
                "You’re peak performance!",
                "Soar high, friend!",
                "Top of the beak to you!",
                "Nothing ruffles your feathers.",
                "High-flying style!",
                "You’re always on point.",
                "Feathers smooth, heart full."
        });

        birdPhrases.put("Feathersby", new String[]{
                "Dapper and delightful!",
                "Feather fancy today!",
                "You bring class to cuisine.",
                "Feathers, flair, and flavor!",
                "Let’s prepare with poise.",
                "Elegance in motion.",
                "Polished and powerful."
        });

        birdPhrases.put("Indiara", new String[]{
                "You’re ink-redibly inspiring.",
                "Shadows can't dim your shine.",
                "Graceful like the evening sky.",
                "Let your ideas flow.",
                "You're painting the world with calm.",
                "You make indigo glow!",
                "Mysterious and marvelous."
        });

        birdPhrases.put("Wingrove", new String[]{
                "Wind beneath our wings!",
                "Glide through the day.",
                "You’re a flight of brilliance.",
                "You uplift everyone.",
                "Feathers full of dreams.",
                "A sky full of you!",
                "Drift sweetly into peace."
        });

        birdPhrases.put("Finchley", new String[]{
                "Berry bold today!",
                "Vintage vibes only.",
                "You’re a blend of bright and wise.",
                "Feather-light and full of flavor!",
                "Purple reigns supreme!",
                "Keep it plush and polished.",
                "Delight in every bite!"
        });

        birdPhrases.put("Plummie", new String[]{
                "You’re plum perfect!",
                "You wear purple like royalty.",
                "Softness is strength.",
                "Let’s mix something mellow.",
                "Smooth as plum jam.",
                "Sweet with a bit of zing!",
                "You're the toast of today!"
        });

        birdPhrases.put("Wattle", new String[]{
                "Wattle you do next? Something great!",
                "Shake your feathers, friend!",
                "You're purplish perfection.",
                "The world needs your warmth.",
                "You’re wattle-ly wonderful!",
                "Gentle but unstoppable.",
                "Keep wobbling with confidence!"
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picker);

        GridView gridView = findViewById(R.id.profileGrid);
        ImageAdapter adapter = new ImageAdapter(this, profileImages, avatarNames);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedImage = profileImages[position];
            String selectedName = avatarNames[position];
            new AlertDialog.Builder(ProfilePickerActivity.this)
                    .setTitle("Set Profile Picture")
                    .setMessage("Use \"" + selectedName + "\" as your profile picture?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Save selected profile to SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("AvatarPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("avatarImage", selectedImage);
                        editor.putString("avatarName", selectedName);

                        // Save 5 phrases for this bird
                        String[] phrases = birdPhrases.getOrDefault(selectedName, new String[]{
                                "You're amazing!", "Keep flying!", "Let’s cook something!", "Chirp chirp!", "Today is yours!"
                        });

                        for (int i = 0; i < phrases.length; i++) {
                            editor.putString("phrase_" + i, phrases[i]);
                        }

                        editor.apply();

                        // Return result
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("selectedImageRes", selectedImage);
                        resultIntent.putExtra("selectedBirdName", selectedName);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

    }
}