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
		Map<Aeropuerto,Double> contador = new HashMap<>();
		for (OcupacionVuelo ocupacion : espacioAereo.ocupacionesVuelos().todas()) {
			if (ocupacion.fecha().isAfter(a) && ocupacion.fecha().isBefore(b)) {
				Vuelo vuelo = espacioAereo.vuelos().vuelo(ocupacion.codigoVuelo()).orElse(null);
				Aeropuerto aeropuerto = espacioAereo.aeropuertos().aeropuerto(vuelo.codigoOrigen()).orElse(null);
				Integer billetes = vuelo.numPlazas();
				Double precio = vuelo.precio();
				if (!contador.containsKey(aeropuerto)) {
					contador.put(aeropuerto, billetes*precio);
				}
				else contador.put(aeropuerto, contador.get(aeropuerto)+billetes*precio);
			}
		}
		if (contador.isEmpty()) {
			return "No hay vuelos en el intervalo de tiempo especificado";
		}
		Aeropuerto aeropuertomax = null;
		for (Aeropuerto aeropuerto : contador.keySet()) {
			if (aeropuertomax == null || contador.get(aeropuerto) > contador.get(aeropuertomax)) {
				aeropuertomax = aeropuerto;
			}
		}
		return aeropuertomax.ciudad();
	}
	
	public String ciudadAeropuertoMayorFacturacionFuncional(LocalDateTime a, LocalDateTime b) {
		
		assert a.isBefore(b) : "La fecha de inicio no puede ser posterior a la fecha de fin";
		assert Duration.between(a, b).toDays() >= 1 : "El intervalo de tiempo debe ser al menos de un día";
		
		return espacioAereo.ocupacionesVuelos().todas().stream()
				.filter(ocupacion -> ocupacion.fecha().isAfter(a) && ocupacion.fecha().isBefore(b))
				.map(ocupacion -> ocupacion.vuelo())
				.map(vuelo -> new AbstractMap.SimpleEntry<>(
				        espacioAereo.aeropuertos().aeropuerto(vuelo.codigoOrigen()),
				        vuelo.numPlazas() * vuelo.precio()))
				    .filter(entry -> entry.getKey().isPresent()) // Filtra Optional vacíos
				    .map(entry -> new AbstractMap.SimpleEntry<>(
				        entry.getKey().get(), entry.getValue())) // Desempaqueta el Optional
				    .collect(Collectors.groupingBy(
				        Map.Entry::getKey,
				        Collectors.summingDouble(Map.Entry::getValue)))
				.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.map(entry -> entry.getKey().ciudad())
				.orElse(null);
		
	}
	
}
