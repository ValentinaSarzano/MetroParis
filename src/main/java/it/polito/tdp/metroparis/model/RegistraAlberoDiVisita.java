package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class RegistraAlberoDiVisita implements TraversalListener<Fermata, DefaultEdge> {

	private Map<Fermata, Fermata> alberoInverso;
	private Graph<Fermata, DefaultEdge> grafo;
	
	
	public RegistraAlberoDiVisita(Map<Fermata, Fermata> alberoInverso, Graph<Fermata, DefaultEdge> grafo) {
		super();
		this.alberoInverso = alberoInverso;
		this.grafo=grafo;
	}

	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override //qui riempo di info il mio albero inverso
	public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
		System.out.println(e.getEdge()); //Mi fa vedere quali sono gli archi che attraverso
		
		//Estremi arco che potremmo voler aggiungere all'albero inverso
		Fermata source = this.grafo.getEdgeSource(e.getEdge());
		Fermata target = this.grafo.getEdgeTarget(e.getEdge());
		System.out.println(source + " -- " + target);
		
		//se source c'è e il target non c'è --> ho scoperto il target a partire dal source
		
		//Aggiungo arco all'albero inverso
		if(!alberoInverso.containsKey(target)) { //se target non esiste già nella mappa lo aggiungo --> ho scoperto il target a partire dal source 
			alberoInverso.put(target, source);
			//System.out.println(target + " si raggiunge da " + source);
		}else if(!alberoInverso.containsKey(source)) { //se source non esiste già nella mappa lo aggiungo --> ho scoperto il source a partire dal target
			alberoInverso.put(source, target); //source = vertice nuovo che ho scoperto, target = vertice da cui arrivo
			//System.out.println(source + " si raggiunge da " + target);
		}
	}

	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> e) {
		// TODO Auto-generated method stub
		
	}

}
