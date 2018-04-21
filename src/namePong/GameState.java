package namePong;

import multigame.*;

/**
 * This class handles all of the game states and the timing and switching
 * between states of your Game
 */
public class GameState {

  private MultiGame mg;
  private int numStates = 3;

  public final int READY = 0;
  public final int PLAYING = 1;
  public final int DEAD = 2;

  private int[] ticks = new int[numStates];
  private int[] maxTicks = new int[numStates];

  private int curState = READY;

  /**
   * Constructor has a parameter so GameState has a reference to the MultiGame
   * Object.
   */
  public GameState(MultiGame mg) {

    this.mg = mg;
    maxTicks[READY] = 450;// 3 seconds by default
    maxTicks[PLAYING] = 15000;// 100 seconds
    maxTicks[DEAD] = 600;// 4 seconds
  }

  /**
   * This method should be called in gameUpdate() only once. It keeps track of
   * how many frames the Game has spent in each state and will switch states
   * after a specified number of frames.
   */
  public void tick() {
    ticks[curState]++;
    if (ticks[curState] > maxTicks[curState]) {
      switch (curState) {

      case READY:
        toState(PLAYING);
        break;
      case PLAYING:
        // should never be reached
        toState(DEAD);
        break;
      case DEAD:
        // should never be reached
        toState(READY);
        break;
      default:
        ;
      }
    }
  }

  /**
   * Sets the number of ticks of the current state to 0.
   */
  public void resetTicks() {
    ticks[curState] = 0;
  }

  /**
   * Returns the the number of updates (ticks) that have been done in the
   * current GameState.
   */
  public int getCurTick() {
    return ticks[curState];
  }

  /**
   * Returns true if the current GameState is state, false otherwise.<br>
   * Sample usage: if (gameState.inState(gameState.READY)) ...
   */
  public boolean inState(int state) {
    return curState == state;
  }

  /**
   * Zeros out the ticks of the current GameState and changes the GameState to
   * state.
   */
  public void toState(int state) {
    ticks[curState] = 0;
    curState = state;
  }

}