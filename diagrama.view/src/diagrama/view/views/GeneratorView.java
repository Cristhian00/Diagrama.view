package diagrama.view.views;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.*;

import diagrama.model.ModelFactoryModel;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import javax.inject.Inject;
import org.eclipse.wb.swt.SWTResourceManager;

public class GeneratorView extends ViewPart {

	public GeneratorView() {
	}

	@Override
	public void createPartControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(null);

		Group grpDeModeloA = new Group(composite, SWT.NONE);
		grpDeModeloA.setBackground(SWTResourceManager.getColor(224, 255, 255));
		grpDeModeloA.setText("Generaci\u00F3n de Modelo a Modelo");
		grpDeModeloA.setBounds(24, 48, 400, 70);

		Button btnNewButton = new Button(grpDeModeloA, SWT.NONE);
		btnNewButton.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		btnNewButton.setForeground(SWTResourceManager.getColor(0, 0, 0));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ModelFactoryModel mfm = ModelFactoryModel.getInstance();
				mfm.generarModelToModel();
			}
		});
		btnNewButton.setBounds(10, 30, 380, 30);
		btnNewButton.setText("Generador de M2M");

		Group grpDeModeloA_1 = new Group(composite, SWT.NONE);
		grpDeModeloA_1.setBackground(SWTResourceManager.getColor(255, 182, 193));
		grpDeModeloA_1.setText("Generaci\u00F3n de modelo a texto");
		grpDeModeloA_1.setBounds(24, 140, 400, 70);

		Button btnNewButton_1 = new Button(grpDeModeloA_1, SWT.NONE);
		btnNewButton_1.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		btnNewButton_1.setBounds(10, 30, 380, 30);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnNewButton_1.setText("Generador de M2T");

	}

	private void fillLocalToolBar(IToolBarManager manager) {

	}

	@Override
	public void setFocus() {

	}
}
