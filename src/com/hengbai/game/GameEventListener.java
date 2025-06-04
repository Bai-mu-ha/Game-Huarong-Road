package com.hengbai.game;

public interface GameEventListener {
    void onWin();
    boolean isPaused();  // 新增
    boolean isGameWon(); // 新增
}