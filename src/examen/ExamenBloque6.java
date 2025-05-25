package examen;
import us.lsi.aeropuerto.*;
import us.lsi.bancos.*;
import us.lsi.biblioteca.*;
import us.lsi.centro.*;
import java.util.*;
import java.util.stream.Collectors;
import java.time.*;

public class ExamenBloque6 {

	private EspacioAereo espacioAereo;

	public ExamenBloque6(EspacioAereo espacioAereo) {
		this.espacioAereo = espacioAereo;
	}
	
	private Banco banco;

	public ExamenBloque6(Banco banco) {
		this.banco = banco;
	}
	
	private Biblioteca biblioteca;

	public ExamenBloque6(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}
	
	private Centro centro;

	public ExamenBloque6(Centro centro) {
		this.centro = centro;
	}
	
	public Map<String, Double> vueloProgramadoMasBaratoPorAerolinea(){
		
		Map<String,List<Double>> preciosAerolinea = new HashMap<>();
		for(VueloProgramado vProgramado: VuelosProgramados.of().todos()) {
            String codigo =vProgramado.codigoAerolinea();
			if (!preciosAerolinea.containsKey(codigo)) {
				preciosAerolinea.put(codigo, new ArrayList<>());
			}
			preciosAerolinea.get(codigo).add(vProgramado.precio());
		}
		Map<String, Double> resultado = new HashMap<>();
		for (Map.Entry<String,List<Double>> entry : preciosAerolinea.entrySet()) {
			Double precioMasBarato = null;
			for (Double precio : entry.getValue()) {
				if (precioMasBarato == null || precio < precioMasBarato) {
					precioMasBarato = precio;
				}
			}
			resultado.put(entry.getKey(), precioMasBarato);
		}
		return resultado;
	}
	
	public List<Libro> top3LibrosConMasPaginasNoBestSeller(){
		
		List<Libro> noBestSellers = new ArrayList<>();
		for (Libro libro: Libros.of().todos()) {
			if (!libro.isBestSeller()) {
				noBestSellers.add(libro);
			}
		}
		noBestSellers.sort(Comparator.comparingInt(Libro::numeroDePaginas).reversed());
		List<Libro> resultado = noBestSellers.subList(0, 3);
		return resultado;
	}
	
	public Long numeroMatriculasDeAsignatura(String nombre) {
	    return Asignaturas.of().todas().stream()
	        .map(Asignatura::nombre)
	        .filter(n -> n.equals(nombre))
	        .findAny()
	        .map(n -> Matriculas.of().todas().stream()
	            .filter(m -> Asignaturas.of().asignatura(m.ida()).nombre().equals(nombre))
	            .count())
	        .orElseThrow(() -> new IllegalArgumentException("Nombre de asignatura no encontrado: " + nombre));
	}
	

	public Map<String,Double> saldoMedioClienteCuentasAbiertasAntesdeFecha(LocalDate fecha){
		
		return Cuentas.of().todas().stream()
				.filter(cuenta->cuenta.fechaDeCreacion().isBefore(fecha))
				.collect(Collectors.groupingBy(
						Cuenta::dni,
						Collectors.averagingDouble(Cuenta::saldo)
						));
	}

	
	public Map<String, Integer> numeroVuelosPorDestino(){
		
		Map<String,Integer> resultado = new HashMap<>();
		for (VueloProgramado vProgramado: VuelosProgramados.of().todos()) {
			String destino = Aeropuertos.of().aeropuerto(vProgramado.codigoDestino()).orElse(null).ciudad();
			if (!resultado.containsKey(destino)) {
				resultado.put(destino, 0);
			}
			resultado.put(destino, resultado.get(destino) + 1);
		}
		return resultado;
	}
	
	public List<String> alumnosPorCurso (Integer idg){
		return Matriculas.of().todas().stream()
				.filter(matricula-> matricula.idg().equals(idg))
				.map(matricula -> Alumnos.of().alumno(matricula.dni()).nombre())
				.collect(Collectors.toList());
	}
	
	public List<String> clientesConMasDeXCuentasActivas(Integer x){
		
		return Cuentas.of().todas().stream()
				.filter(cuenta-> cuenta.fechaDeCreacion().isBefore(LocalDate.now()))
				.collect(Collectors.groupingBy(
						Cuenta::dni,
						Collectors.counting()
						))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > x)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}
	
	public Map<String,Double> duracionMediaDeVuelosPorAerolinea(){
		
		return VuelosProgramados.of().todos().stream()
				.collect(Collectors.groupingBy(
						vProgramado-> Aerolineas.of().aerolinea(vProgramado.codigoAerolinea()).orElse(null).nombre(),
						Collectors.averagingDouble(v -> v.duracion().toMinutes())
						));
	}
	
	public Libro libroConMasPaginasDeAutor(String autor) {
		
		return Libros.of().todos().stream()
				.filter(libro -> libro.autor().equals(autor))
		        .max(Comparator.comparingInt(Libro::numeroDePaginas))
		        .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado: " + autor));
	}
	
	public List<String> asignaturasConMasDeNMatriculas(Integer n){
		
		Map<String,Integer> asignaturasMatriculas = new HashMap<>();
		for (Matricula matricula : Matriculas.of().todas()) {
			String nombre = Asignaturas.of().asignatura(matricula.ida()).nombre();
			if (!asignaturasMatriculas.containsKey(nombre)) {
				asignaturasMatriculas.put(nombre, 0);
			}
			asignaturasMatriculas.put(nombre, asignaturasMatriculas.get(nombre) + 1);
		}
		List<String> resultado = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : asignaturasMatriculas.entrySet()) {
			if (entry.getValue() > n) {
				resultado.add(entry.getKey());
			}
		}
		return resultado;
	}
	
	public List<String> librosDeMasDe300Paginas(String autor, LocalDate fecha){
		
		
		return Libros.of().todos().stream()
				.filter(libro->libro.autor().equals(autor) && libro.numeroDePaginas() > 300 && libro.fechaPublicacion().isBefore(fecha))
				.map(Libro::titulo)
				.collect(Collectors.toList());
				
	}
	
	
	public static void main(String[] args) {
		EspacioAereo espacioAereo1 = EspacioAereo.of();
		ExamenBloque6 examen = new ExamenBloque6(espacioAereo1);

		Map<String, Double> resultado = examen.vueloProgramadoMasBaratoPorAerolinea();

		for (Map.Entry<String, Double> entry : resultado.entrySet()) {
			System.out.println("Aerolinea: " + entry.getKey() + ", Precio más barato: " + entry.getValue());
		}
		
		System.out.println("#####################################################");
		
		Biblioteca biblioteca1 = Biblioteca.of("Reina Mercedes", "41012", "lsi@us.es");
		ExamenBloque6 examen2 = new ExamenBloque6(biblioteca1);
		List<Libro> libros = examen2.top3LibrosConMasPaginasNoBestSeller();
		for (Libro libro:libros) {
			System.out.println(libro);
		}
		
		System.out.println("#####################################################");
		
		Banco banco1 = Banco.of();
		ExamenBloque6 examen4 = new ExamenBloque6(banco1);
		LocalDate fecha = LocalDate.of(2020, 1, 15);
		Map<String, Double> saldosMedios = examen4.saldoMedioClienteCuentasAbiertasAntesdeFecha(fecha);
		System.out.println(saldosMedios);
		
		System.out.println("#####################################################");
		
		Centro centro1 = Centro.of();
		ExamenBloque6 examen3 = new ExamenBloque6(centro1);
		System.out.println(examen3.numeroMatriculasDeAsignatura("Fundamentos de inteligencia artificial"));
		//System.out.println(examen3.numeroMatriculasDeAsignatura(""));
		
		System.out.println("#####################################################");
	
		Map<String, Integer> vuelosPorDestino = examen.numeroVuelosPorDestino();
		System.out.println(vuelosPorDestino);
		
		System.out.println("#####################################################");
		
		List<String> nombres = examen.alumnosPorCurso(1);
		System.out.println("Alumnos del curso con id 1: " + nombres);
		
		System.out.println("#####################################################");
		
		List<String> clientes = examen.clientesConMasDeXCuentasActivas(2);
		System.out.println("Clientes con más de 2 cuentas activas: " + clientes);
		
		System.out.println("#####################################################");
		
		Map<String,Double> duracionMedia = examen.duracionMediaDeVuelosPorAerolinea();
		for (Map.Entry<String, Double> entry : duracionMedia.entrySet()) {
			System.out.println(
					"Aerolinea: " + entry.getKey() + ", Duración media de vuelos: " + entry.getValue() + " minutos");
		}
		System.out.println("#####################################################");
		
		Libro libro = examen.libroConMasPaginasDeAutor("Mercedes Franch Gilabert");
		System.out.println("Libro con más páginas de Mercedes Franch Gilabert: " + libro);
		
		System.out.println("#####################################################");
		
		List<String> asignaturas = examen.asignaturasConMasDeNMatriculas(200);
		System.out.println("Asignaturas con más de 3 matrículas: " + asignaturas);
		
	}
}
