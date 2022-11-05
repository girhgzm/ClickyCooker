package com.example.clickycooker;

public abstract class Event implements Stage {
    abstract void start(GameManager gameManager);
}
