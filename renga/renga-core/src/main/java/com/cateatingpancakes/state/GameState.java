package com.cateatingpancakes.state;

import java.io.Serializable;
import java.util.ArrayList;

import com.cateatingpancakes.hand.BasicHand;
import com.cateatingpancakes.tile.DiscardPond;
import com.cateatingpancakes.wall.BasicWall;

public abstract class GameState implements Serializable
{
    protected BasicWall              wall;
    protected ArrayList<DiscardPond> ponds;
    protected ArrayList<BasicHand>   hands;
    protected ArrayList<GameWind>    seats;
    protected GameMoment             moment;

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

    /**
     * Get a reference to all discard ponds.
     * @return The discard ponds.
     */
    public ArrayList<DiscardPond> getPondsRef()
    {
        return ponds;
    }

    /**
     * Get a reference for all hands.
     * @return The hands.
     */
    public ArrayList<BasicHand> getHandsRef()
    {
        return hands;
    }

    /**
     * Get a reference to the array of seat winds.
     * @return The seats.
     */
    public ArrayList<GameWind> getSeatsRef()
    {
        return seats;
    }

    /**
     * Get a reference to the wall.
     * @return The wall.
     */
    public BasicWall getWallRef()
    {
        return wall;
    }

    /**
     * Get the current moment of the game.
     * @return The moment, expressed as a GameWind object containing a game number and a round wind.
     */
    public GameMoment getMoment()
    {
        return moment;
    }

    /**
     * Sets the current moment of the game.
     * @param moment The moment, expressed as a GameMoment object containing a game number and a round wind.
     */
    public void setMoment(GameMoment moment)
    {
        this.moment = moment;
    }

    /**
     * Determines if no more tiles can be drawn in this state.
     * @return True, if the wall is exhausted.
     */
    public abstract boolean drawExhausted();
}