package me.rabrg.chess.player;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public final class MinimaxPlayer extends Player {

    private int depth;
    private int player;

    public MinimaxPlayer(final int depth, final int player) {
        this.depth = depth;
        this.player = player;
    }

    public short getMove(final Position position) {
        ChessMove bestMove = minimax(position, 1, true); // TODO: this looks weird
        for (int i = 1; i < depth; i++) {
            final ChessMove currentMove = minimax(position, i, true);
            if (bestMove.val < currentMove.val)
                bestMove = currentMove;
        }
        return bestMove.move;
    }

    private ChessMove minimax(final Position position, final int currentDepth, final boolean maximizing) {
        if (currentDepth == 0 || position.isTerminal())
            return new ChessMove(maximizing ? utility(position) : -1 * utility(position), (short) 0);

        final ChessMove bestMove = new ChessMove(maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE, (short) 0);
        for (final short possibleMove : position.getAllMoves()) {
            try {
                position.doMove(possibleMove);

                final ChessMove currentMove = minimax(position, currentDepth - 1, !maximizing);
                if (maximizing && currentMove.val > bestMove.val || !maximizing && currentMove.val < bestMove.val) {
                    bestMove.val = currentMove.val;
                    bestMove.move = possibleMove;
                }

                position.undoMove();
            } catch (final IllegalMoveException e) {
                e.printStackTrace();
            }
        }
        return bestMove;
    }


    private int utility(final Position position) {
        if (position.isStaleMate())
            return 0;
        if (position.getToPlay() == player && (position.isCheck() || position.isMate())) // TODO: test
            return Integer.MAX_VALUE;
        if (position.getToPlay() != player && (position.isCheck() || position.isMate())) // TODO: test
            return Integer.MIN_VALUE;
        return (int) (position.getMaterial() + position.getDomination()); // TODO: play with value
    }

    private static final class ChessMove {

        private int val;
        private short move;

        private ChessMove(final int val, final short move) {
            this.val = val;
            this.move = move;
        }
    }
}