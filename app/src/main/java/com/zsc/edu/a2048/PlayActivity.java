package com.zsc.edu.a2048;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    private final float FLING_MIN_DISTANCE = 50;

    private final int bgpicTotal = 7;

    private ImageView background;

    private GameView playground;

    private Button replayButton;

    private TextView scoreText;

    private TextView stepText;

    private Random random = new Random();

    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        playground = findViewById(R.id.playground);
        replayButton = findViewById(R.id.replay);
        scoreText = findViewById(R.id.score);
        stepText = findViewById(R.id.step);
        background = findViewById(R.id.background);
        int rand = random.nextInt(bgpicTotal) + 1;
        int picId = getResources().getIdentifier("p"+rand,"drawable",getPackageName());
        background.setImageResource(picId);
        playground.addMoveListener(new OnMoveListener() {
            @Override
            public void onMoveStart() {
                stepText.setText(""+playground.getStep());
            }
            @Override
            public void onMoveEnd() {
                scoreText.setText(""+playground.getScore());
            }
        });
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rand = random.nextInt(bgpicTotal) + 1;
                int picId = getResources().getIdentifier("p"+rand,"drawable",getPackageName());
                background.setImageResource(picId);
                playground.initGame();
                scoreText.setText("0");
                stepText.setText("0");
            }
        });
        mDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float x = e2.getX() - e1.getX();
                float y = e2.getY() - e1.getY();
                if (Math.abs(x) > Math.abs(y)){
                    if (x > FLING_MIN_DISTANCE) playground.moveRight();
                    else if (-x > FLING_MIN_DISTANCE) playground.moveLeft();
                } else {
                    if (y > FLING_MIN_DISTANCE) playground.moveDown();
                    else if (-y > FLING_MIN_DISTANCE) playground.moveUp();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tiles = preferences.getString("tiles",null);
        int score = preferences.getInt("score",0);
        int step = preferences.getInt("step",0);
        if (tiles != null){
            playground.load(tiles,score,step);
            scoreText.setText(""+score);
            stepText.setText(""+step);
        }
        else playground.initGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }
}
