package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Label;

import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;

class Scheduler extends ViewComponent {

	private AbsoluteLayout mainLayout;
	
	private Label label;

	public Scheduler() { }

	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("600px");
		mainLayout.setHeight("800px");
		
		// top-level component properties
		setWidth("600px");
		setHeight("800px");
		
		// label
		label = new Label();
		label.setImmediate(false);
		label.setWidth("-1px");
		label.setHeight("-1px");
		label.setValue("<h1>Scheduler</h>");
		label.setContentMode(ContentMode.HTML);
		mainLayout.addComponent(label, "top:100.0px;left:80.0px;");
		
		return mainLayout;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

}
