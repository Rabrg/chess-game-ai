package me.rabrg.chess.player;

import chesspresso.position.Position;

import java.util.Random;

public final class RandomPlayer extends Player {

    private final Random random = new Random();

    @Override
    public short getMove(final Position position) {
        final short[] moves = position.getAllMoves();
        return moves[random.nextInt(moves.length)];
    }
}
