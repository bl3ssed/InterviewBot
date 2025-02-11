package com.example.InterviewBot.controller;


import com.example.InterviewBot.config.BotConfig;
import com.example.InterviewBot.model.Answer;
import com.example.InterviewBot.model.Question;
import com.example.InterviewBot.model.Test;
import com.example.InterviewBot.model.User;
import com.example.InterviewBot.repository.AnswerRepository;
import com.example.InterviewBot.repository.QuestionRepository;
import com.example.InterviewBot.repository.TestRepository;
import com.example.InterviewBot.service.UserService;
import com.example.InterviewBot.util.BotUtils;
import com.example.InterviewBot.util.CurrentTestManager;
import com.example.InterviewBot.util.UserStateManager;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.util.*;

@Component
@AllArgsConstructor
public class TelegramBotController extends TelegramLongPollingBot {

    @Autowired
    private UserStateManager userStateManager;

    @Autowired
    private CurrentTestManager currentTestManager;

    private final String PASSWORD = "goat52!"; // Замените на ваш пароль

    @Autowired
    private final BotConfig botconfig;

    @Autowired
    private UserService userService = new UserService();

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository ;

    @Autowired
    private BotUtils botUtils;

    @Override
    public String getBotUsername() {
        return botconfig.getBotName();
    }



    @Override
    public String getBotToken() {
        return botconfig.getToken();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();
            String firstName = update.getMessage().getFrom().getFirstName();
            Long tgID = update.getMessage().getFrom().getId();

            // Обработка команды /start
            if (messageText.equals("/start")) {
                start(username, firstName, tgID, chatId);
            }
            // Обработка команды /tests
            else if (messageText.equals("/tests")) {
                showTests(chatId);
            }
            // Обработка выбора теста
            else if (userStateManager.getUserStates().getOrDefault(chatId, "").equals("SELECTING_TEST")) {
                startTest(messageText, chatId);
            }else if (userStateManager.getUserStates().getOrDefault(chatId, "").equals("TEST_IN_PROGRESS")) {
                processAnswer(messageText, chatId);
            }
            else if (messageText.equals("/get_admin")) {
                getAdminRequest(tgID, chatId);
            } else if (userStateManager.getUserStates().getOrDefault(chatId, "").equals("WAITING_FOR_PASSWORD")) {
                setAdmin(messageText, tgID, username, firstName, chatId);
            }else {
                // Обработка других команд и сообщений
                botUtils.sendMessage(chatId, "Команда не распознана. Пожалуйста, используйте /start для начала.",this);
            }
        }
    }

    private void showTests(long chatId) {
        List<Test> tests = testRepository.findAllByOrderByCreatedAtDesc(); // Получаем список тестов из репозитория

        if (tests.isEmpty()) {
            botUtils.sendMessage(chatId, "Нет доступных тестов.", this);
            return;
        }

        StringBuilder message = new StringBuilder("Доступные тесты:\n");
        for (Test test : tests) {
            message.append(test.getTestId()).append(". ").append(test.getTitle()).append("\n");
        }
        message.append("Введите ID теста, который хотите пройти.");

        // Устанавливаем состояние пользователя в SELECTING_TEST
        userStateManager.getUserStates().put(chatId, "SELECTING_TEST");

        botUtils.sendMessage(chatId, message.toString(), this);
    }

    private void startTest(String messageText, long chatId) {
        try {
            Integer testId = Integer.parseInt(messageText);
            Test test = testRepository.findByTestId(testId)
                    .orElseThrow(() -> new RuntimeException("Тест не найден"));

            // Получаем список всех вопросов для теста
            List<Question> allQuestions = questionRepository.findByTest_TestId(testId);
            if (allQuestions.isEmpty()) {
                botUtils.sendMessage(chatId, "В этом тесте нет вопросов.", this);
                return;
            }

            // Определяем количество вопросов для прохождения
            int totalQuestionsToAsk = 20; // Например, 5 вопросов
            if (allQuestions.size() < totalQuestionsToAsk) {
                totalQuestionsToAsk = allQuestions.size();
            }

            // Выбираем случайные вопросы
            List<Question> selectedQuestions = getRandomQuestions(allQuestions, totalQuestionsToAsk);

            // Начинаем тест
            userStateManager.getUserStates().put(chatId, "TEST_IN_PROGRESS");
            currentTestManager.setCurrentTest(chatId, testId);
            currentTestManager.setCurrentQuestionIndex(chatId, 0);
            currentTestManager.setQuestions(chatId, selectedQuestions);

            // Отправляем первый вопрос
            sendQuestion(chatId, selectedQuestions.get(0));
        } catch (NumberFormatException e) {
            botUtils.sendMessage(chatId, "Пожалуйста, введите корректный ID теста.", this);
        } catch (RuntimeException e) {
            botUtils.sendMessage(chatId, e.getMessage(), this);
        }
    }

    private void sendQuestion(long chatId, Question question) {
        StringBuilder message = new StringBuilder("Вопрос:\n").append(question.getQuestionText()).append("\n");
        // Здесь можно добавить варианты ответов, если они есть
        botUtils.sendMessage(chatId, message.toString(), this);
    }

    private void endTest(long chatId, long testId) {
        // Логика завершения теста, например, подсчет результатов
        userStateManager.getUserStates().remove(chatId);
        currentTestManager.removeCurrentTest(chatId);
        botUtils.sendMessage(chatId, "Тест завершен. Спасибо за участие!", this);
    }

    private List<Question> getRandomQuestions(List<Question> allQuestions, int totalQuestions) {
        List<Question> shuffledList = new ArrayList<>(allQuestions);
        Collections.shuffle(shuffledList);
        return shuffledList.subList(0, totalQuestions);
    }


    private void processAnswer(String messageText, long chatId) {
        try {
            // Получаем текущий тест и вопрос
            long testId = currentTestManager.getCurrentTest(chatId);
            List<Question> questions = currentTestManager.getQuestions(chatId);
            int currentIndex = currentTestManager.getCurrentQuestionIndex(chatId);
            if (currentIndex >= questions.size()) {
                endTest(chatId, testId);
                return;
            }

            Question currentQuestion = questions.get(currentIndex);

            // Здесь можно добавить логику для проверки ответа
            // Например, сравнить ответ пользователя с правильным ответом
            // Предположим, что ответы хранятся в отдельной таблице answers

            // Пример проверки:
            // Answer correctAnswer = answerRepository.findByQuestionId(currentQuestion.getQuestionId());
            // if (correctAnswer.getAnswerText().equalsIgnoreCase(messageText)) {
            //     // Правильный ответ
            // }

            // Для простоты, предположим, что любой ответ считается правильным
            botUtils.sendMessage(chatId, "Ответ принят. Переходим к следующему вопросу.", this);

            // Переходим к следующему вопросу
            currentTestManager.setCurrentQuestionIndex(chatId, currentIndex + 1);
            if (currentIndex + 1 < questions.size()) {
                sendQuestion(chatId, questions.get(currentIndex + 1));
            } else {
                endTest(chatId, testId);
            }
        } catch (Exception e) {
            botUtils.sendMessage(chatId, "Произошла ошибка при обработке ответа. Пожалуйста, попробуйте еще раз.", this);
        }
    }



    private void setAdmin(String messageText, Long tgID, String username, String firstName, long chatId) {
        String enteredPassword = messageText;
        if (enteredPassword.equals(PASSWORD)) { // Замените на ваш пароль
            // Логика для обновления роли пользователя
            userService.updateUser(tgID, username, firstName,"admin");
            botUtils.sendMessage(chatId, "Вы успешно получили права администратора!",this);
        } else {
            botUtils.sendMessage(chatId, "Неверный пароль. Попробуйте еще раз.",this);
        }
        userStateManager.getUserStates().remove(chatId);
    }

    private void getAdminRequest(Long tgID, long chatId) {
        userStateManager.getUserStates().put(tgID, "WAITING_FOR_PASSWORD");
        botUtils.sendMessage(chatId, "Пожалуйста, введите пароль для получения прав администратора:",this);
    }

    private void start(String username, String firstName, Long tgID, long chatId) {
        try {
            User user = userService.registerUser(username, firstName, "user", tgID);
            botUtils.sendMessage(chatId, "Вы успешно зарегистрированы! Добро пожаловать, " + firstName + ".",this);
        }
        catch (Exception e) {
            botUtils.sendMessage(chatId, "Вы уже зарегистрированы.",this);
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Здравствуйте, " + name + " !" + "\n" +
                "Это тг бот для подготовки к собеседованиям.";
        botUtils.sendMessage(chatId, answer,this);
    }

}
