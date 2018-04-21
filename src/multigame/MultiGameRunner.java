package multigame;

import java.io.*;
import java.net.*;

/**
 * main-class for MultiGame 2.1 ... used to launch the game
 */
public class MultiGameRunner {
	/**
	 * main .. args will be -Xms1024m and true
	 */
	public static void main(String args[]) {
		int fps = 150;// frames per second
		if (args.length >= 2) {
			// true implies freeplay (i.e. 888 credits)
			if (args[1].equalsIgnoreCase("true")) {
				new MultiGame(fps, true);
			} else { // moneyPlay
				new MultiGame(fps, false);
			}
		} else {
			new MultiGame(fps, true);// freeplay
		}

		 new MultiGame(fps, false); //pay to play
		
	} // end of main()
}// end class MultiGameRunner
