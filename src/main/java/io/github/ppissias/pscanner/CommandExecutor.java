package io.github.ppissias.pscanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Simple class that executes a command and reads the first line of its output
 * @author Petros Pissias
 *
 */
public class CommandExecutor {

	/**
	 * Executes a command and returns its first line of output
	 * @param cmd the command to be executed
	 * @return first line of the command output
	 */
	public static String getCommandOutput(String cmd) {
		ProcessBuilder builder = new ProcessBuilder(new String[] { "bash", "-c", cmd });
		builder.redirectErrorStream(true);
		try {
			//exec command
			Process process = builder.start();
			
			//get output and read line
			InputStream is = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();
			
			//stop command execution if needed
			process.destroyForcibly();
			
			//return first command output line
			return line;
			
		} catch (IOException e) {
			//don't care at this point to return an exception. Just return an error string
			return "ERR:cannot execute command";
		}
	}
}
