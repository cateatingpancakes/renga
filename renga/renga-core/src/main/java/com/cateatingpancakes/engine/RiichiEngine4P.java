package com.cateatingpancakes.engine;

import java.util.ArrayList;
import java.util.EnumSet;

import com.cateatingpancakes.engine.GameAction.ActionType;
import com.cateatingpancakes.hand.BasicHand;
import com.cateatingpancakes.state.GameWind;
import com.cateatingpancakes.state.RiichiState4P;
import com.cateatingpancakes.tile.CallData;
import com.cateatingpancakes.tile.DiscardPond;
import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileMeld;
import com.cateatingpancakes.tile.TileMeld.MeldType;
import com.cateatingpancakes.tile.TileSet;
import com.cateatingpancakes.tile.TileTrait;
import com.cateatingpancakes.wall.BasicWall;
import com.cateatingpancakes.wall.RiichiBuilder;
import com.cateatingpancakes.wall.RiichiBuilder4P;

public final class RiichiEngine4P extends RiichiEngine<RiichiState4P>
{
    /**
     * Constructs a new 4-player Riichi engine on a random state.
     */
    public RiichiEngine4P()
    {
        this.state         = new RiichiState4P();
        this.riichi        = new boolean[RiichiState4P.PLAYER_COUNT];
        this.winCallRight  = new boolean[RiichiState4P.PLAYER_COUNT];
        this.activePlayer  = this.state.playerIndexOf(GameWind.EAST);
        this.activeRequest = null;
        this.lastDiscard   = null;
        this.lastResolved  = null;
        this.lastInterrupt = null;
        this.callSelect    = new Integer[RiichiState4P.PLAYER_COUNT];
        this.moment        = EngineMoment.ACTION;

        for(int i = 0; i < RiichiState4P.PLAYER_COUNT; i++)
        {
            riichi[i]       = false;
            winCallRight[i] = true;
            callSelect[i]   = 0;
        }

        buildRequest();
    }

    @Override
    public void buildRequest()
    {
        EngineRequest req = new EngineRequest(RiichiState4P.PLAYER_COUNT);

        if(moment == EngineMoment.ACTION)
        {
            // Non-active players during someone's "priority" can't do anything.
            for(int i = 0; i < RiichiState4P.PLAYER_COUNT; i++)
                if(i != activePlayer)
                    req.directRequest(i).addLegalAction(
                        new GameAction(ActionType.NOTHING, null, null)
                    );


            BasicHand    activeHand = state.directHands().get(activePlayer);
            PlayerRequest activeReq = req.directRequest(activePlayer);

            // Active player discards immediately:
            for(int i = 0; i < activeHand.size(); i++)
            {
                Tile tile = activeHand.get(i);
                activeReq.addLegalAction(
                    new GameAction(ActionType.DISCARD, i, tile)
                );
            }

            // Active player calls a closed kan:
            for(int index = 0; index < RiichiBuilder.INDEX_NUMBER_RIICHI_MAX; index++)
            {
                if(activeHand.countOf(index) >= 4)
                    activeReq.addLegalAction(
                        new GameAction(ActionType.KAN_CLOSED, index, null)
                    );
            }

            // Active player calls an added kan:
            for(Tile tile : activeHand)
            {
                for(TileMeld meld : activeHand.getOpenMelds())
                {
                    Tile meldLead = meld.getLeadingTile();
                    if(meld.getType() == TileMeld.MeldType.PON && meldLead.indexEquals(tile))
                        activeReq.addLegalAction(
                            new GameAction(ActionType.KAN_ADDED, meldLead.toIndex(), tile)
                        );
                }
            }

            // If not already in riichi:
            if(!riichi[activePlayer])
            {
                // Active player calls riichi:
                for(int i = 0; i < activeHand.size(); i++)
                {
                    Tile tile = activeHand.get(i);
                    if(splitter.tilesAway(activeHand.removeNew(tile)) == 0)
                    {
                        activeReq.addLegalAction(
                            new GameAction(ActionType.RIICHI, i, tile)
                        );
                    }
                }
            }

            // Active player can tsumo:
            if(splitter.tilesAway(activeHand) == -1)
            {
                activeReq.addLegalAction(new GameAction(ActionType.TSUMO, null, null));
            }
        }

        if(moment == EngineMoment.INTERRUPT)
        {
            req.directRequest(activePlayer).addLegalAction(
                new GameAction(ActionType.NOTHING, null, null)
            );

            for(int i = 0; i < RiichiState4P.PLAYER_COUNT; i++)
            {
                // Non-active players get a shot to rob closed kan with kokushi, or to rob added kan.
                if(i != activePlayer)
                {                    
                    BasicHand    otherHand = state.directHands().get(i);
                    PlayerRequest otherReq = req.directRequest(i);

                    // Kokushi robs closed kan:
                    if(lastResolved == ActionType.KAN_CLOSED &&
                       splitter.interpretOrphans(
                        otherHand.addNew(lastInterrupt)
                       )
                    )
                    {
                        otherReq.addLegalAction(
                            new GameAction(ActionType.RON, activePlayer, lastInterrupt)
                        );
                    }

                    // Everyone can rob added kan if they would win:
                    if(lastResolved == ActionType.KAN_ADDED &&
                        splitter.tilesAway(
                         otherHand.addNew(lastInterrupt)
                        ) == -1
                    )
                    {
                        otherReq.addLegalAction(
                            new GameAction(ActionType.RON, activePlayer, lastInterrupt)
                        );
                    }

                    // Always allow a player to decline with an ActionType.NOTHING.
                    otherReq.addLegalAction(
                        new GameAction(ActionType.NOTHING, null, null)
                    );
                }
            }
        }

        if(moment == EngineMoment.REACT)
        {
            GameWind       activeSeat = state.directSeats().get(activePlayer);
            ArrayList<GameWind> order = state.windSequence(activeSeat, RiichiState4P.PLAYER_COUNT - 1);

            // Add pons and daiminkans:
            for(GameWind wind : order)
            {
                int            otherPlayer = state.playerIndexOf(wind);
                BasicHand        otherHand = state.directHands().get(otherPlayer);
                PlayerRequest     otherReq = req.directRequest(otherPlayer);

                // Pon:
                if(!riichi[otherPlayer] &&
                    otherHand.countOf(lastDiscard.toIndex()) >= 2)
                {
                    otherReq.addLegalAction(
                        new GameAction(ActionType.PON, activePlayer, lastDiscard)
                    );
                }

                // Daiminkan:
                if(!riichi[otherPlayer] &&
                    otherHand.countOf(lastDiscard.toIndex()) >= 3)
                {
                    otherReq.addLegalAction(
                        new GameAction(ActionType.KAN_OPEN, activePlayer, lastDiscard)
                    );
                }
            }

            // Add chii, only from the right/immediate next player:
            GameWind         shimoWind = state.windAfter(activeSeat);
            int            shimoPlayer = state.playerIndexOf(shimoWind);
            BasicHand        shimoHand = state.directHands().get(shimoPlayer);
            PlayerRequest     shimoReq = req.directRequest(shimoPlayer);

            // If not in riichi, and the tile can be called chii upon:
            if(!riichi[shimoPlayer] &&
                lastDiscard.toIndex() < Tile.INDEX_NUMBER_SUIT_MAX
            )
            {
                // lastDiscard, lastDiscard + 1, lastDiscard + 2
                // Call selection: SELECT_CHII_RIGHT
                if(lastDiscard.getNumber() <= 6 &&
                   shimoHand.contains(lastDiscard.toIndex() + 1) &&
                   shimoHand.contains(lastDiscard.toIndex() + 2)
                )
                {
                    shimoReq.addLegalAction(
                        new GameAction(ActionType.CHII, activePlayer, lastDiscard)
                    );
                }

                // lastDiscard - 1, lastDiscard, lastDiscard + 1
                // Call selection integer: SELECT_CHII_MIDDLE
                if(1 <= lastDiscard.getNumber() && lastDiscard.getNumber() <= 7 &&
                   shimoHand.contains(lastDiscard.toIndex() - 1) &&
                   shimoHand.contains(lastDiscard.toIndex() + 1)
                )
                {
                    shimoReq.addLegalAction(
                        new GameAction(ActionType.CHII, activePlayer, lastDiscard)
                    );
                }

                // lastDiscar - 2, lastDiscard - 1, lastDiscard
                // Call selection integer: SELECT_CHII_LEFT
                if(2 <= lastDiscard.getNumber() &&
                   shimoHand.contains(lastDiscard.toIndex() - 1) &&
                   shimoHand.contains(lastDiscard.toIndex() - 2)
                )
                {
                    shimoReq.addLegalAction(
                        new GameAction(ActionType.CHII, activePlayer, lastDiscard)
                    );
                }
            }

            // Add ron, if the tile completes a hand and ron is legal:
            for(GameWind wind : order)
            {
                int               otherPlayer = state.playerIndexOf(wind);
                BasicHand           otherHand = state.directHands().get(otherPlayer);
                DiscardPond         otherPond = state.directPonds().get(otherPlayer);
                PlayerRequest        otherReq = req.directRequest(otherPlayer);
                ArrayList<Integer> otherWaits = splitter.waits(otherHand);

                // If we're waiting on this tile,
                // and we're not in temporary furiten:
                if(otherWaits.contains(lastDiscard.toIndex()) &&
                   winCallRight[otherPlayer])
                {
                    boolean hasPermFuriten = false;

                    // Check the pond for any of the waits:
                    for(Integer wait : otherWaits)
                        if(otherPond.contains(wait))
                            hasPermFuriten = true;

                    // If none were found among the pond, then this last tile we can call ron upon:
                    if(!hasPermFuriten)
                    {
                        otherReq.addLegalAction(
                            new GameAction(ActionType.RON, activePlayer, lastDiscard)
                        );

                        // After being granted a legal ron, we lose the right to ron until we next
                        // have the opportunity to change our waits, that is, until the next DISCARD
                        // moment where we're the activePlayer.
                        winCallRight[otherPlayer] = false;
                    }
                }
            }

            // Always allow a player to decline with an ActionType.NOTHING.
            for(GameWind wind : order)
            {
                int           otherPlayer = state.playerIndexOf(wind);
                PlayerRequest    otherReq = req.directRequest(otherPlayer);

                // Always allow a player to decline with an ActionType.NOTHING.
                otherReq.addLegalAction(
                    new GameAction(ActionType.NOTHING, null, null)
                );
            }
        }

        if(moment == EngineMoment.DISCARD)
        {
            // Non-active players during someone's "forced post-call discard" can't do anything.
            for(int i = 0; i < RiichiState4P.PLAYER_COUNT; i++)
                if(i != activePlayer)
                    req.directRequest(i).addLegalAction(
                        new GameAction(ActionType.NOTHING, null, null)
                    );

            BasicHand    activeHand = state.directHands().get(activePlayer);
            PlayerRequest activeReq = req.directRequest(activePlayer);

            // Active player discards immediately:
            for(int i = 0; i < activeHand.size(); i++)
            {
                Tile tile = activeHand.get(i);
                activeReq.addLegalAction(
                    new GameAction(ActionType.DISCARD, i, tile)
                );
            }

            // ... and this player can't make any calls from their hand, 
            // nor win the game by declaring a tsumo.
        }

        activeRequest = req;
    }

    @Override
    public void advance() 
    {
        if(moment == EngineMoment.DRAW)
        {
            // No actions requested here.
            if(state.isExhausted())
                moment = EngineMoment.FINALIZE;
            else
            {
                BasicHand hand = state.directHands().get(activePlayer);
                BasicWall wall = state.directWall();
                hand.add(wall.drawTop());

                moment = EngineMoment.ACTION;
            }
        }

        if(moment == EngineMoment.ACTION)
        {
            GameAction action = activeRequest.directRequest(activePlayer).getChoice();
            if(action != null)
            {
                execute(activePlayer, action);

                // Transition state:

                if(action.type() == ActionType.DISCARD || 
                   action.type() == ActionType.RIICHI)
                {
                    moment = EngineMoment.REACT;
                    // Regain right to call ron from temporary furitens:
                    winCallRight[activePlayer] = true;
                }

                if(action.type() == ActionType.KAN_ADDED || 
                   action.type() == ActionType.KAN_CLOSED)
                {
                    moment = EngineMoment.INTERRUPT;
                }

                if(action.type() == ActionType.TSUMO)
                {
                    moment = EngineMoment.FINALIZE;
                }

                buildRequest();
            }
        }

        if(moment == EngineMoment.INTERRUPT)
        {
            int notNullActions = 0;

            GameWind       activeSeat = state.directSeats().get(activePlayer);
            ArrayList<GameWind> order = state.windSequence(activeSeat, RiichiState4P.PLAYER_COUNT - 1);

            for(GameWind wind : order)
            {
                int otherPlayer   = state.playerIndexOf(wind);
                GameAction action = activeRequest.directRequest(otherPlayer).getChoice();
                notNullActions += (action == null) ? 0 : 1;
            }

            // We don't need the active player to say anything.
            // But we do need to know if everyone else passes or not.
            if(notNullActions == RiichiState4P.PLAYER_COUNT - 1)
            {
                for(GameWind wind : order)
                {
                    int   otherPlayer = state.playerIndexOf(wind);
                    GameAction action = activeRequest.directRequest(otherPlayer).getChoice();

                    if(action.type() == ActionType.RON)
                    {
                        activePlayer = otherPlayer;
                        execute(otherPlayer, action);
                        moment = EngineMoment.FINALIZE;
                        buildRequest();
                        return;
                    }
                }

                moment = EngineMoment.DRAW;
            }
        }

        if(moment == EngineMoment.REACT)
        {
            int notNullActions = 0;

            GameWind       activeSeat = state.directSeats().get(activePlayer);
            ArrayList<GameWind> order = state.windSequence(activeSeat, RiichiState4P.PLAYER_COUNT - 1);

            for(GameWind wind : order)
            {
                int otherPlayer   = state.playerIndexOf(wind);
                GameAction action = activeRequest.directRequest(otherPlayer).getChoice();
                notNullActions += (action == null) ? 0 : 1;
            }

            // We don't need the active player to say anything.
            // But we do need to know if everyone else passes or not.
            if(notNullActions == RiichiState4P.PLAYER_COUNT - 1)
            {
                // Check for ron, first, with highest priority:
                for(GameWind wind : order)
                {
                    int   otherPlayer = state.playerIndexOf(wind);
                    GameAction action = activeRequest.directRequest(otherPlayer).getChoice();

                    if(action.type() == ActionType.RON)
                    {
                        execute(otherPlayer, action);
                        moment = EngineMoment.FINALIZE;
                        buildRequest();
                        return;
                    }
                }

                // Check for pon and daiminkan next:
                for(GameWind wind : order)
                {
                    int   otherPlayer = state.playerIndexOf(wind);
                    GameAction action = activeRequest.directRequest(otherPlayer).getChoice();

                    if(action.type() == ActionType.PON ||
                       action.type() == ActionType.KAN_OPEN
                    )
                    {
                        activePlayer = otherPlayer;
                        execute(otherPlayer, action);
                        moment = EngineMoment.DISCARD;
                        buildRequest();
                        return;
                    }
                }

                // Check for chii last:
                // (Still do this loop, for consistency, but only shimocha should be able to chii.)
                for(GameWind wind : order)
                {
                    int   otherPlayer = state.playerIndexOf(wind);
                    GameAction action = activeRequest.directRequest(otherPlayer).getChoice();

                    if(action.type() == ActionType.CHII)
                    {
                        activePlayer = otherPlayer;
                        execute(otherPlayer, action);
                        moment = EngineMoment.DISCARD;
                        buildRequest();
                        return;
                    }
                }

                // No calls made, go next:
                moment = EngineMoment.DRAW;
                activePlayer = state.playerIndexOf(order.getFirst());
            }
        }

        if(moment == EngineMoment.DISCARD)
        {
            GameAction action = activeRequest.directRequest(activePlayer).getChoice();
            if(action != null)
            {
                execute(activePlayer, action);
                // Regain right to call ron from temporary furitens:
                winCallRight[activePlayer] = true;

                // Next moment:
                moment = EngineMoment.REACT;
                buildRequest();
            }
        }
    }

    @Override
    public void execute(int who, GameAction action) 
    {
        if(action.type() != null)
        {
            switch(action.type()) 
            {
                case DISCARD -> 
                {
                    BasicHand   hand = state.directHands().get(who);
                    DiscardPond pond = state.directPonds().get(who);
                    Tile      thrown = hand.remove(action.target());
                    pond.add(thrown);

                    lastDiscard = thrown;
                }

                case RIICHI ->
                {
                    BasicHand   hand = state.directHands().get(who);
                    DiscardPond pond = state.directPonds().get(who);
                    Tile      thrown = hand.remove(action.target());
                    pond.add(thrown);

                    riichi[activePlayer] = true;
                    lastDiscard = thrown;
                }

                case KAN_CLOSED -> 
                {
                    BasicHand    hand = state.directHands().get(who);
                    int         index = action.target();
                    TileSet meldTiles = new TileSet();

                    // Make meld:
                    for(Tile tile : hand)
                    {
                        if(tile.toIndex() == index)
                            meldTiles.add(tile);
                    }
             
                    // Remove tiles now in meld:
                    for(Tile tile : meldTiles)
                    {
                        hand.remove(tile);
                    }
                    
                    hand.addMeld(new TileMeld(TileMeld.MeldType.KAN_CLOSED, meldTiles));

                    // Any of the tiles should work here, we only need one example.
                    lastInterrupt = meldTiles.get(0);
                }

                case KAN_ADDED ->
                {
                    BasicHand hand = state.directHands().get(who);
                    int      index = action.target();
                    Tile addedTile = action.tileTarget();

                    for(TileMeld meld : hand.getOpenMelds())
                    {
                        if(meld.getType() == MeldType.PON &&
                           meld.getLeadingTile().toIndex() == index
                        )
                        {
                            meld.upgradePonIntoKan(addedTile);
                            break;
                        }
                    }

                    hand.remove(addedTile);
                    lastInterrupt = addedTile;
                }

                case KAN_OPEN ->
                {
                    BasicHand         hand = state.directHands().get(who);
                    Tile         takenTile = action.tileTarget();
                    int       targetPlayer = action.target();
                    DiscardPond targetPond = state.directPonds().get(targetPlayer);

                    targetPond.makeLastCall(who);

                    TileSet meldTiles = new TileSet();
                    // Start forming the meld, with the called tile:
                    meldTiles.add(takenTile);

                    // For this type of kan, every tile of that type will be used.
                    // There is no need to check callSelect flags for redness.
                    int searchIdx = takenTile.toIndex();

                    // A for-loop could work here, and might possibly perform better.
                    // Keeping this with a while though because it looks more logical.
                    while(hand.contains(searchIdx))
                    {
                        int handIdx = hand.find(searchIdx);
                        Tile handTile = hand.get(handIdx);
                        meldTiles.add(handTile);
                        hand.remove(handIdx);
                    }

                    // Register the meld:
                    TileMeld newMeld = new TileMeld(MeldType.KAN_OPEN, meldTiles);
                    newMeld.setData(new CallData(takenTile, targetPlayer));
                    hand.addMeld(newMeld);
                }

                case CHII ->
                {
                    BasicHand         hand = state.directHands().get(who);
                    Tile         takenTile = action.tileTarget();
                    int       targetPlayer = action.target();
                    DiscardPond targetPond = state.directPonds().get(targetPlayer);

                    // Mark the tile as called in the pond of the discarder:
                    targetPond.makeLastCall(who);

                    TileSet meldTiles = new TileSet();
                    // Start forming the meld, with the called tile:
                    meldTiles.add(takenTile);

                    // Index numbers of tiles to search for:
                    int searchIdx1 = -1,
                        searchIdx2 = -1;
                    
                    // Find what tiles to use based on the chii called:
                    if((callSelect[activePlayer] & SELECT_CHII_LEFT) 
                        != 0)
                    {
                        searchIdx1 = takenTile.toIndex() - 1;
                        searchIdx2 = takenTile.toIndex() - 2;
                    }

                    if((callSelect[activePlayer] & SELECT_CHII_MIDDLE) 
                        != 0)
                    {
                        searchIdx1 = takenTile.toIndex() - 1;
                        searchIdx2 = takenTile.toIndex() + 1;
                    }

                    if((callSelect[activePlayer] & SELECT_CHII_RIGHT) 
                        != 0)
                    {
                        searchIdx1 = takenTile.toIndex() + 1;
                        searchIdx2 = takenTile.toIndex() + 2;   
                    }
                    
                    Tile searchTile1 = new Tile(searchIdx1),
                         searchTile2 = new Tile(searchIdx2);
    
                    // Switch to red copies if needed:
                    if(searchTile1.getNumber() == RiichiBuilder4P.DEFAULT_RED_NUMBER &&
                       (callSelect[activePlayer] & SELECT_CALL_RED_1) != 0)
                        searchTile1 = new Tile(searchIdx1, EnumSet.of(TileTrait.RED));
    
                    if(searchTile2.getNumber() == RiichiBuilder4P.DEFAULT_RED_NUMBER &&
                       (callSelect[activePlayer] & SELECT_CALL_RED_1) != 0)
                        searchTile2 = new Tile(searchIdx2, EnumSet.of(TileTrait.RED));

                    int handIdx1, handIdx2;

                    // Find and remove the first tile:
                    handIdx1 = hand.find(searchTile1);
                    hand.remove(handIdx1);

                    // Find and remove the second tile:
                    handIdx2 = hand.find(searchTile2);
                    hand.remove(handIdx2);

                    // Add the tiles to the meld:
                    meldTiles.add(searchTile1);
                    meldTiles.add(searchTile2);

                    // Register the new meld.
                    TileMeld newMeld = new TileMeld(MeldType.CHII, meldTiles);
                    newMeld.setData(new CallData(takenTile, targetPlayer));
                    hand.addMeld(newMeld);
                }

                case PON ->
                {
                    BasicHand         hand = state.directHands().get(who);
                    Tile         takenTile = action.tileTarget();
                    int       targetPlayer = action.target();
                    DiscardPond targetPond = state.directPonds().get(targetPlayer);

                    targetPond.makeLastCall(who);

                    TileSet meldTiles = new TileSet();
                    // Start forming the meld, with the called tile:
                    meldTiles.add(takenTile);

                    // Identical search index, and:
                    int searchIdx = takenTile.toIndex();

                    // Identical search tiles.
                    Tile searchTile1 = new Tile(searchIdx),
                         searchTile2 = new Tile(searchIdx);

                    // Switch to red copies if needed:
                    // Tiles are identical, first copy.
                    if(searchTile1.getNumber() == RiichiBuilder4P.DEFAULT_RED_NUMBER &&
                       (callSelect[activePlayer] & SELECT_CALL_RED_1) != 0)
                       searchTile1 = new Tile(searchIdx, EnumSet.of(TileTrait.RED));

                    // Second copy, for pinzu suit with two red fives or some nonstandard variation.
                    if(searchTile2.getNumber() == RiichiBuilder4P.DEFAULT_RED_NUMBER &&
                       (callSelect[activePlayer] & SELECT_CALL_RED_2) != 0)
                       searchTile2 = new Tile(searchIdx, EnumSet.of(TileTrait.RED));

                    int handIdx1, handIdx2;

                    // Adding the tiles to the meld first is fine,
                    // we found what we needed to find already.
                    meldTiles.add(searchTile1);
                    meldTiles.add(searchTile2);

                    // Remove first tile:
                    handIdx1 = hand.find(searchTile1);
                    hand.remove(handIdx1);

                    // Remove second tile:
                    handIdx2 = hand.find(searchTile2);
                    hand.remove(handIdx2);

                    // Register the meld:
                    TileMeld newMeld = new TileMeld(MeldType.PON, meldTiles);
                    newMeld.setData(new CallData(takenTile, targetPlayer));
                    hand.addMeld(newMeld);
                }

                default -> 
                {
                    throw new IllegalArgumentException("Could not execute action of unknown type " + action.type());                
                }
            }

            lastResolved = action.type();
        }
    }
}
