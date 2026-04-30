package com.cateatingpancakes.engine;

import java.util.ArrayList;
import java.util.Iterator;

public class EngineRequest implements Iterable<PlayerRequest>
{
    protected ArrayList<PlayerRequest> requests;

    /**
     * Create a new EngineRequest for actions for a number of players.
     * @param players How many players to request from.
     */
    public EngineRequest(int players)
    {
        requests = new ArrayList<>();

        for(int i = 0; i < players; i++)
            requests.add(new PlayerRequest());
    }

    /**
     * Copy-constructs an engine request.
     * @param other The engine request to copy.
     */
    public EngineRequest(EngineRequest other)
    {
        requests = new ArrayList<>(other.requests);
    }

    /**
     * Sets one of the player-bound requests.
     * @param who Whose request to set.
     * @param what What request to set.
     */
    public void setRequest(int who, PlayerRequest what)
    {
        requests.set(who, what);
    }

    /**
     * Returns a reference to a player-bound request.
     * @param who Whose request to fetch.
     * @return The request.
     */
    public PlayerRequest directRequest(int who)
    {
        return requests.get(who);
    }

    /**
     * Returns an iterator over the underlying ArrayList of player requests.
     */
    @Override
    public Iterator<PlayerRequest> iterator()
    {
        return requests.iterator();
    }
}
