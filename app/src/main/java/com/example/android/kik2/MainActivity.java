package com.example.android.kik2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private boolean singlePlayer = true;
    private Bundle dane = new Bundle();
    private boolean firstPlayerIsCross = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActivityBackgroundColor();

        //Setting custom font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Quikhand.ttf");
        Button button = (Button) findViewById(R.id.button_exit);
        button.setTypeface(font);
        button = (Button) findViewById(R.id.button_one_player);
        button.setTypeface(font);
        button = (Button) findViewById(R.id.button_two_players);
        button.setTypeface(font);

        TextView text = (TextView) findViewById(R.id.text_choose_sign);
        text.setTypeface(font);

    }

    /*
    Method allowing user to choose single or multi player mode.
    */
    public void chooseMainMenuOption(View view){
        Intent intentToStartGame = new Intent(this, GameActivity.class);

        switch (view.getId()){
            case (R.id.button_one_player):
                //Single player mode was chosen.
                singlePlayer = true;
                //If single player mode was chosen user must choose his sign.
                changeMenu(false);
                break;
            case (R.id.button_two_players):
                //Multi player mode was chosen.
                singlePlayer = false;
                firstPlayerIsCross = true;
                dane.putBoolean("SINGLE_PLAYER", singlePlayer);
                dane.putBoolean("FIRST_IS_CROSS", firstPlayerIsCross);
                intentToStartGame.putExtra("EXTRA_MESSAGE", dane);
                startActivity(intentToStartGame);
                break;
            case (R.id.button_exit):
                //User has chosen to exit app.
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
        }

    }

    /*
    Method allowing user to chose his sign.
     */
    public void chooseYourSign(View view){
        Intent intentToStartGame = new Intent(this, GameActivity.class);

        if (view.getId()==R.id.button_cross){
            firstPlayerIsCross = true;
        } else if (view.getId()==R.id.button_circle){
            firstPlayerIsCross = false;
        }

        dane.putBoolean("SINGLE_PLAYER", singlePlayer);
        dane.putBoolean("FIRST_IS_CROSS", firstPlayerIsCross);
        intentToStartGame.putExtra("EXTRA_MESSAGE", dane);

        //After choosing game sign menu is restored
        changeMenu(true);

        startActivity(intentToStartGame);
    }

    /*
    Method changing activity background color
     */
    public void setActivityBackgroundColor(){
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(Color.BLACK);
        view.invalidate();
    }

    /*
    Method changing main menu so that user can choose his game sign and then returns to previous state.
     If argument is true game mode menu is displayed, if false sign choose menu is displayed.
     */
    private void changeMenu(boolean restoreMenu){

        //Menu fo choosing players sign
        Button button = (Button) findViewById(R.id.button_cross);
        if (restoreMenu){
            button.setVisibility(View.INVISIBLE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
        button.setClickable(!restoreMenu);

        button = (Button) findViewById(R.id.button_circle);
        if (restoreMenu){
            button.setVisibility(View.INVISIBLE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
        button.setClickable(!restoreMenu);

        TextView text = (TextView) findViewById(R.id.text_choose_sign);
        if (restoreMenu){
            text.setVisibility(View.INVISIBLE);
        } else {
            text.setVisibility(View.VISIBLE);
        }
        text.setClickable(!restoreMenu);

        //Menu for choosing game mode
        button = (Button) findViewById(R.id.button_one_player);
        if (restoreMenu){
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
        }
        button.setClickable(restoreMenu);

        button = (Button) findViewById(R.id.button_two_players);
        if (restoreMenu){
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
        }
        button.setClickable(restoreMenu);

        button = (Button) findViewById(R.id.button_exit);
        if (restoreMenu){
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
        }
        button.setClickable(restoreMenu);
    }

    /*
    Method adding custom menu bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preference_menu, menu);
        return true;
    }

    /*
    Method handling clicks on menu bar items
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.settings){
            Intent settingsIntent = new Intent(this, PreferenceActivity.class);
            startActivity(settingsIntent);
        }
        return true;
    }

}
