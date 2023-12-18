package qengine.program;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Jena {
	
	
	
	
	public Map<Integer, Set<String>> executeQuery(String dataFile,String queryFile ) {
	   
		Map<Integer, Set<String>> resultsList= new HashMap<>();
	    Model model = ModelFactory.createDefaultModel();
	    
	    InputStream in = FileManager.get().open(dataFile);
	    if (in != null) {
	        model.read(in, null, "N-TRIPLE");
	    } else {
	        System.err.println("Impossible de trouver le fichier : " + dataFile);
	        return resultsList;
	    }
	    
	  
	    List<String> queries = readQueriesFromFile(queryFile); // Méthode à implémenter pour lire toutes les requêtes du fichier
	    
	    int i = 0;
	    for (String queryString : queries) {
	        i++;
	        Query query = QueryFactory.create(queryString);
	        try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
	            ResultSet results = qe.execSelect();
	            
	            // Création d'une nouvelle liste pour stocker les résultats de cette requête
	            Set<String> resultMap = new HashSet<>();
	            while (results.hasNext()) {
	                QuerySolution solution = results.next();
	                Iterator<String> varNames = solution.varNames();
	                while (varNames.hasNext()) {
	                    String varName = varNames.next();
	                    RDFNode value = solution.get(varName);
	                    resultMap.add(value.toString());
	                }
	            }
	            
	            // Stockage de la liste de résultats dans la Map pour cette requête spécifique
	            resultsList.put(i, resultMap);
	            
	        } catch (QueryParseException e) {
	            System.err.println("Erreur de syntaxe dans la requête : " + queryString);
	            e.printStackTrace();
	        }
	    }
	    return resultsList;
	}

	
	
	
    
    // Vérifie si l'une des Map est incluse dans l'autre
    public static boolean contains(Map<Integer, String> map1, Map<Integer, String> map2) {
        // Vérification de la taille des deux Map
        if (map1.size() > map2.size()) {
            return false;
        }

        // Vérification des paires clé-valeur dans la première Map
        for (Map.Entry<Integer, String> entry : map1.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();

            // Vérification de l'existence de la clé et de la valeur correspondante dans la deuxième Map
            if (!map2.containsKey(key) || !map2.get(key).equals(value)) {
                return false;
            }
        }

        // Si toutes les paires clé-valeur de map1 existent également dans map2, map1 est incluse dans map2
        return true;
    }

	
	
	
    
    public static List<String> readQueriesFromFile(String filePath) {
        List<String> queries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder queryBuilder = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    queryBuilder.append(line).append("\n");

                    // Si une requête se termine avec '}' (fin du bloc SPARQL), ajoute-la à la liste
                    if (line.trim().endsWith("}") || line.trim().equals("}")) {
                        queries.add(queryBuilder.toString());
                        queryBuilder = new StringBuilder(); // Réinitialise le StringBuilder pour la prochaine requête
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return queries;
    }
    
    
    
    
    
}
