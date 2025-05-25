package entrega6.preguntas;
import us.lsi.aeropuerto.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.Duration;

public class PreguntasAeropuertos {

	private EspacioAereo espacioAereo;

	public PreguntasAeropuertos(EspacioAereo espacioAereo) {
		this.espacioAereo = espacioAereo;
	}
	
	public String ciudadAeropuertoMayorFacturacionImperativo(LocalDateTime a, LocalDateTime b) {
		if (a.isAfter(b)) {
			throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
		}
		if (Duration.between(a, b).toDays() < 1) {
			throw new IllegalArgumentException("El intervalo de tiempo debe ser al menos de un día");
		}
		Map<String,Double> contador = new HashMap<>();
		for (Vuelo vuelo : espacioAereo.vuelos().todas()) {
			LocalDateTime fecha = vuelo.fechaSalida().atTime(vuelo.horaSalida());
			if (fecha.isAfter(a) && fecha.isBefore(b)) {
				VueloProgramado vProgramado = vuelo.vueloProgramado();
				String ciudad = vProgramado.ciudadOrigen();
				Integer billetes = vuelo.numPasajeros();
				Double precio = vProgramado.precio();
				if (!contador.containsKey(ciudad)) {
					contador.put(ciudad, billetes*precio);
				}
				else contador.put(ciudad, contador.get(ciudad)+billetes*precio);
			}
		}
		if (contador.isEmpty()) {
			return "No hay vuelos en el intervalo de tiempo especificado";
		}
		String ciudadmax = null;
		for (String ciudad : contador.keySet()) {
			if (ciudadmax == null || contador.get(ciudad) > contador.get(ciudadmax)) {
				ciudadmax = ciudad;
			}
		}
		return ciudadmax;
	}
	
	public String ciudadAeropuertoMayorFacturacionFuncional(LocalDateTime a, LocalDateTime b) {
		
		assert a.isBefore(b) : "La fecha de inicio no puede ser posterior a la fecha de fin";
		assert Duration.between(a, b).toDays() >= 1 : "El intervalo de tiempo debe ser al menos de un día";
		
		return espacioAereo.vuelos().todas().stream()
				.filter(vuelo ->  vuelo.fechaSalida().atTime(vuelo.horaSalida()).isAfter(a)
						&&  vuelo.fechaSalida().atTime(vuelo.horaSalida()).isBefore(b))
				.collect(Collectors.groupingBy(
							vuelo -> vuelo.vueloProgramado().ciudadOrigen(),
							Collectors.summingDouble(vuelo -> vuelo.numPasajeros()*vuelo.vueloProgramado().precio())
						))
				.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey)
				.orElseThrow(()-> new NoSuchElementException("No hay vuelos en el rango de fechas"));
	}
	
}
