package entrega6.preguntas;
import us.lsi.centro.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PreguntasCentro {
	
	private Centro centro;

    public PreguntasCentro(Centro centro) {
        this.centro = centro;
        
        
    }
	
	public Double promedioEdadProfesoresImperativo (String dniAlumno) {
		
		// Obtener el conjunto de asignaciones que corresponden al alumno con el dni especificado
	    Set<Matricula> matriculas = centro.matriculas().todas();
	    
	    int totalEdad = 0;
	    int contador = 0;

	    // Recorrer todas las asignaciones y verificar si el dni del alumno coincide
	    for (Matricula matricula : matriculas) {
	        if (matricula.dni().equals(dniAlumno)) {
				for (Asignacion asignacion : centro.asignaciones().todas()) {
					if (asignacion.grupo().equals(matricula.grupo())) {
						for (Profesor profesor : centro.profesores().todos()) {
							if (profesor.dni().equals(asignacion.dni())) {
								totalEdad += profesor.edad();
								contador++;
							}
						}
					}
				}	
	        }
	    }

	    // Calcular y devolver el promedio
	    if (contador == 0) {
	        return 0.0; // Si no hay profesores, devolver 0
	    } else {
	        return (double) totalEdad / contador;
	    }
	}
	
	public Double promedioEdadProfesoresFuncional (String dniAlumno) {
	
		return centro.matriculas().todas().stream()
		        .filter(m -> m.dni().equals(dniAlumno)) // Filtrar solo las matrículas del alumno
		        .flatMap(m -> centro.asignaciones().todas().stream()
		            .filter(a -> a.grupo().equals(m.grupo())) // Buscar asignaciones del mismo grupo
		            .map(a -> centro.profesores().profesor(a.dni())) // Obtener el profesor por su DNI
		        )
		        .filter(p -> p != null) // Evitar nulls por seguridad
		        .mapToInt(Profesor::edad) // Obtener la edad de cada profesor
		        .average() // Calcular promedio
		        .orElse(0.0); // Devolver 0.0 si no hay profesores
	}

	public Grupo grupoMayorDiversidadEdadImperativo() {
	    
		Map<Grupo, List<Integer>> edadesGrupo = new HashMap<>();

	    for (Grupo grupo : centro.grupos().todos()) {
	        for (Matricula matricula : centro.matriculas().todas()) {
	            if (matricula.grupo().equals(grupo)) {
	                for (Alumno alumno : centro.alumnos().todos()) {
	                    if (alumno.dni().equals(matricula.dni())) {
	                    	if (!edadesGrupo.containsKey(grupo)) {
								edadesGrupo.put(grupo, new ArrayList<>());
							}
							edadesGrupo.get(grupo).add(alumno.edad());
	                    }
	                }
	            }
	        }
	    }

	    Map<Grupo, Integer> diferenciaEdades = new HashMap<>();
	    for (Grupo grupo : edadesGrupo.keySet()) {
	        List<Integer> edades = edadesGrupo.get(grupo);
	        Integer min = edades.get(0);
	        Integer max = edades.get(0);
	        for (Integer edad : edades) {
	            if (edad < min) min = edad;
	            if (edad > max) max = edad;
	        }
	        diferenciaEdades.put(grupo, max - min);
	    }

	    Grupo grupoMayor = null;
		for (Grupo grupo : diferenciaEdades.keySet()) {
			grupoMayor = grupo;
			break;
			
		}
		for (Grupo grupo : diferenciaEdades.keySet()) {
			if (diferenciaEdades.get(grupo) > diferenciaEdades.get(grupoMayor)) {
				grupoMayor = grupo;
			}
		}
		return grupoMayor;
	}



	
	public Grupo grupoMayorDiversidadEdadFuncional() {
		return centro.matriculas().todas().stream()
				//Agrupar alumnos por grupo (Map grupo, edades)
				.collect(Collectors.groupingBy(
                        Matricula::grupo,
                        Collectors.mapping(
                        		m->centro.alumnos().alumno(m.dni()).edad(), 
                        		Collectors.toList()
                        )
                 ))
				//Buscar grupo con mayor diversidad de edad
				.entrySet().stream()
				.max(Comparator.comparing(
						e -> e.getValue().stream().mapToInt(Integer::intValue).max().orElse(0) -
						e.getValue().stream().mapToInt(Integer::intValue).min().orElse(0)
                 ))
				//Devuelve el grupo con mayor diversidad de edad
				.map(Map.Entry::getKey)
				.orElse(null);
	}

	public Alumno alumnoMasMatriculasImperativo() {
		Map<Alumno,Integer> matriculasPorAlumno = new HashMap<>();
		for (Alumno alumno : centro.alumnos().todos()) {
			int contador = 0;
			for (Matricula matricula : centro.matriculas().todas()) {
				if (matricula.dni().equals(alumno.dni())) {
					contador++;
				}
			}
			matriculasPorAlumno.put(alumno, contador);
		}
		Alumno alumnoMasMatriculas = null;
		for (Alumno alumno : matriculasPorAlumno.keySet()) {
			alumnoMasMatriculas = alumno;
			break;
		}
		for (Alumno alumno : matriculasPorAlumno.keySet()) {
			if (matriculasPorAlumno.get(alumno) > matriculasPorAlumno.get(alumnoMasMatriculas)) {
				alumnoMasMatriculas = alumno;
			}
		}
		return alumnoMasMatriculas;
	}

	
	public Alumno alumnoMasMatriculasFuncional() {
		return centro.matriculas().todas().stream()
				.collect(Collectors.groupingBy(
                        Matricula::dni,
                        Collectors.counting() // Contar matrículas por alumno
                ))
				.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.map(entry -> centro.alumnos().alumno(entry.getKey())) // Obtener el alumno con más matrículas)
				.orElse(null);
	}
	
	public Map<String, List<String>> rangosEdadPorAlumnoImperativo (String rangoEdad) {
		
		if (rangoEdad == null || rangoEdad.isEmpty()) {
			throw new IllegalArgumentException("El rango de edad no puede ser nulo o vacío");
		}
		
		Map<String, List<String>> resultado = new HashMap<>();
		List<Integer> solapamiento = new ArrayList<>();
		String[] rangos = rangoEdad.split(",");
		for (String r : rangos) {
			if (!resultado.containsKey(r)) {
				resultado.put(r, new ArrayList<>());
			}
			String[] edades = r.split("-");
			if (edades.length != 2) {
				throw new IllegalArgumentException("El rango de edad debe tener el formato 'min-max'");
			}
			try {
				int edadMin = Integer.parseInt(edades[0].strip());
				int edadMax = Integer.parseInt(edades[1].strip());
				solapamiento.add(edadMin);
				solapamiento.add(edadMax);
				for (int i = 0; i < solapamiento.size()-1; i++) {
				    int actual = solapamiento.get(i);
				    int siguiente = solapamiento.get(i + 1);
				    
				    if (actual >= siguiente) {
				        throw new IllegalArgumentException("El rango de edad debe estar ordenado de menor a mayor, sin solaparse");
				    }
				}
				for (Alumno alumno : centro.alumnos().todos()) {
					if (alumno.edad() >= edadMin && alumno.edad() <= edadMax) {
						resultado.get(r).add(alumno.nombre());
						}
				}
			}
			catch (NumberFormatException e) {
				throw new IllegalArgumentException("El rango de edad debe contener números enteros");
			}
			
		}

		return resultado;
	}
	
	public Map<String, List<String>> rangosEdadPorAlumnoFuncional(String rangoEdad) {
	    assert rangoEdad != null : "El rango de edad no puede ser nulo";
	    assert !rangoEdad.isBlank() : "El rango de edad no puede estar vacío";

	    // Paso 1: Parsear y validar rangos
	    List<int[]> rangos = Arrays.stream(rangoEdad.split(","))
	        .map(String::strip)
	        .map(rango -> {
	            String[] partes = rango.split("-");
	            assert partes.length == 2 : "Formato de rango incorrecto";
	            int inicio = Integer.parseInt(partes[0].strip());
	            int fin = Integer.parseInt(partes[1].strip());  // <- CORREGIDO AQUÍ
	            assert inicio < fin : "El rango debe ir de menor a mayor";
	            return new int[]{inicio, fin};
	        })
	        .sorted(Comparator.comparingInt(r -> r[0]))
	        .toList();

	    assert IntStream.range(0, rangos.size() - 1)
        	.allMatch(i -> rangos.get(i)[1] < rangos.get(i + 1)[0])
        	: "Los rangos no deben solaparse";

	    // Paso 2: Agrupar alumnos por rango
	    return centro.alumnos().todos().stream()
	        .flatMap(alumno -> rangos.stream()
	            .filter(r -> alumno.edad() >= r[0] && alumno.edad() <= r[1])
	            .findFirst()
	            .map(r -> Map.entry(r[0] + " - " + r[1], alumno.nombre()))
	            .stream()
	        )
	        .collect(Collectors.groupingBy(
	            Map.Entry::getKey,               // clave: el rango como texto
	            Collectors.mapping(Map.Entry::getValue, Collectors.toList()) // valor: nombres
	        ));
	}
	
	public String nombreProfesorMasGruposImperativo (Integer min , Integer max){
		
		if (max<min) throw new IllegalArgumentException("El rango de edad es incorrecto");
		String nombreProfesor = null;
		Integer numAsignaciones = 0;
		for (Profesor profesor : centro.profesores().todos()) {
			if (profesor.edad()>= min && profesor.edad()<=max) {
				Integer cont = 0;
				for (Asignacion asignacion : centro.asignaciones().todas()) {
					if (asignacion.dni().equals(profesor.dni())) {
						cont++;
					}
				}
				if (cont>numAsignaciones) {
					numAsignaciones = cont;
					nombreProfesor = profesor.nombre();
				}
			}
		}
		return nombreProfesor;
	}
	
	public String nombreProfesorMasGruposFuncional (Integer min, Integer max) {
		assert min<=max : "El rango de edad es incorrecto";
		return centro.asignaciones().todas().stream()
				.collect(Collectors.groupingBy(
                        Asignacion::dni,
                        Collectors.counting()
						))
				.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.map(entry -> centro.profesores().profesor(entry.getKey()).nombre())
				.orElse(null);
	}
	
	public List<String> nombresAlumnosMayorNotaImperativo (Integer n, Integer a){
		if (a == null || n == null) throw new IllegalArgumentException("Datos invalidos");
		if (n<1 || n>10) throw new IllegalArgumentException("nº de alumnos invalido");
		
		List<Alumno> filtro = new ArrayList<>();
		for (Alumno alumno : centro.alumnos().todos()) {
			if (alumno.fechaDeNacimiento().getYear()>a) {
				filtro.add(alumno);
			}
		}
		for (int i = 0; i < filtro.size() - 1; i++) {
	        for (int j = 0; j < filtro.size() - i - 1; j++) {
	            Alumno a1 = filtro.get(j);
	            Alumno a2 = filtro.get(j + 1);
	            if (a1.nota() < a2.nota()) {
	                filtro.set(j, a2);
	                filtro.set(j + 1, a1);
	            }
	        }
	    }
		List<String> resultado = new ArrayList<>();
		for (int i = 0; i < n && i < filtro.size(); i++) {
			resultado.add(filtro.get(i).nombre());
		}
		return resultado;
	}
	
	public List<String> nombresAlumnosMayorNotaFuncional(Integer n, Integer a) {
		assert n > 0 && n <= 10 : "Número de alumnos inválido";
		assert a != null : "El año no puede ser nulo";
		
		return centro.alumnos().todos().stream()
				.filter(alumno -> alumno.fechaDeNacimiento().getYear() > a)
				.sorted(Comparator.comparingDouble(Alumno::nota).reversed())
				.limit(n)
				.map(Alumno::nombre)
				.collect(Collectors.toList());
	}
	
	
	
}
