package multigame;

/**
 * This class handles all of the game states and the timing and switching
 * between states of MultiGame
 */
public class MultiGameState {

  private MultiGame mg;
  private int numStates = 4;

  final int SCREENSAVER = 0;
  final int MENU = 1;
  final int INITGAME = 2;
  final int GAMEPLAYING = 3;

  private int[] ticks = new int[numStates];
  private int[] maxTicks = new int[numStates];

  private int curState = SCREENSAVER;

  /**
   * Constructor has a parameter so MultiGameState has a reference to the
   * MultiGame Object.
   */
  public MultiGameState(MultiGame mg) {

    this.mg = mg;
    maxTicks[SCREENSAVER] = 50000000;// days
    maxTicks[MENU] = 90000;// 60 seconds..
    maxTicks[INITGAME] = 150;// 1 seconds
    maxTicks[GAMEPLAYING] = 100000;// 11 minutes
  }

  void tick() {
    ticks[curState]++;
    if (ticks[curState] > maxTicks[curState]) {
      switch (curState) {

      case SCREENSAVER:
        // should never be reached
        break;
      case MENU:
        // 10 seconds
        toState(SCREENSAVER);
        mg.getMenu().destroyMenu();
        mg.setPeriod(10);
        break;
      case INITGAME:
        // 1 seconds
        toState(GAMEPLAYING);
        break;
      case GAMEPLAYING:
        mg.getMenu().initMenu();
        toState(MENU);
        break;

      default:
        ;
      }
    }
  }

  void resetTicks() {
    ticks[curState] = 0;
  }

  /**
   * Can only be called if in the Menu state.. so should only be called from
   * class Menu.
   */
  public void resetMenuTicks() {
    if (curState == MENU) {
      ticks[MENU] = 0;
    }

  }

  int getCurTick() {
    return ticks[curState];
  }

  boolean inState(int state) {
    return curState == state;
  }

  void toState(int state) {
    ticks[curState] = 0;
    if (state == MENU) {
      // mg.getKeyHandler().clearAllClicks();
      mg.initMenu();
    } else {
      mg.destroyMenu();
    }
    curState = state;
  }

}