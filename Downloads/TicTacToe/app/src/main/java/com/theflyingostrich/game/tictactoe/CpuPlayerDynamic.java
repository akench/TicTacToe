package com.theflyingostrich.game.tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CpuPlayerDynamic implements CpuPlayer {
    private BoxState cpuSymbol;

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    private int difficulty;
    private int score;
    private Random rand = new Random();
    private Map<Integer, List<MoveType>> diffucultyToMoveCollectionMap;

    public BoxState getMySymbol() {
        return cpuSymbol;
    }

    public void setMySymbol(BoxState s) {
        cpuSymbol = s;
    }

    private BoxState getOpponentSymbol() {
        if (cpuSymbol == BoxState.X) {
            return BoxState.O;
        } else {
            return BoxState.X;
        }
    }

    public RowColPos calculateSymbolPlacement() {
       List<MoveType> moveTypes = diffucultyToMoveCollectionMap.get(difficulty);
        //Now pick a move randomly from the moveTypes.
        int decider = rand.nextInt(moveTypes.size());

        MoveType moveTodo = moveTypes.get(decider);

        RowColPos retVal = null;
        if (moveTodo == MoveType.RANDOM_ONLY) {
            retVal = calculateSymbolPlacementRandom();
        }
        else if(moveTodo == MoveType.WIN_THEN_RANDOM) {
            retVal = gameWinMoveThenRandom();
        }
        else if (moveTodo == MoveType.WIN_THEN_BLOCK_THEN_RANDOM) {
            retVal = gameWinMoveThenBlockThenRandom();
        }
        else if (moveTodo == MoveType.BEST_MOVE) {
            retVal = calculateSymbolPlacementBestMove();
        }
        //catch alll, in case null then pick random on.
        if (retVal == null) {
            return calculateSymbolPlacementRandom();
        }
        else {
            return retVal;
        }
    }

    public RowColPos calculateSymbolPlacementRandom(){

        //symbol placement depends on difficulty
        Random r = new Random();

        RowColPos rowCol = null;

        do {
            rowCol = RowColPos.convertRowCol(r.nextInt(3), r.nextInt(3));

        }while(GameController.getInstance().isBoxOccupied(rowCol.getRow(), rowCol.getCol()));


        return rowCol;
    }
    public RowColPos gameWinMoveThenRandom() {
        RowColPos result = null;
        GameBoard gameBoard = GameController.getInstance().getBoardObj();

        for (ThreeAdjacentBoxes adjacentCells : GameController.getInstance().getBoardObj().getWinningCombinations()) {
            BoxState firstCellState = gameBoard.getBoxState(adjacentCells.getFirstCell());
            BoxState secondCellState = gameBoard.getBoxState(adjacentCells.getSecondCell());
            BoxState thirdCellState = gameBoard.getBoxState(adjacentCells.getThirdCell());
            if (firstCellState == BoxState.Empty && secondCellState == getMySymbol() &&
                    thirdCellState == getMySymbol()) {
                result = adjacentCells.getFirstCell();
                return result;
            }
            if (firstCellState == getMySymbol() && secondCellState == BoxState.Empty &&
                    thirdCellState == getMySymbol()) {
                result = adjacentCells.getSecondCell();
                return result;
            }
            if (firstCellState == getMySymbol() && secondCellState == getMySymbol() &&
                    thirdCellState == BoxState.Empty) {
                result = adjacentCells.getThirdCell();
                return result;
            }
        }
        //Here means no winning move found, so return random
        return calculateSymbolPlacementRandom();
    }
    public RowColPos gameWinMoveThenBlockThenRandom() {

        RowColPos result = null;
        GameBoard gameBoard = GameController.getInstance().getBoardObj();
        for (ThreeAdjacentBoxes adjacentCells : GameController.getInstance().getBoardObj().getWinningCombinations()) {
            BoxState firstCellState = gameBoard.getBoxState(adjacentCells.getFirstCell());
            BoxState secondCellState = gameBoard.getBoxState(adjacentCells.getSecondCell());
            BoxState thirdCellState = gameBoard.getBoxState(adjacentCells.getThirdCell());
            if (firstCellState == BoxState.Empty && secondCellState == getMySymbol() &&
                    thirdCellState == getMySymbol()) {
                result = adjacentCells.getFirstCell();
                return result;
            }
            if (firstCellState == getMySymbol() && secondCellState == BoxState.Empty &&
                    thirdCellState == getMySymbol()) {
                result = adjacentCells.getSecondCell();
                return result;
            }
            if (firstCellState == getMySymbol() && secondCellState == getMySymbol() &&
                    thirdCellState == BoxState.Empty) {
                result = adjacentCells.getThirdCell();
                return result;
            }
        }

        //Step 2. If opponent can win in one step, then block it.
        for (ThreeAdjacentBoxes adjacentCells : GameController.getInstance().getBoardObj().getWinningCombinations()) {
            BoxState firstCellState = gameBoard.getBoxState(adjacentCells.getFirstCell());
            BoxState secondCellState = gameBoard.getBoxState(adjacentCells.getSecondCell());
            BoxState thirdCellState = gameBoard.getBoxState(adjacentCells.getThirdCell());
            if (firstCellState == BoxState.Empty && secondCellState == getOpponentSymbol() &&
                    thirdCellState == getOpponentSymbol()) {
                result = adjacentCells.getFirstCell();
                return result;
            }
            if (firstCellState == getOpponentSymbol() && secondCellState == BoxState.Empty &&
                    thirdCellState == getOpponentSymbol()) {
                result = adjacentCells.getSecondCell();
                return result;
            }
            if (firstCellState == getOpponentSymbol() && secondCellState == getOpponentSymbol() &&
                    thirdCellState == BoxState.Empty) {
                result = adjacentCells.getThirdCell();
                return result;
            }
        }
        //Here means no winning move found, also no need to block opponent, so return random
        return calculateSymbolPlacementRandom();
    }

    public CpuPlayerDynamic(int difficulty) {
        this.difficulty = difficulty;
        score = 0;

        diffucultyToMoveCollectionMap = new HashMap<Integer, List<MoveType>>();
        //0 is random only move
        diffucultyToMoveCollectionMap.put(0, Arrays.asList(MoveType.RANDOM_ONLY));
        diffucultyToMoveCollectionMap.put(1, Arrays.asList(MoveType.RANDOM_ONLY, MoveType.WIN_THEN_RANDOM));
        diffucultyToMoveCollectionMap.put(2, Arrays.asList(MoveType.RANDOM_ONLY, MoveType.WIN_THEN_RANDOM, MoveType.WIN_THEN_RANDOM));

        //min move is WIN_THEN_RANDOM
        diffucultyToMoveCollectionMap.put(3, Arrays.asList(MoveType.WIN_THEN_RANDOM, MoveType.WIN_THEN_RANDOM,
                MoveType.WIN_THEN_RANDOM, MoveType.WIN_THEN_RANDOM));
        diffucultyToMoveCollectionMap.put(4, Arrays.asList(MoveType.WIN_THEN_RANDOM, MoveType.WIN_THEN_RANDOM,
                MoveType.WIN_THEN_RANDOM,MoveType.WIN_THEN_BLOCK_THEN_RANDOM));
        diffucultyToMoveCollectionMap.put(5, Arrays.asList(MoveType.WIN_THEN_RANDOM, MoveType.WIN_THEN_RANDOM,
                MoveType.WIN_THEN_BLOCK_THEN_RANDOM,MoveType.WIN_THEN_BLOCK_THEN_RANDOM));
        diffucultyToMoveCollectionMap.put(6, Arrays.asList(MoveType.WIN_THEN_RANDOM, MoveType.WIN_THEN_RANDOM,
                MoveType.WIN_THEN_BLOCK_THEN_RANDOM,MoveType.WIN_THEN_BLOCK_THEN_RANDOM));

        //min move is WIN_THEN_BLOCK_THEN_RANDOM
        diffucultyToMoveCollectionMap.put(7, Arrays.asList(MoveType.WIN_THEN_BLOCK_THEN_RANDOM,
                MoveType.WIN_THEN_BLOCK_THEN_RANDOM, MoveType.WIN_THEN_BLOCK_THEN_RANDOM, MoveType.WIN_THEN_BLOCK_THEN_RANDOM,
                MoveType.BEST_MOVE, MoveType.BEST_MOVE, MoveType.BEST_MOVE,
                MoveType.BEST_MOVE, MoveType.BEST_MOVE, MoveType.BEST_MOVE));
        diffucultyToMoveCollectionMap.put(8, Arrays.asList(MoveType.WIN_THEN_BLOCK_THEN_RANDOM,
                MoveType.WIN_THEN_BLOCK_THEN_RANDOM, MoveType.WIN_THEN_BLOCK_THEN_RANDOM, MoveType.BEST_MOVE,
                MoveType.BEST_MOVE, MoveType.BEST_MOVE, MoveType.BEST_MOVE,
                MoveType.BEST_MOVE, MoveType.BEST_MOVE, MoveType.BEST_MOVE));
        diffucultyToMoveCollectionMap.put(9, Arrays.asList(MoveType.WIN_THEN_BLOCK_THEN_RANDOM,
                MoveType.WIN_THEN_BLOCK_THEN_RANDOM, MoveType.BEST_MOVE, MoveType.BEST_MOVE,
                MoveType.BEST_MOVE, MoveType.BEST_MOVE, MoveType.BEST_MOVE,
                MoveType.BEST_MOVE, MoveType.BEST_MOVE, MoveType.BEST_MOVE));
        //Level 10 - 10% chance of not playing best move, means 9 moves are best out of 10
        diffucultyToMoveCollectionMap.put(10, Arrays.asList(MoveType.WIN_THEN_BLOCK_THEN_RANDOM,
                MoveType.BEST_MOVE, MoveType.BEST_MOVE, MoveType.BEST_MOVE,
                MoveType.BEST_MOVE, MoveType.BEST_MOVE, MoveType.BEST_MOVE,
                MoveType.BEST_MOVE, MoveType.BEST_MOVE, MoveType.BEST_MOVE));

    }

    public RowColPos calculateSymbolPlacementBestMove() {
        RowColPos result = null;
        GameBoard gameBoard = GameController.getInstance().getBoardObj();
        int boxesOccupiedNo = gameBoard.getCountOccupiedBoxes();

        //Step 0. If only 1 empty board and my turn, then just play that move, no need for extra calculations
        if (boxesOccupiedNo == 8) {
            Iterator<RowColPos> iter = GameController.getInstance().getBoardObj().getRowCosPosForBoxState(BoxState.Empty).iterator();
            return iter.next();
        }

        //Step 1. If cpu can win then do that move

        for (ThreeAdjacentBoxes adjacentCells : GameController.getInstance().getBoardObj().getWinningCombinations()) {
            BoxState firstCellState = gameBoard.getBoxState(adjacentCells.getFirstCell());
            BoxState secondCellState = gameBoard.getBoxState(adjacentCells.getSecondCell());
            BoxState thirdCellState = gameBoard.getBoxState(adjacentCells.getThirdCell());
            if (firstCellState == BoxState.Empty && secondCellState == getMySymbol() &&
                    thirdCellState == getMySymbol()) {
                result = adjacentCells.getFirstCell();
                return result;
            }
            if (firstCellState == getMySymbol() && secondCellState == BoxState.Empty &&
                    thirdCellState == getMySymbol()) {
                result = adjacentCells.getSecondCell();
                return result;
            }
            if (firstCellState == getMySymbol() && secondCellState == getMySymbol() &&
                    thirdCellState == BoxState.Empty) {
                result = adjacentCells.getThirdCell();
                return result;
            }
        }

        //Step 2. If opponent can win in one step, then block it.
        for (ThreeAdjacentBoxes adjacentCells : GameController.getInstance().getBoardObj().getWinningCombinations()) {
            BoxState firstCellState = gameBoard.getBoxState(adjacentCells.getFirstCell());
            BoxState secondCellState = gameBoard.getBoxState(adjacentCells.getSecondCell());
            BoxState thirdCellState = gameBoard.getBoxState(adjacentCells.getThirdCell());
            if (firstCellState == BoxState.Empty && secondCellState == getOpponentSymbol() &&
                    thirdCellState == getOpponentSymbol()) {
                result = adjacentCells.getFirstCell();
                return result;
            }
            if (firstCellState == getOpponentSymbol() && secondCellState == BoxState.Empty &&
                    thirdCellState == getOpponentSymbol()) {
                result = adjacentCells.getSecondCell();
                return result;
            }
            if (firstCellState == getOpponentSymbol() && secondCellState == getOpponentSymbol() &&
                    thirdCellState == BoxState.Empty) {
                result = adjacentCells.getThirdCell();
                return result;
            }
        }
        //Step 3. If this is the first turn in the game, meaning board is empty then place it in one of the corner or center
        if (boxesOccupiedNo == 0) {
            return getRandomFromCollection(RowColPos.getAllCornerAndMidCenterPos());
        }
        //Step 4. If this is the second turn in game. If other player has played in any corner then play in center. If other player played in center then play in any corner
        if (boxesOccupiedNo == 1) {
            RowColPos otherPlayerPos = GameController.getInstance().getBoardObj().getRowCosPosForBoxState(getOpponentSymbol()).iterator().next();
            if (otherPlayerPos.isCorner()) {
                return RowColPos.MID_CENTER;
            }
            if (otherPlayerPos == RowColPos.MID_CENTER) {
                return getRandomFromCollection(RowColPos.getAllCornerPos());
            }
            //means other player has played an edge, which is TOP_CENTER or MID_LEFT or MID_RIGHT or BOTTOM_CENTER.
            // In this case place on opposite edge or mid center
            Collection<RowColPos> options = new ArrayList<RowColPos>();
            options.add(RowColPos.MID_CENTER);
            if(otherPlayerPos == RowColPos.TOP_CENTER) {
                options.add(RowColPos.BOTTOM_CENTER);
            }
            else if(otherPlayerPos == RowColPos.BOTTOM_CENTER){
                options.add(RowColPos.TOP_CENTER);
            }
            else if (otherPlayerPos == RowColPos.MID_LEFT) {
                options.add(RowColPos.MID_RIGHT);
            }
            else if (otherPlayerPos == RowColPos.MID_RIGHT) {
                options.add(RowColPos.MID_LEFT);
            }
            return getRandomFromCollection(options);
        }
        //Step 5. Boxes occupied is 2, means CPU played first, then human player went second.
        //If CPU played first we can assume that CPU player either a corner or mid center.
        if (boxesOccupiedNo == 2) {
            RowColPos otherPlayerPos = GameController.getInstance().getBoardObj().getRowCosPosForBoxState(getOpponentSymbol()).iterator().next();
            RowColPos myPos = GameController.getInstance().getBoardObj().getRowCosPosForBoxState(getMySymbol()).iterator().next();
            if (myPos.isCorner()) {
                //My corner, other player corner, create split, which corner does not matter
                if (otherPlayerPos.isCorner()) {
                    Collection<RowColPos> allCorners = RowColPos.getAllCornerPos();
                    allCorners.remove(myPos);
                    allCorners.remove(otherPlayerPos);
                    return getRandomFromCollection(allCorners);
                }
                //My corner, other player center, does not matter since it will be draw worst case
                if (otherPlayerPos == RowColPos.MID_CENTER) {
                    return getRandomFromCollection(gameBoard.getRowCosPosForBoxState(BoxState.Empty));
                }
                //My corner, other player edge.
                if (otherPlayerPos.isEdge()) {
                    // Case a. It is adjacent edge. Here place it on either corner which is not adjacent to the edge.
                    if (myPos.isAdjacentCell(otherPlayerPos)) {
                        Collection<RowColPos> allCorners = RowColPos.getAllCornerPos();
                        allCorners.remove(myPos);
                        Collection<RowColPos> filteredCorners = new ArrayList<RowColPos>();
                        Iterator<RowColPos> iterator = allCorners.iterator();
                        while (iterator.hasNext()) {
                            RowColPos i = iterator.next();
                            if (!i.isAdjacentCell(otherPlayerPos)) {
                                filteredCorners.add(i);
                            }
                        }
                        return getRandomFromCollection(filteredCorners);
                    }
                    // Case b. It is a non adjacent edge. Here place it on any corner which is adjacent to my position.
                    else {
                        Collection<RowColPos> allCorners = RowColPos.getAllCornerPos();
                        allCorners.remove(myPos);
                        Collection<RowColPos> filteredCorners = new ArrayList<RowColPos>();
                        Iterator<RowColPos> iterator = allCorners.iterator();
                        while (iterator.hasNext()) {
                            RowColPos i = iterator.next();
                            if (i.isAdjacentCell(myPos)) {
                                filteredCorners.add(i);
                            }
                        }
                        return getRandomFromCollection(filteredCorners);
                    }
                }

            } else if (myPos == RowColPos.MID_CENTER) {
                //My mid center, other player corner, play opposite corner, so possibility of split later on
                if (otherPlayerPos == RowColPos.TOP_LEFT) {
                    return RowColPos.BOTTOM_RIGHT;
                } else if (otherPlayerPos == RowColPos.TOP_RIGHT) {
                    return RowColPos.BOTTOM_LEFT;
                } else if (otherPlayerPos == RowColPos.BOTTOM_LEFT) {
                    return RowColPos.TOP_RIGHT;
                } else if (otherPlayerPos == RowColPos.BOTTOM_RIGHT) {
                    return RowColPos.TOP_LEFT;
                }
                //My mid center, other player edge, play other edge to create a split later on

            }
            //my player pos is edge
            else if (myPos.isEdge()) {
                //This situation is not possible as per current logic, so nothing coded here
            }

        }

        //So other player played first, then I played then other player played
        //If split can be created now, then do it
        RowColPos splitPosIfAvailable = getSplitPosIfAvailableForCpu(gameBoard);
        if (splitPosIfAvailable != null) {
            return splitPosIfAvailable;
        }

        //default return random from empty collection
        Collection<RowColPos> positionsToAvoid = positionsToAvoid(gameBoard);

        //We are here means no split could be created
        //Check for each empty position if we can force other player to play a
        //position and after which we can create a split.
        //This means first check that if we play this empty cell then we can win if other
        //player does not block. If that is the case then create a copy of game board and then
        //make my play and the opponent blocking play and then check if split is possible.
        Iterator<RowColPos> emptyPosIter = gameBoard.getRowCosPosForBoxState(BoxState.Empty).iterator();
        while(emptyPosIter.hasNext()) {
            //Is it better to copy gameBoard here or to iterate?
            //If we copy gameBoard means more memory used, but easier on CPU
            //If we don't copy gameBoard then we need to have more loops.
            //For now I am doing copy gameBoard cause it works well with current function.
            //TODO change this logic from copy gameBoard to looping if needed

            //First get the winning combinations with current emptyPosIter.
            //Second check if that will force the other play
            //If it will then copy gameboard, make those placements and then check if split possible, and if
            //split is possible then use that move.

            RowColPos emptyPos = emptyPosIter.next();
            Iterator<ThreeAdjacentBoxes> threeAdjIter = gameBoard.getPossibleWinComboWithRowCol(emptyPos).iterator();
            while(threeAdjIter.hasNext()) {
                ThreeAdjacentBoxes three = threeAdjIter.next();
                RowColPos first = three.getFirstCell();
                RowColPos second = three.getSecondCell();
                RowColPos third = three.getThirdCell();
                if (three.getFirstCell().getRow() == emptyPos.getRow() && three.getFirstCell().getCol() == emptyPos.getCol()) {
                    //check if out of rest two one is empty and other is my symbol
                    if (gameBoard.getBoxState(second) == BoxState.Empty && gameBoard.getBoxState(third) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(second.getRow(), second.getCol(), getOpponentSymbol());
                        RowColPos splitInTrial = getSplitPosIfAvailableForCpu(trialBoard);
                        if (splitInTrial != null && !isSplitCreatedForOpponent(trialBoard) && !opponentHasThreeInALine(trialBoard)) {
                            return emptyPos;
                        }
                        if(isSplitCreatedForOpponent(trialBoard) || opponentHasThreeInALine(trialBoard)) {
                            //Avoid this cause either opponent will have split created or opponent can win with next move
                            positionsToAvoid.add(emptyPos);
                        }
                    }
                    if (gameBoard.getBoxState(third) == BoxState.Empty && gameBoard.getBoxState(second) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(third.getRow(), third.getCol(), getOpponentSymbol());
                        RowColPos splitInTrial = getSplitPosIfAvailableForCpu(trialBoard);
                        if (splitInTrial != null && !isSplitCreatedForOpponent(trialBoard) && !opponentHasThreeInALine(trialBoard)) {
                            return emptyPos;
                        }
                        if(isSplitCreatedForOpponent(trialBoard) || opponentHasThreeInALine(trialBoard)) {
                            //Avoid this cause either opponent will have split created or opponent can win with next move
                            positionsToAvoid.add(emptyPos);
                        }
                    }
                } else if (three.getSecondCell().getRow() == emptyPos.getRow() && three.getSecondCell().getCol() == emptyPos.getCol()) {
                    //check if out of rest two one is emplty and other is my symbol
                    if (gameBoard.getBoxState(first) == BoxState.Empty && gameBoard.getBoxState(third) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(first.getRow(), first.getCol(), getOpponentSymbol());
                        RowColPos splitInTrial = getSplitPosIfAvailableForCpu(trialBoard);
                        if (splitInTrial != null && !isSplitCreatedForOpponent(trialBoard) && !opponentHasThreeInALine(trialBoard)) {
                            return emptyPos;
                        }
                        if(isSplitCreatedForOpponent(trialBoard) || opponentHasThreeInALine(trialBoard)) {
                            //Avoid this cause either opponent will have split created or opponent can win with next move
                            positionsToAvoid.add(emptyPos);
                        }
                    }
                    if (gameBoard.getBoxState(third) == BoxState.Empty && gameBoard.getBoxState(first) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(third.getRow(), third.getCol(), getOpponentSymbol());
                        RowColPos splitInTrial = getSplitPosIfAvailableForCpu(trialBoard);
                        if (splitInTrial != null && !isSplitCreatedForOpponent(trialBoard) && !opponentHasThreeInALine(trialBoard)) {
                            return emptyPos;
                        }
                        if(isSplitCreatedForOpponent(trialBoard) || opponentHasThreeInALine(trialBoard)) {
                            //Avoid this cause either opponent will have split created or opponent can win with next move
                            positionsToAvoid.add(emptyPos);
                        }
                    }
                }  else if (three.getThirdCell().getRow() == emptyPos.getRow() && three.getThirdCell().getCol() == emptyPos.getCol()) {
                    //check if out of rest two one is emplty and other is my symbol
                    if (gameBoard.getBoxState(first) == BoxState.Empty && gameBoard.getBoxState(second) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(first.getRow(), first.getCol(), getOpponentSymbol());
                        RowColPos splitInTrial = getSplitPosIfAvailableForCpu(trialBoard);
                        if (splitInTrial != null && !isSplitCreatedForOpponent(trialBoard) && !opponentHasThreeInALine(trialBoard)) {
                            return emptyPos;
                        }
                        if(isSplitCreatedForOpponent(trialBoard) || opponentHasThreeInALine(trialBoard)) {
                            //Avoid this cause either opponent will have split created or opponent can win with next move
                            positionsToAvoid.add(emptyPos);
                        }
                    }
                    if (gameBoard.getBoxState(second) == BoxState.Empty && gameBoard.getBoxState(first) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(second.getRow(), second.getCol(), getOpponentSymbol());
                        RowColPos splitInTrial = getSplitPosIfAvailableForCpu(trialBoard);
                        if (splitInTrial != null && !isSplitCreatedForOpponent(trialBoard) && !opponentHasThreeInALine(trialBoard)) {
                            return emptyPos;
                        }
                        if(isSplitCreatedForOpponent(trialBoard) || opponentHasThreeInALine(trialBoard)) {
                            //Avoid this cause either opponent will have split created or opponent can win with next move
                            positionsToAvoid.add(emptyPos);
                        }
                    }
                }
            }

        }



        Collection<RowColPos> anyoneFromThis = gameBoard.getRowCosPosForBoxState(BoxState.Empty);
        if(positionsToAvoid.size() > 0) {
            anyoneFromThis.removeAll(positionsToAvoid);
        }

        //TODO check if sometimes anyoneFromThis becomes of size 0
        if(anyoneFromThis.size() < 1) {
            result = getRandomFromCollection(gameBoard.getRowCosPosForBoxState(BoxState.Empty));
        }
        else {

            result = getRandomFromCollection(anyoneFromThis);
        }

        return result;

    }
    private Collection<RowColPos> positionsToAvoid(GameBoard gameBoard) {
        //check each empty cell. If I place my symbol there, then can the other player player create a split. If yes then avoid it.
        //also check if I play this empty cell, then can the other player force me a play and after that it can create a split.
        Collection<RowColPos> toAvoid = new HashSet<RowColPos>();
        Iterator<RowColPos> emptyPosIter = gameBoard.getRowCosPosForBoxState(BoxState.Empty).iterator();
        while(emptyPosIter.hasNext()) {
            RowColPos emptyPos = emptyPosIter.next();
            Iterator<ThreeAdjacentBoxes> threeAdjIter = gameBoard.getPossibleWinComboWithRowCol(emptyPos).iterator();
            while(threeAdjIter.hasNext()) {
                ThreeAdjacentBoxes three = threeAdjIter.next();
                RowColPos first = three.getFirstCell();
                RowColPos second = three.getSecondCell();
                RowColPos third = three.getThirdCell();
                if (three.getFirstCell().getRow() == emptyPos.getRow() && three.getFirstCell().getCol() == emptyPos.getCol()) {
                    //check if out of rest two one is empty and other is my symbol
                    if (gameBoard.getBoxState(second) == BoxState.Empty && gameBoard.getBoxState(third) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(second.getRow(), second.getCol(), getOpponentSymbol());
                        if(isSplitCreatedForOpponent(trialBoard)) {
                            toAvoid.add(emptyPos);
                        }
                    }
                    else if (gameBoard.getBoxState(third) == BoxState.Empty && gameBoard.getBoxState(second) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(third.getRow(), third.getCol(), getOpponentSymbol());
                        if(isSplitCreatedForOpponent(trialBoard)) {
                            toAvoid.add(emptyPos);
                        }
                    }
                    //opponent play is not forced, in this case see if opponent can create a split, if yes then avoid this move
                    else {
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        if (getSplitPosIfAvailableForOpponent(trialBoard) !=null) {
                            toAvoid.add(emptyPos);
                        }
                    }
                } else if (three.getSecondCell().getRow() == emptyPos.getRow() && three.getSecondCell().getCol() == emptyPos.getCol()) {
                    //check if out of rest two one is emplty and other is my symbol
                    if (gameBoard.getBoxState(first) == BoxState.Empty && gameBoard.getBoxState(third) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(first.getRow(), first.getCol(), getOpponentSymbol());
                        if(isSplitCreatedForOpponent(trialBoard)) {
                            toAvoid.add(emptyPos);
                        }
                    }
                    else if (gameBoard.getBoxState(third) == BoxState.Empty && gameBoard.getBoxState(first) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(third.getRow(), third.getCol(), getOpponentSymbol());
                        if(isSplitCreatedForOpponent(trialBoard)) {
                            toAvoid.add(emptyPos);
                        }
                    }
                    //opponent play is not forced, in this case see if opponent can create a split, if yes then avoid this move
                    else {
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        if (getSplitPosIfAvailableForOpponent(trialBoard) !=null) {
                            toAvoid.add(emptyPos);
                        }
                    }
                } else if (three.getThirdCell().getRow() == emptyPos.getRow() && three.getThirdCell().getCol() == emptyPos.getCol()) {
                    //check if out of rest two one is emplty and other is my symbol
                    if (gameBoard.getBoxState(first) == BoxState.Empty && gameBoard.getBoxState(second) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(first.getRow(), first.getCol(), getOpponentSymbol());
                        if(isSplitCreatedForOpponent(trialBoard)) {
                            toAvoid.add(emptyPos);
                        }
                    }
                    else if (gameBoard.getBoxState(second) == BoxState.Empty && gameBoard.getBoxState(first) == getMySymbol()) {
                        //now I can force opponent move, so copy gameBoard and see if in future move I can cause a split
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        trialBoard.setBoxState(second.getRow(), second.getCol(), getOpponentSymbol());
                        if(isSplitCreatedForOpponent(trialBoard)) {
                            toAvoid.add(emptyPos);
                        }
                    }
                    //opponent play is not forced, in this case see if opponent can create a split, if yes then avoid this move
                    else {
                        GameBoard trialBoard = gameBoard.getDeepCopy();
                        trialBoard.setBoxState(emptyPos.getRow(), emptyPos.getCol(), getMySymbol());
                        if (getSplitPosIfAvailableForOpponent(trialBoard) !=null) {
                            toAvoid.add(emptyPos);
                        }
                    }
                }
            }
        }
        return toAvoid;
    }

    private RowColPos getRandomFromCollection(Collection<RowColPos> collection) {

        int r = rand.nextInt(collection.size());
        Iterator<RowColPos> iterator = collection.iterator();
        RowColPos result = iterator.next();
        for (int i = 0; i <= r && iterator.hasNext(); i++) {
            result = iterator.next();
        }
        return result;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int s) {
        score = s;
    }


    private RowColPos getSplitPosIfAvailableForCpu(GameBoard gameBoard) {
        return getSplitPosIfAvailableForSymbol(gameBoard, getMySymbol());

    }
    private RowColPos getSplitPosIfAvailableForOpponent(GameBoard gameBoard) {
        return getSplitPosIfAvailableForSymbol(gameBoard, getOpponentSymbol());
    }

    private RowColPos getSplitPosIfAvailableForSymbol(GameBoard gameBoard, BoxState symbol) {
        //Find the RowColPos which will create a split, meaning after placing symbol at this
        // rowcolpos, we will have two different winning positions empty to place.
        RowColPos result = null;
        Iterator<RowColPos> emptyPosIter = gameBoard.getRowCosPosForBoxState(BoxState.Empty).iterator();
        while(emptyPosIter.hasNext()) {
            RowColPos emptyPos = emptyPosIter.next();
            Iterator<ThreeAdjacentBoxes> threeAdjIter = gameBoard.getPossibleWinComboWithRowCol(emptyPos).iterator();
            int futurePossibleWin = 0;
            while(threeAdjIter.hasNext()) {
                ThreeAdjacentBoxes three = threeAdjIter.next();
                RowColPos first = three.getFirstCell();
                RowColPos second = three.getSecondCell();
                RowColPos third = three.getThirdCell();
                if(first.getCol() == emptyPos.getCol() && first.getRow() == emptyPos.getRow()) {
                    //check if out of rest two one is emplty and other is my symbol
                    if(gameBoard.getBoxState(second) == BoxState.Empty && gameBoard.getBoxState(third) == symbol) {
                        futurePossibleWin++;
                    }
                    if(gameBoard.getBoxState(third) == BoxState.Empty && gameBoard.getBoxState(second) == symbol) {
                        futurePossibleWin++;
                    }
                }
                else if(second.getCol() == emptyPos.getCol() && second.getRow() == emptyPos.getRow()) {
                    //check if out of rest two one is emplty and other is my symbol
                    if(gameBoard.getBoxState(first) == BoxState.Empty && gameBoard.getBoxState(third) == symbol) {
                        futurePossibleWin++;
                    }
                    if(gameBoard.getBoxState(third) == BoxState.Empty && gameBoard.getBoxState(first) == symbol) {
                        futurePossibleWin++;
                    }
                }
                else if(third.getCol() == emptyPos.getCol() && third.getRow() == emptyPos.getRow()) {
                    //check if out of rest two one is emplty and other is my symbol
                    if(gameBoard.getBoxState(first) == BoxState.Empty && gameBoard.getBoxState(second) == symbol) {
                        futurePossibleWin++;
                    }
                    if(gameBoard.getBoxState(second) == BoxState.Empty && gameBoard.getBoxState(first) == symbol) {
                        futurePossibleWin++;
                    }
                }
            }
            if (futurePossibleWin >= 2) {
                //Playing this will create a split
                return emptyPos;
            }
        }
        return result;
    }

    private boolean isSplitCreatedForCpu(GameBoard gameBoard) {
        return isSplitCreatedForSymbol(gameBoard, getMySymbol());
    }
    private boolean isSplitCreatedForOpponent(GameBoard gameBoard) {
        return isSplitCreatedForSymbol(gameBoard, getOpponentSymbol()) ;
    }
    private boolean isSplitCreatedForSymbol(GameBoard gameBoard, BoxState symbol) {
        Iterator<ThreeAdjacentBoxes> threeAdjIter = gameBoard.getWinningCombinations().iterator();
        int futurePossibleWin = 0;
        while(threeAdjIter.hasNext()) {
            ThreeAdjacentBoxes three = threeAdjIter.next();
            RowColPos first = three.getFirstCell();
            RowColPos second = three.getSecondCell();
            RowColPos third = three.getThirdCell();

            if(gameBoard.getBoxState(first) == BoxState.Empty && gameBoard.getBoxState(second) == symbol && gameBoard.getBoxState(third) == symbol) {
                futurePossibleWin++;
            }
            if(gameBoard.getBoxState(first) == symbol && gameBoard.getBoxState(second) == BoxState.Empty && gameBoard.getBoxState(third) == symbol) {
                futurePossibleWin++;
            }
            if(gameBoard.getBoxState(first) == symbol && gameBoard.getBoxState(second) == symbol && gameBoard.getBoxState(third) == BoxState.Empty) {
                futurePossibleWin++;
            }
        }
        if (futurePossibleWin >= 2) {
            return true;
        }

        return false;
    }
    private boolean opponentHasThreeInALine(GameBoard currentGb) {
        boolean result = false;
        for (ThreeAdjacentBoxes adjacentCells : GameController.getInstance().getBoardObj().getWinningCombinations()) {
            BoxState firstCellState = currentGb.getBoxState(adjacentCells.getFirstCell());
            BoxState secondCellState = currentGb.getBoxState(adjacentCells.getSecondCell());
            BoxState thirdCellState = currentGb.getBoxState(adjacentCells.getThirdCell());
            if (firstCellState == getOpponentSymbol() && secondCellState == getOpponentSymbol() &&
                    thirdCellState == getOpponentSymbol()) {
                return true;
            }
        }
        return result;
    }
}
enum MoveType {
    RANDOM_ONLY, WIN_THEN_RANDOM, WIN_THEN_BLOCK_THEN_RANDOM, BEST_MOVE;
}