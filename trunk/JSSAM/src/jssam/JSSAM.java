package jssam;

/**
 *
 * @author sebastien roux
 * @mail roux.sebastien@gmail.com
 *
 */
public class JSSAM {

	/**
	 * Main class of JSSAM application <p>
	 *
	 * @author Sébastien Roux
	 */

	public static void main(String[] args) {

		CLI checkCmdLineArg = new CLI();
		checkCmdLineArg.setArgs(args);
		checkCmdLineArg.checkArg();
	}
}
