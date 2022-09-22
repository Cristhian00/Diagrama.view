package diagrama.model;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import abstracta.AbstractaFactory;
import abstracta.AbstractaPackage;
import abstracta.TCDPaquete;
import diagrama_concreta.Diagrama_concretaFactory;
import diagrama_concreta.Diagrama_concretaPackage;
import diagrama_concreta.ModelFactory;
import diagrama_concreta.TCDAgregacion;
import diagrama_concreta.TCDAsociacion;
import diagrama_concreta.TCDClase;
import diagrama_concreta.TCDComposicion;
import diagrama_concreta.TCDDependencia;
import diagrama_concreta.TCDDiagramaClases;
import diagrama_concreta.TCDHerencia;
import diagrama_concreta.TCDRelacion;

public class ModelFactoryModel {

	// ------------------------------ Singleton
	// ------------------------------------------------
	// Clase estatica oculta. Tan solo se instanciara el singleton una vez
	private static class SingletonHolder {
		// El constructor de Singleton puede ser llamado desde aquí al ser protected
		private final static ModelFactoryModel eINSTANCE = new ModelFactoryModel();
	}

	// Método para obtener la instancia de nuestra clase
	public static ModelFactoryModel getInstance() {
		return SingletonHolder.eINSTANCE;
	}

	// ------------------------------ Singleton
	// ------------------------------------------------
	// ModelFactory modelFactory =
	// Diagrama_concretaFactory.eINSTANCE.createModelFactory();

	private ModelFactory modelFactoryConcreta;
	private abstracta.ModelFactory modelFactoryAbstracta;

	public ModelFactoryModel() {

	}

	public ModelFactory cargarConcreta() {
		ModelFactory modelFactory = null;

		Diagrama_concretaPackage whoownmePackage = Diagrama_concretaPackage.eINSTANCE;
		org.eclipse.emf.ecore.resource.ResourceSet resourceSet = new org.eclipse.emf.ecore.resource.impl.ResourceSetImpl();

		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI
				.createURI("platform:/resource/test/src/model/model2.diagrama_concreta");

		org.eclipse.emf.ecore.resource.Resource resource = resourceSet.createResource(uri);

		try {
			resource.load(null);
			modelFactory = (ModelFactory) resource.getContents().get(0);
			System.out.println("loaded: " + modelFactory);
		} catch (java.io.IOException e) {
			System.out.println("failed to read " + uri);
			System.out.println(e);
		}
		return modelFactory;
	}

	public abstracta.ModelFactory cargarAbstracta() {
		abstracta.ModelFactory modelFactory = null;

		AbstractaPackage whoownmePackage = AbstractaPackage.eINSTANCE;
		org.eclipse.emf.ecore.resource.ResourceSet resourceSet = new org.eclipse.emf.ecore.resource.impl.ResourceSetImpl();

		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI
				.createURI("platform:/resource/test/src/model/model.abstracta");

		org.eclipse.emf.ecore.resource.Resource resource = resourceSet.createResource(uri);

		try {
			resource.load(null);
			modelFactory = (abstracta.ModelFactory) resource.getContents().get(0);
			System.out.println("loaded: " + modelFactory);
		} catch (java.io.IOException e) {
			System.out.println("failed to read " + uri);
			System.out.println(e);
		}
		return modelFactory;
	}

	public void salvarAbstracta() {

		org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI
				.createURI("platform:/resource/test/src/model/model.abstracta");

		org.eclipse.emf.ecore.resource.ResourceSet resourceSet = new org.eclipse.emf.ecore.resource.impl.ResourceSetImpl();

		org.eclipse.emf.ecore.resource.Resource resource = resourceSet.createResource(uri);
		resource.getContents().add(modelFactoryAbstracta);
		try {
			resource.save(java.util.Collections.EMPTY_MAP);
		} catch (java.io.IOException e) {
			// TO-DO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	public void generarModelToModel() {

		modelFactoryConcreta = cargarConcreta();
		modelFactoryAbstracta = cargarAbstracta();
		TransformacionM2M transformacionM2M = new TransformacionM2M(modelFactoryConcreta, modelFactoryAbstracta);
		String msj = transformacionM2M.transformarM2M();
		JOptionPane.showMessageDialog(null, msj);
		salvarAbstracta();

	}

	public void crearPaquete(diagrama_concreta.TCDPaquete paquete) {

		String ruta = paquete.getRuta();
		String[] split = ruta.split("/");
		String nombrePaquete;
		diagrama_concreta.TCDPaquete paqueteAux;
		for (int i = 0; i < split.length; i++) {
			nombrePaquete = split[0];
			paqueteAux = obtenerPaquete(nombrePaquete);
		}
	}

	private diagrama_concreta.TCDPaquete obtenerPaquete(String nombrePaquete) {
		// TODO Auto-generated method stub
		return null;
	}

	public void generarModelToText() {

		modelFactoryAbstracta = cargarAbstracta();
		TransformacionM2T transformacionM2T = new TransformacionM2T(modelFactoryAbstracta);
		String msj = transformacionM2T.transformarM2T();
		JOptionPane.showMessageDialog(null, msj);
	}

	/*
	 * private ArrayList<Object> listaRelacionesClase(String nombre) { // TODO
	 * Auto-generated method stub ArrayList<Object> relacionsalida = new
	 * ArrayList<>(); for (TCDDiagramaClases diagrama :
	 * modelFactory.getListaDiagramas()) { for (TCDRelacion relaciones :
	 * diagrama.getListaRelaciones()) { if (relaciones.getSource().equals(nombre)) {
	 * relacionsalida.add(relaciones.getSource()); } }
	 * 
	 * for (TCDRelacion relaciones : diagrama.getListaRelaciones()) { if
	 * (relaciones.getSource() instanceof TCDComposicion) {
	 * relacionsalida.add(relaciones.getSource()); } } for (TCDRelacion relaciones :
	 * diagrama.getListaRelaciones()) { if (relaciones.getSource() instanceof
	 * TCDAsociacion) { relacionsalida.add(relaciones.getSource()); } } for
	 * (TCDRelacion relaciones : diagrama.getListaRelaciones()) { if
	 * (relaciones.getSource() instanceof TCDDependencia) {
	 * relacionsalida.add(relaciones.getSource()); } } for (TCDRelacion relaciones :
	 * diagrama.getListaRelaciones()) { if (relaciones.getSource() instanceof
	 * TCDAgregacion) { relacionsalida.add(relaciones.getSource()); } } for
	 * (TCDRelacion relaciones : diagrama.getListaRelaciones()) { if
	 * (relaciones.getSource() instanceof TCDHerencia) {
	 * relacionsalida.add(relaciones.getSource()); } }
	 * 
	 * } return relacionsalida; }
	 */

	/*
	 * private TCDClase obtenerClase(String nombre) { // TODO Auto-generated method
	 * stub TCDClase clase = null; for (TCDDiagramaClases diagrama :
	 * modelFactory.getListaDiagramas()) {
	 * System.out.println("Diagramas de clases: " + diagrama.getNombre() + "\n");
	 * for (TCDClase clases : diagrama.getListaClases()) { if
	 * (clases.getNombre().equals(nombre)) { return clases; }
	 * 
	 * } }
	 * 
	 * return clase; }
	 */

}
