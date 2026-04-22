package com.cateatingpancakes.hand;

import java.io.Serializable;
import java.util.ArrayList;

import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileMeld;

public class HandInterpretation implements Serializable
{
    protected ArrayList<Tile>     pair;
    protected ArrayList<TileMeld> melds;

    /**
     * Constructs an empty hand interpretation.
     */
    public HandInterpretation()
    {
        this.pair  = new ArrayList<>();
        this.melds = new ArrayList<>();
    }

    /**
     * Copy-constructs a hand interpretation.
     * @param other The hand interpretation to copy.
     */
    public HandInterpretation(HandInterpretation other)
    {
        this.pair  = new ArrayList<>(other.pair);
        this.melds = new ArrayList<>(other.melds);
    }

    /**
     * Returns a copy of the interpretation's pair.
     * @return The pair.
     */
    public ArrayList<Tile> getPair()
    {
        return new ArrayList<>(this.pair);
    }

    /**
     * Returns a copy of the list of melds in the interpretation.
     * @return The melds.
     */
    public ArrayList<TileMeld> getMelds()
    {
        return new ArrayList<>(this.melds);
    }

    /**
     * Copies and sets the interpretation's pair.
     * @param pair The new pair.
     */
    public void setPair(ArrayList<Tile> pair)
    {
        this.pair = new ArrayList<>(pair);
    }

    /**
     * Copies and adds a tile meld to the interpretation.
     * @param meld The meld to be added.
     */
    public void addMeld(TileMeld meld)
    {
        this.melds.add(new TileMeld(meld));
    }

    /**
     * Removes the last meld added with addMeld() to the interpretation.
     */
    public void removeLastMeld()
    {
        this.melds.removeLast();
    }

    /**
     * Finds the number of melds in the interpretation.
     * @return The number of melds.
     */
    public int meldCount()
    {
        return melds.size();
    }
}
