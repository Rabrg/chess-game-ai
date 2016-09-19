package me.rabrg.chess.player;

import chesspresso.move.Move;
import chesspresso.position.Position;

import java.util.Scanner;

public final class HumanPlayer extends Player {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public short getMove(final Position position) {
        final int from = Integer.parseInt(scanner.next());
        final int to = Integer.parseInt(scanner.next());
        for (short move: position.getAllMoves())
            if (Move.getFromSqi(move) == from && Move.getToSqi(move) == to)
                return move;
        return 0;
    }
}
