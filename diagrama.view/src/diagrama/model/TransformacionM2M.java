package diagrama.model;

import abstracta.AbstractaFactory;
import abstracta.Visibilidad;
import diagrama_concreta.ModelFactory;
import diagrama_concreta.TCDClase;
import diagrama_concreta.TCDDiagramaClases;
import diagrama_concreta.TCDPaquete;

public class TransformacionM2M {

	private ModelFactory modelFactoryConcreta;
	private abstracta.ModelFactory modelFactoryAbstracta;

	public TransformacionM2M(ModelFactory modelFactoryConcreta, abstracta.ModelFactory modelFactoryAbstracta) {
		super();
		this.modelFactoryConcreta = modelFactoryConcreta;
		this.modelFactoryAbstracta = modelFactoryAbstracta;
	}

	public String transformarM2M() {
		String mensaje = "Se ha realizado la transformacion M2M";

		for (TCDDiagramaClases diagramaConcreta : modelFactoryConcreta.getListaDiagramas()) {
			// crear los paquetes
			System.out.println("Lista paquetes Concre: " + diagramaConcreta.getListaPaquetes());
			System.out.println("Lista clases Concre: " + diagramaConcreta.getListaClases());
			System.out.println("Lista relaciones Concre: " + diagramaConcreta.getListaRelaciones());
			for (TCDPaquete paquete : diagramaConcreta.getListaPaquetes()) {
				crearPaquetes(paquete);
			}
		}
		return mensaje;
	}

	private abstracta.TCDPaquete obtenerPaquete(String ruta) {
		// TODO Auto-generated method stub
		return null;
	}

	private void crearPaquetes(TCDPaquete paquete) {

		String ruta = paquete.getRuta() + paquete.getNombre();// a
		String[] split = ruta.split("/");
		abstracta.TCDPaquete paqueteParent = null;
		String rutaNombrePaquete;
		System.out.println("Lista paquetes de " + paquete.getNombre() + ": " + paquete.getListaPaquetes());
		System.out.println("Lista clases de " + paquete.getNombre() + ": " + paquete.getListaClases());
		
		// [root, universidad , bienestar, a]
		String nuevaRuta = "";
		for (int i = 0; i < split.length; i++) {
			rutaNombrePaquete = split[i];
			System.out.println("entre- rutaPaquete: " + rutaNombrePaquete);
			paqueteParent = obtenerPaqueteAbstracta(rutaNombrePaquete, nuevaRuta, paqueteParent);
			nuevaRuta += split[i] + "/";
		}
	}

	private abstracta.TCDPaquete obtenerPaqueteAbstracta(String nombrePaquete, String nuevaRuta,
			abstracta.TCDPaquete paqueteParent) {

		if (paqueteParent == null) {
			for (int i = 0; i < modelFactoryAbstracta.getListaPaquetes().size(); i++) {
				if (modelFactoryAbstracta.getListaPaquetes().get(i).getNombre().equals(nombrePaquete)) {
					return modelFactoryAbstracta.getListaPaquetes().get(i);
				}
			}
			abstracta.TCDPaquete nuevoPackage = AbstractaFactory.eINSTANCE.createTCDPaquete();
			nuevoPackage.setNombre(nombrePaquete);
			nuevoPackage.setRuta(nuevaRuta);
			modelFactoryAbstracta.getListaPaquetes().add(nuevoPackage);
			return nuevoPackage;

		} else {
			for (int i = 0; i < paqueteParent.getListapaquetes().size(); i++) {
				if (paqueteParent.getListapaquetes().get(i).getNombre().equals(nombrePaquete)) {
					if (paqueteParent.getListapaquetes().get(i).getRuta().equals(nuevaRuta)) {
						return paqueteParent.getListapaquetes().get(i);
					}
				}
			}
		}

		abstracta.TCDPaquete nuevoPackage = AbstractaFactory.eINSTANCE.createTCDPaquete();
		nuevoPackage.setNombre(nombrePaquete);
		nuevoPackage.setRuta(nuevaRuta);
		paqueteParent.getListapaquetes().add(nuevoPackage);
		return nuevoPackage;
	}

	private void crearClase(TCDClase tcdClaseC) {
		String ruta = tcdClaseC.getRuta();// a
		String name = tcdClaseC.getNombre();

		abstracta.TCDClase claseAbstracta = obtenerClaseAbstracta(name, ruta);
		if (claseAbstracta == null) {

			abstracta.TCDClase tcdClaseA = AbstractaFactory.eINSTANCE.createTCDClase();
			tcdClaseA.setNombre(tcdClaseC.getNombre());
			tcdClaseA.setDocumentacion(tcdClaseC.getDocumentacion());
			tcdClaseA.setEstereotipo(tcdClaseC.getEstereotipo());
			tcdClaseA.setIsAbstract(tcdClaseC.isIsAbstract());

			diagrama_concreta.Visibilidad visibilidadC = tcdClaseC.getModificadorAcceso();
			if (visibilidadC.PRIVATE_VALUE == Visibilidad.PRIVATE_VALUE) {
				tcdClaseA.setModificadorAcceso(Visibilidad.PRIVATE);
			} else if (visibilidadC.PUBLIC_VALUE == Visibilidad.PUBLIC_VALUE) {
				tcdClaseA.setModificadorAcceso(Visibilidad.PUBLIC);
			} else {
				tcdClaseA.setModificadorAcceso(Visibilidad.PROTECTED);
			}

			abstracta.TCDPaquete paquetePadre = obtenerPaquete(ruta);
			paquetePadre.getListaClases().add(tcdClaseA);
		}
	}

	private abstracta.TCDClase obtenerClaseAbstracta(String name, String ruta) {

		abstracta.TCDPaquete tcdPaquete = modelFactoryAbstracta.getListaPaquetes().get(0);

		for (abstracta.TCDClase tcdClase : tcdPaquete.getListaClases()) {
			if (tcdClase.getNombre().equalsIgnoreCase(name)) {
				return tcdClase;
			}
		}
		for (abstracta.TCDPaquete tcdPaquete2 : tcdPaquete.getListapaquetes()) {
			abstracta.TCDClase tcdClase = obtenerClasePaquete(tcdPaquete2, name, ruta);
			if (tcdClase != null) {
				return tcdClase;
			}
		}
		return null;
	}

	private abstracta.TCDClase obtenerClasePaquete(abstracta.TCDPaquete tcdPaquete, String name, String ruta) {

		for (abstracta.TCDClase tcdClase : tcdPaquete.getListaClases()) {
			if (tcdClase.getNombre().equals(name)) {
				return tcdClase;
			}
		}
		for (abstracta.TCDPaquete tcdPaquete2 : tcdPaquete.getListapaquetes()) {
			abstracta.TCDClase TCDClase = obtenerClasePaquete(tcdPaquete2, name, ruta);
			if (TCDClase != null) {
				return TCDClase;
			}
		}

		return null;
	}
}
