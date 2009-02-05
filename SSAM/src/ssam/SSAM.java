package ssam;

/**
 *
 * @author sebastien roux
 * @mail roux.sebastien@gmail.com
 *
 */
public class SSAM {

	/**
	 * Main class of SSAM application <p>
	 *
	 * @author Sébastien Roux
	 */

	public static void main(String[] args) {

		CLI checkCmdLineArg = new CLI();
		checkCmdLineArg.setArgs(args);
		checkCmdLineArg.checkArg();
	}
}
