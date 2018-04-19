package com.example.android.kik2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {
    private int [] boardStatus;
    private final int [] imagesID = {R.id.pole0, R.id.pole1, R.id.pole2, R.id.pole3, R.id.pole4,
    R.id.pole5, R.id.pole6, R.id.pole7, R.id.pole8};
    private ImageView [] boardImages;
    private String [] numbers  = {"NUM_0", "NUM_1", "NUM_2", "NUM_3", "NUM_4", "NUM_5", "NUM_6",
    "NUM_7", "NUM_8"};
    private boolean first_player = true;
    private boolean singlePlayer;
    private TextView communication;
    private boolean hasCrossWon;
    private boolean levelWasChoosed;
    private boolean firstPlayerIsCross;
    private int howWasWon;
    private String fpic = "FIRST_PLAYER_IS_CROSS";
    private String hcw = "HAS_CROSS_WON";
    private String lh = "LEVEL_IS_HARD";
    private String fp = "FIRST_PLAYER";
    private String lwc = "LEVEL_WAS_CHOOSED";
    private boolean difficultyLevel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Adding custom font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Quikhand.ttf");
        TextView tectview = (TextView) findViewById(R.id.greeting);
        tectview.setTypeface(font);

        //Checking if user has chosen single or multi player mode
        Intent intentStartingGame = getIntent();
        Bundle dane = intentStartingGame.getBundleExtra("EXTRA_MESSAGE");
        singlePlayer = dane.getBoolean("SINGLE_PLAYER");
        firstPlayerIsCross = dane.getBoolean("FIRST_IS_CROSS");

        /*
        Array containing game field situation
        0 - field is empty
        1 - field contains X
        -1 - field contains O
         */
        boardStatus = new int[9];
        for (int i=0;i<9;i++) {
                boardStatus[i] = 0;
            }

        CharSequence gameMode;
        communication = (TextView) findViewById(R.id.greeting);
        if (firstPlayerIsCross){
            communication.setText(R.string.player_one_cross);
        } else {
            communication.setText(R.string.player_one_circle);
        }
        levelWasChoosed = false;
        hasCrossWon = false;

        if (singlePlayer) {
            gameMode = getString(R.string.one_player);
        } else {
            gameMode = getString(R.string.two_players);
        }

        final Toast greetingMessage = Toast.makeText(getApplicationContext(), gameMode, Toast.LENGTH_SHORT);
        greetingMessage.show();

        if(!singlePlayer){
            levelWasChoosed = true;
        }

        boardImages = new ImageView[9];
        for(int i=0;i<9;i++){
            final int tmp = i;
            boardImages[i] = (ImageView) findViewById(imagesID[i]);
            boardImages[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (boardStatus[tmp]==0){
                        if (first_player && !hasSomeoneWon() && !isDraw()){

                            if (firstPlayerIsCross){
                                boardImages[tmp].setImageResource(R.drawable.cross);
                            } else {
                                boardImages[tmp].setImageResource(R.drawable.circle);
                            }

                            boardStatus[tmp] = 1;
                            first_player = false;
                            displayNews(singlePlayer);
                            showHowWasWon();

                            /*
                            If game is in single player mode application makes second players move
                             */
                            if (singlePlayer && !isDraw() && !hasSomeoneWon()){
                                makeMove();
                                displayNews(singlePlayer);
                                showHowWasWon();
                                first_player = true;
                            }

                        } else if (!first_player && !hasSomeoneWon() && !isDraw() && !singlePlayer){
                            if (firstPlayerIsCross){
                                boardImages[tmp].setImageResource(R.drawable.circle);
                            } else {
                                boardImages[tmp].setImageResource(R.drawable.cross);
                            }
                            boardStatus[tmp] = -1;
                            first_player = true;
                            displayNews(singlePlayer);
                            showHowWasWon();
                        }
                    }
                }
            });
        }

        if (savedInstanceState!=null) {
            for (int i=0;i<9;i++){
                boardStatus[i] = savedInstanceState.getInt(numbers[i]);
            }

            first_player = savedInstanceState.getBoolean(fp);
            levelWasChoosed = savedInstanceState.getBoolean(lwc);
            hasCrossWon = savedInstanceState.getBoolean(hcw);
            firstPlayerIsCross = savedInstanceState.getBoolean(fpic);
        }

        //If screen was rotated app restores game field state
        this.refreshImages();
    }

    /*
    Method setting correct image on game field if field is taken.
    It's called after screen rotation to refresh screen state.
     */
    private void refreshImages(){
        for (int i=0;i<9;i++)
                if (boardStatus[i]==0){
                    boardImages[i].setImageResource(R.drawable.blank);
                } else if(boardStatus[i]==1){
                    if (firstPlayerIsCross){
                        boardImages[i].setImageResource(R.drawable.cross);
                    } else {
                        boardImages[i].setImageResource(R.drawable.circle);
                    }
                } else if (boardStatus[i]==-1){
                    if (firstPlayerIsCross){
                        boardImages[i].setImageResource(R.drawable.circle);
                    } else {
                        boardImages[i].setImageResource(R.drawable.cross);
                    }
                }
    }

    /*
    Method saving game fields situation on screen rotation when screen is rotated.
    */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        for (int i=0;i<9;i++){
            savedInstanceState.putInt(numbers[i], boardStatus[i]);
        }

        savedInstanceState.putBoolean(fp, first_player);
        savedInstanceState.putBoolean(lwc, levelWasChoosed);
        savedInstanceState.putBoolean(hcw, hasCrossWon);
        savedInstanceState.putBoolean(fpic, firstPlayerIsCross);
    }

    /*
    Method getting field situation after screen rotation.
    */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        for (int i=0;i<9;i++){
            boardStatus[i] = savedInstanceState.getInt(numbers[i]);
        }

        first_player = savedInstanceState.getBoolean(fp);
        levelWasChoosed = savedInstanceState.getBoolean(lwc);
        hasCrossWon = savedInstanceState.getBoolean(hcw);
        firstPlayerIsCross = savedInstanceState.getBoolean(fpic);
    }

    /*
    Method checking if someone has won.
    Numbers from 0 to 8 represent different winning combinations.
     */
    private boolean hasSomeoneWon(){

        //Checking horizontal options
        for (int i=0;i<=6;i+=3) {
            if ((boardStatus[i]==boardStatus[i+1])&&(boardStatus[i]==boardStatus[i+2])&&(boardStatus[i]!=0)){
                howWasWon = (int) i/3;
                if (boardStatus[i]==1) {
                    hasCrossWon = true;
                }
                else {
                    hasCrossWon = false;
                }
                return true;
            }
        }

        //Checking vertical options
        for (int i=0;i<=2;i++){
            if ((boardStatus[i]==boardStatus[i+3])&&(boardStatus[i]==boardStatus[i+6])&&(boardStatus[i]!=0)){
                howWasWon = i+3;
                if (boardStatus[i]==1) {
                    hasCrossWon = true;
                }
                else {
                    hasCrossWon = false;
                }
                return true;
            }
        }

        //Checking crosswise options
        if ((boardStatus[0]==boardStatus[4])&&(boardStatus[0]==boardStatus[8])&&(boardStatus[0]!=0)){
           howWasWon = 6;
            if (boardStatus[0]==1) {
                hasCrossWon = true;
            }
            else {
                hasCrossWon = false;
            }
            return true;
        }

        if ((boardStatus[2]==boardStatus[4])&&(boardStatus[2]==boardStatus[6])&&(boardStatus[2]!=0)){
            howWasWon = 7;
            if (boardStatus[2]==1) {
                hasCrossWon = true;
            }
            else {
                hasCrossWon = false;
            }
            return true;
        }

        return false;
    }

    /*
    Checking if game has ended with draw.
     */
    private boolean isDraw(){
        boolean isdraw = true;
        for (int i=0;i<9;i++) {
            if (boardStatus[i] == 0) {
                isdraw = false;
            }
        }

        return isdraw;
    }

    /*
    Method changing message above game field. It informs which users has to make move,
    and notifies when games with someones victory or draw.
     */
    private void displayNews(boolean singleplayer){
        if (!hasSomeoneWon() && !isDraw() && first_player){
            if (firstPlayerIsCross){
                communication.setText(R.string.player_one_cross);
            } else {
                communication.setText(R.string.player_one_circle);
            }
        } else if (!hasSomeoneWon() && !isDraw() && !first_player && !singleplayer){
            if (firstPlayerIsCross){
                communication.setText(R.string.player_two_circle);
            } else {
                communication.setText(R.string.player_two_cross);
            }
        } else if (hasSomeoneWon() && !isDraw() && hasCrossWon){
            communication.setText(R.string.player_one_won);
        } else if (hasSomeoneWon() && !isDraw() && !hasCrossWon && !singleplayer){
            communication.setText(R.string.player_two_won);
        } else if (hasSomeoneWon() && !isDraw() && !hasCrossWon && singleplayer){
            communication.setText(R.string.computer_won);
        } else if (!hasCrossWon && isDraw()){
            communication.setText(R.string.draw);
        }
    }

    /*
    Method checking who and how has won, to correctly colorise game field.
     */
    private void showHowWasWon(){
        if (hasSomeoneWon()){
            if (hasCrossWon){
                switch (howWasWon){
                    case (0):
                        if (firstPlayerIsCross){
                            boardImages[0].setImageResource(R.drawable.cross_horisontal);
                            boardImages[1].setImageResource(R.drawable.cross_horisontal);
                            boardImages[2].setImageResource(R.drawable.cross_horisontal);
                        } else {
                            boardImages[0].setImageResource(R.drawable.circle_horisontal);
                            boardImages[1].setImageResource(R.drawable.circle_horisontal);
                            boardImages[2].setImageResource(R.drawable.circle_horisontal);
                        }
                        break;
                    case (1):
                        if (firstPlayerIsCross){
                            boardImages[3].setImageResource(R.drawable.cross_horisontal);
                            boardImages[4].setImageResource(R.drawable.cross_horisontal);
                            boardImages[5].setImageResource(R.drawable.cross_horisontal);
                        } else {
                            boardImages[3].setImageResource(R.drawable.circle_horisontal);
                            boardImages[4].setImageResource(R.drawable.circle_horisontal);
                            boardImages[5].setImageResource(R.drawable.circle_horisontal);
                        }
                        break;
                    case (2):
                        if (firstPlayerIsCross){
                            boardImages[6].setImageResource(R.drawable.cross_horisontal);
                            boardImages[7].setImageResource(R.drawable.cross_horisontal);
                            boardImages[8].setImageResource(R.drawable.cross_horisontal);
                        } else {
                            boardImages[6].setImageResource(R.drawable.circle_horisontal);
                            boardImages[7].setImageResource(R.drawable.circle_horisontal);
                            boardImages[8].setImageResource(R.drawable.circle_horisontal);
                        }
                        break;
                    case (3):
                        if (firstPlayerIsCross){
                            boardImages[0].setImageResource(R.drawable.cross_vertical);
                            boardImages[3].setImageResource(R.drawable.cross_vertical);
                            boardImages[6].setImageResource(R.drawable.cross_vertical);
                        } else {
                            boardImages[0].setImageResource(R.drawable.circle_vertical);
                            boardImages[3].setImageResource(R.drawable.circle_vertical);
                            boardImages[6].setImageResource(R.drawable.circle_vertical);
                        }
                        break;
                    case (4):
                        if (firstPlayerIsCross){
                            boardImages[1].setImageResource(R.drawable.cross_vertical);
                            boardImages[4].setImageResource(R.drawable.cross_vertical);
                            boardImages[7].setImageResource(R.drawable.cross_vertical);
                        } else {
                            boardImages[1].setImageResource(R.drawable.circle_vertical);
                            boardImages[4].setImageResource(R.drawable.circle_vertical);
                            boardImages[7].setImageResource(R.drawable.circle_vertical);
                        }
                        break;
                    case (5):
                        if (firstPlayerIsCross){
                            boardImages[2].setImageResource(R.drawable.cross_vertical);
                            boardImages[5].setImageResource(R.drawable.cross_vertical);
                            boardImages[8].setImageResource(R.drawable.cross_vertical);
                        } else {
                            boardImages[2].setImageResource(R.drawable.circle_vertical);
                            boardImages[5].setImageResource(R.drawable.circle_vertical);
                            boardImages[8].setImageResource(R.drawable.circle_vertical);
                        }
                        break;
                    case (6):
                        if (firstPlayerIsCross){
                            boardImages[0].setImageResource(R.drawable.cross_left_right);
                            boardImages[4].setImageResource(R.drawable.cross_left_right);
                            boardImages[8].setImageResource(R.drawable.cross_left_right);
                        } else {
                            boardImages[0].setImageResource(R.drawable.circle_left_right);
                            boardImages[4].setImageResource(R.drawable.circle_left_right);
                            boardImages[8].setImageResource(R.drawable.circle_left_right);
                        }
                        break;
                    case (7):
                        if (firstPlayerIsCross){
                            boardImages[2].setImageResource(R.drawable.cross_right_left);
                            boardImages[4].setImageResource(R.drawable.cross_right_left);
                            boardImages[6].setImageResource(R.drawable.cross_right_left);
                        } else {
                            boardImages[2].setImageResource(R.drawable.circle_right_left);
                            boardImages[4].setImageResource(R.drawable.circle_right_left);
                            boardImages[6].setImageResource(R.drawable.circle_right_left);
                        }
                }
            } else if (hasCrossWon==false){
                switch (howWasWon){
                    case (0):
                        if (firstPlayerIsCross){
                            boardImages[0].setImageResource(R.drawable.circle_horisontal);
                            boardImages[1].setImageResource(R.drawable.circle_horisontal);
                            boardImages[2].setImageResource(R.drawable.circle_horisontal);
                        } else {
                            boardImages[0].setImageResource(R.drawable.cross_horisontal);
                            boardImages[1].setImageResource(R.drawable.cross_horisontal);
                            boardImages[2].setImageResource(R.drawable.cross_horisontal);
                        }
                        break;
                    case (1):
                        if (firstPlayerIsCross){
                            boardImages[3].setImageResource(R.drawable.circle_horisontal);
                            boardImages[4].setImageResource(R.drawable.circle_horisontal);
                            boardImages[5].setImageResource(R.drawable.circle_horisontal);
                        } else {
                            boardImages[3].setImageResource(R.drawable.cross_horisontal);
                            boardImages[4].setImageResource(R.drawable.cross_horisontal);
                            boardImages[5].setImageResource(R.drawable.cross_horisontal);
                        }
                        break;
                    case (2):
                        if (firstPlayerIsCross){
                            boardImages[6].setImageResource(R.drawable.circle_horisontal);
                            boardImages[7].setImageResource(R.drawable.circle_horisontal);
                            boardImages[8].setImageResource(R.drawable.circle_horisontal);
                        } else {
                            boardImages[6].setImageResource(R.drawable.cross_horisontal);
                            boardImages[7].setImageResource(R.drawable.cross_horisontal);
                            boardImages[8].setImageResource(R.drawable.cross_horisontal);
                        }
                        break;
                    case (3):
                        if (firstPlayerIsCross){
                            boardImages[0].setImageResource(R.drawable.circle_vertical);
                            boardImages[3].setImageResource(R.drawable.circle_vertical);
                            boardImages[6].setImageResource(R.drawable.circle_vertical);
                        } else {
                            boardImages[0].setImageResource(R.drawable.cross_vertical);
                            boardImages[3].setImageResource(R.drawable.cross_vertical);
                            boardImages[6].setImageResource(R.drawable.cross_vertical);
                        }
                        break;
                    case (4):
                        if (firstPlayerIsCross){
                            boardImages[1].setImageResource(R.drawable.circle_vertical);
                            boardImages[4].setImageResource(R.drawable.circle_vertical);
                            boardImages[7].setImageResource(R.drawable.circle_vertical);
                        } else {
                            boardImages[1].setImageResource(R.drawable.cross_vertical);
                            boardImages[4].setImageResource(R.drawable.cross_vertical);
                            boardImages[7].setImageResource(R.drawable.cross_vertical);
                        }
                        break;
                    case (5):
                        if (firstPlayerIsCross){
                            boardImages[2].setImageResource(R.drawable.circle_vertical);
                            boardImages[5].setImageResource(R.drawable.circle_vertical);
                            boardImages[8].setImageResource(R.drawable.circle_vertical);
                        } else {
                            boardImages[2].setImageResource(R.drawable.cross_vertical);
                            boardImages[5].setImageResource(R.drawable.cross_vertical);
                            boardImages[8].setImageResource(R.drawable.cross_vertical);
                        }
                        break;
                    case (6):
                        if (firstPlayerIsCross){
                            boardImages[0].setImageResource(R.drawable.circle_left_right);
                            boardImages[4].setImageResource(R.drawable.circle_left_right);
                            boardImages[8].setImageResource(R.drawable.circle_left_right);
                        } else {
                            boardImages[0].setImageResource(R.drawable.cross_left_right);
                            boardImages[4].setImageResource(R.drawable.cross_left_right);
                            boardImages[8].setImageResource(R.drawable.cross_left_right);
                        }
                        break;
                    case (7):
                        if (firstPlayerIsCross){
                            boardImages[2].setImageResource(R.drawable.circle_right_left);
                            boardImages[4].setImageResource(R.drawable.circle_right_left);
                            boardImages[6].setImageResource(R.drawable.circle_right_left);
                        } else {
                            boardImages[2].setImageResource(R.drawable.cross_right_left);
                            boardImages[4].setImageResource(R.drawable.cross_right_left);
                            boardImages[6].setImageResource(R.drawable.cross_right_left);
                        }
                }
            }
        }
    }

    /*
    Method adding custom menu bar.
     */
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    /*
    Method handling click events on custom menu bar elements.
    One button restarts game by clearing game field and second allows user to return to main menu.
     */
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId()==R.id.reload_button){
            for (int i=0;i<9;i++){
                boardStatus[i] = 0;
                boardImages[i].setImageResource(R.drawable.blank);
            }
            this.refreshImages();
            first_player = true;
            communication.setText(R.string.player_one);
        } else if (item.getItemId()==R.id.back_button){
            finish();
        }
        return true;
    }

    /*
    Method making seconds player move if game is in single player mode.
     */
    private void makeMove(){

        if (difficultyLevel && !areTwoInRow(true) && !areTwoInColumn(true) && !areTwoAcross(true) && !areTwoInRow(false) && !areTwoInColumn(false) && !areTwoInColumn(false)){
            if ((boardStatus[0]==1||boardStatus[2]==0||boardStatus[6]==0||boardStatus[8]==0)&&boardStatus[4]==0) {
                //If no one has two signs in row app randomly chooses field.
                //If player has chosen corner field computer chooses central.
                boardStatus[4] = -1;
                if (firstPlayerIsCross){
                    boardImages[4].setImageResource(R.drawable.circle);
                } else {
                    boardImages[4].setImageResource(R.drawable.cross);
                }
            } else if ((boardStatus[4]==1)&&(boardStatus[0]==0||boardStatus[2]==0||boardStatus[6]==0||boardStatus[8]==0)){
                //If player has chosen central field computer chooses corner
                for (int i=0;i<=8;i++){
                    switch (i){
                        case (0):
                            if (boardStatus[0]==0){
                                if (firstPlayerIsCross){
                                    boardImages[0].setImageResource(R.drawable.circle);
                                } else {
                                    boardImages[0].setImageResource(R.drawable.cross);
                                }
                                boardStatus[0] = -1;
                                return;
                            }
                        case (2):
                            if (boardStatus[2]==0){
                                if (firstPlayerIsCross){
                                    boardImages[2].setImageResource(R.drawable.circle);
                                } else {
                                    boardImages[2].setImageResource(R.drawable.cross);
                                }
                                boardStatus[2] = -1;
                                return;
                            }
                        case (6):
                            if (boardStatus[6]==0){
                                if (firstPlayerIsCross){
                                    boardImages[6].setImageResource(R.drawable.circle);
                                } else {
                                    boardImages[6].setImageResource(R.drawable.cross);
                                }
                                boardStatus[6] = -1;
                                return;
                            }
                        case (8):
                            if (boardStatus[8]==0){
                                if (firstPlayerIsCross){
                                    boardImages[8].setImageResource(R.drawable.circle);
                                } else {
                                    boardImages[8].setImageResource(R.drawable.cross);
                                }
                                boardStatus[8] = -1;
                                return;
                            }
                        default:
                            break;
                    }
                }
            } else {
                //If none of above situations happens computer chooses field randomly.
                int min = 0;
                int max = 8;
                boolean isGood = false;
                int nowepole = -1;

                while(!isGood){
                    nowepole = min + (int)(Math.random() * ((max - min) + 1));
                    if(boardStatus[nowepole]==0)
                        isGood = true;
                }

                boardStatus[nowepole] = -1;
                if (firstPlayerIsCross){
                    boardImages[nowepole].setImageResource(R.drawable.circle);
                } else {
                    boardImages[nowepole].setImageResource(R.drawable.cross);
                }
            }
        }
    }

    /*
    Method setting computers sign on free field if possible
     */
    private boolean moveWasMade(int num1, int num2, int num3){
        if (boardStatus[num1]==0){
            boardStatus[num1] = -1;
            if (firstPlayerIsCross){
                boardImages[num1].setImageResource(R.drawable.circle);
            } else {
                boardImages[num1].setImageResource(R.drawable.cross);
            }
            return true;
        } else if (boardStatus[num2]==0){
            boardStatus[num2] = -1;
            if (firstPlayerIsCross){
                boardImages[num2].setImageResource(R.drawable.circle);
            } else {
                boardImages[num2].setImageResource(R.drawable.cross);
            }
            return true;
        } else if (boardStatus[num3]==0){
            boardStatus[num3] = -1;
            if (firstPlayerIsCross){
                boardImages[num3].setImageResource(R.drawable.circle);
            } else {
                boardImages[num3].setImageResource(R.drawable.cross);
            }
            return true;
        }

        return false;
    }

    /*
    Method checking if player has two fields in a row. If so computer puts its sign on the last
    free field and method returns true. If no method returns false.
     */
    private boolean areTwoInRow(boolean isCross){
        int count = 0;
        int point;

        if(isCross){
            point = 1;
        } else {
            point = -1;
        }

        //Row 0
        for (int i=0;i<3;i++)
            if (boardStatus[i]==point)
                count++;

        if (count==2)
            if (moveWasMade(0, 1, 2))
                return true;

        //Row 1
        count = 0;

        for (int i=3;i<6;i++)
            if (boardStatus[i]==point)
                count++;

        if (count==2)
            if (moveWasMade(3, 4, 5))
                return true;

        //Row 2
        count = 0;

        for (int i=6;i<9;i++)
            if (boardStatus[i]==point)
                count++;

        if (count==2)
            if (moveWasMade(6, 7, 8))
                return true;

        return false;
    }

    /*
    Method checking if player has two fields in a column. If so computer puts its sign on the last
    free field and method returns true. If no method returns false.
     */
    private boolean areTwoInColumn(boolean isCross){
        int count = 0;
        int point;

        if (isCross)
            point = 1;
        else
            point = -1;

        //Column 0
        for(int i=0;i<9;i+=3)
            if (boardStatus[i]==point)
                count++;

        if (count==2)
            if (moveWasMade(0, 3, 6))
                return true;

        //Column 1
        count = 0;

        for (int i=1;i<9;i+=3)
            if (boardStatus[i]==point)
                count++;

        if (count==2)
            if (moveWasMade(1, 4, 7))
                return true;

        //Column 2
        count = 0;

        for (int i=2;i<9;i+=3)
            if (boardStatus[i]==point)
                count++;

        if (count==2)
            if (moveWasMade(2, 5, 8))
                return true;

        return false;
    }

    /*
    Method checking if player has two fields in cross. If so computer puts its sign on the last
    free field and method returns true. If no method returns false.
     */
    private boolean areTwoAcross(boolean isCross){
        int point;
        int count = 0;

        if (isCross)
            point = 1;
        else
            point = -1;

        //Checking left to right
        for (int i=0;i<9;i+=4)
            if (boardStatus[i]==point)
                count++;

        if (count==2)
            if (moveWasMade(0, 4, 8))
                return true;

        //Checking right to left
        count = 0;

        for (int i=2;i<=6;i+=2)
            if (boardStatus[i]==point)
                count++;

        if (count==2)
            if (moveWasMade(2, 4, 6))
                return true;

        return false;
    }


}