package jssam;

import java.io.*;
import java.util.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;
import org.apache.oro.text.perl.Perl5Util;

/**
 *
 * @author sebastien roux
 * @mail roux.sebastien@gmail.com
 *
 */
public class Core {

    /* Filter arg */
    private static String filter = "";
    /* Filter case */
    //private static Boolean filterCaseSensitive = true;
    /* Sort */
    private static String sortOrder = "default";
    /* Files extensions constants */
    private static final String ATX_EXTENSION = "atx";
    private static final String ALG_EXTENSION = "alg";
    private static final String ZIP_EXTENSION = "zip";
    /* Date format      */
    private static String dateFormat = "default";
    /* Input path */
    private static String inputDir = "";
    /* Output file name */
    private static String outputFile = "";
    /* Compression */
    private static Boolean zipOption = false;

    /* Filter sensitive - not used */
    /*
    public static Boolean getFilterCaseSensitive() {
    return filterCaseSensitive;
    }

    public static void setFilterCaseSensitive(Boolean filterCaseSensitive) {
    Core.filterCaseSensitive = filterCaseSensitive;
    }
     */
    public static String getSortOrder() {
        return sortOrder;
    }

    public static void setSortOrder(String sortOrder) {
        Core.sortOrder = sortOrder;
    }

    public static String getInputDir() {
        return inputDir;
    }

    public static void setInputDir(String inputDir) {
        Core.inputDir = inputDir;
    }

    public static String getOutputFile() {
        return outputFile;
    }

    public static void setOutputFile(String outputFile) {
        Core.outputFile = outputFile;
    }

    public static String getFilter() {
        return filter;
    }

    public static void setFilter(String filter) {
        Core.filter = filter;
    }

    public static String getDateFormat() {
        return dateFormat;
    }

    public static void setDateFormat(String dateFormat) {
        Core.dateFormat = dateFormat;
    }

    public static Boolean getZipOption() {
        return zipOption;
    }

    public static void setZipOption(Boolean zipOption) {
        Core.zipOption = zipOption;
    }
    private static Integer fileCounter = 0;

    public static Integer getFileCounter() {
        return fileCounter;
    }

    public static void setFileCounter(Integer fileCounter) {
        Core.fileCounter = fileCounter;
    }

    public static void go() {

        // Jakarta ORO regex library
        Perl5Util regEx = new Perl5Util();

        // Row variables
        String line = null;
        String fullHeader = null;

        // Directory and file variables
        File[] inputFiles = null;

        if (getSortOrder().equals("asc")) {
            inputFiles = Core.dirListByAscendingDate(new File(getInputDir()));
        } else if (getSortOrder().equals("desc")) {
            inputFiles = Core.dirListByDescendingDate(new File(getInputDir()));
        } else {
            inputFiles = new File(getInputDir()).listFiles();
        }

        File fileAlg;

        try {
            // File output
            // stdout or file output
            File output = new File(getOutputFile());
            FileWriter out = new FileWriter(output);

            // Loop directory array into variable
            for (File inputFile : inputFiles) {

                // Match alg file
                if (regEx.match("/\\." + ALG_EXTENSION + "$/", inputFile.getName())) {
                    setFileCounter(fileCounter + 1);

                    // Get current file
                    fileAlg = new File(getInputDir(), inputFile.getName());

                    // Get size, next if empty
                    if (fileAlg.length() == 0) {
                        continue;
                    }

                    // File input
                    FileReader reader = new FileReader(fileAlg);
                    BufferedReader buffer = new BufferedReader(reader);

                    Integer rowCounter = 1;

                    while ((line = buffer.readLine()) != null) {
                        // Skip empty rows
                        if (line.equals("")) {
                            continue;
                        } // If first date/time row (ie:
                        // "[Sat Mar 29 01:07:08 2008]")
                        else if (rowCounter == 1 && regEx.match("/^\\[/", line)) {

                            // If filter arg is specified
                            if (regEx.match("/^.*" + getFilter() + ".*$/", line + " (" + fileAlg.toString().substring(0, fileAlg.toString().length() - 4) + ")")) {

                                // Format date if specified
                                if (!getDateFormat().equals("default")) {
                                    line = ChangeDateFormat(line);
                                }
                                out.write(line + " (" + fileAlg.toString().substring(0, fileAlg.toString().length() - 4) + ")" + "\n");
                            }

                        } // If first description row (ie:
                        // "Create Spreadsheet Update Log")
                        else if (rowCounter == 2 && regEx.match("/^Create/", line)) {

                            // If filter arg is specified
                            if (regEx.match("/^.*" + getFilter() + ".*$/", line)) {
                                out.write(line + "\n\n");
                            }
                        } // Date/time row followed by "Log Updates" row
                        else if (regEx.match("/^\\[/", line)) {

                            // Change date if specified
                            if (!getDateFormat().equals("default")) {
                                line = ChangeDateFormat(line);
                            }

                            // Store date for later use...
                            fullHeader = line;
                        } // Description row: "Log Updates From User..."
                        else {

                            List<String> liste = new ArrayList<String>();
                            regEx.split(liste, "/ /", line);

                            // Concatenate stored date and user login
                            fullHeader = fullHeader + " - " + liste.get(5).toString() + " (" + fileAlg.toString().substring(0, fileAlg.toString().length() - 4) + ")";

                            // If filter arg is specified
                            if (regEx.match("/^.*" + getFilter() + ".*$/", line + " (" + fileAlg.toString().substring(0, fileAlg.toString().length() - 4) + ")")) {

                                out.write(fullHeader + "\n");

                                // Get first and last data rows
                                Integer fRow = Integer.parseInt(liste.get(11));
                                Integer lRow = Integer.parseInt(liste.get(11)) + Integer.parseInt(liste.get(18)) - 2;

                                // Build ATX filename
                                File fileAtx = new File(fileAlg.toString().substring(0, fileAlg.toString().length() - 3) + ATX_EXTENSION);
                                if (!fileAtx.exists()) {
                                    out.write("File " + fileAtx.toString() + " was not found in specified directory!" + "\n\n");
                                } else {
                                    // Read ATX filename
                                    FileReader reader2 = new FileReader(fileAtx);
                                    LineNumberReader lnreader = new LineNumberReader(
                                            reader2);

                                    // Reading and writing data set for specified row range
                                    while ((line = lnreader.readLine()) != null) {
                                        if (lnreader.getLineNumber() > lRow) {
                                            break;
                                        } else if (lnreader.getLineNumber() >= fRow && lnreader.getLineNumber() <= lRow) {
                                            if (lnreader.getLineNumber() == lRow) {
                                                out.write("\t" + line + "\n\n");
                                            } else {
                                                out.write("\t" + line + "\n");
                                            }
                                        }
                                    }
                                    lnreader.close();
                                    reader2.close();
                                }
                            }
                        }
                        rowCounter++;
                    }
                    reader.close();
                    buffer.close();
                }
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Compression
        if (getZipOption().equals(true)) {
            try {
                zipOutputFile();
            } catch (IOException ex) {
                Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
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
        if (getDateFormat().equals("iso")) {
            line = sb.append((String) list.get(4)).append((String) "/").append(
                    (String) list.get(1)).append((String) "/").append(
                    (String) list.get(2)).append((String) " ").toString();
        } // Set date format to US style (MM/DD/YYYY)
        else if (getDateFormat().equals("us")) {
            line = sb.append((String) list.get(1)).append((String) "/").append(
                    (String) list.get(2)).append((String) "/").append(
                    (String) list.get(4)).append((String) " ").toString();
        } // Set date format to European style (DD/MM/YYYY)
        else if (getDateFormat().equals("eur")) {
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

            String[] MonthListMMM = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            String[] MonthListMM = {"01", "02", "03", "04", "05", "06", "07",
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

    // Return total ALG files in selected directory
    public static Integer countALGATXFiles(File[] inputFile) {
        Perl5Util regEx = new Perl5Util();
        Integer totalALGFiles = 0;

        for (File inputFiles : inputFile) {
            if (regEx.match("/\\." + ALG_EXTENSION + "$/", inputFiles.getName())) {
                totalALGFiles = totalALGFiles + 1;
            } else {
                continue;
            }
        }
        return totalALGFiles;
    }

    @SuppressWarnings("unchecked")

    // Order files by descending date
    public static File[] dirListByDescendingDate(File directory) {
        if (!directory.isDirectory()) {
            return null;
        }
        File[] inputFiles = directory.listFiles();
        //countALGATXFiles(inputFiles);

        Arrays.sort(inputFiles, new Comparator() {

            public int compare(final Object o1, final Object o2) {
                return new Long(((File) o2).lastModified()).compareTo(new Long(
                        ((File) o1).lastModified()));
            }
        });
        return inputFiles;
    }

    @SuppressWarnings("unchecked")

    // Order files by ascending date
    public static File[] dirListByAscendingDate(File directory) {
        if (!directory.isDirectory()) {
            return null;
        }
        File[] inputFiles = directory.listFiles();
        //countALGATXFiles(inputFiles);

        Arrays.sort(inputFiles, new Comparator() {

            public int compare(final Object o1, final Object o2) {
                return new Long(((File) o1).lastModified()).compareTo(new Long(
                        ((File) o2).lastModified()));
            }
        });
        return inputFiles;
    }

    public static void zipOutputFile() throws IOException {

        File source = new File(getOutputFile().toString());
        //String sSource = source.getAbsoluteFile().toString();
        String sSource = source.toString();

        // Create a buffer for reading the files
        byte[] buffer = new byte[1024];

        // Create the ZIP file
        String target = getOutputFile().toString().substring(0, getOutputFile().length() - 3) + ZIP_EXTENSION;
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target));
            // Compress the files
            FileInputStream in = new FileInputStream(source);

            // Add ZIP entry to output stream.
            out.setLevel(Deflater.BEST_COMPRESSION);
            out.putNextEntry(new ZipEntry(sSource));

            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            out.closeEntry();
            in.close();
            out.close();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            System.exit(0);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(0);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        }
    }
}