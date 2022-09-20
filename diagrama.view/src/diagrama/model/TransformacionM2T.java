package diagrama.model;

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
		// TODO Auto-generated method stub
		String mensaje;
		StringBuilder textoCodigo = new StringBuilder();

		for (TCDClase tcdClase : modelFactoryAbstracta.getListaTodasClases()) {
			// crear las clases
			generarClase(tcdClase, textoCodigo);
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
			textoCodigo.append("private " + relacion.getTarget().getNombre() + " " + relacion.getRol());
		}
		
	}

	private void agregarEncabezado(TCDClase tcdClase, StringBuilder textoCodigo) {
		// TODO Auto-generated method stub
		textoCodigo.append(tcdClase.getModificadorAcceso() + " class " + tcdClase.getNombre() + " {\n\n");
	}
}
