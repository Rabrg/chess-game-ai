package me.rabrg.chess;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashMap;
import java.util.Map;

import chesspresso.Chess;
import chesspresso.game.Game;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import me.rabrg.chess.player.MinimaxPlayer;
import me.rabrg.chess.player.Player;

public class ChessGame extends ApplicationAdapter implements InputProcessor {

    private static final int BOARD_SIZE = 8;
    private static final int STONE_SIZE = 60;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Map<Short, TextureRegion> pieceTextureRegions;

    private Game game;

    @Override
	public void create () {
        setupRendering();
        loadPieceTextureRegions();
        setupGame();

        Gdx.input.setInputProcessor(this);
	}

    private void setupRendering() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    private void loadPieceTextureRegions() {
        final Texture pieceTexture = new Texture(Gdx.files.internal("chess-sprite-sheet.png"));

        pieceTextureRegions = new HashMap<Short, TextureRegion>();
        pieceTextureRegions.put(Chess.WHITE_KING, new TextureRegion(pieceTexture, 0, 0, 60, 60));
        pieceTextureRegions.put(Chess.WHITE_QUEEN, new TextureRegion(pieceTexture, 60, 0, 60, 60));
        pieceTextureRegions.put(Chess.WHITE_BISHOP, new TextureRegion(pieceTexture, 120, 0, 60, 60));
        pieceTextureRegions.put(Chess.WHITE_KNIGHT, new TextureRegion(pieceTexture, 180, 0, 60, 60));
        pieceTextureRegions.put(Chess.WHITE_ROOK, new TextureRegion(pieceTexture, 240, 0, 60, 60));
        pieceTextureRegions.put(Chess.WHITE_PAWN, new TextureRegion(pieceTexture, 300, 0, 60, 60));

        pieceTextureRegions.put(Chess.BLACK_KING, new TextureRegion(pieceTexture, 0, 60, 60, 60));
        pieceTextureRegions.put(Chess.BLACK_QUEEN, new TextureRegion(pieceTexture, 60, 60, 60, 60));
        pieceTextureRegions.put(Chess.BLACK_BISHOP, new TextureRegion(pieceTexture, 120, 60, 60, 60));
        pieceTextureRegions.put(Chess.BLACK_KNIGHT, new TextureRegion(pieceTexture, 180, 60, 60, 60));
        pieceTextureRegions.put(Chess.BLACK_ROOK, new TextureRegion(pieceTexture, 240, 60, 60, 60));
        pieceTextureRegions.put(Chess.BLACK_PAWN, new TextureRegion(pieceTexture, 300, 60, 60, 60));
    }

    private void setupGame() {
        game = new Game();
    }

	@Override
	public void render () {
        clearScreen();
        drawBoard();
        drawPieces();
        play();
	}

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void drawBoard() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        boolean red = false;
        for (int row = 0; row < BOARD_SIZE; row++) {
            red = !red;
            for (int column = 0; column < BOARD_SIZE; column++) {
                shapeRenderer.setColor(red ? Color.RED : Color.BLUE);
                shapeRenderer.rect(column * STONE_SIZE, row * STONE_SIZE, STONE_SIZE, STONE_SIZE);
                red = !red;
            }
        }
        shapeRenderer.end();
    }

    private void drawPieces() {
        batch.begin();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                final short stone = (short) game.getPosition().getStone(Chess.coorToSqi(column, row));
                if (stone != Chess.NO_STONE)
                    batch.draw(pieceTextureRegions.get(stone), column * STONE_SIZE, row * STONE_SIZE);
            }
        }
        batch.end();
    }

    final Player ai = new MinimaxPlayer(4);

    private void play() {
        if (from != -1 && to != -1 && game.getPosition().getToPlay() == Chess.WHITE) {
            for (final short m : game.getPosition().getAllMoves()) {
                if (Move.getToSqi(m) == to && Move.getFromSqi(m) == from) {
                    try {
                        game.getPosition().doMove(m);
                    } catch (final IllegalMoveException e) {
                        e.printStackTrace();
                    }
                }
            }
            from = -1;
            to = -1;
        } else if (game.getPosition().getToPlay() == Chess.BLACK) {
            try {
                game.getPosition().doMove(ai.getMove(game.getPosition()));
            } catch (final IllegalMoveException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    private int movePointer = -1;

    private int from = -1, to = -1;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (movePointer == -1) {
            from = getChesspressoCoord(screenX, screenY);
            movePointer = pointer;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (movePointer == pointer) {
            to = getChesspressoCoord(screenX, screenY);
            movePointer = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private int getBoardX(final int screenX) {
        return screenX / STONE_SIZE;
    }

    private int getBoardY(final int screenY) {
        return (Gdx.graphics.getHeight() - screenY) / STONE_SIZE;
    }

    private int getChesspressoCoord(final int screenX, final int screenY) {
        return getBoardY(screenY) * 8 + getBoardX(screenX);
    }
}
