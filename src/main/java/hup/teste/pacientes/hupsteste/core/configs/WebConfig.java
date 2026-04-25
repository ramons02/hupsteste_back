package hup.teste.pacientes.hupsteste.core.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Adicione a URL real da sua Vercel aqui
                .allowedOrigins(
                        "https://front-avaliacao.vercel.app",
                        "http://localhost:8080", // Opcional: para testes locais
                        "https://front-avaliacao-ramons02.vercel.app" // Exemplo de URL de deploy da Vercel
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // Importante para o Bearer Token que você está usando no Rust
    }

}