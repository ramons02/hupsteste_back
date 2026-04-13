package hup.teste.pacientes.hupsteste.core.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    
    private static final String DEFAULT_PATTERN = "dd/MM/yyyy HH:mm:ss";

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);
        return dateTime.format(formatter);
    }
}
