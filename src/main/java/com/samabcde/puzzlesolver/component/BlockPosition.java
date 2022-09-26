package com.samabcde.puzzlesolver.component;

import com.samabcde.puzzlesolver.solve.state.PointFillState;

import java.util.*;

public class BlockPosition implements Comparable<BlockPosition> {
    public final int id;
    final Block block;
    boolean[] isPositionIdIntersect;

    void setIsPositionIdIntersectBitSet(BitSet isPositionIdIntersectBitSet) {
        this.isPositionIdIntersectBitSet = isPositionIdIntersectBitSet;
    }

    private BitSet isPositionIdIntersectBitSet;
    private final Position position;
    int intersectCount;
    private int intersectScore = Integer.MIN_VALUE;
    List<Integer> intersectPositionIds = new ArrayList<>();
    int[] canFillPoints;
    private int priority;

    public boolean[] getIsPositionIdIntersect() {
        return isPositionIdIntersect;
    }

    public Block getBlock() {
        return block;
    }

    public List<Integer> getIntersectPositionIds() {
        return intersectPositionIds;
    }

    public int[] getCanFillPoints() {
        return canFillPoints;
    }

    public boolean canFill(PointFillState point) {
        return Arrays.stream(canFillPoints).anyMatch(i -> i == point.getPosition());
    }

    public int getIntersectScore(BlockPuzzle blockPuzzle) {
        if (intersectScore != Integer.MIN_VALUE) {
            return intersectScore;
        }
        intersectScore = 0;
        for (int i = 0; i < blockPuzzle.getPositionCount(); i++) {
            if (!this.isPositionIdIntersect[i]) {
                continue;
            }
            BlockPosition blockPosition = blockPuzzle.getBlockPositionById(i);
            double blockPriorityScore = 10;
            double blockExtraScoreFactor = 3 / (2 + blockPosition.block.priority);
            double positionExtraScoreFactor = 1;
            if (blockPosition.block.priority < this.block.priority) {
                positionExtraScoreFactor = Math.pow(10, 2 / (1 + blockPosition.priority));
            }
            int score = (int) Math.ceil(Math.pow(blockPriorityScore, blockExtraScoreFactor) * positionExtraScoreFactor);
            intersectScore += score;
        }
        return intersectScore;
    }

    public boolean isPositionIntersect(int id) {
        return this.isPositionIdIntersect[id];
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public BlockPosition(Dimension puzzleDimension, Block block, Position position, int id) {
        super();
        this.id = id;
        this.block = block;
        this.position = position;
        this.canFillPoints = new int[block.weight];
        int pointIndex = 0;
        for (int rowIndex = this.position.y(); rowIndex < this.position.y() + this.block.getHeight(); rowIndex++) {
            for (int columnIndex = this.position.x(); columnIndex < this.position.x() + this.block.getWidth(); columnIndex++) {
                if (this.get(rowIndex, columnIndex)) {
                    this.canFillPoints[pointIndex] = puzzleDimension.width() * rowIndex + columnIndex;
                    pointIndex++;
                }
            }
        }
    }

    boolean isIntersect(BlockPosition other) {
        int startRow = Math.max(this.position.y(), other.position.y());
        int startCol = Math.max(this.position.x(), other.position.x());
        int endRow = Math.min(this.position.y() + this.block.getHeight(), other.position.y() + other.block.getHeight());
        int endCol = Math.min(this.position.x() + this.block.getWidth(), other.position.x() + other.block.getWidth());
        for (int i = startRow; i < endRow; i++) {
            for (int j = startCol; j < endCol; j++) {
                if (this.get(i, j) && other.get(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Position getPosition() {
        return position;
    }

    public BitSet getIsPositionIdIntersectBitSet() {
        return isPositionIdIntersectBitSet;
    }

    private boolean get(int row, int col) {
        if (row - position.y() < 0 || row - position.y() > block.getHeight() - 1) {
            return false;
        }
        if (col - position.x() < 0 || col - position.x() > block.getWidth() - 1) {
            return false;
        }
        return block.get(row - position.y(), col - position.x());
    }


    void addIntersectPosition(BlockPosition intersect) {
        this.intersectCount++;
        this.block.totalIntersectCount++;
        this.isPositionIdIntersect[intersect.id] = true;
        this.isPositionIdIntersectBitSet.set(intersect.id);
        this.intersectPositionIds.add(intersect.id);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "position:" + position + ",block:" + System.lineSeparator() + block.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BlockPosition other = (BlockPosition) obj;
        return this.id == other.id;
    }

    @Override
    public int compareTo(BlockPosition arg0) {
        return this.id - arg0.id;
    }

}