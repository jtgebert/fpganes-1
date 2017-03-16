// binary dump of a file: xxd -b -c 1 S$INFILE| cut -d" " -f 2 > $OUTFILE

import java.io.*;

public class nes2mif {

  public static void main(String[] args) {

    if (args.length != 2) {
      System.err.println("Incorrect arguements\nPlease use: \"java nes2mif"
                         + " <infile> <outfile>\"");
      System.exit(-1);
    }

    String inPath, progOutPath;
    inPath = args[0];
    progOutPath = args[1];

    try {
      File infile = new File(inPath);
      File progOutfile = new File(progOutPath);

      byte[] bytes = new byte[(int) infile.length()];
      InputStream bytesIn = new BufferedInputStream(new FileInputStream(infile));

      PrintWriter progRomPrinter = new PrintWriter(progOutfile);

      try {
        bytesIn.read(bytes, 0, (int) infile.length());
        bytesIn.close();
      }
      catch (IOException ioe) {
        System.out.println("IOException when reading bytes");
        System.exit(-1);
      }

      int progRomEnd; // the ending byte of the character rom
      int charRomStart;
      int charRomLength;

      // determine prog and char rom size
      progRomEnd = 32783; // currently only support mmc0
      charRomStart = 262160;
      charRomLength = 8192;

      // print mif headers to the output files
      progRomPrinter.println("DEPTH = 266239;\nWIDTH = 16;\nADDRESS_RADIX = HEX;"
                              + "\nDATA_RADIX = BIN;\nCONTENT\nBEGIN\n");

      // loop over all bytes and print them to correct rom file
      for (int address = 0; address < charRomStart + charRomLength / 2; address++) {

        // use header to determine char and prog rom size
        if (address <= 15) {
          // these bytes are for the ines header
        }
        // write prog rom file
        else if (address <= progRomEnd / 2 + 8) {
          progRomPrinter.print(String.format("%6X", (address - 16)).replace(" ", "0"));
          progRomPrinter.print(" : ");
          progRomPrinter.print(String.format("%8s", Integer.toBinaryString(((int) bytes[address * 2 - 16]) & 0xFF)).replace(' ', '0'));
          progRomPrinter.println(String.format("%8s;", Integer.toBinaryString(((int) bytes[address * 2 - 15]) & 0xFF)).replace(' ', '0'));
        }
        else if (address < charRomStart) {
          progRomPrinter.print(String.format("%6X", (address - 16)).replace(" ", "0"));
          progRomPrinter.print(" : ");
          progRomPrinter.println("0000000000000000;");
        }
        // write char rom file
        else if (address <= charRomStart + charRomLength / 2) {
          progRomPrinter.print(String.format("%6X", (address - 16)).replace(" ", "0"));
          progRomPrinter.print(" : ");
          progRomPrinter.print(String.format("%8s", Integer.toBinaryString(((int) bytes[(address - charRomStart) * 2 + progRomEnd + 1] & 0xFF))).replace(' ', '0'));
          progRomPrinter.println(String.format("%8s;", Integer.toBinaryString(((int) bytes[(address - charRomStart) * 2 + progRomEnd + 2] & 0xFF))).replace(' ', '0'));
        }

        // print to console
        /*
        System.out.print(String.format("%6X", address).replace(" ", "0"));
        System.out.print(" : ");
        System.out.print(String.format("%8s", Integer.toBinaryString(((int) bytes[address]) & 0xFF)).replace(' ', '0'));
        System.out.println(String.format("%8s;", Integer.toBinaryString(((int) bytes[address + 1]) & 0xFF)).replace(' ', '0'));
        */
      }

      progRomPrinter.close();

    }
    catch (FileNotFoundException fnfe) {
      System.err.println("File paths incorrect");
      System.exit(-1);
    }
  }
}
