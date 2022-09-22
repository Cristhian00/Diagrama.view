package diagrama.model;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import abstracta.ModelFactory;
import abstracta.TCDAgregacion;
import abstracta.TCDAsociacion;
import abstracta.TCDAtributo;
import abstracta.TCDClase;
import abstracta.TCDComposicion;
import abstracta.TCDDependencia;
import abstracta.TCDHerencia;
import abstracta.TCDPaquete;
import abstracta.TCDRelacion;

public class TransformacionM2T {

	private abstracta.ModelFactory modelFactoryAbstracta;

	public TransformacionM2T(ModelFactory modelFactoryAbstracta) {
		super();
		this.modelFactoryAbstracta = modelFactoryAbstracta;
	}

	public String transformarM2T() {

		String mensaje = "";
		String pathRaiz = "";
		StringBuilder textoCodigo = new StringBuilder();

		DirectoryDialog fd = new DirectoryDialog(new Shell(), SWT.SELECTED);
		fd.setText("Generacion de codigo");
		pathRaiz = fd.open();

		for (TCDClase tcdClase : modelFactoryAbstracta.getListaTodasClases()) {
			// crear las clases
			textoCodigo = new StringBuilder();
			generarClase(tcdClase, textoCodigo);
			guardarArchivo(textoCodigo.toString(), pathRaiz + "/" + tcdClase.getRuta(), tcdClase.getNombre() + ".ts");
		}
		return "Se ha generado el código de su proyecto";
	}

	private void generarClase(TCDClase tcdClase, StringBuilder textoCodigo) {

		agregarEncabezado(tcdClase, textoCodigo);
		agregarAtributos(tcdClase, textoCodigo);
		agregarConstructor(tcdClase, textoCodigo);
		agregarGetAndSet(tcdClase, textoCodigo);
		agregarMetodos(tcdClase, textoCodigo);
	}

	private void agregarEncabezado(TCDClase tcdClase, StringBuilder textoCodigo) {

		textoCodigo.append("class " + tcdClase.getNombre());
		for (TCDRelacion relacion : tcdClase.getListaRelaciones()) {
			if (relacion instanceof TCDHerencia) {
				if (!relacion.getSource().getNombre().equals(tcdClase.getNombre())) {
					textoCodigo.append(" extends " + relacion.getSource().getNombre());
				}
			}
		}
		textoCodigo.append(" {\n\n");
	}

	private void agregarAtributos(TCDClase tcdClase, StringBuilder textoCodigo) {

		String multiplicidad;
		for (TCDAtributo atributo : tcdClase.getListaAtributos()) {
			// public name: string;
			textoCodigo.append("\t" + atributo.getVisibilidad().getName().toLowerCase() + " " + atributo.getNombre()
					+ ": " + atributo.getTipoDato().getName().toLowerCase());
			if (atributo.getValorDefecto() != null) {
				if (!atributo.getValorDefecto().equals("")) {
					textoCodigo.append(" = " + atributo.getValorDefecto() + ";\n");
				} else {
					textoCodigo.append(";\n");
				}
			} else {
				textoCodigo.append(";\n");
			}
		}
		textoCodigo.append("\n");
		for (TCDRelacion relacion : tcdClase.getListaRelaciones()) {
			// private mascota: Mascota[];
			// private mascota: Mascota;
			if (!(relacion instanceof TCDHerencia)) {
				multiplicidad = "";
				textoCodigo.append("\tprivate ");
				if (relacion instanceof TCDAgregacion) {
					textoCodigo.append(((TCDAgregacion) relacion).getNombreDestino() + ": "
							+ ((TCDAgregacion) relacion).getTarget().getNombre());
					multiplicidad = ((TCDAgregacion) relacion).getMultiplicidadDestino().getName();
				} else if (relacion instanceof TCDAsociacion) {
					if (((TCDAsociacion) relacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
						break;
					}
					textoCodigo.append(((TCDAsociacion) relacion).getNombreDestino() + ": "
							+ ((TCDAsociacion) relacion).getTarget().getNombre());
					multiplicidad = ((TCDAsociacion) relacion).getMultiplicidadDestino().getName();
				} else if (relacion instanceof TCDComposicion) {
					textoCodigo.append(((TCDComposicion) relacion).getNombreDestino() + ": "
							+ ((TCDComposicion) relacion).getTarget().getNombre());
					multiplicidad = ((TCDComposicion) relacion).getMultiplicidadDestino().getName();
				} else if (relacion instanceof TCDDependencia) {
					textoCodigo.append(((TCDDependencia) relacion).getNombreDestino() + ": "
							+ ((TCDDependencia) relacion).getTarget().getNombre());
					multiplicidad = ((TCDDependencia) relacion).getMultiplicidadDestino().getName();
				}
				if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
					textoCodigo.append(";\n");
				} else if (multiplicidad.equalsIgnoreCase("_0_n") || multiplicidad.equalsIgnoreCase("_1_n")) {
					textoCodigo.append("[];\n");
				}
			}
		}
		textoCodigo.append("\n");
	}

	private void agregarConstructor(TCDClase tcdClase, StringBuilder textoCodigo) {
		// constructor(name: string, email:string, age: number, peso: number) {
		// super(name, email, age, peso);
		// this.mascota = [];
		// }
		TCDAtributo atributo;

		textoCodigo.append("\tconstructor (");
		TCDClase claseSuper = clasePadre(tcdClase);
		EList<TCDAtributo> allAtributos;
		if (claseSuper != null) {
			System.out.println("clase " + tcdClase.getNombre() + " extends " + claseSuper.getNombre());
			for (TCDAtributo attr : claseSuper.getListaAtributos()) {
				System.out.println("ClaseSuper: " + attr.getNombre());
			}
			for (TCDAtributo tcdAtributo : tcdClase.getListaAtributos()) {
				System.out.println("ClaseHija: " + tcdAtributo.getNombre());
			}
			allAtributos = claseSuper.getListaAtributos();
			allAtributos.addAll(tcdClase.getListaAtributos());
		} else {
			allAtributos = tcdClase.getListaAtributos();
		}
		for (int i = 0; i < allAtributos.size(); i++) {
			atributo = allAtributos.get(i);
			if (i == allAtributos.size() - 1) {
				textoCodigo.append(atributo.getNombre() + ": " + atributo.getTipoDato() + ") {\n");
			} else {
				textoCodigo.append(atributo.getNombre() + ": " + atributo.getTipoDato() + ", ");
			}
		}

		if (claseSuper != null) {
			textoCodigo.append("\t\tsuper(");
			TCDAtributo atributoSuper;

			for (int i = 0; i < claseSuper.getListaAtributos().size(); i++) {
				atributoSuper = claseSuper.getListaAtributos().get(i);
				if (i == claseSuper.getListaAtributos().size() - 1) {
					textoCodigo.append(atributoSuper.getNombre() + ");\n");
				} else {
					textoCodigo.append(atributoSuper.getNombre() + ", ");
				}
			}
		}
		TCDAtributo atributoAUx;
		for (int i = 0; i < tcdClase.getListaAtributos().size(); i++) {
			atributoAUx = tcdClase.getListaAtributos().get(i);
			textoCodigo.append("\t\tthis." + atributoAUx.getNombre() + " = " + atributoAUx.getNombre() + ";\n");
		}
		textoCodigo.append("\t}");
	}

	private TCDClase clasePadre(TCDClase tcdClase) {
		for (TCDRelacion relacion : tcdClase.getListaRelaciones()) {
			if (relacion instanceof TCDHerencia) {
				if (!relacion.getSource().getNombre().equals(tcdClase.getNombre())) {
					return relacion.getSource();
				}
			}
		}
		return null;
	}

	private void agregarGetAndSet(TCDClase tcdClase, StringBuilder textoCodigo) {
		// TODO Auto-generated method stub

	}

	private void agregarMetodos(TCDClase tcdClase, StringBuilder textoCodigo) {
		// TODO Auto-generated method stub

	}

	private void guardarArchivo(String cadena, String ruta, String nombre) {
		try {
			File archivo = new File(ruta);
			System.out.println("");
			if (archivo.exists() == false) {
				archivo.mkdirs();
			}
			FileWriter escribir = new FileWriter(archivo + "/" + nombre, true);
			escribir.write(cadena);
			escribir.close();
		} catch (Exception e) {
			System.out.println("Error al Guardar");
		}
	}
}
