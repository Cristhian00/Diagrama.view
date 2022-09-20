package diagrama.model;

import abstracta.AbstractaFactory;
import abstracta.TipoDato;
import abstracta.TipoRetorno;
import abstracta.Visibilidad;
import diagrama_concreta.ModelFactory;
import diagrama_concreta.TCDAtributo;
import diagrama_concreta.TCDClase;
import diagrama_concreta.TCDDiagramaClases;
import diagrama_concreta.TCDMetodo;
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

		modelFactoryAbstracta.getListaTodasClases().clear();
		modelFactoryAbstracta.getListaTodosPaquetes().clear();

		for (TCDDiagramaClases diagramaConcreta : modelFactoryConcreta.getListaDiagramas()) {
			// crear los paquetes
			for (TCDPaquete tcdPaquete : diagramaConcreta.getListaPaquetes()) {
				crearPaquetes(tcdPaquete);
			}

			for (TCDClase tcdClase : diagramaConcreta.getListaClases()) {
				crearClase(tcdClase);
			}
		}
		return mensaje;
	}

	private void crearPaquetes(TCDPaquete tcdPaquete) {

		String ruta = tcdPaquete.getRuta() + tcdPaquete.getNombre();
		String[] split = ruta.split("/");
		abstracta.TCDPaquete paqueteParent = null;
		String rutaNombrePaquete;

		// [root, universidad , bienestar, a]
		String nuevaRuta = "";
		for (int i = 0; i < split.length; i++) {
			rutaNombrePaquete = split[i];
			if (!rutaNombrePaquete.equals("")) {
				paqueteParent = obtenerPaqueteAbstracta(rutaNombrePaquete, nuevaRuta, paqueteParent);
				nuevaRuta += split[i] + "/";
			}
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
			modelFactoryAbstracta.getListaTodosPaquetes().add(nuevoPackage);
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
		modelFactoryAbstracta.getListaTodosPaquetes().add(nuevoPackage);
		return nuevoPackage;
	}

	private void crearClase(TCDClase tcdClaseC) {
		String ruta = tcdClaseC.getRuta();
		String name = tcdClaseC.getNombre();

		abstracta.TCDClase claseAbstracta = obtenerClaseAbstracta(name, ruta);
		if (claseAbstracta == null) {

			abstracta.TCDClase tcdClaseA = AbstractaFactory.eINSTANCE.createTCDClase();
			tcdClaseA.setNombre(tcdClaseC.getNombre());
			tcdClaseA.setDocumentacion(tcdClaseC.getDocumentacion());
			tcdClaseA.setEstereotipo(tcdClaseC.getEstereotipo());
			tcdClaseA.setIsAbstract(tcdClaseC.isIsAbstract());
			tcdClaseA.setRuta(tcdClaseC.getRuta());
			modelFactoryAbstracta.getListaTodasClases().add(tcdClaseA);

			for (TCDAtributo tcdAtributo : tcdClaseC.getListaAtributos()) {
				crearAtributo(tcdAtributo, tcdClaseA);
			}
			for (TCDMetodo tcdMetodo : tcdClaseC.getListaMetodos()) {
				crearMetodo(tcdMetodo, tcdClaseA);
			}
			abstracta.TCDPaquete paquetePadre = obtenerPaquete(ruta);
			paquetePadre.getListaClases().add(tcdClaseA);
		}
	}

	private void crearAtributo(TCDAtributo tcdAtributoC, abstracta.TCDClase tcdClaseA) {

		abstracta.TCDAtributo tcdAtributoA = AbstractaFactory.eINSTANCE.createTCDAtributo();
		tcdAtributoA.setNombre(tcdAtributoC.getNombre());
		if (tcdAtributoC.getTipoDato().getName().equalsIgnoreCase("string")) {
			tcdAtributoA.setTipoDato(TipoDato.STRING);
		} else if (tcdAtributoC.getTipoDato().getName().equalsIgnoreCase("number")) {
			tcdAtributoA.setTipoDato(TipoDato.NUMBER);
		} else if (tcdAtributoC.getTipoDato().getName().equalsIgnoreCase("boolean")) {
			tcdAtributoA.setTipoDato(TipoDato.BOOLEAN);
		} else if (tcdAtributoC.getTipoDato().getName().equalsIgnoreCase("undefined")) {
			tcdAtributoA.setTipoDato(TipoDato.UNDEFINED);
		} else if (tcdAtributoC.getTipoDato().getName().equalsIgnoreCase("null")) {
			tcdAtributoA.setTipoDato(TipoDato.NULL);
		}
		if (tcdAtributoC.getVisibilidad().getName().equalsIgnoreCase("public")) {
			tcdAtributoA.setVisibilidad(Visibilidad.PUBLIC);
		} else if (tcdAtributoC.getVisibilidad().getName().equalsIgnoreCase("private")) {
			tcdAtributoA.setVisibilidad(Visibilidad.PRIVATE);
		} else if (tcdAtributoC.getVisibilidad().getName().equalsIgnoreCase("protected")) {
			tcdAtributoA.setVisibilidad(Visibilidad.PROTECTED);
		} else if (tcdAtributoC.getVisibilidad().getName().equalsIgnoreCase("readonly")) {
			tcdAtributoA.setVisibilidad(Visibilidad.READONLY);
		}
		tcdAtributoA.setIsConstante(tcdAtributoC.isIsConstante());
		tcdAtributoA.setValorDefecto(tcdAtributoC.getValorDefecto());

		tcdClaseA.getListaAtributos().add(tcdAtributoA);
	}

	private void crearMetodo(TCDMetodo tcdMetodoC, abstracta.TCDClase tcdClaseA) {

		abstracta.TCDMetodo tcdMetodoA = AbstractaFactory.eINSTANCE.createTCDMetodo();

		tcdMetodoA.setNombre(tcdMetodoC.getNombre());
		if (tcdMetodoC.getModificadorAcceso().getName().equalsIgnoreCase("public")) {
			tcdMetodoA.setModificadorAcceso(Visibilidad.PUBLIC);
		} else if (tcdMetodoC.getModificadorAcceso().getName().equalsIgnoreCase("private")) {
			tcdMetodoA.setModificadorAcceso(Visibilidad.PRIVATE);
		} else if (tcdMetodoC.getModificadorAcceso().getName().equalsIgnoreCase("protected")) {
			tcdMetodoA.setModificadorAcceso(Visibilidad.PROTECTED);
		} else if (tcdMetodoC.getModificadorAcceso().getName().equalsIgnoreCase("readonly")) {
			tcdMetodoA.setModificadorAcceso(Visibilidad.READONLY);
		}
		if (tcdMetodoC.getTipoRetorno().getName().equalsIgnoreCase("string")) {
			tcdMetodoA.setTipoRetorno(TipoRetorno.STRING);
		} else if (tcdMetodoC.getTipoRetorno().getName().equalsIgnoreCase("number")) {
			tcdMetodoA.setTipoRetorno(TipoRetorno.NUMBER);
		} else if (tcdMetodoC.getTipoRetorno().getName().equalsIgnoreCase("boolean")) {
			tcdMetodoA.setTipoRetorno(TipoRetorno.BOOLEAN);
		} else if (tcdMetodoC.getTipoRetorno().getName().equalsIgnoreCase("undefined")) {
			tcdMetodoA.setTipoRetorno(TipoRetorno.UNDEFINED);
		}
		tcdMetodoA.setSemantica(tcdMetodoC.getSemantica());
		tcdMetodoA.getListaParametros().addAll(tcdMetodoC.getListaParametros());

		tcdClaseA.getListaMetodos().add(tcdMetodoA);
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

	private abstracta.TCDPaquete obtenerPaquete(String ruta) {

		for (abstracta.TCDPaquete tcdPaquete : modelFactoryAbstracta.getListaTodosPaquetes()) {
			String rutaAux = tcdPaquete.getRuta() + tcdPaquete.getNombre();
			if (rutaAux.equals(ruta)) {
				return tcdPaquete;
			}
		}
		return null;
	}

}
