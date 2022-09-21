package diagrama.model;

import java.io.File;
import java.io.FileWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import abstracta.ModelFactory;
import abstracta.TCDAtributo;
import abstracta.TCDClase;
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
			System.out.println("clase: " + tcdClase.getNombre());
			textoCodigo = new StringBuilder();
			generarClase(tcdClase, textoCodigo);
			guardarArchivo(textoCodigo.toString(), pathRaiz + "/" + tcdClase.getRuta(), tcdClase.getNombre());
		}
		return "Se ha generado el código de su proyecto";
	}

	private void generarClase(TCDClase tcdClase, StringBuilder textoCodigo) {
		// Agregar la declaración del paquete
		textoCodigo.append("package " + tcdClase.getRuta() + "; \n\n");

		agregarEncabezado(tcdClase, textoCodigo);
		agregarAtributos(tcdClase, textoCodigo);
		agregarConstructor(tcdClase, textoCodigo);
		agregarGetAndSet(tcdClase, textoCodigo);
		agregarMetodos(tcdClase, textoCodigo);
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

	private void agregarMetodos(TCDClase tcdClase, StringBuilder textoCodigo) {
		// TODO Auto-generated method stub

	}

	private void agregarGetAndSet(TCDClase tcdClase, StringBuilder textoCodigo) {
		// TODO Auto-generated method stub

	}

	private void agregarConstructor(TCDClase tcdClase, StringBuilder textoCodigo) {
		// TODO Auto-generated method stub

		textoCodigo.append("public " + tcdClase.getNombre() + "(");

		for (int i = 0; i < tcdClase.getListaAtributos().size(); i++) {

		}

	}

	private void agregarAtributos(TCDClase tcdClase, StringBuilder textoCodigo) {
		// TODO Auto-generated method stub
		for (TCDAtributo atributo : tcdClase.getListaAtributos()) {
			textoCodigo.append(atributo.getVisibilidad() + " " + atributo.getNombre() + ";\n");
		}
		for (TCDRelacion relacion : tcdClase.getListaRelaciones()) {
			textoCodigo.append("private " + relacion.getTarget().getNombre() + " " + relacion.getSource().getNombre());
		}

	}

	private void agregarEncabezado(TCDClase tcdClase, StringBuilder textoCodigo) {
		// TODO Auto-generated method stub
		textoCodigo.append("class " + tcdClase.getNombre() + " {\n\n");
	}
}
