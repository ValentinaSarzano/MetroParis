package it.polito.tdp.metroparis;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Model;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

public class MetroController {

	private Model model;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Fermata> cmbArrivo;

    @FXML
    private ComboBox<Fermata> cmbPartenza;

    @FXML
    private TextArea txtResult;
    
    @FXML
    private TableView<Fermata> tbPercorso; //Passo il tipo di oggetto che rappresenta ciascuna riga della colonna 
    
    @FXML
    private TableColumn<Fermata, String> clmnFermata; //Primo parametro: tipo di dato della tabella, Secondo parametro: tipo di dato che verrà visualizzato nella cella

    @FXML
    void handleCerca(ActionEvent event) {
        txtResult.clear();
    	Fermata partenza = cmbPartenza.getValue();
    	Fermata arrivo = cmbArrivo.getValue();
    	
    	if(partenza != null && arrivo!= null && !partenza.equals(arrivo)) { //vado avanti solo se sono state selezionate sia la partenza che l'arrivo e se sono differenti (uso equals perchè sono oggetti) 
    		List<Fermata> percorso = model.calcolaPercorso(partenza, arrivo);
    		
    		tbPercorso.setItems(FXCollections.observableArrayList(percorso)); //setItems vuole un oggetto di tipo ObservableList, quindi uso un metodo statico che prende un arraylist e costruie un oggetto observable compatibile con setItems
    		                                                                  //gli passo la lista da convertire
    		txtResult.setText("Percorso trovato con "+percorso.size()+" stazioni:\n");
    		//txtResult.setText(percorso.toString());
    	} else {
    		txtResult.setText("ERRORE: devi selezionare due stazioni, diverse tra loro!\n");
    	}
    	
    }

    @FXML
    void initialize() {
        assert cmbArrivo != null : "fx:id=\"cmbArrivo\" was not injected: check your FXML file 'Metro.fxml'.";
        assert cmbPartenza != null : "fx:id=\"cmbPartenza\" was not injected: check your FXML file 'Metro.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Metro.fxml'.";
        clmnFermata.setCellValueFactory(new PropertyValueFactory<Fermata, String>("nome"));// nome = gli passo il nome della proprietà di fermata che vogliamo visualizzare (vogliamo visualizzare il nome della fermata) 
    }

	public void setModel(Model m) { //Nonappena conosco il modello la prima cosa che farò è di chiedere al modello di fornirmi l'elenco delle fermate per popolare le tendine
		this.model=m;
		cmbPartenza.getItems().addAll(this.model.getFermate());
		cmbArrivo.getItems().addAll(this.model.getFermate());
	}

}

