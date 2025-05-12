package entrega6.preguntas;
import java.util.*;
import java.util.stream.Collectors;
import java.time.*;
import us.lsi.bancos.*;
import us.lsi.ejemplos_b1_tipos.Persona;

public class PreguntasBancos {
	
	private Banco banco;

    public PreguntasBancos(Banco banco) {
        this.banco = banco;
    }

	public Map<Persona,Double> valorTotalPrestamosImperativo (Integer e, Double a, Double b, LocalDate f){
		if (e<18) throw new IllegalArgumentException("Edad menor de 18 años.");
		if (a<0 || b<0) throw new IllegalArgumentException("No está permitido valores negativos.");
		if (a>b) throw new IllegalArgumentException("El valor inicial no puede ser mayor que el final.");
		
		Map<Persona,Double> resultado = new HashMap<>();
		for (Prestamo prestamo : banco.prestamos().todos()) {
			if (prestamo.cantidad()>=a && prestamo.cantidad()<=b && prestamo.fechaComienzo().isAfter(f)) {
				String dni = prestamo.dniCliente();
				Optional<Persona> clienteOpt = banco.personas().personaDni(dni);
				if (clienteOpt.isPresent()) {
	                Persona cliente = clienteOpt.get();
					if (cliente.edad()<e) {
						if (!resultado.containsKey(cliente)) {
							resultado.put(cliente, prestamo.cantidad());
						}
						else {
							resultado.put(cliente, resultado.get(cliente)+prestamo.cantidad());
						}
					}
				}
			}
		}
		return resultado;
	}
	
	public Map<Persona,Double> valorTotalPrestamosFuncional (Integer e, Double a, Double b, LocalDate f){
		
		assert e != null && e >= 18 : "Edad menor de 18 años.";
	    assert a != null && b != null : "Los valores no pueden ser null.";
	    assert a >= 0 && b >= 0 : "No se permiten valores negativos.";
	    assert a <= b : "El valor inicial no puede ser mayor que el final.";

	    return banco.prestamos().todos().stream()
	        .filter(p -> p.cantidad() >= a && p.cantidad() <= b && p.fechaComienzo().isAfter(f))
	        .map(p -> Map.entry(banco.personas().personaDni(p.dniCliente()), p))
	        .filter(e1 -> e1.getKey().isPresent()) // filtrar Optional vacíos
	        .map(e1 -> Map.entry(e1.getKey().get(), e1.getValue()))
	        .filter(e2 -> e2.getKey().edad() < e) // filtrar por edad
	        .collect(Collectors.toMap(
	            Map.Entry::getKey,
	            e3 -> e3.getValue().cantidad(),
	            Double::sum // acumulador para claves duplicadas
	        ));
	}
	
}
