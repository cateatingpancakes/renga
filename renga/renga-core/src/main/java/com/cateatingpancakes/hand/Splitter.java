package com.cateatingpancakes.hand;

import java.util.ArrayList;

/**
 * This interface's contract is fulfilled if the implementor provides the required methods
 * to determine the n-away number and to recognize all possible interpretations of a given
 * hand.
 */
public interface Splitter 
{
    /**
     * Finds the n-away number of a hand.
     * @param hand The hand.
     * @return The n-away number.
     */
    public int tilesAway(Splittable hand);

    /**
     * Interprets a hand in standard form.
     * @param hand The hand.
     * @return An ArrayList of all its interpretations.
     */
    public ArrayList<HandInterpretation> interpret(Splittable hand);

    /**
     * Interprets a hand as thirteen orphans.
     * @param hand The hand.
     * @return True, if the hand is thirteen orphans.
     */
    public boolean interpretOrphans(Splittable hand);

    /**
     * Interprets a hand as seven pairs.
     * @param hand
     * @return True, if the hand is seven pairs.
     */
    public boolean interpretPairs(Splittable hand);
}
