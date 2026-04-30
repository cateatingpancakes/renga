package com.cateatingpancakes.state;

import java.io.Serializable;
import java.util.ArrayList;

import com.cateatingpancakes.hand.BasicHand;
import com.cateatingpancakes.tile.DiscardPond;
import com.cateatingpancakes.tile.Tile;
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
    public ArrayList<DiscardPond> directPonds()
    {
        return ponds;
    }

    /**
     * Get a reference for all hands.
     * @return The hands.
     */
    public ArrayList<BasicHand> directHands()
    {
        return hands;
    }

    /**
     * Get a reference to the array of seat winds.
     * @return The seats.
     */
    public ArrayList<GameWind> directSeats()
    {
        return seats;
    }

    /**
     * Get a reference to the wall.
     * @return The wall.
     */
    public BasicWall directWall()
    {
        return wall;
    }

    /**
     * Get the current moment of the game.
     * @return The moment, expressed as a GameWind object.
     */
    public GameMoment getMoment()
    {
        return moment;
    }

    /**
     * Sets the current moment of the game.
     * @param moment The moment, expressed as a GameMoment object.
     */
    public void setMoment(GameMoment moment)
    {
        this.moment = moment;
    }

    /**
     * Make a player throw a tile into their pond.
     * @param who The player.
     * @param what The tile.
     */
    public void throwTile(int who, Tile what)
    {
        ponds.get(who).add(new Tile(what));
        hands.get(who).remove(what);
    }

    /**
     * Shifts the winds 1 time. Effectively, this method makes it so that
     * the ArrayList seats now holds the correct winds for the next game of 
     * this round, after repeat hands have ended.
     */
    public void shiftWinds()
    {
        seats.add(seats.getFirst());
        seats.removeFirst();
    }

    /**
     * Make a player throw a tile into their pond.
     * @param who The player.
     * @param what The tile's index in the player's hand.
     */
    public void throwTile(int who, int what)
    {
        Tile tile = new Tile(hands.get(who).get(what));
        throwTile(who, tile);
    }

    /**
     * Returns the index of the player with a certain wind.
     * @param wind The wind to look for.
     * @return The player's index.
     */
    public int playerIndexOf(GameWind wind)
    {
        for(int i = 0; i < seats.size(); i++)
            if(seats.get(i) == wind)
                return i;

        throw new IllegalStateException("Could not find player with seat wind " + wind);
    }

    /**
     * Returns the wind immediately after a given wind.
     * @param wind The given wind.
     * @return The next wind.
     */
    public GameWind windAfter(GameWind wind)
    {
        int currentIndex = playerIndexOf(wind);
        int targetIndex  = currentIndex + 1;

        // Loop around forward:
        if(targetIndex >= seats.size())
            targetIndex = 0;

        return seats.get(targetIndex);
    }

    /**
     * Returns the wind immediately before a given wind.
     * @param wind The given wind.
     * @return The previous wind.
     */
    public GameWind windBefore(GameWind wind)
    {
        int currentIndex = playerIndexOf(wind);
        int targetIndex  = currentIndex - 1;

        // Loop around backward:
        if(targetIndex < 0)
            targetIndex = seats.size() - 1;

        return seats.get(targetIndex);
    }

    /**
     * Determines if no more tiles can be drawn in this state.
     * @return True, if the wall is exhausted.
     */
    public abstract boolean drawExhausted();
}