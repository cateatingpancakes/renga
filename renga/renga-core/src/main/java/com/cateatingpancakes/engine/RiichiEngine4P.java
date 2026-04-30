package com.cateatingpancakes.engine;

import com.cateatingpancakes.engine.GameAction.ActionType;
import com.cateatingpancakes.hand.BasicHand;
import com.cateatingpancakes.state.GameWind;
import com.cateatingpancakes.state.RiichiState4P;
import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileMeld;

public class RiichiEngine4P extends RiichiEngine<RiichiState4P>
{
    /**
     * Constructs a new 4-player Riichi engine on a random state.
     */
    public RiichiEngine4P()
    {
        this.state         = new RiichiState4P();
        this.activePlayer  = this.state.playerIndexOf(GameWind.EAST);
        this.activeRequest = null;
        this.lastDiscard   = null;
        this.moment        = EngineMoment.ACTION;
    }

    /**
     * Build and set the a request based on the active player and the engine moment.
     */
    public void buildRequest()
    {
        EngineRequest req = new EngineRequest(RiichiState4P.PLAYER_COUNT);

        if(moment == EngineMoment.ACTION)
        {
            // Non-active players during someone's "priority" can't do anything.
            for(int i = 0; i < RiichiState4P.PLAYER_COUNT; i++)
                if(i != activePlayer)
                    req.directRequest(i).addLegalAction(new GameAction(ActionType.NOTHING, null, null));


            BasicHand    activeHand = state.directHands().get(activePlayer);
            PlayerRequest activeReq = req.directRequest(activePlayer);

            // Active player discards immediately:
            for(int i = 0; i < activeHand.size(); i++)
            {
                Tile tile = activeHand.get(i);
                activeReq.addLegalAction(new GameAction(ActionType.DISCARD, i, tile));
            }

            // Active player calls a closed kan:
            for(int index = 0; index < RiichiEngine.INDEX_NUMBER_RIICHI_MAX; index++)
            {
                if(activeHand.countOf(index) >= 4)
                    activeReq.addLegalAction(new GameAction(ActionType.KAN_CLOSED, index, null));
            }

            // Active player calls an added kan:
            for(Tile tile : activeHand)
            {
                for(TileMeld meld : activeHand.getOpenMelds())
                {
                    Tile meldLead = meld.getLeadingTile();
                    if(meld.getType() == TileMeld.MeldType.PON && meldLead.indexEquals(tile))
                        activeReq.addLegalAction(new GameAction(ActionType.KAN_ADEDD, meldLead.toIndex(), tile));
                }
            }
        }

        activeRequest = req;
    }

    @Override
    public void advance() 
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'advance'");
    }
}
