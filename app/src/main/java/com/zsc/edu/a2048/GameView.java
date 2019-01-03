package com.zsc.edu.a2048;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.zsc.edu.a2048.SquareLayout.SquareGridLayout;

import java.util.Random;

/**
 * Created by 落忆枫音 on 2018/6/23.
 */

interface OnMoveListener {
    void onMoveStart();
    void onMoveEnd();
}

public class GameView extends SquareGridLayout {

    private static final String TAG = "GameView";

    private final int dirx[] = {-1,1,0,0};

    private final int diry[] = {0,0,-1,1};

    private Random random = new Random();

    private Tile[][] gameBoard = new Tile[4][4];

    private int tileSum = 0;

    private boolean win = false;

    private int score = 0;

    private int step = 0;

    private OnMoveListener listener;

    public GameView(Context context) {
        super(context);
    }
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initGame(){
        for(int i = 0;i < 4;i++){
            for(int j = 0;j < 4;j++){
                if (gameBoard[i][j] != null){
                    removeView(gameBoard[i][j]);
                    gameBoard[i][j] = null;
                }
            }
        }
        tileSum = score = step = 0;
        createTile();
        createTile();
    }

    private boolean isLose(){
        if (tileSum < 16) return false;
        for(int i = 0;i < 4;i++){
            for(int j = 0;j < 4;j++){
                int level = gameBoard[i][j].getBackground().getLevel();
                for(int k = 0;k < 4;k++){
                    int x = i + dirx[k];
                    int y = j + diry[k];
                    if (0 <= x && x < 4 && 0 <= y && y < 4 &&
                            gameBoard[x][y].getBackground().getLevel() == level ){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void putTile(Tile tile, int row, int col){
        gameBoard[row][col] = tile;
        LayoutParams params = new LayoutParams(spec(row,1f),spec(col,1f));
        params.width = 0;
        params.height = 0;
        addView(tile,params);
    }

    private void createTile(){
        int rand = random.nextInt(16 - tileSum);
        int index = -1, i = 0, j = 0;
        for(;i < 4;i++){
            for(j = 0;j < 4;j++){
                if (gameBoard[i][j] == null)
                    if (++index == rand) break;
            }
            if (index == rand) break;
        }
        Tile tile = (Tile)LayoutInflater.from(getContext()).inflate(R.layout.tile_1, this, false);
        tile.getBackground().setLevel(1);
        putTile(tile, i, j);
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.create);
        tile.startAnimation(animation);
        tileSum++;
        save();
    }

    public void moveUp(){
        AnimatorSet animatorSet = new AnimatorSet();
        for(int i = 1;i < 4;i++){
            for(int j = 0;j < 4;j++){
                if (gameBoard[i][j] == null) continue;
                int level = gameBoard[i][j].getBackground().getLevel(), k;
                boolean crashed = false;
                for(k = i;k > 0 && gameBoard[k-1][j] == null;k--);
                if (k > 0 && gameBoard[k-1][j].getBackground().getLevel() == level && !gameBoard[k-1][j].crashed){
                    k--; crashed = true;
                }
                if (k != i) {
                    ObjectAnimator animator = gameBoard[i][j].translateY(k-i);
                    if (crashed) union(animator, k, j);
                    else gameBoard[k][j] = gameBoard[i][j];
                    gameBoard[i][j] = null;
                    animatorSet.play(animator);
                }
            }
        }
        startMove(animatorSet);
    }

    public void moveLeft(){
        AnimatorSet animatorSet = new AnimatorSet();
        for(int i = 0;i < 4;i++){
            for(int j = 1;j < 4;j++){
                if (gameBoard[i][j] == null) continue;
                int level = gameBoard[i][j].getBackground().getLevel(), k;
                boolean crashed = false;
                for(k = j;k > 0 && gameBoard[i][k-1] == null;k--);
                if (k > 0 && gameBoard[i][k-1].getBackground().getLevel() == level && !gameBoard[i][k-1].crashed){
                    k--; crashed = true;
                }
                if (k != j) {
                    ObjectAnimator animator = gameBoard[i][j].translateX(k-j);
                    if (crashed) union(animator, i, k);
                    else gameBoard[i][k] = gameBoard[i][j];
                    gameBoard[i][j] = null;
                    animatorSet.play(animator);
                }
            }
        }
        startMove(animatorSet);
    }

    public void moveRight(){
        AnimatorSet animatorSet = new AnimatorSet();
        for(int i = 3;i >= 0;i--){
            for(int j = 2;j >= 0;j--){
                if (gameBoard[i][j] == null) continue;
                int level = gameBoard[i][j].getBackground().getLevel(), k;
                boolean crashed = false;
                for(k = j;k < 3 && gameBoard[i][k+1] == null;k++);
                if (k < 3 && gameBoard[i][k+1].getBackground().getLevel() == level && !gameBoard[i][k+1].crashed){
                    k++; crashed = true;
                }
                if (k != j) {
                    ObjectAnimator animator = gameBoard[i][j].translateX(k-j);
                    if (crashed) union(animator, i, k);
                    else gameBoard[i][k] = gameBoard[i][j];
                    gameBoard[i][j] = null;
                    animatorSet.play(animator);
                }
            }
        }
        startMove(animatorSet);
    }

    public void moveDown(){
        AnimatorSet animatorSet = new AnimatorSet();
        for(int i = 2;i >= 0;i--){
            for(int j = 3;j >= 0;j--){
                if (gameBoard[i][j] == null) continue;
                int level = gameBoard[i][j].getBackground().getLevel(), k;
                boolean crashed = false;
                for(k = i;k < 3 && gameBoard[k+1][j] == null;k++);
                if (k < 3 && gameBoard[k+1][j].getBackground().getLevel() == level && !gameBoard[k+1][j].crashed){
                    k++; crashed = true;
                }
                if (k != i) {
                    ObjectAnimator animator = gameBoard[i][j].translateY(k-i);
                    if (crashed) union(animator, k, j);
                    else gameBoard[k][j] = gameBoard[i][j];
                    gameBoard[i][j] = null;
                    animatorSet.play(animator);
                }
            }
        }
        startMove(animatorSet);
    }

    private void union(final ObjectAnimator animator, final int x, final int y){
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                GameView.this.removeView((View) animator.getTarget());
                int level = gameBoard[x][y].levelUp();
                gameBoard[x][y].crashed = false;
                score += level;
                if (level == 1024) win = true;
            }
        });
        gameBoard[x][y].crashed = true;
        tileSum--;
    }

    private void startMove(AnimatorSet animatorSet){
        if (animatorSet.getChildAnimations().size() > 0) {
            animatorSet.setDuration(100);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (listener != null) listener.onMoveEnd();
                    createTile();
                    if (isLose()) gameOver();
                }
            });
            step++;
            if (listener != null) listener.onMoveStart();
            animatorSet.start();
        }
    }

    private void gameOver(){
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.game_over)
                .setMessage(R.string.lose)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        initGame();
                    }
                }).setNegativeButton(R.string.cancel, null).show();
    }

    public int getScore(){
        return score;
    }

    public int getStep(){
        return step;
    }

    public void addMoveListener(OnMoveListener listener){
        this.listener = listener;
    }

    private void save(){
        StringBuffer str = new StringBuffer();
        for(int i = 0;i < 4;i++){
            for(int j = 0;j < 4;j++){
                if (gameBoard[i][j] != null)
                    str.append(gameBoard[i][j].getBackground().getLevel()+" ");
                else str.append("0 ");
            }
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString("tiles",str.toString().trim());
        editor.putInt("score",score);
        editor.putInt("step",step);
        editor.apply();
    }

    public void load(String tiles, int score, int step){
        String arr[] = tiles.split(" ");
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for(int i = 0;i < 16;i++){
            int level = Integer.valueOf(arr[i]);
            if (level > 0) {
                tileSum++;
                Tile tile = (Tile)inflater.inflate(R.layout.tile_1,this,false);
                tile.setLevel(level);
                putTile(tile,i/4,i%4);
            }
        }
        this.score = score;
        this.step = step;
    }
}
