package qengine.program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class WriteCSV<U > {
	
/********************************ECRITURE ET LECTURE DANS UN FICHIER CSV
 * @throws IOException ********************************/ 
	
	 public void writeCSVinfo(String chemin, List<String> tableauNom,  List<U> resultsList, String nomFichier ) throws IOException {
			
		  String cheminvers = chemin  + nomFichier +".csv";
		  try  {
			  
			  File fichierCSV = new File(cheminvers);
			  
			
			  	// Vérifie si le fichier existe déjà
	            if (fichierCSV.createNewFile()) {
	                System.out.println("[CSV] : Le fichier CSV a été créé avec succès !");
	            } else {
	                System.out.println("[CSV] : Le fichier existe déjà.");
	            } 			  
			  StringBuilder sb = new StringBuilder();
			  for (int i = 0; i< tableauNom.size(); i++) {
				  sb.append(tableauNom.get(i));
			      sb.append(',');
			      sb.append("\" "+ resultsList.get(i) + "\" ");
			      sb.append('\n');
				 }
			  
			  PrintWriter writer = new PrintWriter(fichierCSV);
		      writer.write(sb.toString());
		      writer.close();
		     
		      
		      
		      
		      /*************************************/
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    }
	   // System.out.println(records.toString());
	  }
	  
	 
	  
	 public void writeCSV(String chemin, List<String> tableauNom, Map<Integer, Set<String>> resultsList, String nomFichier ) throws IOException {
			
		  String cheminvers = chemin  + nomFichier +".csv";
		  try  {
			  
			  File fichierCSV = new File(cheminvers);
			  
			
			  	// Vérifie si le fichier existe déjà
	            if (fichierCSV.createNewFile()) {
	                System.out.println("[CSV] : Le fichier CSV a été créé avec succès !");
	            } else {
	                System.out.println("[CSV] : Le fichier existe déjà.");
	            } 			  
			  StringBuilder sb = new StringBuilder();
			  for (int i = 0; i< tableauNom.size(); i++) {
				  sb.append(tableauNom.get(i));
			      sb.append(',');
			      sb.append("\" "+ resultsList.get(i+1) + "\" ");
			      sb.append(',');
			      sb.append("\" "+ resultsList.get(i+1).size() + "\" ");
			      sb.append('\n');
				 }
			  
			  PrintWriter writer = new PrintWriter(fichierCSV);
		      writer.write(sb.toString());
		      writer.close();
		     
		      
		      
		      
		      /*************************************/
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    }
	   // System.out.println(records.toString());
	  }
	  
	  
	}



