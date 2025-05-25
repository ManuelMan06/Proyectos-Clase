package entrega6.test;
import entrega6.preguntas.PreguntasCentro;
import us.lsi.centro.*;
import java.util.List;
import java.util.Map;

public class TestCentro {
	public static void main(String[] args) {
		
		Centro centro = Centro.of("../java_v1");
		PreguntasCentro preguntas = new PreguntasCentro(centro);
		
		double media = preguntas.promedioEdadProfesoresImperativo("26573082D");
		System.out.println("La media de edad de los profesores del alumno con DNI 26573082D es: " + media);
		double media2 = preguntas.promedioEdadProfesoresFuncional("26573082D");
		System.out.println("La media de edad de los profesores del alumno con DNI 26573082D es: " + media2);
		
		Grupo grupoMayor = preguntas.grupoMayorDiversidadEdadImperativo();
		System.out.println("El grupo con mayor diversidad de edad es: " + grupoMayor);
		Grupo grupoMayor2 = preguntas.grupoMayorDiversidadEdadFuncional();
		System.out.println("El grupo con mayor diversidad de edad es: " + grupoMayor2);
		
		Alumno alumno = preguntas.alumnoMasMatriculasImperativo();
		System.out.println("El alumno con más matrículas es: " + alumno);
		Alumno alumno2 = preguntas.alumnoMasMatriculasFuncional();
		
		System.out.println("El alumno con más matrículas es: " + alumno2);
		Map<String,List<String>> alumnosPorEdad = preguntas.rangosEdadPorAlumnoImperativo("20-23,24-26");
		System.out.println("Alumnos por rango de edad: " + alumnosPorEdad);
		Map<String,List<String>> alumnosPorEdad2 = preguntas.rangosEdadPorAlumnoFuncional("20-23,24-26");
		System.out.println("Alumnos por rango de edad: " + alumnosPorEdad2);
		
		String nombreProfesor = preguntas.nombreProfesorMasGruposImperativo(20,25);
		System.out.println("El profesor con más grupos es: " + nombreProfesor);
		String nombreProfesor2 = preguntas.nombreProfesorMasGruposFuncional(20,25);
		System.out.println("El profesor con más grupos es: " + nombreProfesor2);
		
		List<String> alumnosMayorNota = preguntas.nombresAlumnosMayorNotaImperativo(10,2000);
		System.out.println("Los alumnos con mayor nota son: " + alumnosMayorNota);
		List<String> alumnosMayorNota2 = preguntas.nombresAlumnosMayorNotaFuncional(10,2000);
		System.out.println("Los alumnos con mayor nota son: " + alumnosMayorNota2);
		
	}
}
