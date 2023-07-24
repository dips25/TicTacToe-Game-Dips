package com.example.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tictactoe.Models.Board;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int WON = 1;
    private static final int NOT_DONE = 0;
    boolean compwaiting = true;
    boolean pwaiting = false;
    StringBuilder stringBuilder = new StringBuilder(9);

    Random random = new Random();
    int comp = 0;

    FrameLayout layout = null;

    ArrayList<Integer> BOARD_BUTTONS = new ArrayList<>();

    int[] FINAL_BUTTONS = {R.id.topLeft, R.id.topMiddle, R.id.topRight, R.id.middleLeft, R.id.middleMiddle, R.id.middleRight
            , R.id.bottomLeft, R.id.bottomMiddle, R.id.bottomRight};


    //Views
    TextView victory;
    Button restart;

    Board board;

    ProgressBar progressBar;
    private int TIED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //board = new Board();



        victory = (TextView) findViewById(R.id.victory);
        restart = (Button) findViewById(R.id.restartButton);

        BOARD_BUTTONS.add(R.id.topLeft);
        BOARD_BUTTONS.add(R.id.topMiddle);
        BOARD_BUTTONS.add(R.id.topRight);
        BOARD_BUTTONS.add(R.id.middleLeft);
        BOARD_BUTTONS.add(R.id.middleMiddle);
        BOARD_BUTTONS.add(R.id.middleRight);
        BOARD_BUTTONS.add(R.id.bottomLeft);
        BOARD_BUTTONS.add(R.id.bottomMiddle);
        BOARD_BUTTONS.add(R.id.bottomRight);

        stringBuilder.append("---------");

        progressBar = new ProgressBar(MainActivity.this);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.INVISIBLE);


    }

    public void clickSquare(View v) {

        if (!pwaiting && compwaiting) {

            int id = v.getId();

            FrameLayout frameLayout = (FrameLayout) findViewById(id);
            ImageView imageView = (ImageView) frameLayout.getChildAt(0);
            imageView.setBackground(null);
            imageView.setImageDrawable(null);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.cross));
            imageView.setVisibility(View.VISIBLE);

            for (int i = 0; i < BOARD_BUTTONS.size(); i++) {

                if (BOARD_BUTTONS.get(i) == id) {

                    stringBuilder.setCharAt(i, 'X');
                    BOARD_BUTTONS.set(i , 0);

                }
            }


            //fillCells(stringBuilder.toString());

            //victory.setText(stringBuilder.toString());

            Log.d(MainActivity.class.getSimpleName(), "clickSquare: " + stringBuilder.toString());




        }


        int rowstatus = checkRowsCols(stringBuilder.toString());
        int diagonalstatus = checkDiagonals(stringBuilder.toString());

        Log.d(MainActivity.class.getSimpleName(), "clickSquare: " + rowstatus +" "+ diagonalstatus);

        int finalStatus = rowstatus | diagonalstatus;

        pwaiting = true;
        compwaiting = false;

        if (finalStatus == NOT_DONE) {

           int tiestatus =  getTieStatus();

           if (tiestatus == TIED) {

               AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                       .setIcon(R.mipmap.ic_launcher_round)
                       .setTitle("Game Tied")
                       .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                               resetGame(new View(MainActivity.this));
                           }
                       }).setCancelable(false)

                       .show();

               return;


           }

            comp = random.nextInt(stringBuilder.length());

            getComputerMove(stringBuilder.toString(), comp);


        } else if (finalStatus == WON){

            getVictory();

        }


    }

    private int getTieStatus() {

        if (stringBuilder.indexOf("-") == -1) {

            return TIED;
        }

        return NOT_DONE;

    }

    private void getComputerMove(String boardString, int mcomp) {

        //Board board1 = new Board();
        //board1.setState(boardString);
        fillCells(boardString);

        comp = mcomp;

        progressBar.setVisibility(View.VISIBLE);


        //board1.setState(boardString);
        //fillCells(board1.getState());


        if (comp == 0) {

            if (BOARD_BUTTONS.get(mcomp) != 0) {

                FrameLayout layout = (FrameLayout) findViewById(BOARD_BUTTONS.get(mcomp));
                ImageView imageView = (ImageView) layout.getChildAt(0);
                imageView.setImageDrawable(null);
                imageView.setBackground(null);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.cross_red));
                imageView.setVisibility(View.VISIBLE);

                stringBuilder.setCharAt(mcomp , 'O');
                BOARD_BUTTONS.set(mcomp , 0);
                Log.d(MainActivity.class.getSimpleName(), "clickSquare: " + stringBuilder.toString());

                int rowstatus = checkRowsCols(stringBuilder.toString());
                int diagonalstatus = checkDiagonals(stringBuilder.toString());

                int finalStatus = rowstatus | diagonalstatus;

                pwaiting = false;
                compwaiting = true;

                Log.d(MainActivity.class.getSimpleName(), "getComputerMove: " + rowstatus + " " + diagonalstatus);

                if (finalStatus == NOT_DONE) {

                    int tiestatus = getTieStatus();

                    if (tiestatus == TIED) {

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Game Tied")
                                .setIcon(R.mipmap.ic_launcher_round)
                                .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        resetGame(new View(MainActivity.this));
                                    }
                                }).setCancelable(false)

                                .show();

                        return;


                    }

                    Toast.makeText(MainActivity.this, "Players Turn", Toast.LENGTH_SHORT).show();

                } else {

                    getVictory();
                }

                progressBar.setVisibility(View.GONE);


                progressBar.setVisibility(View.GONE);

            } else {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        generateUnique(comp);

                    }
                }).start();

            }

        } else {

            if (BOARD_BUTTONS.get(mcomp) != 0) {

                FrameLayout layout = (FrameLayout) findViewById(BOARD_BUTTONS.get(mcomp));
                ImageView imageView = (ImageView) layout.getChildAt(0);
                imageView.setImageDrawable(null);
                imageView.setBackground(null);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.cross_red));
                imageView.setVisibility(View.VISIBLE);

                stringBuilder.setCharAt(mcomp , 'O');
                BOARD_BUTTONS.set(mcomp , 0);

                int rowstatus = checkRowsCols(stringBuilder.toString());
                int diagonalstatus = checkDiagonals(stringBuilder.toString());

                int finalStatus = rowstatus | diagonalstatus;

                pwaiting = false;
                compwaiting = true;

                Log.d(MainActivity.class.getSimpleName(), "getComputerMove: " + rowstatus + " " + diagonalstatus);

                if (finalStatus == NOT_DONE) {

                    int tiestatus = getTieStatus();

                    if (tiestatus == TIED) {

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Game Tied")
                                .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        resetGame(new View(MainActivity.this));
                                    }
                                }).setCancelable(false)

                                .show();

                        return;


                    }

                    Toast.makeText(MainActivity.this, "Players Turn", Toast.LENGTH_SHORT).show();

                } else {

                    getVictory();
                }

                progressBar.setVisibility(View.GONE);


                Log.d(MainActivity.class.getSimpleName(), "clickSquare: " + stringBuilder.toString());

            } else {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (BOARD_BUTTONS.get(comp) == 0) {

                          comp  =  random.nextInt(stringBuilder.length());

                        }

                        generateUnique(comp);

                    }
                }).start();


            }


        }




    }

    private int generateUnique(int fcomp) {


            layout = (FrameLayout) findViewById(BOARD_BUTTONS.get(fcomp));

            if (layout != null) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ImageView imageView = (ImageView) layout.getChildAt(0);
                        imageView.setBackground(null);
                        imageView.setImageDrawable(null);
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.cross_red));
                        imageView.setVisibility(View.VISIBLE);

                        stringBuilder.setCharAt(fcomp, 'O');
                        BOARD_BUTTONS.set(fcomp, 0);

                        Log.d(MainActivity.class.getSimpleName(), "clickSquare: " + stringBuilder.toString());



                        int rowstatus = checkRowsCols(stringBuilder.toString());
                        int diagonalstatus = checkDiagonals(stringBuilder.toString());

                        int finalStatus = rowstatus | diagonalstatus;

                        pwaiting = false;
                        compwaiting = true;

                        Log.d(MainActivity.class.getSimpleName(), "getComputerMove: " + rowstatus + " " + diagonalstatus);

                        if (finalStatus == NOT_DONE) {

                            int tiestatus = getTieStatus();

                            if (tiestatus == TIED) {

                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Game Tied")
                                        .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                resetGame(new View(MainActivity.this));
                                            }
                                        }).setCancelable(false)

                                        .show();

                                return;


                            }

                            Toast.makeText(MainActivity.this, "Players Turn", Toast.LENGTH_SHORT).show();

                        } else {

                            getVictory();
                        }

                        progressBar.setVisibility(View.GONE);

                    }
                });


            }




        return this.comp;

    }

    private void fillCells(String boardString) {

        for (int i = 0; i < BOARD_BUTTONS.size(); i++) {

            if (stringBuilder.charAt(i) == 'X' || stringBuilder.charAt(i) == 'O') {

                continue;

            } else {

                stringBuilder.setCharAt(i, '-');
            }
        }




    }

    private int checkDiagonals(String string) {

        int status = 0;

        String section = checkSection(string, 2, 4, 6);
        status |= getStatus(section);

        String section1 = checkSection(string, 0, 4, 8);
        status |= getStatus(section1);

        return status;


    }

    private void getVictory() {

        if (pwaiting) {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setTitle("You Won")
                    .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            resetGame(new View(MainActivity.this));
                        }
                    }).setCancelable(false)

                    .show();

        } else if (compwaiting) {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setTitle("You Lost")
                    .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            resetGame(new View(MainActivity.this));
                        }
                    }).setCancelable(false)

                    .show();
        }
    }


    private int checkRowsCols(String boardString) {

        int status = 0;

        for (int i = 0; i < 3; i++) {

            String section = checkSection(boardString, i*3, (i * 3) + 1, (i * 3) + 2);
            status |= getStatus(section);

            String section1 = checkSection(boardString, i, (i + 3), (i + 6));
            status |= getStatus(section1);
        }

        return status;


    }

    private int getStatus(String section) {

        char a = section.charAt(0);
        char b = section.charAt(1);
        char c = section.charAt(2);

        if (a == b && a == c) {

            if (a == 'X' || a == 'O') {

                disableAll();
                return WON;
            }
        }

        if (stringBuilder.indexOf("-") == -1) {

            disableAll();
            return TIED;
        }

        return NOT_DONE;
    }

    private void disableAll() {

        for (int i = 0 ; i<BOARD_BUTTONS.size() ; i++) {

            if (BOARD_BUTTONS.get(i) != 0) {

                FrameLayout frameLayout = (FrameLayout) findViewById(BOARD_BUTTONS.get(i));
                ImageView imageView = (ImageView) frameLayout.getChildAt(0);
                imageView.setVisibility(View.INVISIBLE);
                frameLayout.setClickable(false);

            }
        }
    }


    private String checkSection(String boardString, int i, int i1, int i2) {

        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(boardString.charAt(i));
        stringBuilder1.append(boardString.charAt(i1));
        stringBuilder1.append(boardString.charAt(i2));
        return stringBuilder1.toString();


    }

    public void resetGame(View view) {

        for (int i = 0 ; i<stringBuilder.length() ; i++) {

            stringBuilder.setCharAt(i , '-');
        }

        for (int i = 0 ; i<FINAL_BUTTONS.length ; i++) {

            BOARD_BUTTONS.set(i , FINAL_BUTTONS[i]);
            FrameLayout layout1 = (FrameLayout) findViewById(FINAL_BUTTONS[i]);
            ImageView imageView = (ImageView) layout1.getChildAt(0);
            imageView.setBackground(null);
            imageView.setImageDrawable(null);
            imageView.setVisibility(View.INVISIBLE);
            layout1.setClickable(true);

            victory.setText("");
        }

        pwaiting = false;
        compwaiting = true;




    }
}