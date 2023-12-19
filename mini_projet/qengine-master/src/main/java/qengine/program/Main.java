package qengine.program;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.ntriples.NTriplesParser;

/**
 * Programme simple lisant un fichier de requête et un fichier de données.
 * 
 * <p>
 * Les entrées sont données ici de manière statique,
 * à vous de programmer les entrées par passage d'arguments en ligne de commande comme demandé dans l'énoncé.
 * </p>
 * 
 * <p>
 * Le présent programme se contente de vous montrer la voie pour lire les triples et requêtes
 * depuis les fichiers ; ce sera à vous d'adapter/réécrire le code pour finalement utiliser les requêtes et interroger les données.
 * On ne s'attend pas forcémment à ce que vous gardiez la même structure de code, vous pouvez tout réécrire.
 * </p>
 * 
 * @author Olivier Rodriguez <olivier.rodriguez1@umontpellier.fr>
 */
final class Main {
	static final String baseURI = null;

	/**
	 * Votre répertoire de travail où vont se trouver les fichiers à lire
	 */
	static final String workingDir = "data/";

	/**
	 * Fichier contenant les requêtes sparql
	 */
//	static final String queryFile = workingDir + "sample_query.queryset";

	/**
	 * Fichier contenant des données rdf
	 */
//	static final String dataFile = workingDir + "sample_data.nt";
	
	
	//Les variables 
	static  Map<Integer, Set<String>> resultsList = new HashMap<>();
	static long tempslectureRequete = 0;
	
	static  String queryFile = "";
	static  String dataFile = "";
	static  String pathCSV = "";
	static boolean shuffle; 
	static float pourcentageWarn; 
	static boolean warn ; 
	static int nbRequetes = 0; 
	static boolean jenaB; 
	
	
	// ========================================================================

	/**
	 * Méthode utilisée ici lors du parsing de requête sparql pour agir sur l'objet obtenu.
	 */
	public static  Map<Integer, Set<String>> processAQuery(ParsedQuery query,Dictionnaire<Integer, String> dico1,Index index) {
		List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
		
		
		
		nbRequetes++; 
		
		//Les requetes sont encodée comme suit : SPO
		// on utilise l'index POS pour repondre a ces requetes
		
		int sizeQuery = 0; 
		List<Integer> liste_loco = new ArrayList<>();
		
		for(int i =0; i<patterns.size(); i++) {
				
				
				sizeQuery = patterns.size();
				
				//On récupere les termes de la requetes 
				String objet = patterns.get(i).getObjectVar().getValue().toString();
				String predicat = patterns.get(i).getPredicateVar().getValue().toString();
			
				//On encode la requetes a l'aide du dico
				int v1 = dico1.getKey(predicat);
				int v2 = dico1.getKey(objet);
				
				//on cherche les index commençant par la valeur de v1 et v2
				liste_loco.addAll(index.get1(v1, v2));
			}
			Set<Integer> reponse = new HashSet<>();
			
			//on fait la jonction entre les conditions
			for(int j = 0; j< liste_loco.size(); j++) {
				if(countOccurrences(liste_loco,liste_loco.get(j)) == sizeQuery) {
					reponse.add(liste_loco.get(j));
				}
			}
			
			Set<String> setReponseDecoder =  new HashSet<>();
			for(int s : reponse){
				//On décode 
				//int --> string
				setReponseDecoder.add(dico1.decode(s));
			}
				//On ajoute les requetes decoder a une map 
			
			resultsList.put(nbRequetes, setReponseDecoder);
				// Utilisation d'une classe anonyme
				query.getTupleExpr().visit(new AbstractQueryModelVisitor<RuntimeException>() {

			public void meet(Projection projection) {
				//System.out.println(projection.getProjectionElemList().getElements());
			}
		});
				return resultsList;
	}
	
	public static int countOccurrences(List<Integer> list, int target) {
        int count = 0;

        for (int element : list) {
            if (Objects.equals(element, target)) {
                count++;
            }
        }

        return count;
    }
	
	
	

	/**
	 * **************************************************MAIN*******************************************************************
	 */
	public static void main(String[] args) throws Exception {
		
		
		 if (args.length <  6) {
	            System.out.println("Un élément nécéssaire au bon fonctionnement du programmme manque à l'appelle : -data -queries -output");
	           
	        }else {
	        	for(int i = 0; i < args.length-1; i++ ) {
	        		
	        		switch(args[i]) {
	        		
	        		
	        		case "-data" : 
	        			dataFile = args[i+1] + "100K.nt";
	        			System.out.println("[AFFICHAGE CHEMIN DATA] : " + dataFile  );
	        			
	        			break;
	        		case "-query" : 
	        			queryFile = args[i+1] + "STAR_ALL_workload.queryset"; 
	        			System.out.println("[AFFICHAGE CHEMIN REQUETES] : " + queryFile );
	        			
	        			break;
	        		case "-output" : 
	        			pathCSV = args[i+1];
	        			//System.out.println("AFFICHAGE " + pathCSV );
	        			break;
	        		
	        		case "-jena" : 
	        			System.out.println("[OPTION] : jena activé "  );
	        			jenaB = true; 
	        			
	        			
	        			break;
	        		case "-shuffle" :
	        			System.out.println("[OPTION] : shuffle activé");
	        			shuffle = true; 
	        			break;
	        			
	        		case "-warm" : 
	        			System.out.println("[OPTION] : warm activé");
	        			warn = true; 
	        			pourcentageWarn = Float.parseFloat(args[i+1])/100; 
	        			break;
	        			
	       
	        		}
	        	}


	   
		
		
		/**************************************DEBUT PROGRAMME *****************************************/
		
		long startTimeProgramme = System.currentTimeMillis();
		
		
		/***************************************CREATION DICTIONNAIRE ET INDEX***************************/
        
		//evaluation du temps de la fonction parseData
		long tempslectureDonnee; 
		MainRDFHandler rdf  = parseData();
		long endTimeDonnee = System.currentTimeMillis();
		tempslectureDonnee =  endTimeDonnee -startTimeProgramme; 
		
		//On récupére le dico 
		Dictionnaire<Integer, String> dico; 
		dico = rdf.getDictionnaireEncode();
		
		//on récupére les index du fichier MainRDF
	 	// vous pouvez aussi afficher d'autre index : POS, SOP etc ...
		Index index_ops = new Index();
		index_ops = rdf.getIndex("POS");
		
		
		
		/****************************DECOMMENTER ICI POUR VOIR L'AFFICHAGE DU DICTIONNAIRE *************/
		
		
		//System.out.println("\nAFFICHAGE DICTIONNAIRE\n");
		//dico.affichage();
		
		
		
		/******************************VARIABLE POUR LE TEMPS D'EXECUTION*********************************/
		
		long tempsdico = rdf.getTempsExecutionDico();
		long tempsIndex = rdf.getTempsExecutionIndex();
	  
		//on récupére le nombre de triplet 
		int nbTriplet = rdf.getnbTriplet() ;
		
		/***************************DECOMMENTER ICI POUR VOIR L'AFFICHAGE DE L'INDEX ***************************************/
		
		
		//System.out.println("\nAFFICHAGE INDEX OPS\n");
		//System.out.println(  index_ops + " \n");
		
		/***************************DECOMMENTER ICI POUR VOIR L'AFFICHAGE DES REPONSES ***************************************/
		
		//lecture, encodage, reponse et décodage des requetes 
		
		long startTimeWorkload = System.currentTimeMillis();
		parseQueries(dico,index_ops);
		long endTimeWorkload = System.currentTimeMillis();
		long timeElapsedWorkload = endTimeWorkload - startTimeWorkload;
		
		//affichage du resulats 
		//System.out.println("Affichage des resultats des requetes " + resultsList);
				
		//resultat en applicant jena 
		Jena jena = new Jena(); 
		Map<Integer, Set<String>> resultsListJenna = new HashMap<>();
		resultsListJenna = jena.executeQuery(dataFile,queryFile); 
	//	System.out.println("Affichage des resultats des requetes avec jena "+ resultsListJenna);
		if(jenaB) {
			
		
			if (areEqual(resultsListJenna,resultsList)) {
			System.out.println("La verification à était faite les résulats sont correcte ");
			
			}else {
			
			System.out.println("La verification à était faite les résulats sont incorrecte ");
			
			}
		}
	
		/**************************************FIN PROGRAMME *****************************************/
		long endTimeProgramme = System.currentTimeMillis();
		long timeElapsedProgramme = endTimeProgramme - startTimeProgramme;
		
		/****************************************ECRITURE DANS UN FICHIER CSV***************************************/
		
		/*écriture des informations suivantes dans un fichier csv 
			nom du fichier de données | nom du dossier des requêtes | nombre de triplets
		  	RDF | nombre de requêtes | temps de lecture des données (ms) | temps
		  	de lecture des requêtes (ms) | temps création dico (ms) | nombre d’index |
		  	temps de création des index (ms) | temps total d’évaluation du workload (ms) ??
		   	temps total (du début à la fin du programme) 
		*/
		List<String> listenomFichier = Arrays.asList("nom du fichier de données", "nom du dossier des requêtes", 
				"nombre de triplets RDF", "nombre de requêtes", "temps lecture des données (ms)", 
				"temps lecture des requêtes (ms)", "temps création dico (ms)", "nombre d’index",
				"temps de création des index (ms)", "temps total d’évaluation du workload (ms)", "temp Total");

		
		List<Object> tab = Arrays.asList(dataFile, queryFile, nbTriplet,nbRequetes,tempslectureDonnee - tempsdico,tempslectureRequete,tempsdico,index_ops.size(),tempsIndex,timeElapsedWorkload,timeElapsedProgramme);
		WriteCSV<Object> executionTimeCSV = new WriteCSV<Object>();
		executionTimeCSV.writeCSVinfo(pathCSV,listenomFichier,tab,"Info Execution_100K" );
		
		//ecriture des reponses des requetes sur un fichier csv
		List<String> listenomRequete = new ArrayList<String>(); 
		for (int i = 0; i < nbRequetes;i++) {
			listenomRequete.add("Réponse requete " + i);
		}
		
		WriteCSV<Set<String>> requeteCSV = new WriteCSV<Set<String>>();
		requeteCSV.writeCSV(pathCSV,listenomRequete,resultsList, "Réponse requêtes_100K" );
	        }
		 System.out.println("\nProgramme terminé "  );
	}

	// ========================================================================

	/**
	 * Traite chaque requête lue dans {@link #queryFile} avec {@link #processAQuery(ParsedQuery)}.
	 */
	private static void  parseQueries(Dictionnaire<Integer, String> dico1,Index index) throws FileNotFoundException {
		/**
		 * Try-with-resources
		 * 
		 * @see <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
		 */
		/*
		 * On utilise un stream pour lire les lignes une par une, sans avoir à toutes les stocker
		 * entièrement dans une collection.
		 */
		
		List<ParsedQuery> queryList = new ArrayList<>();
		long startTimeQuery = System.currentTimeMillis();
		
		try (Stream<String> lineStream = Files.lines(Paths.get(queryFile))) {
			SPARQLParser sparqlParser = new SPARQLParser();
			Iterator<String> lineIterator = lineStream.iterator();
			StringBuilder queryString = new StringBuilder();

			while (lineIterator.hasNext())
			/*
			 * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par un '}'
			 * On considère alors que c'est la fin d'une requête
			 */
			{
				String line = lineIterator.next();
				
				queryString.append(line);
			
				if (line.trim().endsWith("}")) {
					ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);
				
					

						// Traitement de la requête, à adapter/réécrire pour votre programme
						//on fait un tableau de requetes 
						queryList.add(query);
						
				
					
					
					
					queryString.setLength(0); // Reset le buffer de la requête en chaine vide
				}
			}
		}catch (IOException e) {
           
            // gestion des erreurs 
			//si le code ne trouve pas le fichier on lui donne une valeur par defaut 
            System.err.println("Une erreur est survenue lors de la lecture du fichier : " + e.getMessage());
            queryFile = workingDir + "sample_query.queryset";
            parseQueries( dico1, index);
            
        }
		
		//option shuffle --> mélange des rêquetes 
		if(shuffle) {
			
			
	        Collections.shuffle(queryList); 
	        
	        if(!warn) {
	        	//option warn --> on considére une pourcentage des rêquetes 
	        	
	        	//execution des requetes 
				for(int i =0; i < queryList.size();i++) {
					processAQuery(queryList.get(i),dico1,index);
				}
	        	
	        }else {
	        	
	        //	System.out.println("[OPTION] : shuffle et warn activé");
	        	float size = queryList.size() * pourcentageWarn;
	        	System.out.println("Le pourcentage de requetes qu'on veut considéré est" + pourcentageWarn+"\nsur un fichier de requetes de taille " + queryList.size()+ " donc " + size);
	        	
	        	//on arrondi toujours au nombre suppérieur --> 2.4 = 3
	        	for(int i =0; i < size;i++) {
					processAQuery(queryList.get(i),dico1,index);
				}
	        }
	        //si c'est pas shuffle mais que c'est warn
		}else {
			
			if(warn) {
			
			System.out.println("[OPTION] : warn activé");
			float size = queryList.size() * pourcentageWarn;
			System.out.println("Le pourcentage de requetes qu'on veut considéré est" + pourcentageWarn+"\nsur un fichier de requetes de taille" + queryList.size()+ " donc " + size);
			
			for(int i =0; i < size;i++) {
				processAQuery(queryList.get(i),dico1,index);
			}
			}else {
				
				for(int i =0; i < queryList.size();i++) {
					processAQuery(queryList.get(i),dico1,index);
				}
				
			}
			
		}
		
		long endTimeRequete = System.currentTimeMillis();
		tempslectureRequete = endTimeRequete - startTimeQuery; 
		
	}
	
	

	




	/**
	 * Traite chaque triple lu dans {@link #dataFile} avec {@link MainRDFHandler}.
	 */
	private static MainRDFHandler parseData() throws FileNotFoundException, IOException {

		try (Reader dataReader = new FileReader(dataFile)) {
			
			// On va parser des données au format ntriples
			//RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
			NTriplesParser rdfParser = new NTriplesParser(); 
			// On utilise notre implémentation de handler
			
			MainRDFHandler rdf = new MainRDFHandler();
			rdfParser.setRDFHandler(rdf);
		 
			// Parsing et traitement de chaque triple par le handler
			rdfParser.parse(dataReader, baseURI);
			
			return rdf; 
		}catch (FileNotFoundException e) {
			// gestion des erreurs 
			//si le code ne trouve pas le fichier on lui donne une valeur par defaut
			
		    System.err.println("Le fichier spécifié dans dataFile n'a pas été trouvé : " + e.getMessage()+ "on attribut par defaut une valeur au chemin .");
		    dataFile = workingDir + "sample_data.nt";
		    
		    
		    return parseData();
		}
	}
	// Vérifie si les deux Map sont égales
    
	   public static boolean areEqual(Map<Integer, Set<String>> map1, Map<Integer, Set<String>> map2) {
	        // Vérification de la taille des deux Map
	        if (map1.size() != map2.size()) {
	            return false;
	        }

	        // Vérification des ensembles de valeurs pour chaque clé dans la première Map
	        for (Map.Entry<Integer, Set<String>> entry : map1.entrySet()) {
	            Integer key = entry.getKey();
	            Set<String> values1 = entry.getValue();

	            // Vérification de l'existence de la clé et des ensembles de valeurs correspondants dans la deuxième Map
	            if (!map2.containsKey(key) || !map2.get(key).equals(values1)) {
	                return false;
	            }
	        }

	        // Si tous les ensembles de valeurs de map1 existent également dans map2 pour chaque clé, les Map sont égales
	        return true;
	    }
    
}
