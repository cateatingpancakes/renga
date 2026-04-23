package com.cateatingpancakes;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class GameState implements Serializable
{
    public static enum GameWind
    {
        W_EAST, W_SOUTH, W_WEST, W_NORTH
    }

    /**
     * Returns the game wind immediately following a given game wind.
     * @param wind The game wind.
     * @return The next wind.
     */
    public static GameWind nextWind(GameWind wind)
    {
        switch(wind)
        {
            case W_EAST -> {
                return GameWind.W_SOUTH;
            }
            case W_SOUTH -> {
                return GameWind.W_WEST;
            }
            case W_WEST -> {
                return GameWind.W_NORTH;
            }
            case W_NORTH -> {
                return GameWind.W_EAST;
            }
            default -> throw new AssertionError("Could not resolve game wind " + wind);
        }
    }

    protected BasicWall              wall;
    protected ArrayList<DiscardPond> ponds;
    protected ArrayList<BasicHand>   hands;
    protected ArrayList<GameWind>    seats;
    protected GameWind               round;

    /**
     * Constructs an empty game state.
     */
    public GameState()
    {
        this.ponds = new ArrayList<>();
        this.hands = new ArrayList<>();
        this.seats = new ArrayList<>();
    }

    /**
     * Constructs a game state starting from a given wall.
     * @param wall The wall to use.
     */
    public GameState(BasicWall wall)
    {
        this();
        this.wall = new BasicWall(wall);
    }
}