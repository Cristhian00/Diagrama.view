package diagrama.model;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import abstracta.ModelFactory;
import abstracta.Multiplicidad;
import abstracta.TCDAgregacion;
import abstracta.TCDAsociacion;
import abstracta.TCDAtributo;
import abstracta.TCDClase;
import abstracta.TCDComposicion;
import abstracta.TCDDependencia;
import abstracta.TCDHerencia;
import abstracta.TCDMetodo;
import abstracta.TCDRelacion;
import abstracta.TipoRetorno;

public class TransformacionM2T {

	private abstracta.ModelFactory modelFactoryAbstracta;

	public TransformacionM2T(ModelFactory modelFactoryAbstracta) {
		super();
		this.modelFactoryAbstracta = modelFactoryAbstracta;
	}

	public String transformarM2T() {

		String pathRaiz = "";
		StringBuilder txtCodigo = new StringBuilder();

		DirectoryDialog fd = new DirectoryDialog(new Shell(), SWT.SELECTED);
		fd.setText("Generacion de codigo");
		pathRaiz = fd.open();
		// pathRaiz = "C:\\Users\\crist\\Desktop\\Semestre 10\\Ingenieria de
		// software\\Codigo";

		for (TCDClase tcdClase : modelFactoryAbstracta.getListaTodasClases()) {
			// crear las clases
			txtCodigo = new StringBuilder();
			generarClase(tcdClase, txtCodigo);
			guardarArchivo(txtCodigo.toString(), pathRaiz + "/" + tcdClase.getRuta(), tcdClase.getNombre() + ".ts");
			tcdClase = null;
		}
		return "Se ha generado el código de su proyecto";
	}

	private void generarClase(TCDClase tcdClase, StringBuilder txtCodigo) {

		agregarImport(tcdClase, txtCodigo);
		agregarEncabezado(tcdClase, txtCodigo);
		agregarAtributos(tcdClase, txtCodigo);
		agregarConstructor(tcdClase, txtCodigo);
		agregarGetAndSet(tcdClase, txtCodigo);
		agregarMetodos(tcdClase, txtCodigo);
		txtCodigo.append("}");
	}

	private void agregarImport(TCDClase tcdClase, StringBuilder txtCodigo) {
		TCDClase claseRelacion;
		int nivel = 0;

		for (TCDRelacion relacion : tcdClase.getListaRelaciones()) {
			if (relacion instanceof TCDHerencia) {
				if (relacion.getSource().getNombre().equals(tcdClase.getNombre())) {
					continue;
				}
				claseRelacion = relacion.getSource();
			} else {
				claseRelacion = relacion.getTarget();
			}

			txtCodigo.append("import { " + claseRelacion.getNombre() + " } from \"");
			nivel = calcularDiferencia(tcdClase, claseRelacion);
			if (nivel == 0) {
				rutasSinDiferencia(tcdClase, claseRelacion, tcdClase.getRuta().split("/"),
						claseRelacion.getRuta().split("/"), txtCodigo, true);
			} else if (nivel > 0) {
				String[] rutaNew = tcdClase.getRuta().split("/");
				while (nivel != 0) {
					txtCodigo.append("../");
					rutaNew = Arrays.copyOf(rutaNew, rutaNew.length - 1);
					nivel--;
				}
				rutasSinDiferencia(tcdClase, claseRelacion, rutaNew, claseRelacion.getRuta().split("/"), txtCodigo,
						false);
			} else if (nivel < 0) {
				String[] rutaNew = claseRelacion.getRuta().split("/");
				while (nivel != 0) {
					rutaNew = Arrays.copyOf(rutaNew, rutaNew.length - 1);
					nivel++;
				}
				String[] rutaClase = tcdClase.getRuta().split("/");
				if (!rutasIguales(rutaClase, rutaNew)) {
					int tamanio = rutaNew.length - 1;
					for (int i = tamanio; i >= 0; i--) {
						if (!rutaNew[i].equals(rutaClase[i])) {
							txtCodigo.append("../");
							rutaNew = Arrays.copyOf(rutaNew, rutaNew.length - 1);
						} else {
							break;
						}
					}
				} else {
					txtCodigo.append("./");
				}
				String[] rutaFin = claseRelacion.getRuta().split("/");
				for (int i = rutaNew.length; i < rutaFin.length; i++) {
					txtCodigo.append(rutaFin[i] + "/");
				}
				txtCodigo.append(claseRelacion.getNombre() + "\";\n");
			}
		}
		txtCodigo.append("\n");
	}

	private int calcularDiferencia(TCDClase tcdClase, TCDClase claseRelacion) {

		String[] rutaClase1 = tcdClase.getRuta().split("/");
		String[] rutaClase2 = claseRelacion.getRuta().split("/");
		int diferencia = rutaClase1.length - rutaClase2.length;
		if (diferencia < 0) {
			System.out.println("---Relaciones para imports---");
			System.out.println(tcdClase.getNombre() + "<--->" + claseRelacion.getNombre());
			System.out.println(tcdClase.getNombre() + "-> ruta1: " + tcdClase.getRuta());
			System.out.println(claseRelacion.getNombre() + "-> ruta2: " + claseRelacion.getRuta());
			System.out.println("Diferencia de nivel: " + diferencia + "\n");
		}

		return diferencia;
	}

	private void rutasSinDiferencia(TCDClase tcdClase, TCDClase claseRelacion, String[] rutaClase1, String[] rutaClase2,
			StringBuilder txtCodigo, boolean mismoNivel) {

		if (rutasIguales(rutaClase1, rutaClase2)) {
			if (mismoNivel) {
				txtCodigo.append("./");
			}
			txtCodigo.append(claseRelacion.getNombre() + "\";\n");
		} else {
			String finRuta = rutaClase2[rutaClase2.length - 1];
			txtCodigo.append("../" + finRuta + "/" + claseRelacion.getNombre() + "\";\n");
		}
	}

	private boolean rutasIguales(String[] rutaClase1, String[] rutaClase2) {
		for (int i = 0; i < rutaClase1.length; i++) {
			if (!rutaClase1[i].equals(rutaClase2[i])) {
				return false;
			}
		}
		return true;
	}

	private void agregarEncabezado(TCDClase tcdClase, StringBuilder txtCodigo) {

		txtCodigo.append("export ");
		if (tcdClase.isIsAbstract()) {
			txtCodigo.append("abstract ");
		}
		txtCodigo.append("class " + tcdClase.getNombre());
		for (TCDRelacion relacion : tcdClase.getListaRelaciones()) {
			if (relacion instanceof TCDHerencia) {
				if (!relacion.getSource().getNombre().equals(tcdClase.getNombre())) {
					txtCodigo.append(" extends " + relacion.getSource().getNombre());
				}
			}
		}
		txtCodigo.append(" {\n\n");
	}

	private void agregarAtributos(TCDClase tcdClase, StringBuilder txtCodigo) {

		// Lista todos los atriutos de cada clase y los agrega
		String multiplicidad;
		for (TCDAtributo atributo : tcdClase.getListaAtributos()) {
			txtCodigo.append("\t" + atributo.getVisibilidad().getName().toLowerCase() + " " + atributo.getNombre()
					+ ": " + atributo.getTipoDato().getName().toLowerCase());
			if (atributo.getValorDefecto() != null) {
				if (!atributo.getValorDefecto().equals("")) {
					txtCodigo.append(" = " + atributo.getValorDefecto() + ";\n");
				} else {
					txtCodigo.append(";\n");
				}
			} else {
				txtCodigo.append(";\n");
			}
		}

		// Lista Todas las relaciones que tiene la clase, exceptuando la de herencia, y
		// las agrega
		ArrayList<TCDRelacion> relaciones = obtenerRelaciones(tcdClase);
		for (TCDRelacion relacion : relaciones) {
			multiplicidad = "";
			if (relacion instanceof TCDAgregacion) {
				if (((TCDAgregacion) relacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				} else {
					txtCodigo.append("\tprivate ");
					txtCodigo.append(((TCDAgregacion) relacion).getNombreDestino() + ": "
							+ ((TCDAgregacion) relacion).getTarget().getNombre());
					multiplicidad = ((TCDAgregacion) relacion).getMultiplicidadDestino().getName();
				}
			} else if (relacion instanceof TCDAsociacion) {
				if (((TCDAsociacion) relacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				} else {
					txtCodigo.append("\tprivate " + ((TCDAsociacion) relacion).getNombreDestino() + ": "
							+ ((TCDAsociacion) relacion).getTarget().getNombre());
					multiplicidad = ((TCDAsociacion) relacion).getMultiplicidadDestino().getName();
				}
			} else if (relacion instanceof TCDComposicion) {
				if (((TCDComposicion) relacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				} else {
					txtCodigo.append("\tprivate " + ((TCDComposicion) relacion).getNombreDestino() + ": "
							+ ((TCDComposicion) relacion).getTarget().getNombre());
					multiplicidad = ((TCDComposicion) relacion).getMultiplicidadDestino().getName();
				}
			} else if (relacion instanceof TCDDependencia) {
				if (((TCDDependencia) relacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				} else {
					txtCodigo.append("\tprivate " + ((TCDDependencia) relacion).getNombreDestino() + ": "
							+ ((TCDDependencia) relacion).getTarget().getNombre());
					multiplicidad = ((TCDDependencia) relacion).getMultiplicidadDestino().getName();
				}
			}
			// Agrega un ; si es una clase de multiplicidad 1 o unos corchetes si es n
			if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
				txtCodigo.append(";\n");
			} else if (multiplicidad.equalsIgnoreCase("_0_n") || multiplicidad.equalsIgnoreCase("_1_n")) {
				txtCodigo.append("[];\n");
			}
		}
		txtCodigo.append("\n");
	}

	private void agregarConstructor(TCDClase tcdClase, StringBuilder txtCodigo) {

		TCDAtributo atributo;
		ArrayList<TCDAtributo> allAtributos = new ArrayList<>();
		TCDClase claseSuper = clasePadre(tcdClase);
		txtCodigo.append("\tconstructor (");

		// Pregunta si la clase hereda de otra para agregar los atributos de la clase
		// padre en los parametros de la clase hija
		if (claseSuper != null) {
			allAtributos.addAll(claseSuper.getListaAtributos());
			allAtributos.addAll(tcdClase.getListaAtributos());
		} else {
			allAtributos.addAll(tcdClase.getListaAtributos());
		}

		// Agrega en los parametros todos los atributos que necesita la clase
		for (int i = 0; i < allAtributos.size(); i++) {
			atributo = allAtributos.get(i);
			if (i == allAtributos.size() - 1) {
				txtCodigo.append(atributo.getNombre() + ": " + atributo.getTipoDato());
			} else {
				txtCodigo.append(atributo.getNombre() + ": " + atributo.getTipoDato() + ", ");
			}
		}

		ArrayList<TCDRelacion> relacionesPadre = null;
		if (claseSuper != null) {
			relacionesPadre = obtenerRelacionesSimples(claseSuper);
			for (TCDRelacion tcdRelacion : relacionesPadre) {
				txtCodigo.append(", " + tcdRelacion.getTarget().getNombre().toLowerCase() + ": "
						+ tcdRelacion.getTarget().getNombre());
			}
		}

		// Obtiene las relaciones que tenga la clase para saber si las agrega como
		// parametros del construtor
		ArrayList<TCDRelacion> relaciones = obtenerRelaciones(tcdClase);
		TCDRelacion relacionAux;
		String multiplicidad;
		char ultimoCaracter = txtCodigo.charAt(txtCodigo.length() - 1);
		for (int i = 0; i < relaciones.size(); i++) {
			relacionAux = relaciones.get(i);
			if (relacionAux instanceof TCDAgregacion) {
				if (((TCDAgregacion) relacionAux).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				} else {
					multiplicidad = ((TCDAgregacion) relacionAux).getMultiplicidadDestino().getName();
					if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
						if (ultimoCaracter != '(') {
							txtCodigo.append(", ");
						}
						txtCodigo.append(((TCDAgregacion) relacionAux).getNombreDestino() + ": ");
						txtCodigo.append(((TCDAgregacion) relacionAux).getTarget().getNombre());
					}
				}
			} else if (relacionAux instanceof TCDAsociacion) {
				if (((TCDAsociacion) relacionAux).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				} else {
					multiplicidad = ((TCDAsociacion) relacionAux).getMultiplicidadDestino().getName();
					if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
						if (ultimoCaracter != '(') {
							txtCodigo.append(", ");
						}
						txtCodigo.append(((TCDAsociacion) relacionAux).getNombreDestino() + ": ");
						txtCodigo.append(((TCDAsociacion) relacionAux).getTarget().getNombre());
					}
				}
			} else if (relacionAux instanceof TCDComposicion) {
				if (((TCDComposicion) relacionAux).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				} else {
					multiplicidad = ((TCDComposicion) relacionAux).getMultiplicidadDestino().getName();
					if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
						if (ultimoCaracter != '(') {
							txtCodigo.append(", ");
						}
						txtCodigo.append(((TCDComposicion) relacionAux).getNombreDestino() + ": ");
						txtCodigo.append(((TCDComposicion) relacionAux).getTarget().getNombre());
					}
				}
			} else if (relacionAux instanceof TCDDependencia) {
				if (((TCDDependencia) relacionAux).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				} else {
					multiplicidad = ((TCDDependencia) relacionAux).getMultiplicidadDestino().getName();
					if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
						if (ultimoCaracter != '(') {
							txtCodigo.append(", ");
						}
						txtCodigo.append(((TCDDependencia) relacionAux).getNombreDestino() + ": ");
						txtCodigo.append(((TCDDependencia) relacionAux).getTarget().getNombre());
					}
				}
			}
		}
		txtCodigo.append(") {\n");

		// Si la clase heredo de otra agregar el metodo super con los parametros de la
		// clase padre
		if (claseSuper != null) {
			txtCodigo.append("\t\tsuper(");
			TCDAtributo atributoSuper;

			for (int i = 0; i < claseSuper.getListaAtributos().size(); i++) {
				atributoSuper = claseSuper.getListaAtributos().get(i);
				if (i == claseSuper.getListaAtributos().size() - 1) {
					txtCodigo.append(atributoSuper.getNombre());
				} else {
					txtCodigo.append(atributoSuper.getNombre() + ", ");
				}
			}
			for (TCDRelacion tcdRelacion : relacionesPadre) {
				txtCodigo.append(", " + tcdRelacion.getTarget().getNombre().toLowerCase());
			}
			txtCodigo.append(");\n");
		}

		// Agrega los atributos propios de la clase y los inicializa en el construtor
		TCDAtributo atributoAUx;
		for (int i = 0; i < tcdClase.getListaAtributos().size(); i++) {
			atributoAUx = tcdClase.getListaAtributos().get(i);
			txtCodigo.append("\t\tthis." + atributoAUx.getNombre() + " = " + atributoAUx.getNombre() + ";\n");
		}

		// Agrega las relaciones que tiene la clase e inicializa con las que vienen por
		// parametro y las listas las inicializa con []
		for (TCDRelacion relacion : tcdClase.getListaRelaciones()) {
			if (!(relacion instanceof TCDHerencia)) {
				multiplicidad = "";
				if (relacion instanceof TCDAgregacion) {
					if (((TCDAgregacion) relacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
						continue;
					} else {
						txtCodigo.append("\t\tthis.");
						txtCodigo.append(((TCDAgregacion) relacion).getNombreDestino() + " = ");
						multiplicidad = ((TCDAgregacion) relacion).getMultiplicidadDestino().getName();
						if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
							txtCodigo.append(((TCDAgregacion) relacion).getNombreDestino() + ";\n");
						}
					}
				} else if (relacion instanceof TCDAsociacion) {
					if (((TCDAsociacion) relacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
						continue;
					} else {
						txtCodigo.append("\t\tthis.");
						txtCodigo.append(((TCDAsociacion) relacion).getNombreDestino() + " = ");
						multiplicidad = ((TCDAsociacion) relacion).getMultiplicidadDestino().getName();
						if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
							txtCodigo.append(((TCDAsociacion) relacion).getNombreDestino() + ";\n");
						}
					}
				} else if (relacion instanceof TCDComposicion) {
					if (((TCDComposicion) relacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
						continue;
					} else {
						txtCodigo.append("\t\tthis.");
						txtCodigo.append(((TCDComposicion) relacion).getNombreDestino() + " = ");
						multiplicidad = ((TCDComposicion) relacion).getMultiplicidadDestino().getName();
						if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
							txtCodigo.append(((TCDComposicion) relacion).getNombreDestino() + ";\n");
						}
					}
				} else if (relacion instanceof TCDDependencia) {
					if (((TCDDependencia) relacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
						continue;
					} else {
						txtCodigo.append("\t\tthis.");
						txtCodigo.append(((TCDDependencia) relacion).getNombreDestino() + " = ");
						multiplicidad = ((TCDDependencia) relacion).getMultiplicidadDestino().getName();
						if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
							txtCodigo.append(((TCDDependencia) relacion).getNombreDestino() + ";\n");
						}
					}
				}
				if (multiplicidad.equalsIgnoreCase("_0_n") || multiplicidad.equalsIgnoreCase("_1_n")) {
					txtCodigo.append("[];\n");
				}
			}
		}
		txtCodigo.append("\t}\n\n");
	}

	private ArrayList<TCDRelacion> obtenerRelaciones(TCDClase tcdClase) {
		ArrayList<TCDRelacion> relaciones = new ArrayList<>();

		for (TCDRelacion relacion3 : tcdClase.getListaRelaciones()) {
			if (!(relacion3 instanceof TCDHerencia)) {
				relaciones.add(relacion3);
			}
		}
		return relaciones;
	}

	private ArrayList<TCDRelacion> obtenerRelacionesSimples(TCDClase tcdClase) {
		ArrayList<TCDRelacion> relaciones = new ArrayList<>();

		for (TCDRelacion relacion3 : tcdClase.getListaRelaciones()) {
			if (!(relacion3 instanceof TCDHerencia)) {
				if (relacion3 instanceof TCDAgregacion) {
					if (((TCDAgregacion) relacion3).getNavegavilidad().getName().equalsIgnoreCase("none")) {
						continue;
					}
					if (((TCDAgregacion) relacion3).getMultiplicidadDestino() == Multiplicidad._1
							|| ((TCDAgregacion) relacion3).getMultiplicidadDestino() == Multiplicidad._01) {
						relaciones.add(relacion3);
					}
				} else if (relacion3 instanceof TCDAsociacion) {
					if (((TCDAsociacion) relacion3).getNavegavilidad().getName().equalsIgnoreCase("none")) {
						continue;
					}
					if (((TCDAsociacion) relacion3).getMultiplicidadDestino() == Multiplicidad._1
							|| ((TCDAsociacion) relacion3).getMultiplicidadDestino() == Multiplicidad._01) {
						relaciones.add(relacion3);
					}
				} else if (relacion3 instanceof TCDComposicion) {
					if (((TCDComposicion) relacion3).getNavegavilidad().getName().equalsIgnoreCase("none")) {
						continue;
					}
					if (((TCDComposicion) relacion3).getMultiplicidadDestino() == Multiplicidad._1
							|| ((TCDComposicion) relacion3).getMultiplicidadDestino() == Multiplicidad._01) {
						relaciones.add(relacion3);
					}
				} else if (relacion3 instanceof TCDDependencia) {
					if (((TCDDependencia) relacion3).getNavegavilidad().getName().equalsIgnoreCase("none")) {
						continue;
					}
					if (((TCDDependencia) relacion3).getMultiplicidadDestino() == Multiplicidad._1
							|| ((TCDDependencia) relacion3).getMultiplicidadDestino() == Multiplicidad._01) {
						relaciones.add(relacion3);
					}
				}
			}
		}
		return relaciones;
	}

	private TCDClase clasePadre(TCDClase tcdClase) {
		for (TCDRelacion relacion2 : tcdClase.getListaRelaciones()) {
			if (relacion2 instanceof TCDHerencia) {
				if (!relacion2.getSource().getNombre().equals(tcdClase.getNombre())) {
					return relacion2.getSource();
				}
			}
		}
		return null;
	}

	private void agregarGetAndSet(TCDClase tcdClase, StringBuilder txtCodigo) {
		/*
		 * public get Name() : string { return this.name; }
		 * 
		 * public set Name(name : string) { this.name = name; }
		 */
		String nombre;
		for (TCDAtributo atributo : tcdClase.getListaAtributos()) {
			nombre = atributo.getNombre().toUpperCase().charAt(0)
					+ atributo.getNombre().substring(1, atributo.getNombre().length());
			txtCodigo.append("\tpublic get " + nombre + "() : " + atributo.getTipoDato() + " { return this."
					+ atributo.getNombre() + "; }\n\n");
			txtCodigo.append("\tpublic set " + nombre + "(" + atributo.getNombre() + " : " + atributo.getTipoDato()
					+ ") { this." + atributo.getNombre() + " = " + atributo.getNombre() + "; }\n\n");
		}

		ArrayList<TCDRelacion> relaAux = obtenerRelaciones(tcdClase);

		String multiplicidad;
		for (TCDRelacion tcdRelacion : relaAux) {
			if (tcdRelacion instanceof TCDAgregacion) {
				if (((TCDAgregacion) tcdRelacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				}
				multiplicidad = ((TCDAgregacion) tcdRelacion).getMultiplicidadDestino().getName();
				nombre = ((TCDAgregacion) tcdRelacion).getNombreDestino().toUpperCase().charAt(0)
						+ ((TCDAgregacion) tcdRelacion).getNombreDestino().substring(1,
								((TCDAgregacion) tcdRelacion).getNombreDestino().length());
				if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
					txtCodigo.append("\tpublic get " + nombre + "() : " + tcdRelacion.getTarget().getNombre()
							+ " { return this." + ((TCDAgregacion) tcdRelacion).getNombreDestino() + "; }\n\n");
					txtCodigo.append("\tpublic set " + nombre + "(" + ((TCDAgregacion) tcdRelacion).getNombreDestino()
							+ " : " + tcdRelacion.getTarget().getNombre() + ") { this."
							+ ((TCDAgregacion) tcdRelacion).getNombreDestino() + " = "
							+ ((TCDAgregacion) tcdRelacion).getNombreDestino() + "; }\n\n");
				} else {
					txtCodigo.append("\tpublic get " + nombre + "() : " + tcdRelacion.getTarget().getNombre()
							+ "[] { return this." + ((TCDAgregacion) tcdRelacion).getNombreDestino() + "; }\n\n");
					txtCodigo.append("\tpublic set " + nombre + "(" + ((TCDAgregacion) tcdRelacion).getNombreDestino()
							+ " : " + tcdRelacion.getTarget().getNombre() + "[]) { this."
							+ ((TCDAgregacion) tcdRelacion).getNombreDestino() + " = "
							+ ((TCDAgregacion) tcdRelacion).getNombreDestino() + "; }\n\n");
				}
			} else if (tcdRelacion instanceof TCDAsociacion) {
				if (((TCDAsociacion) tcdRelacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				}
				multiplicidad = ((TCDAsociacion) tcdRelacion).getMultiplicidadDestino().getName();
				nombre = ((TCDAsociacion) tcdRelacion).getNombreDestino().toUpperCase().charAt(0)
						+ ((TCDAsociacion) tcdRelacion).getNombreDestino().substring(1,
								((TCDAsociacion) tcdRelacion).getNombreDestino().length());
				if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
					txtCodigo.append("\tpublic get " + nombre + "() : " + tcdRelacion.getTarget().getNombre()
							+ " { return this." + ((TCDAsociacion) tcdRelacion).getNombreDestino() + "; }\n\n");
					txtCodigo.append("\tpublic set " + nombre + "(" + ((TCDAsociacion) tcdRelacion).getNombreDestino()
							+ " : " + tcdRelacion.getTarget().getNombre() + ") { this."
							+ ((TCDAsociacion) tcdRelacion).getNombreDestino() + " = "
							+ ((TCDAsociacion) tcdRelacion).getNombreDestino() + "; }\n\n");
				} else {
					txtCodigo.append("\tpublic get " + nombre + "() : " + tcdRelacion.getTarget().getNombre()
							+ "[] { return this." + ((TCDAsociacion) tcdRelacion).getNombreDestino() + "; }\n\n");
					txtCodigo.append("\tpublic set " + nombre + "(" + ((TCDAsociacion) tcdRelacion).getNombreDestino()
							+ " : " + tcdRelacion.getTarget().getNombre() + "[]) { this."
							+ ((TCDAsociacion) tcdRelacion).getNombreDestino() + " = "
							+ ((TCDAsociacion) tcdRelacion).getNombreDestino() + "; }\n\n");
				}
			} else if (tcdRelacion instanceof TCDComposicion) {
				if (((TCDComposicion) tcdRelacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				}
				multiplicidad = ((TCDComposicion) tcdRelacion).getMultiplicidadDestino().getName();
				nombre = ((TCDComposicion) tcdRelacion).getNombreDestino().toUpperCase().charAt(0)
						+ ((TCDComposicion) tcdRelacion).getNombreDestino().substring(1,
								((TCDComposicion) tcdRelacion).getNombreDestino().length());
				if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {
					txtCodigo.append("\tpublic get " + nombre + "() : " + tcdRelacion.getTarget().getNombre()
							+ " { return this." + ((TCDComposicion) tcdRelacion).getNombreDestino() + "; }\n\n");
					txtCodigo.append("\tpublic set " + nombre + "(" + ((TCDComposicion) tcdRelacion).getNombreDestino()
							+ " : " + tcdRelacion.getTarget().getNombre() + ") { this."
							+ ((TCDComposicion) tcdRelacion).getNombreDestino() + " = "
							+ ((TCDComposicion) tcdRelacion).getNombreDestino() + "; }\n\n");
				} else {
					txtCodigo.append("\tpublic get " + nombre + "() : " + tcdRelacion.getTarget().getNombre()
							+ "[] { return this." + ((TCDComposicion) tcdRelacion).getNombreDestino() + "; }\n\n");
					txtCodigo.append("\tpublic set " + nombre + "(" + ((TCDComposicion) tcdRelacion).getNombreDestino()
							+ " : " + tcdRelacion.getTarget().getNombre() + "[]) { this."
							+ ((TCDComposicion) tcdRelacion).getNombreDestino() + " = "
							+ ((TCDComposicion) tcdRelacion).getNombreDestino() + "; }\n\n");
				}
			} else if (tcdRelacion instanceof TCDDependencia) {
				if (((TCDDependencia) tcdRelacion).getNavegavilidad().getName().equalsIgnoreCase("none")) {
					continue;
				}
				multiplicidad = ((TCDDependencia) tcdRelacion).getMultiplicidadDestino().getName();
				nombre = ((TCDDependencia) tcdRelacion).getNombreDestino().toUpperCase().charAt(0)
						+ ((TCDDependencia) tcdRelacion).getNombreDestino().substring(1,
								((TCDDependencia) tcdRelacion).getNombreDestino().length());
				if (multiplicidad.equalsIgnoreCase("_1") || multiplicidad.equalsIgnoreCase("_0_1")) {

					txtCodigo.append("\tpublic get " + nombre + "() : " + tcdRelacion.getTarget().getNombre()
							+ " { return this." + ((TCDDependencia) tcdRelacion).getNombreDestino() + "; }\n\n");
					txtCodigo.append("\tpublic set " + nombre + "(" + ((TCDDependencia) tcdRelacion).getNombreDestino()
							+ " : " + tcdRelacion.getTarget().getNombre() + ") { this."
							+ ((TCDDependencia) tcdRelacion).getNombreDestino() + " = "
							+ ((TCDDependencia) tcdRelacion).getNombreDestino() + "; }\n\n");
				} else {
					txtCodigo.append("\tpublic get " + nombre + "() : " + tcdRelacion.getTarget().getNombre()
							+ "[] { return this." + ((TCDDependencia) tcdRelacion).getNombreDestino() + "; }\n\n");
					txtCodigo.append("\tpublic set " + nombre + "(" + ((TCDDependencia) tcdRelacion).getNombreDestino()
							+ " : " + tcdRelacion.getTarget().getNombre() + "[]) { this."
							+ ((TCDDependencia) tcdRelacion).getNombreDestino() + " = "
							+ ((TCDDependencia) tcdRelacion).getNombreDestino() + "; }\n\n");
				}
			}
		}
	}

	private void agregarMetodos(TCDClase tcdClase, StringBuilder txtCodigo) {
		for (TCDMetodo metodo : tcdClase.getListaMetodos()) {
			txtCodigo.append(
					"\t" + metodo.getModificadorAcceso().getName().toLowerCase() + " " + metodo.getNombre() + "(");
			for (int i = 0; i < metodo.getListaParametros().size(); i++) {
				if (i == metodo.getListaParametros().size() - 1) {
					txtCodigo.append(metodo.getListaParametros().get(i) + ": any");
				} else {
					txtCodigo.append(metodo.getListaParametros().get(i) + ": any, ");
				}
			}
			txtCodigo.append("): " + metodo.getTipoRetorno().getName() + " {\n");
			if (metodo.getSemantica() == null) {
				txtCodigo.append("\t\treturn");
				if (metodo.getTipoRetorno() == TipoRetorno.UNDEFINED || metodo.getTipoRetorno() == TipoRetorno.VOID) {
					txtCodigo.append(";");
				} else if (metodo.getTipoRetorno() == TipoRetorno.STRING) {
					txtCodigo.append(" '';");
				} else if (metodo.getTipoRetorno() == TipoRetorno.NUMBER) {
					txtCodigo.append(" 0;");
				} else if (metodo.getTipoRetorno() == TipoRetorno.BOOLEAN) {
					txtCodigo.append(" false;");
				}
				txtCodigo.append("\n\t}\n\n");
			} else {
				txtCodigo.append("\t\t" + metodo.getSemantica());
			}
		}

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
