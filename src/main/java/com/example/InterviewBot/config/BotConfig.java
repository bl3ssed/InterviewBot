package com.example.InterviewBot.config;

// Импорт необходимых библиотек и аннотаций
import lombok.Data; // Аннотация Lombok, которая автоматически генерирует геттеры, сеттеры, toString, equals и hashCode
import org.springframework.beans.factory.annotation.Value; // Аннотация Spring для внедрения значений из properties-файлов
import org.springframework.context.annotation.Configuration; // Аннотация Spring, указывающая, что класс является конфигурационным
import org.springframework.context.annotation.PropertySource; // Аннотация Spring для указания источника properties-файла

// Аннотация @Configuration указывает, что этот класс является конфигурационным классом Spring.
// Spring будет использовать этот класс для настройки бинов и других компонентов.
@Configuration

// Аннотация @Data из библиотеки Lombok автоматически генерирует стандартные методы:
// геттеры, сеттеры, toString, equals и hashCode для всех полей класса.
@Data

// Аннотация @PropertySource указывает Spring, где искать файл с настройками.
// В данном случае, это файл "application.properties", который должен находиться в classpath.
@PropertySource("application.properties")

// Класс BotConfig предназначен для хранения конфигурационных данных бота,
// таких как имя бота и токен, которые будут загружены из файла application.properties.
public class BotConfig {

    // Аннотация @Value используется для внедрения значений из properties-файла в поля класса.
    // В данном случае, значение для поля botName будет взято из свойства "bot.name" в файле application.properties.
    @Value("${bot.name}")
    String botName;

    // Аналогично, значение для поля token будет взято из свойства "bot.token" в файле application.properties.
    @Value("${bot.token}")
    String token;
}