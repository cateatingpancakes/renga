package com.cateatingpancakes.engine;

import java.util.ArrayList;
import java.util.Iterator;

public class PlayerRequest implements Iterable<GameAction>
{
    protected ArrayList<GameAction> legalActions;
    protected GameAction            takenAction;

    /**
     * Constructs an empty player request.
     */
    public PlayerRequest()
    {
        this.legalActions = new ArrayList<>();
        this.takenAction  = null;
    }

    /**
     * Constructs a player request from a set of actions legal to that player.
     * @param actions The actions.
     */
    public PlayerRequest(ArrayList<GameAction> actions)
    {
        this.legalActions = new ArrayList<>(actions);
        this.takenAction  = null;
    }

    /**
     * Copy-constructs a player request.
     * @param other The player request to copy.
     */
    public PlayerRequest(PlayerRequest other)
    {
        this.legalActions = new ArrayList<>(other.legalActions);
        this.takenAction  = null;
    }

    /**
     * Returns a copy of the array of legal actions.
     * @return The legal actions.
     */
    public ArrayList<GameAction> getLegalActions()
    {
        return new ArrayList<>(legalActions);
    }

    /**
     * Add a legal action.
     * @param action The action to add.
     */
    public void addLegalAction(GameAction action)
    {
        legalActions.add(new GameAction(action));
    }

    /**
     * Choose a game action as the answer to the request.
     * @param chosen The chosen GameAction.
     */
    public void chooseAction(GameAction chosen)
    {
        takenAction = chosen;
    }

    /**
     * Choose a game action as the answer to the request.
     * @param index The index of the chosen GameAction in the ArrayList of legal actions.
     */
    public void chooseAction(int index)
    {
        takenAction = legalActions.get(index);
    }

    /**
     * Returns the number of legal actions registered with the request.
     * @return The size of the legal actions ArrayList.
     */
    public int size()
    {
        return legalActions.size();
    }

    /**
     * Obtains the chosen action.
     * @return The chosen action.
     */
    public GameAction getChoice()
    {
        return takenAction;
    }

    /**
     * Returns an iterator over the underlying ArrayList of legal actions.
     */
    @Override
    public Iterator<GameAction> iterator()
    {
        return legalActions.iterator();
    }
}
