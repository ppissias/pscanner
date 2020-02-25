package io.github.ppissias.pscanner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {
  public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, TransformerException {
    if (args.length != 1) {
      System.out.println("file argument missing");
      return;
    } 
    
    //read provided the XML file
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    Document document = docBuilder.parse(new File(args[0]));
    NodeList nodeList = document.getElementsByTagName("*");
    
    //to keep record of the comparison results
    StringBuffer packagesNotInstalled = new StringBuffer();
    StringBuffer packagesDiffVers = new StringBuffer();
    Integer totalNumberOfPackages = Integer.valueOf(0);
    
    //for each element in the XML file 
    for (int i = 0; i < nodeList.getLength(); i++) {
      System.out.print("*"); //report progress 
      
      Node node = nodeList.item(i);
      if (node.getNodeType() == 1) { //correct HTML elements 
        NamedNodeMap attributes = node.getAttributes();
        
        if (attributes.getNamedItem("kind") != null && 
          attributes.getNamedItem("kind").getNodeValue().equals("package")) {
        	//found an entry we need to process, get details 
        	String name = attributes.getNamedItem("name").getNodeValue();
	        String ver = attributes.getNamedItem("ver").getNodeValue();
	        	        
	        //build command 
	        String command = "rpm -q " + name;
	        //execute command and get output 
	        String commandOutput = CommandExecutor.getCommandOutput(command);
          if (commandOutput.startsWith(name)) { //package installed 
            String packageVesion = commandOutput.substring(name.length() + 1);
            String[] versions = packageVesion.split("-");
            if (versions.length != 2) {
              System.out.println("\nstrange version:" + packageVesion + " on command output:" + commandOutput);
            } else if (!versions[0].equals(ver)) {
              packagesDiffVers.append(name + " expected:" + ver + " found:" + versions[0] + "\n");
            } 
          } else {
            packagesNotInstalled.append(name + "\n");
          } 
        } 
      } 
    } //finished processing CXML doc 
    
    //process results for presentation
    String[] pArray = packagesNotInstalled.toString().split("\n");
    Arrays.parallelSort(pArray);
    packagesNotInstalled = new StringBuffer();
    for (int j = 0; j < pArray.length; j++) {
      packagesNotInstalled.append(pArray[j] + "\n");
    }
    
    String[] vDiffArray = packagesDiffVers.toString().split("\n");
    Arrays.parallelSort(vDiffArray);
    packagesDiffVers = new StringBuffer();
    for (int k = 0; k < vDiffArray.length; k++) {
      packagesDiffVers.append(vDiffArray[k] + "\n");
    }
    
    //output
    System.out.println("");
    System.out.println("MISSING PACKAGES\n" + packagesNotInstalled);
    System.out.println("\nDIFFERENT VERSIONS\n" + packagesDiffVers);
    System.out.println("***");
    System.out.println("Summary: " + pArray.length + " packages are not installed and " + vDiffArray.length + " have different versions out of " + totalNumberOfPackages + " packages");
    System.out.println("***");
  }
}
