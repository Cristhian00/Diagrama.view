package diagrama.model;

import abstracta.AbstractaFactory;
import abstracta.TipoDato;
import abstracta.TipoRetorno;
import abstracta.Visibilidad;
import diagrama_concreta.ModelFactory;
import diagrama_concreta.TCDAgregacion;
import diagrama_concreta.TCDAsociacion;
import diagrama_concreta.TCDAtributo;
import diagrama_concreta.TCDClase;
import diagrama_concreta.TCDComposicion;
import diagrama_concreta.TCDDependencia;
import diagrama_concreta.TCDDiagramaClases;
import diagrama_concreta.TCDHerencia;
import diagrama_concreta.TCDMetodo;
import diagrama_concreta.TCDPaquete;
import diagrama_concreta.TCDRelacion;

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
			// Crea los paquetes
			for (TCDPaquete tcdPaqueteC : diagramaConcreta.getListaPaquetes()) {
				crearPaquetes(tcdPaqueteC);
			}
			// Crea las clases
			for (TCDClase tcdClaseC : diagramaConcreta.getListaClases()) {
				crearClase(tcdClaseC);
			}
			// Crea las relaciones
			for (TCDRelacion tcdRelacionC : diagramaConcreta.getListaRelaciones()) {
				crearRelacion(tcdRelacionC);
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

			for (TCDAtributo tcdAtributo : tcdClaseC.getListaAtributos()) {
				crearAtributo(tcdAtributo, tcdClaseA);
			}
			for (TCDMetodo tcdMetodo : tcdClaseC.getListaMetodos()) {
				crearMetodo(tcdMetodo, tcdClaseA);
			}
			abstracta.TCDPaquete paquetePadre = obtenerPaquete(ruta);
			paquetePadre.getListaClases().add(tcdClaseA);
			modelFactoryAbstracta.getListaTodasClases().add(tcdClaseA);
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

	private void crearRelacion(TCDRelacion tcdRelacionC) {

		TCDClase RelSourceCon = tcdRelacionC.getSource();
		TCDClase RelTargetCon = tcdRelacionC.getTarget();

		abstracta.TCDClase ClaseSourceAbs = obtenerClaseAbstracta(RelSourceCon.getNombre(), RelSourceCon.getRuta());
		abstracta.TCDClase ClaseTargetAbs = obtenerClaseAbstracta(RelTargetCon.getNombre(), RelTargetCon.getRuta());

		abstracta.TCDRelacion nuevaRelacionSource = null;
		abstracta.TCDRelacion nuevaRelacionTarget = null;

		if (tcdRelacionC instanceof TCDHerencia) {
			nuevaRelacionSource = AbstractaFactory.eINSTANCE.createTCDHerencia();
			nuevaRelacionTarget = AbstractaFactory.eINSTANCE.createTCDHerencia();
		} else {
			if (tcdRelacionC instanceof TCDAgregacion) {
				nuevaRelacionSource = AbstractaFactory.eINSTANCE.createTCDAgregacion();
				nuevaRelacionTarget = AbstractaFactory.eINSTANCE.createTCDAgregacion();
				relacionTCDAgregacion(tcdRelacionC, ((abstracta.TCDAgregacion) nuevaRelacionSource),
						((abstracta.TCDAgregacion) nuevaRelacionTarget));
			} else if (tcdRelacionC instanceof TCDAsociacion) {
				nuevaRelacionSource = AbstractaFactory.eINSTANCE.createTCDAsociacion();
				nuevaRelacionTarget = AbstractaFactory.eINSTANCE.createTCDAsociacion();
				relacionTCDAsociacion(tcdRelacionC, ((abstracta.TCDAsociacion) nuevaRelacionSource),
						((abstracta.TCDAsociacion) nuevaRelacionTarget));
			} else if (tcdRelacionC instanceof TCDComposicion) {
				nuevaRelacionSource = AbstractaFactory.eINSTANCE.createTCDComposicion();
				nuevaRelacionTarget = AbstractaFactory.eINSTANCE.createTCDComposicion();
				relacionTCDComposicion(tcdRelacionC, ((abstracta.TCDComposicion) nuevaRelacionSource),
						((abstracta.TCDComposicion) nuevaRelacionTarget));
			} else if (tcdRelacionC instanceof TCDDependencia) {
				nuevaRelacionSource = AbstractaFactory.eINSTANCE.createTCDDependencia();
				nuevaRelacionTarget = AbstractaFactory.eINSTANCE.createTCDDependencia();
				relacionTCDDependencia(tcdRelacionC, ((abstracta.TCDDependencia) nuevaRelacionSource),
						((abstracta.TCDDependencia) nuevaRelacionTarget));
			}
		}
		nuevaRelacionSource.setSource(ClaseSourceAbs);
		nuevaRelacionSource.setTarget(ClaseTargetAbs);

		nuevaRelacionTarget.setSource(ClaseTargetAbs);
		nuevaRelacionTarget.setTarget(ClaseSourceAbs);

		ClaseSourceAbs.getListaRelaciones().add(nuevaRelacionSource);
		ClaseTargetAbs.getListaRelaciones().add(nuevaRelacionTarget);
	}

	private void relacionTCDDependencia(TCDRelacion tcdRelacionC, abstracta.TCDDependencia nuevaRelacionSource,
			abstracta.TCDDependencia nuevaRelacionTarget) {

		nuevaRelacionSource.setNombreOrigen(((TCDDependencia) tcdRelacionC).getNombreOrigen());
		nuevaRelacionSource.setNombreDestino(((TCDDependencia) tcdRelacionC).getNombreDestino());

		nuevaRelacionTarget.setNombreOrigen(((TCDDependencia) tcdRelacionC).getNombreDestino());
		nuevaRelacionTarget.setNombreDestino(((TCDDependencia) tcdRelacionC).getNombreOrigen());

		String multiplicidadOrigen = ((TCDDependencia) tcdRelacionC).getMultiplicidadOrigen().getName();
		String multiplicidadDestino = ((TCDDependencia) tcdRelacionC).getMultiplicidadDestino().getName();

		if (multiplicidadOrigen.equalsIgnoreCase("_1")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._1);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._1);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_0_1")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._01);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._01);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_0_n")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._0N);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._0N);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_1_n")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._1N);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._1N);
		}

		if (multiplicidadDestino.equalsIgnoreCase("_1")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._1);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._1);
		} else if (multiplicidadDestino.equalsIgnoreCase("_0_1")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._01);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._01);
		} else if (multiplicidadDestino.equalsIgnoreCase("_0_n")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._0N);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._0N);
		} else if (multiplicidadDestino.equalsIgnoreCase("_1_n")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._1N);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._1N);
		}
	}

	private void relacionTCDComposicion(TCDRelacion tcdRelacionC, abstracta.TCDComposicion nuevaRelacionSource,
			abstracta.TCDComposicion nuevaRelacionTarget) {

		nuevaRelacionSource.setNombreOrigen(((TCDComposicion) tcdRelacionC).getNombreOrigen());
		nuevaRelacionSource.setNombreDestino(((TCDComposicion) tcdRelacionC).getNombreDestino());

		nuevaRelacionTarget.setNombreOrigen(((TCDComposicion) tcdRelacionC).getNombreDestino());
		nuevaRelacionTarget.setNombreDestino(((TCDComposicion) tcdRelacionC).getNombreOrigen());

		String multiplicidadOrigen = ((TCDComposicion) tcdRelacionC).getMultiplicidadOrigen().getName();
		String multiplicidadDestino = ((TCDComposicion) tcdRelacionC).getMultiplicidadDestino().getName();

		if (multiplicidadOrigen.equalsIgnoreCase("_1")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._1);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._1);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_0_1")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._01);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._01);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_0_n")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._0N);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._0N);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_1_n")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._1N);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._1N);
		}

		if (multiplicidadDestino.equalsIgnoreCase("_1")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._1);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._1);
		} else if (multiplicidadDestino.equalsIgnoreCase("_0_1")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._01);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._01);
		} else if (multiplicidadDestino.equalsIgnoreCase("_0_n")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._0N);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._0N);
		} else if (multiplicidadDestino.equalsIgnoreCase("_1_n")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._1N);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._1N);
		}
	}

	private void relacionTCDAsociacion(TCDRelacion tcdRelacionC, abstracta.TCDAsociacion nuevaRelacionSource,
			abstracta.TCDAsociacion nuevaRelacionTarget) {

		if (((TCDAsociacion) tcdRelacionC).getNavegavilidad().getName().equalsIgnoreCase("bidireccional")) {
			((abstracta.TCDAsociacion) nuevaRelacionSource).setNavegavilidad(abstracta.Navegavilidad.BIDIRECCIONAL);
			((abstracta.TCDAsociacion) nuevaRelacionTarget).setNavegavilidad(abstracta.Navegavilidad.BIDIRECCIONAL);
		} else {
			((abstracta.TCDAsociacion) nuevaRelacionSource).setNavegavilidad(abstracta.Navegavilidad.NONE);
			((abstracta.TCDAsociacion) nuevaRelacionTarget).setNavegavilidad(abstracta.Navegavilidad.UNIDIRECCIONAL);
		}

		nuevaRelacionSource.setNombreOrigen(((TCDAsociacion) tcdRelacionC).getNombreOrigen());
		nuevaRelacionSource.setNombreDestino(((TCDAsociacion) tcdRelacionC).getNombreDestino());

		nuevaRelacionTarget.setNombreOrigen(((TCDAsociacion) tcdRelacionC).getNombreDestino());
		nuevaRelacionTarget.setNombreDestino(((TCDAsociacion) tcdRelacionC).getNombreOrigen());

		String multiplicidadOrigen = ((TCDAsociacion) tcdRelacionC).getMultiplicidadOrigen().getName();
		String multiplicidadDestino = ((TCDAsociacion) tcdRelacionC).getMultiplicidadDestino().getName();

		if (multiplicidadOrigen.equalsIgnoreCase("_1")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._1);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._1);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_0_1")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._01);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._01);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_0_n")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._0N);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._0N);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_1_n")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._1N);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._1N);
		}

		if (multiplicidadDestino.equalsIgnoreCase("_1")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._1);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._1);
		} else if (multiplicidadDestino.equalsIgnoreCase("_0_1")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._01);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._01);
		} else if (multiplicidadDestino.equalsIgnoreCase("_0_n")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._0N);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._0N);
		} else if (multiplicidadDestino.equalsIgnoreCase("_1_n")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._1N);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._1N);
		}
	}

	private void relacionTCDAgregacion(TCDRelacion tcdRelacionC, abstracta.TCDAgregacion nuevaRelacionSource,
			abstracta.TCDAgregacion nuevaRelacionTarget) {

		nuevaRelacionSource.setNombreOrigen(((TCDAgregacion) tcdRelacionC).getNombreOrigen());
		nuevaRelacionSource.setNombreDestino(((TCDAgregacion) tcdRelacionC).getNombreDestino());

		nuevaRelacionTarget.setNombreOrigen(((TCDAgregacion) tcdRelacionC).getNombreDestino());
		nuevaRelacionTarget.setNombreDestino(((TCDAgregacion) tcdRelacionC).getNombreOrigen());

		String multiplicidadOrigen = ((TCDAgregacion) tcdRelacionC).getMultiplicidadOrigen().getName();
		String multiplicidadDestino = ((TCDAgregacion) tcdRelacionC).getMultiplicidadDestino().getName();

		if (multiplicidadOrigen.equalsIgnoreCase("_1")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._1);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._1);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_0_1")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._01);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._01);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_0_n")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._0N);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._0N);
		} else if (multiplicidadOrigen.equalsIgnoreCase("_1_n")) {
			nuevaRelacionSource.setMultiplicidadOrigen(abstracta.Multiplicidad._1N);
			nuevaRelacionTarget.setMultiplicidadDestino(abstracta.Multiplicidad._1N);
		}

		if (multiplicidadDestino.equalsIgnoreCase("_1")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._1);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._1);
		} else if (multiplicidadDestino.equalsIgnoreCase("_0_1")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._01);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._01);
		} else if (multiplicidadDestino.equalsIgnoreCase("_0_n")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._0N);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._0N);
		} else if (multiplicidadDestino.equalsIgnoreCase("_1_n")) {
			nuevaRelacionSource.setMultiplicidadDestino(abstracta.Multiplicidad._1N);
			nuevaRelacionTarget.setMultiplicidadOrigen(abstracta.Multiplicidad._1N);
		}
	}
}