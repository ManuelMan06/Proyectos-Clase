package entrega6.test;
import us.lsi.aeropuerto.*;
import entrega6.preguntas.PreguntasAeropuertos;
import java.time.LocalDateTime;

public class TestAeropuertos {
	public static void main(String[] args) {
		
		EspacioAereo espacioAereo = EspacioAereo.of();
		PreguntasAeropuertos preguntas = new PreguntasAeropuertos(espacioAereo);
		
		String ciudad = preguntas.ciudadAeropuertoMayorFacturacionImperativo(LocalDateTime.of(2015, 10, 1, 0, 0),
				LocalDateTime.of(2023, 10, 31, 23, 59));
		System.out.println("La ciudad del aeropuerto con mayor facturación es: " + ciudad);
		
		String ciudad2 = preguntas.ciudadAeropuertoMayorFacturacionFuncional(
				LocalDateTime.of(2015, 10, 1, 0, 0), LocalDateTime.of(2023, 10, 31, 23, 59));
		System.out.println("La ciudad del aeropuerto con mayor facturación es: " + ciudad2);
	}

}
