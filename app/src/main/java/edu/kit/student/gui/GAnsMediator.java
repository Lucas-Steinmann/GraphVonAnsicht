package edu.kit.student.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class GAnsMediator extends Application {
	private GAnsApplication app;
	
	private Stage primaryStage;
	private Parameters params;
	
	public GAnsMediator() {
		this.app = new GAnsApplication(this);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.app.start(this.primaryStage, params == null ? getParameters() : params);
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void openGraph(int id) {
		this.app.openGraph(id);
	}
	
	public GAnsApplication getGansApp() {
	    return app;
	}
}
