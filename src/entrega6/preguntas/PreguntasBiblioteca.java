package entrega6.preguntas;
import us.lsi.biblioteca.*;
import java.util.*;
import java.util.stream.Collectors;

public class PreguntasBiblioteca {

	private Biblioteca biblioteca;

	public PreguntasBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}
	
	public Map.Entry<Libro,Integer> masVecesPrestadoImperativo (){
		
		Map<Libro,Integer> contador = new HashMap<>();
		for (Prestamo prestamo : biblioteca.prestamos().todos()) {
			Libro libro = biblioteca.libros().libro(prestamo.isbn());
			if (!contador.containsKey(libro)) {
                    contador.put(libro, 1);
                } else {
                    contador.put(libro, contador.get(libro) + 1);
            }
        }
		Map.Entry<Libro,Integer> resultado = null;
		for (Map.Entry<Libro, Integer> entry : contador.entrySet()) {
			if (resultado == null || entry.getValue() > resultado.getValue()) {
				resultado = entry;
			}
		}
		
		return resultado;
	}
	
	public Map.Entry<Libro,Integer> masVecesPrestadoFuncional (){
		
		return biblioteca.prestamos().todos().stream()
				.collect(Collectors.groupingBy(
                        p -> biblioteca.libros().libro(p.isbn()),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)))
				.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.orElse(null);
	}
	
	public Map <String, Set<String>> librosPorAutorImperativo(Libros libros, List<String> nombres){
		
		Map<String, Set<String>> resultado = new HashMap<>();
		for (Libro libro : biblioteca.libros().todos()) {
			if (nombres == null || nombres.contains(libro.autor())) {
				if (!resultado.containsKey(libro.autor())) {
					resultado.put(libro.autor(), new HashSet<>());
				}
				resultado.get(libro.autor()).add(libro.titulo());
			}
				
		}
		return resultado;
			
	}

	public Map <String, Set<String>> librosPorAutorFuncional(Libros libros, List<String> nombres){
		return libros.todos().stream()
				.filter(l -> nombres == null || nombres.contains(l.autor()))
				.collect(Collectors.groupingBy(
						Libro::autor,
						Collectors.mapping(Libro::titulo, Collectors.toSet()
								)));
	}
	
}
