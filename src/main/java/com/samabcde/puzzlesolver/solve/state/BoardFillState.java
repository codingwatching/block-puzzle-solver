package com.samabcde.puzzlesolver.solve.state;

import com.samabcde.puzzlesolver.component.Block;
import com.samabcde.puzzlesolver.component.BlockPosition;
import com.samabcde.puzzlesolver.component.BlockPuzzle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BoardFillState {
    private static final Logger logger = LoggerFactory.getLogger(BoardFillState.class);
    private final List<PointFillState> emptyPoints;

    private final List<PointFillState> pointFillStatesOrderByPosition;
    private int noOfFillPoint = 0;
    private final BlockPuzzle blockPuzzle;
    private boolean isFillabilityChanged = false;

    public BoardFillState(BlockPuzzle blockPuzzle) {
        this.blockPuzzle = blockPuzzle;
        int pointCount = blockPuzzle.getSize();
        pointFillStatesOrderByPosition = new ArrayList<>(pointCount);
        for (int i = 0; i < pointCount; i++) {
            pointFillStatesOrderByPosition.add(new PointFillState(blockPuzzle, i));
        }
        this.emptyPoints = new LinkedList<>(pointFillStatesOrderByPosition);
    }

    private BoardFillState(BoardFillState boardFillState) {
        this.blockPuzzle = boardFillState.blockPuzzle;
        this.noOfFillPoint = boardFillState.noOfFillPoint;
        this.pointFillStatesOrderByPosition = new ArrayList<>();
        for (PointFillState pointFillState : boardFillState.pointFillStatesOrderByPosition) {
            this.pointFillStatesOrderByPosition.add(pointFillState.clone());
        }

        this.isFillabilityChanged = boardFillState.isFillabilityChanged;
        this.emptyPoints = new LinkedList<>(boardFillState.emptyPoints);
        for (int i = 0; i < this.emptyPoints.size(); i++) {
            this.emptyPoints.set(i, this.pointFillStatesOrderByPosition.get(this.emptyPoints.get(i).getPosition()));
        }
    }

    public BoardFillState clone() {
        return new BoardFillState(this);
    }

    public boolean existCannotFillPoint() {
        List<PointFillState> emptyPoints = getEmptyPoints();
        for (int i = 0; i < emptyPoints.size(); i++) {
            if (!emptyPoints.get(i).canFill()) {
                return true;
            }
        }
        return false;
    }

    public Optional<Block> getOnlyBlock() {
        for (PointFillState pointFillState : getEmptyPoints()) {
            Optional<Block> onlyBlock = pointFillState.onlyBlock();
            if (onlyBlock.isPresent()) {
                return onlyBlock;
            }
        }
        return Optional.empty();
    }

    public List<PointFillState> getEmptyPoints() {
        return this.emptyPoints;
    }

    public void addBlockPosition(BlockPosition addBlockPosition) {
        for (int canFillPoint : addBlockPosition.getCanFillPoints()) {
            pointFillStatesOrderByPosition.get(canFillPoint).setIsFilled(true);
            emptyPoints.remove(pointFillStatesOrderByPosition.get(canFillPoint));
            noOfFillPoint++;
            this.isFillabilityChanged = true;
        }
    }

    public void removeCanFillBlockPosition(BlockPosition blockPosition) {
        for (int canFillPoint : blockPosition.getCanFillPoints()) {
            pointFillStatesOrderByPosition.get(canFillPoint).removeCanFillBlockPosition(blockPosition);
            this.isFillabilityChanged = true;
        }
    }


    public void removeBlockPosition(BlockPosition removeBlockPosition) {
        for (int canFillPoint : removeBlockPosition.getCanFillPoints()) {
            pointFillStatesOrderByPosition.get(canFillPoint).setIsFilled(false);
            noOfFillPoint--;
            emptyPoints.add(pointFillStatesOrderByPosition.get(canFillPoint));
            this.isFillabilityChanged = true;
        }
    }

    public void addCanFillBlockPosition(BlockPosition blockPosition) {
        for (int canFillPoint : blockPosition.getCanFillPoints()) {
            pointFillStatesOrderByPosition.get(canFillPoint).addCanFillBlockPosition(blockPosition);
            this.isFillabilityChanged = true;
        }
    }
}