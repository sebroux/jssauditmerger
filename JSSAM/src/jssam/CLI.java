package jssam;

import java.io.File;
import org.apache.commons.cli.*;

/**
 *
 * @author sebastien roux
 * @mail roux.sebastien@gmail.com
 *
 */
public class CLI {

    public static boolean runGUI;
    private String[] arguments;

    public String[] getArgs() {
        return arguments;
    }

    public void setArgs(String[] arguments) {
        this.arguments = arguments;
    }

    @SuppressWarnings("static-access")
    public void checkArg() {

        // create Options object
        CommandLineParser parser = new GnuParser();
        Options options = new Options();

        // add input directory option
        @SuppressWarnings("static-access")
        Option atxDirectory = OptionBuilder.withArgName("directory").hasArg().withDescription("specify SSAudit logs directory, arg: <directory>").create("i");
        options.addOption(atxDirectory);

        // add output file option
        @SuppressWarnings("static-access")
        Option outputFile = OptionBuilder.withArgName("file").hasArg().withDescription("specify output file, arg: <outputfile>").create("o");
        options.addOption(outputFile);

        // add date format option - require an argument (iso, eur, us)
        @SuppressWarnings("static-access")
        Option dateFormat = OptionBuilder.withArgName("date format").hasOptionalArg().withDescription(
                "specify date format, arg: <iso|eur|us>").create("d");
        options.addOption(dateFormat);

        // add filter option - require an argument
        Option filter = OptionBuilder.withArgName("filter").hasOptionalArg().withDescription("specify filter on headers (case sensitive), arg: <*>").create("f");
        options.addOption(filter);

        // add sort option - require an argument (asc, desc)
        Option sort = OptionBuilder.withArgName("sort").hasOptionalArg().withDescription("specify sorting input files by date, arg: <asc|desc>").create("s");
        options.addOption(sort);

        // add zip output option - no arg required
        Option zip = OptionBuilder.withArgName("zip output").withDescription("specify compression of output file (zip) - DISABLED").create("z");
        options.addOption(zip);

        // add help
        options.addOption("help", false, "display usage");

        CommandLine cmd;

        try {
            Core core = new Core();
            cmd = parser.parse(options, arguments);

            // INPUT ARG: for each specified directory check if it exists
            String inputDirectory = cmd.getOptionValue("i");
            if (cmd.hasOption("i") || getArgs().length >= 2) {
                if (!new File(inputDirectory).isDirectory()) {
                    displayHelp(options);
                } else {
                    core.setInputDir(inputDirectory.toString());
                }
            } else if (getArgs().length == 0) {
                runGUI = true;
            } else {
                displayHelp(options);
            }

            if (cmd.hasOption("o") || getArgs().length >= 2) {
                String outputFile2 = cmd.getOptionValue("o");
                core.setOutputFile(outputFile2);
            } else if (getArgs().length == 0) {
                runGUI = true;
            } else {
                displayHelp(options);
            }

            if (cmd.hasOption("f")) {
                String filter2 = cmd.getOptionValue("f");
                core.setFilter(filter2);
            }

            if (cmd.hasOption("d")) {
                if (cmd.getOptionValue("d") != null) {
                    String dateFormatArg = cmd.getOptionValue("d").toLowerCase();
                    if (dateFormatArg.equals("eur") || dateFormatArg.equals("iso") || dateFormatArg.equals("us")) {
                        core.setDateFormat(dateFormatArg);
                    } else {
                        displayHelp(options);
                    }
                }
            }

            if (cmd.hasOption("s")) {
                if (cmd.getOptionValue("s") != null) {
                    String sortOrderArg = cmd.getOptionValue("s").toLowerCase();
                    if (sortOrderArg.equals("asc") || sortOrderArg.equals("desc")) {
                        core.setSortOrder(sortOrderArg);
                    } else {
                        displayHelp(options);
                    }
                }
            }

            if (cmd.hasOption("z")) {
                //DISABLED
                core.setZipOption(true);
            }

            if (cmd.hasOption("help")) {

                displayHelp(options);
            }

            if (runGUI == true) {
                GUI gui = new GUI();
                gui.go();
            } else {
                core.go();
            }
        } catch (ParseException e) {
            // if arguments missing for specified options
            displayHelp(options);
        }
    }

    /**
     * Display usage and help
     */
    public final void displayHelp(Options options) {

        final String HELP_DESC = "DESCRIPTION:\n" + "Merge Essbase SSAudit files (.ATX, .ATG) to a single file.\n";

        final String HELP_USAGESAMPLE = "USAGE SAMPLE:\n" + "";

        final String HELP_REQU = "REQUIREMENTS:\n" + "JRE 1.6 or higher\nCurrent JRE version: " + JavaVersionDisplayApplet() + "\n";

        final String HELP_VERS = "VERSION:\n" + "version 0.9\n";

        final String HELP_AUTH = "AUTHOR:\n" + "Proudly coded & released for the Essbase community by Sebastien Roux <roux.sebastien@gmail.com>\n";

        final String HELP_SITE = "SITE:\n" + "http://code.google.com/p/jssauditmerger/\n";

        final String HELP_LICE = "LICENCE:\n" + "GNU General Public License version 3 (GPLv3)\n";

        final String HELP_NOTE = "NOTES:\n" + "Use at your own risk!\n" + "You will be solely responsible for any damage\n" + "to your computer system or loss of data\n" + "that may result from the download\n" + "or the use of the following application.\n";

        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("java jssam.jar -i <directory> -o <outputFile> [OPTIONS]",
                "OPTIONS:", options, HELP_DESC + HELP_REQU + HELP_VERS + HELP_AUTH + HELP_SITE + HELP_LICE + HELP_NOTE);
        System.exit(0);
    }

    public static String JavaVersionDisplayApplet() {
        String jVersion = System.getProperty("java.version");
        /*
        String jVersion = System.getProperty("java.specification.version");

        BigDecimal version = new BigDecimal(System.getProperty("java.specification.version"));

        if (version.compareTo(new BigDecimal("1.5")) < 0) {
        System.out.println("Your Java version is too old. Please install Java 5 or newer.");
        }
         */
        return jVersion;
    }
}
