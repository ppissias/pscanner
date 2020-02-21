package io.github.ppissias.pscanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandExecutor {
  public static String getCommandOutput(String cmd) {
    ProcessBuilder builder = new ProcessBuilder(new String[] { "bash", "-c", cmd });
    builder.redirectErrorStream(true);
    try {
      Process process = builder.start();
      InputStream is = process.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String line = reader.readLine();
      process.destroyForcibly();
      return line;
    } catch (IOException e) {
      return "cannot execute command";
    } 
  }
}
