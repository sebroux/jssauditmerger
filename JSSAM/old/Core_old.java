package ssam;

import java.io.*;
//import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import org.apache.oro.text.perl.Perl5Util;

/**
 *
 * @author sebastien roux
 * @mail roux.sebastien@gmail.com
 *
 */
public class Core_old {

    /* SSAudit files extensions constant */
    private static final String ATX_EXTENSION = "atx";
    private static final String ALG_EXTENSION = "alg";
    /**
     * Output file delimiter constant
     */
    private static final String DATE_DELIMITER = "/";
    /**
     * Date format
     */
    private static String dateFormat = "eur";
    /**
     * Output file name
     */
    private static String outputFileName = "c:\\temp\\test.txt";
    /**
     * Input path
     */
    private static String inputPath = "c:\\temp\\alg_atx";

    public static File[] dirListByDescendingDate(File directory) {
        if (!directory.isDirectory()) {
            return null;
        }
        File files[] = directory.listFiles();
        Arrays.sort(files, new Comparator() {

            public int compare(final Object o1, final Object o2) {
                return new Long(((File) o2).lastModified()).compareTo(new Long(
                        ((File) o1).lastModified()));
            }
        });
        return files;
    }

    public static void main(String[] args) {

        // Jakarta ORO regex library
        Perl5Util regEx = new Perl5Util();

        // Row variables
        String line = null;
        String fullHeader = null;

        // Directory and file variables
        File directory = new File(inputPath);
        String[] list = null;

        File fileAlg;

        // Store file or directory content into array
        if (directory.isDirectory()) {
            list = directory.list();

        } else {
            //regEx.split(list, "/[;]/", inputPath);
            //System.out.println("it is a file!");
            //String[] list = directory.toString();
        }

        // Loop directory array into variable
        for (int i = 0; i < list.length; i++) {
            // Match alg file
            if (regEx.match("/\\." + ALG_EXTENSION + "$/", list[i])) {
                try {
                    // Get current file
                    fileAlg = new File(directory, list[i]);

                    // Get size, next if empty
                    if (fileAlg.length() == 0) {
                        continue;
                    }

                    // Test
                    //System.out.println(file);
                    //System.out.println(file.length());

                    // Waste
                    // long timestamp = file.lastModified();
                    // Date when = new Date(timestamp);
                    // SimpleDateFormat sdf = new
                    // SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
                    // sdf.setTimeZone(TimeZone.getDefault());
                    // String display = sdf.format(timestamp).toString();
                    // output += list[i] + " " + display + "\n";

                    // File input
                    FileReader reader = new FileReader(fileAlg);
                    BufferedReader buffer = new BufferedReader(reader);

                    // File output
                    File MonFichierOut = new File(outputFileName);
                    FileWriter out;
                    out = new FileWriter(MonFichierOut, true);

                    Integer rowCounter = 1;

                    while ((line = buffer.readLine()) != null) {
                        // Skip empty rows
                        if (line.equals("")) {
                            continue;
                        } // If first date/time row (ie:
                        // "[Sat Mar 29 01:07:08 2008]")
                        else if (rowCounter == 1 && regEx.match("/^\\[/", line)) {
                            // Format date if specified
                            if (!dateFormat.equals("default")) {
                                line = ChangeDateFormat(line);
                            }
                            out.write(line + "\n");
                        } // If first description row (ie:
                        // "Create Spreadsheet Update Log")
                        else if (rowCounter == 2 && regEx.match("/^Create/", line)) {
                            out.write(line + "\n\n");
                        } // Date/time row followed by "Log Updates" row
                        else if (regEx.match("/^\\[/", line)) {

                            // Change date if specified
                            if (!dateFormat.equals("default")) {
                                line = ChangeDateFormat(line);
                            }
                            // Store date for later use...
                            fullHeader = line;
                        } // Description row: "Log Updates From User..."
                        else {

                            List<String> liste = null;
                            liste = new ArrayList<String>();
                            regEx.split(liste, "/ /", line);

                            // Concatenate stored date and user login
                            fullHeader = fullHeader + " - " + liste.get(5).toString();
                            out.write(fullHeader + "\n");

                            // Get first and last data rows
                            Integer fRow = Integer.parseInt(liste.get(11));
                            Integer lRow = Integer.parseInt(liste.get(11)) + Integer.parseInt(liste.get(18)) - 2;

                            // Build ATX filename
                            File file2 = new File(fileAlg.toString().substring(0, fileAlg.toString().length() - 3) + ATX_EXTENSION);
                            if (!file2.exists()) {
                                out.write("File " + file2.toString() + " was not found!" + "\n\n");
                            } else {

                                FileReader reader2 = new FileReader(file2);
                                LineNumberReader lnreader = new LineNumberReader(
                                        reader2);

                                // Reading and writing data set for specified row range
                                while ((line = lnreader.readLine()) != null) {
                                    if (lnreader.getLineNumber() > lRow) {
                                        break;
                                    } else if (lnreader.getLineNumber() >= fRow && lnreader.getLineNumber() <= lRow) {
                                        if (lnreader.getLineNumber() == lRow) {
                                            out.write(line + "\n\n");
                                        } else {
                                            out.write(line + "\n");
                                        }
                                    }
                                }
                                lnreader.close();
                                reader2.close();
                            }
                        }
                        rowCounter++;
                    }
                    reader.close();
                    buffer.close();
                    out.flush();
                    out.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // Change date format
    private static String ChangeDateFormat(String line) {

        line = changeMonthFormat(line);

        Perl5Util regEx = new Perl5Util();
        List<String> list = new ArrayList<String>();

        // Replace [] by nothing
        line =
                regEx.substitute("s/^\\[//", line);
        line =
                regEx.substitute("s/\\]$//", line);
        // Split according delimiter
        regEx.split(list, "/ /", line);

        StringBuffer sb = new StringBuffer();

        // Set date format to ISO 8601 style (YYYY-MM-DD)
        if (dateFormat.equals("iso")) {
            line = sb.append((String) list.get(4)).append((String) "/").append(
                    (String) list.get(1)).append((String) "/").append(
                    (String) list.get(2)).append((String) " ").toString();
        } // Set date format to US style (MM/DD/YYYY)
        else if (dateFormat.equals("us")) {
            line = sb.append((String) list.get(1)).append((String) "/").append(
                    (String) list.get(2)).append((String) "/").append(
                    (String) list.get(4)).append((String) " ").toString();
        } // Set date format to European style (DD/MM/YYYY)
        else if (dateFormat.equals("eur")) {
            line = sb.append((String) list.get(2)).append((String) "/").append(
                    (String) list.get(1)).append((String) "/").append(
                    (String) list.get(4)).append((String) " ").toString();
        }

        line = sb.append((String) list.get(3)).toString();

        return line;
    }

// Change month format
    private static String changeMonthFormat(String line) {
        Perl5Util regEx = new Perl5Util();

        if (regEx.match("/(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)/",
                line)) {

            String matchResult = regEx.getMatch().toString();

            String MonthListMMM[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            String MonthListMM[] = {"01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12"};

            for (int counter = 0; counter <
                    MonthListMMM.length; counter++) {
                if (matchResult.equals(MonthListMMM[counter])) {
                    line = regEx.substitute("s/" + matchResult + "/" + MonthListMM[counter] + "/", line);
                    break;

                }


            }
        }
        return line;
    }
}
