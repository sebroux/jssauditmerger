### Description ###

JSSAuditMerger (JSSAM) utility was developed to **merge Oracle Essbase® SSAudit logs** from a specified directory, to a single file.

This utility supports **Essbase v.5 'til v.11** _SpreadSheet Audit_ logs.
For further details about this functionality, please refer to **`SSAUDIT`** and **`SSAUDITR`** configuration settings in Oracle Essbase® **_Technical Reference_**.

JSSAM stands for Java SpreadSheet Audit Merger, that was developed first as a PERL script (http://code.google.com/p/ssauditmerger).

### Options available ###

  * Advanced date formatting - change default timestamp to convenient one,
  * Headers filtering - search and filter logs headers for accurate information,
  * Sorting of input files by date - sort log files by date,
  * ~~Zip output - compress output for archiving, emailing etc...~~ _temporarily disabled_

### Availability ###

**JSSAuditMerger** is available in the downloads section as a Java JAR application. It requires JRE 1.6 or upper to run. **JSSAuditMerger** provide a command line access and a GUI interface as well.
At the moment **JSSAuditMerger** has been successfully tested on Windows environments only (XP¨, Vista).

### Contribution ###

Please feel free to contribute bringing **ideas**, **code** or doing some **tests** (ie. **nix servers or desktops)!**

### Instructions of use ###

Command line :

```
usage: java -jar jssam.jar -i <directory> -o <outputFile> [OPTIONS]
OPTIONS:
 -d <date format>   specify date format, arg: <iso|eur|us>
 -f <filter>        specify filter on headers (case sensitive), arg: <*>
 -help              display usage
 -i <directory>     specify SSAudit logs directory, arg: <directory>
 -o <file>          specify output file, arg: <outputfile>
 -s <sort>          specify sorting input files by date, arg: <asc|desc>
 ~~-z                 specify compression of output file (zip)~~
```

Launch GUI :

Double click jar or cmd line call without arguments

### Examples ###


### To come... ###


### See also ###