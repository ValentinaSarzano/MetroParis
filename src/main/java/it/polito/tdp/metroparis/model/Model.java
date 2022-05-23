package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	//mi conviene spostare la lista di fermate a livello di classe perchè non mi serve solo per il grafo ma anche per popolare i menù a tendina
	private List<Fermata> fermate;
	private Graph<Fermata, DefaultEdge> grafo; //abbiamo ora una struttura dati quale un grafo sul quale possiamo fare le ricerche di percorsi
	private Map<Integer, Fermata> fermateIdMap;
	
	//Metodo che restituisce la lista di fermate al controller che ne ha bisogno per popolare le tendine
	public List<Fermata> getFermate(){
		if(this.fermate==null) { //se non è stato inizializzato
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.getAllFermate(); //scritto così ogni volta che viene chiamato questo metodo fa una query: metto un if, lo fa solo se fermate è vuoto
		
		this.fermateIdMap = new HashMap<Integer, Fermata>(); //Mappa che continene le fermate insieme ai loro id
		for(Fermata f: this.fermate)
			this.fermateIdMap.put(f.getIdFermata(), f);
		} //Se la lista fermate è già popolata, la restituisco solamente
		return this.fermate;
	}
	
	//metodo che legge le info dal db e popolerà il grafo
	public void creaGrafo() {
	
	//Volendo posso mettere un controllo del tipo if(this.grafo == null) --> se il grafo non c'è ancora lo istanzio, altrimenti..
		
	//ISTANZIO IL GRAFO come orientato e non pesato (SimpleDirectedGraph)
	this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);

	//Creo i VERTICI estraendo dal db una lista di oggetti di tipo fermata nella classe DAO
	//MetroDAO dao = new MetroDAO();
	//CHIAMARE IL MINOR NUMERO DI VOLTE POSSIBILI I METODI DEL DAO !!!! PERCHE OGNI VOLTA CHE LI CHIAMIAMO ESEGUONO UN GRAN NUMERO DI QUERY CHE RALLENTANO TUTTO
	//List<Fermata> fermate = dao.getAllFermate(); //ATTENZIONE, bisogna sempre verificare che i VERTICI SIANO UNIVOCI (Fermata deve avere hashcode() e equals() che lavorano su un identificativo univoco della fermata stessa)
	
	MetroDAO dao = new MetroDAO();
	
	//A questo punto potrei iterare su fermate e aggungere i vertici uno a uno:
	// for(Fermata f: fermate){
	//     this.grafo.addVertex(f);
	//}
	//Oppure:
	Graphs.addAllVertices(this.grafo, getFermate()); //parametri sono il grafo a cui voglio settare i vertici e la collection di vertici che voglio aggiungere(fermate ottenute dal db, il singolo vertice corrisponderà ad un oggetto di tipo fermata)
	
	//Creo gli ARCHI
	
	//METODO 1: tra 2 vertici c'è un arco se esiste almeno una linea che collega queste due fermate(vertici) --> lento perchè devo fare un numero di accessi al db pari al quadrato del numero di vertici
	//----> VA BENE PER GRAFI PICCOLI
	/*for(Fermata partenza: fermate) { //PER OGNI ITERAZIONE DI QUESTO CICLO FARA' UNA CHIAMATA AL DAO E QUINDI UNA QUERY!!!
		for(Fermata arrivo: fermate) {
			if(dao.isFermateConnesse(partenza, arrivo))//esiste almeno una connessione tra partenza e arrivo (metodo da aggiungere nel DAO sottoforma di query)
				this.grafo.addEdge(partenza, arrivo);
		}
	}
	
	//METODO 2: guardo gli archi uscenti da un un vertice per volta usando una query che data una fermata di partenza mi restituisca l'elenco delle fermate di arrivo --> piu semplice, devo fare una sola query per ogni vertice
	  
	  Variante 2a): il DAO restituisce un elenco di ID numerici (ID delle stazioni direttamente collegate alla stazione di partenza)
	    for(Fermata partenza: fermate) {
		  List<Integer> idConnesse = dao.getIdFermateConnesse(partenza); //ottengo gli id delle fermate connesse a questa partenza
          for(Integer id: idConnesse) {
    	     Fermata arrivo = null;
    	     for(Fermata f: fermate) { (oppure al posto di fermate this.grafo.vertexSet(), mai fare dao.getAllFemate() --> avrei di nuovo lo stesso problema di numero di chiamate e query da fare!!!)
    		     if(f.getIdFermata() == id) {
    		  	     arrivo = f;
    			     break;
    		     }
    	     }
    	     this.grafo.addEdge(partenza, arrivo);
         } 
	  } 
	
	  Variante 2b): il DAO restituisce un elenco di oggetti di tipo Fermata, le fermate direttamente connesse alla stazione di partenza
	    for(Fermata partenza: fermate) {
	    	List<Fermata> arrivi = dao.getFermateConnesse(partenza);
	    	for(Fermata arrivo: arrivi) {
	    		this.grafo.addEdge(partenza, arrivo);
	    	}
	    }
	    
	  Variante 2c): il DAO restituisce un elenco di ID numerici, che converto in oggetti tramite una mappa Map<Integer, Fermata> --> 'Identity Map'
	    for(Fermata partenza: fermate) {
	    	List<Integer> idConnesse = dao.getIdFermateConnesse(partenza);
	    	for(int id: idConnesse) {
	    		Fermata arrivo = fermateIdMap.get(id);
	    		this.grafo.addEdge(partenza, arrivo);
	    	}
	    }*/
	    
	//METODO 3: Delego quasi tutto il lavoro al db: faccio una sola query che mi restituisce una listab contenente  le coppie di fermate da collegare
	List<CoppiaId> fermateDaCollegareNelGrafo = dao.getAllFermateConnesse();
	for(CoppiaId coppia: fermateDaCollegareNelGrafo) {
		this.grafo.addEdge(
				           fermateIdMap.get(coppia.getIdPartenza()),
				           fermateIdMap.get(coppia.getIdArrivo())
				           );
	} //NB: nella mappa abbiamo TUTTE le fermate, ma io voglio creare gli archi con le sole fermate che sono connesse!
	    
	//Variante preferita: 3 con l'uso di una mappa
	
	/*System.out.println(this.grafo);
	System.out.println("Insieme dei VERTICI: " + this.grafo.vertexSet());
	System.out.println("Numero di VERTICI: " + this.grafo.vertexSet().size());
	System.out.println("Numero di ARCHI: " + this.grafo.edgeSet().size());

	visitaGrafo(fermate.get(0)); //scelgo un vertice di partenza
	*/}
	
	public List<Fermata> calcolaPercorso(Fermata partenza, Fermata arrivo){ //restituisce come risultato la sequenza di stazioni da attraversare per arrivare dalla partenza alla destinazione
	    creaGrafo();
	    Map<Fermata, Fermata> alberoInverso = visitaGrafo(partenza); //data la fermata di partenza mi restituisce quelle ad essa adiacenti
	    Fermata corrente = arrivo;
	    List<Fermata> percorso = new ArrayList<>(); 
	    
	    while(corrente != null) { //finchè il predecessore è diverso da null
	    	percorso.add(0, corrente);;//Ho costruito una lista di ciò che trovo risalendo all'indietro, anche questa lista (percorso.add(corrente)) però sarà al contrario, ma a questo punto posso rimetterla nell'ordine corretto specificando la posizione in cui voglio inserire l'elemento (in testa)
	    	corrente = alberoInverso.get(corrente); //itero, al prossimo giro avrò un nuovo corrente finchè corrente != null
	    	//corrente = getParent(corrente); --> MI EVITA DI IMPLEMENTARE IL LISTENER!!!
	    	}
	    return percorso;
	
	}
	
	
	//Data la fermata di partenza voglio trovare le fermate adiacenti
	public Map<Fermata, Fermata> visitaGrafo(Fermata partenza) {
		
	    GraphIterator<Fermata, DefaultEdge> iteratore = new BreadthFirstIterator<>(this.grafo, partenza); //creo un iteratore con un preciso punto di partenza iniziale: 'partenza', che itera sul this.grafo
	  //GraphIterator<Fermata, DefaultEdge> iteratore = new DepthFirstIterator<>(this.grafo, partenza); --> se volessi visitare il grafo in rpofondità mi basterà cambiare l'oggetto
		
		Map<Fermata, Fermata> alberoInverso = new HashMap<>();
		alberoInverso.put(partenza, null); //Gli dico che il suo predecessore non c'è, perchè stiamo iniziando dal punto di partenza
		//Applico all'iteratore un listener che farà qualcosa ogni volta che itero su iteratore:
		iteratore.addTraversalListener(new RegistraAlberoDiVisita(alberoInverso, this.grafo)); //Prima di far lavorare l'iteratore gli aggancio un listener: l'algoritmo di attraversamento ogni volta che fa qualcosa chiamerà uno dei metodi di questa istanza di questa classe RegistraAlberoDiVisita
		                                                                                       //RegistraAlberoDiVisita avrà come parametro nel costruttore la mappa nella quale voglio che mi inserisca le info che mi servono
		while (iteratore.hasNext()) {//L'iteratore ci da una sequenza di vertici uno dopo l'altro
			Fermata f = iteratore.next();
			//System.out.println(f);
		}
		//Grazie al TraversalListener qui sotto trovo l'alberoInverso pieno
		return alberoInverso;
		/*Ricostruiamo il percorso (ovvero l'albero) a partire dall'albero inverso: QUESTA PROCEDURA LA FACCIO IN calcolaPercorso()
		List<Fermata> percorso = new ArrayList<>();
		fermata = arrivo //parto dal fondo che è arrivo e risalgo finchè non trovo la radice che ha come precedente il null
				while(fermata != null)
				fermata = alberoInverso.get(fermata);
		        percorso.add(fermata); //Ho costruito una lista di ciò che trovo risalendo all'indietro, anche questa lista però sarà al contrario, ma a questo punto posso rimetterla nell'ordine corretto
	*/
	}
	
		
}
