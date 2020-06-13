package com.example.android.modulo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView image[] = new ImageView[6];
    Button[] button = new Button[5];
    ArrayList<String>cards = new ArrayList<>();
    Random rand = new Random();
    ArrayList<Integer>winner = new ArrayList<>(6);
    int money = 100, bet = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(); //Initialize all the images, buttons and the card directory paths
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.instructions:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.instructions)
                        .setTitle(R.string.howToPlay);
                builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * The init method initializes all the Imageview arrays, Button arrays and the image directory hashMap.
     * The hashMap stores the key in the form of a string, which implies the card number. The value is the corresponding drawable id.
     */
    private void init() {
        /**
         * Assign Imageview objects to corresponding cards
         */
        int id = R.id.card1;
        for (int i=0;i<image.length;++i) {
            image[i] = findViewById(id);
            id++;
        }
        /**
         * Assign Button objects to corresponding buttons
         */
        id = 1;
        for (int i=1;i<=button.length;++i) {
            button[i-1] = (Button) findViewById(getResources().getIdentifier("button" + id, "id",
                    this.getPackageName()));
            id++;
        }
        setCards();
    }

    /**
     * Assign image directories
     */
    private void setCards() {
        for (int i = 1; i <= 4; ++i) {
            for (int j = 1; j <= 13; ++j) {
                cards.add("card_"+i+j);
            }
        }
    }

    /**
     * Listener for the start button
     *
     * @param view The Button View
     */
    public void start(View view) {
        String s = ((EditText) findViewById(R.id.bet_amount)).getText().toString();
        if (s.length() == 0) {  //If user inputs nothing in the bet editText
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.error_bet).setTitle(R.string.error).show();
        } else {
            try {
                double d = Double.valueOf(s);
                if (d != Math.floor(d)) {  //If user does not input an integer
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.error_integer).setTitle(R.string.error).show();
                } else if (d < 0) { //If user does not input a positive number
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.error_positive).setTitle(R.string.error).show();
                } else if (d > money) { //If user bets more than he owns
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.error_money).setTitle(R.string.error).show();
                } else {
                    for (int i = 0; i <= 4; ++i) {
                        if (i == 3) {
                            button[i].setEnabled(false);
                            continue;
                        }
                        button[i].setEnabled(true);
                    }
                    bet = (int) d;
                    TextView v = findViewById(R.id.money_amount);
                    v.setText(getString(R.string.bet_result, bet, money));
                    Game(3);
                }
            } catch (NumberFormatException nfe) { //If input contains characters other than numbers
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.error_string).setTitle(R.string.error).show();
            }
        }
    }

    /**
     * Listener for the Replace 1 card button
     * @param view
     */

    public void replace1(View view) {
        button[0].setEnabled(false);
        if(!button[1].isEnabled()) {
            button[2].setEnabled(false);
        }
        else if(!button[2].isEnabled()) {
            button[1].setEnabled(false);
        }
        replace(image[3],3);
    }

    /**
     * Listener for the Replace 2 card button
     * @param view
     */
    public void replace2(View view) {
        button[1].setEnabled(false);
        if(!button[0].isEnabled()) {
            button[2].setEnabled(false);
        }
        else if(!button[2].isEnabled()) {
            button[0].setEnabled(false);
        }
        replace(image[4],4);
    }

    /**
     * Listener for the Replace 3 card button
     * @param view
     */
    public void replace3(View view) {
        button[2].setEnabled(false);
        if(!button[1].isEnabled()) {
            button[0].setEnabled(false);
        }
        else if(!button[0].isEnabled()) {
            button[1].setEnabled(false);
        }
        replace(image[5],5);
    }


    /**
     * Listener for the result button
     * @param view
     */
    public void result(View view) {
        Game(0);
    }





    /**
     * This function is called if the player has no more money left and has to start a new game
     */
    private void moneyLeft() {
        if(money<=0) {
            for(Button b: button) {
                b.setEnabled(false);
            }
            TextView v = findViewById(R.id.money_amount);
            v.setText(getString(R.string.end));
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.end).setTitle(R.string.over);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
            builder.show();
        }

        else {
            for (int i = 0; i < 6; ++i) {
                image[i].setImageResource(getResources().getIdentifier("card_back", "drawable", this.getPackageName()));
            }
            for (int i = 0; i < button.length; ++i) {
                if (i == 3) {
                    button[i].setEnabled(true);
                } else {
                    button[i].setEnabled(false);
                }
            }
            TextView v = findViewById(R.id.money_amount);
            v.setText(getString(R.string.bet_result, bet, money));
            setCards();
        }
    }

    /**
     * Implementation of the game. It is called when we start the game. A random number from 0 to 51 is selected. This is done to pick a card randomly.
     * Random card image directory from card ArrayList is selected for displaying image. The face value of the card is added to the winner ArrayList for determining the winner.
     * The card image directory selected is removed from the cards ArrayList to avoid picking duplicate cards. When all 6 cards are added, the Hand() method is called to pick the winner.
     *
     * @param c An integer which directs the program on whether to add cards in the dealer panel or player panel
     */
    private void Game(int c) {
        for(int i=c;i<=c+2;++i) {
            int index = rand.nextInt(cards.size());
            image[i].setImageResource(getResources().getIdentifier(cards.get(index),"drawable", this.getPackageName()));
            int s = Integer.valueOf(cards.get((index)).substring(6));
            winner.add(s);
            cards.remove(index);
            if(i==2) {
                Hand();
            }
        }
    }

    /**
     * This function is called only when the player wants to change one of their cards. Hence, only one card is changed, the face value of the card added to the winner ArrayList, and the corresponding card image directory removed from card ArrayList.
     * @param i Card Image directory
     * @param j An integer which directs the program on which card out of the 3 is being replaced
     */
    private void replace(ImageView i, int j) {
        int index = rand.nextInt(cards.size());
        i.setImageResource(getResources().getIdentifier(cards.get(index),"drawable", this.getPackageName()));
        Integer s = Integer.valueOf(cards.get((index)).substring(6));
        winner.set(j-3, s);
        cards.remove(index);
    }

    /**
     * Main implementation of the game to decide the winner. The for loops count the number of special cards for both the player and the dealer.
     * If both of them have same number of special cards, winner is chosen on face value of cards.
     */
    private void Hand() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        int playerSpecialCards=0, dealerSpecialCards=0, playerSum=0, dealerSum=0;
        for(int i=0;i<=2;++i) {
            if(winner.get(i)>10) {
                playerSpecialCards++;
            }
            playerSum+=winner.get(i);
        }
        for(int i=3;i<=5;++i) {
            if(winner.get(i)>10) {
                dealerSpecialCards++;
            }
            dealerSum+=winner.get(i);
        }
        if(playerSpecialCards>dealerSpecialCards) {
            money+=bet;
            builder.setMessage(R.string.win).setTitle(R.string.result);
        }
        else if(dealerSpecialCards>playerSpecialCards) {
            money-=bet;
            builder.setMessage(R.string.lose).setTitle(R.string.result);

        }
        else if(dealerSpecialCards==playerSpecialCards) {
            if((playerSum%10)>(dealerSum%10)) {
                money+=bet;
                builder.setMessage(R.string.win).setTitle(R.string.result);

            }
            else {
                money-=bet;
                builder.setMessage(R.string.lose).setTitle(R.string.result);
            }
        }
        winner.clear();
        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                moneyLeft();
            }
        });
        builder.show();
    }
}



//getResources().getIdentifier("card_" + i + j, "drawable", this.getPackageName())