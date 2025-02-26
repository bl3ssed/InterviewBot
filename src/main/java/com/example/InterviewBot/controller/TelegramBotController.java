package com.example.InterviewBot.controller;


import com.example.InterviewBot.config.BotConfig;
import com.example.InterviewBot.dto.AnswerJson;
import com.example.InterviewBot.dto.QuestionJson;
import com.example.InterviewBot.dto.TestJson;
import com.example.InterviewBot.model.*;
import com.example.InterviewBot.repository.*;
import com.example.InterviewBot.service.*;
import com.example.InterviewBot.util.BotUtils;
import com.example.InterviewBot.util.CurrentTestManager;
import com.example.InterviewBot.util.UserStateManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static org.apache.commons.io.FileUtils.getFile;

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
    private UserRepository userRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionRepository questionRepository ;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private BotUtils botUtils;
    @Autowired
    private TestService testService;


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
        long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();
        String firstName = update.getMessage().getFrom().getFirstName();
        Long tgID = update.getMessage().getFrom().getId();
        String currentState = userStateManager.getUserStates().getOrDefault(chatId,null);
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            if (currentState == null) {
                if (messageText.equals("/help")) {
                    getStarted(username, firstName, tgID, chatId);
                }
                // Обработка команды /start
                else if (messageText.equals("/start")) {
                    start(username, firstName, tgID, chatId);
                }
                // Обработка команды /tests
                else if (messageText.equals("/tests")) {
                    showTests(chatId);
                }// Обработка фидбека
                else if (messageText.equals("/feedback")) {
                    getFeedback(chatId,tgID);
                }
                else if (messageText.equals("/get_admin")) {
                    getAdminRequest(tgID, chatId);
                }
                else if (messageText.equals("/stat")) {
                    getStatistics(tgID, chatId);
                }
                else if (messageText.equals("/add_test1")) {
                    //addTest(tgID, chatId);
                }
                else if(messageText.equals("/add_test")){
                    addTest1(tgID, chatId);
                }
                else {
                    // Обработка других команд и сообщений
                    botUtils.sendMessage(chatId, "Команда не распознана. Пожалуйста, используйте /start для начала.",this);
                }
            }
            else {
                if (messageText.equals("/stop")) {
                    stopActive(tgID, chatId);
                }
                else {
                    switch (currentState) {
                        case "SELECTING_TEST":
                            // Обработка выбора теста
                            startTest(messageText, chatId);
                            break;
                        case "TEST_IN_PROGRESS":
                            processAnswer(messageText, chatId);
                            break;
                        case "WAITING_FOR_PASSWORD":
                            setAdmin(messageText, tgID, username, firstName, chatId);
                            break;
                        case "WAITING_FOR_FEEDBACK_TEXT":
                            // Обработка выбора теста
                            getFeedbackText(messageText, chatId, tgID);
                            break;
                        case "WAITING_FOR_FEEDBACK_RATE":
                            // Обработка выбора теста
                            getFeedbackRate(messageText, chatId, tgID);
                            break;

                        default:
                            userStateManager.getUserStates().remove(chatId);
                            botUtils.sendMessage(chatId, "Неизвестное состояние. Пожалуйста, начните с команды /start.", this);
                            break;
                    }
                }
            }
        }
        else if (update.getMessage().hasDocument()) {
            Document document = update.getMessage().getDocument();

                    // Обработка выбора теста
                    if (document.getFileName().endsWith(".json")) {
                        getJsonTest(document, chatId, tgID);
                    } else {
                        botUtils.sendMessage(chatId, "Пожалуйста, загрузите файл в формате JSON.", this);
                    }
        }
    }

    private void getJsonTest(Document jsondoc, long chatId, Long tgID) {
        String fileId = jsondoc.getFileId();

        try {
            // Получаем объект File с помощью getFile()
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);
            File file = execute(getFile); // Выполняем запрос для получения файла
            String filePath = file.getFilePath();

            // Получаем URL для скачивания файла
            String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;

            // Загружаем JSON-файл
            java.io.File jsonFile = downloadFileFromUrl(fileUrl); // Изменённый вызов, передаём URL
            String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()), StandardCharsets.UTF_8);

            // Добавляем тест из JSON в базу
            addTestFromJson(jsonContent, chatId);
        } catch (Exception e) {
            botUtils.sendMessage(chatId, "Ошибка при обработке файла.", this);
            e.printStackTrace();
        }
    }


    public java.io.File downloadFileFromUrl(String fileUrl) throws java.io.IOException {
        // Загружаем файл и сохраняем его на сервере
        java.io.File downloadedFile = new java.io.File("C:\\Users\\blessed\\Projects\\InterviewBot\\src\\main\\resources\\tests_to_add\\test.json");

        try (InputStream in = new URL(fileUrl).openStream();
             FileOutputStream out = new FileOutputStream(downloadedFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return downloadedFile; // Возвращаем скачанный файл
    }



    private void addTestFromJson(String jsonContent, long chatId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TestJson testJson = objectMapper.readValue(jsonContent, TestJson.class);

            // Создаем тест
            Test test = testService.createTest(testJson.getTestName(), "Описание теста");

            // Обработка вопросов
            for (QuestionJson q : testJson.getQuestions()) {
                // Создаем вопрос с развернутым текстом
                Question question = questionService.createQuestion(test.getTestId(), q.getText(), q.getDetailedText());

                // Обработка ответов
                for (AnswerJson a : q.getAnswers()) {
                    answerService.createAnswer(question.getQuestionId(), a.getText(), a.getOrder(), a.isCorrect());
                }
            }

            botUtils.sendMessage(chatId, "Тест успешно добавлен!", this);
        } catch (Exception e) {
            botUtils.sendMessage(chatId, "Ошибка при разборе JSON. Проверьте формат файла.", this);
            e.printStackTrace();
        }
    }


    private void addTest1(Long tgID, long chatId) {
        User user = userRepository.findByTgId(tgID).orElse(null);
        if (user != null && user.getRole().equals("admin")) {
            userStateManager.getUserStates().put(chatId,"WAITING_JSON");
            botUtils.sendMessage(chatId, "Ожидаю JSON: ", this);
        }
    }


//    private void addTest(Long tgID, long chatId) {
//        User user = userRepository.findByTgId(tgID).orElse(null);
//        if (user != null && user.getRole().equals("admin")) {
//
//            Scanner scanner = new Scanner(System.in);
//            botUtils.sendMessage(chatId,"Вы добавляете тест.",this);
//            System.out.println("Введите количество тестов:\n");
//            int cnt_tst = scanner.nextInt();
//            for (int i = 0; i < cnt_tst; i++) {
//                System.out.println("Введите название теста:\n");
//                String testName = scanner.next();
//                Test addtest =testService.createTest(testName,"desc");
//                System.out.println("Введите кол-во вопросов к тесту");
//                int cnt_question = scanner.nextInt();
//                for (int j = 0; j < cnt_question; j++) {
//                    System.out.println("Введите текст вопроса:");
//                    String questiontext = scanner.next();
//                    Question addquestion =questionService.createQuestion(addtest.getTestId(),questiontext);
//                    System.out.println("Введите кол-во ответовк этому вопросу:");
//                    int cnt_answer = scanner.nextInt();
//                    for (int k = 0; k < cnt_answer; k++) {
//                        System.out.println("Введите текст ответа:");
//                        String answertext = scanner.next();
//                        boolean fl = false;
//                        System.out.println("Если он правильный введите 1, если нет - ничего не вводите");
//                        String bol = scanner.nextLine();
//                        if (!bol.isEmpty()) {
//                            fl = true;
//                        }
//                        answerService.createAnswer(addquestion.getQuestionId(),answertext,k+1,fl);
//                    }
//                    System.out.println("Вы ввели ответы на вопрос: "+addquestion.getQuestionId());
//                }
//                System.out.println("Вы ввели вопросы на тест: "+addtest.getTestId());
//            }
//
//        }
//        else {
//            botUtils.sendMessage(chatId,"Sorry, but you is not admin( about all questions: @bl3ssed_k1d",this);
//        }
//    }

    private void getStarted(String username, String firstName, Long tgID, long chatId) {
        var msg = "Здравствуйте, "+username+"!\n"+"Добро пожаловать в главное меню!\n" +"Вот справка по боту:\n"+"/start - регистрация\n"+"/stat - статистика по тестам\n"+ "/tests - список тестов\n"+"/feedback - оставить отзыв о работе бота\n"+"/stop - выйти из текущей активности\n";
        botUtils.sendMessage(chatId, msg, this);
    }

    private void getStatistics(Long tgID, long chatId) {
        List<Statistics> stats = statisticsRepository.findByUser(userRepository.findByTgId(tgID).orElse(null));
        if (stats != null) {
            for (Statistics s : stats) {
                String msg = ("Тема: \n" + s.getTest().getTitle() + "\n" + "Результат: \n" + s.getScore() +"/"+ s.getTotalQuestions()+"\n");
                botUtils.sendMessage(chatId,msg,this);
            }

        }
        else {
            botUtils.sendMessage(chatId,"Вы не проходили тестов, или произошла ошибка(\n",this);
        }
    }

    private void stopActive(Long tgID, long chatId) {
        userStateManager.getUserStates().remove(chatId);
        currentTestManager.removeCurrentTest(chatId);
        var msg = "Добро пожаловать в главное меню!\n";
        botUtils.sendMessageWithKeyboard(chatId, msg, this);
    }

    private void getFeedbackRate(String messageText, long chatId, Long tgID) {
        try{
            Integer rate = Integer.parseInt(messageText);
            System.out.println("msg text = "+messageText);
            System.out.println("rate text = "+rate.toString());
            feedbackService.setFeedbackRate(tgID,rate);

            userStateManager.getUserStates().remove(chatId);
            String msg = "Спасибо за ваш отзыв!\n";
            botUtils.sendMessage(chatId, msg, this);
        }catch (Exception e){
            botUtils.sendMessage(chatId, "Пожалуйста, введите число 1-10", this);
        }


    }

    private void getFeedbackText(String messageText, long chatId,long tgID) {
        // get text feedback...
        if (feedbackRepository.findByUser_tgId(tgID).isEmpty()) {
            feedbackService.createFeedback(tgID,messageText,0);
        }
        else {
            feedbackService.updateFeedback(Objects.requireNonNull(userRepository.findByTgId(tgID).orElse(null)),messageText,0);
        }
        userStateManager.getUserStates().remove(chatId);
        userStateManager.getUserStates().put(tgID, "WAITING_FOR_FEEDBACK_RATE");
        String msg = "Введите вашу оценку 1-10:\n";
        botUtils.sendMessage(chatId, msg, this);
    }

    private void getFeedback(long chatId,Long tgID) {
        userStateManager.getUserStates().put(tgID, "WAITING_FOR_FEEDBACK_TEXT");
        String msg = "Напишите отзыв о работе бота:\n";
        botUtils.sendMessage(chatId, msg, this);
    }

    private void showTests(long chatId) {
        List<Test> tests = testRepository.findAllByOrderByTestId(); // Получаем список тестов из репозитория

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

        botUtils.sendMessageWithKeyboardTestList(chatId, message.toString(), this,tests);
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
            sendQuestion(chatId, selectedQuestions.getFirst());
        } catch (NumberFormatException e) {
            botUtils.sendMessage(chatId, "Пожалуйста, введите корректный ID теста.", this);
        } catch (RuntimeException e) {
            botUtils.sendMessage(chatId, e.getMessage(), this);
        }
    }

    private void sendQuestion(long chatId, Question question) {
        StringBuilder message = new StringBuilder("Вопрос:\n").append(question.getQuestionText()).append("\n");
        List<Answer> currentAnswers = answerRepository.findByQuestion_QuestionId(question.getQuestionId());
        currentAnswers.sort(Comparator.comparing(Answer::getNumber));
        int cnt_ans = 0;
        for (Answer ans : currentAnswers) {
            cnt_ans++;
            message.append(ans.getNumber().toString()).append(") ").append(ans.getAnswerText()).append("\n");
        }
        // Здесь можно добавить варианты ответов, если они есть
        botUtils.sendMessageWithKeyboardTest(chatId, message.toString(), this,cnt_ans);
    }

    private void endTest(long chatId, long testId) {
        // Получаем текущий счет
        int score = currentTestManager.getScore(chatId);
        int totalQuestions = currentTestManager.getTotalQuestions(chatId);

        // Сохраняем статистику
        User user = userRepository.findByTgId(chatId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Test test = testRepository.findByTestId((int) testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));
        if (statisticsRepository.findByUserAndTest(user, test) == null) {
            statisticsService.saveStatistics(user, test, score, totalQuestions);
        }
        else {
            statisticsService.updateStatistics(user, test, score, totalQuestions);
        }


        // Очищаем состояние теста
        userStateManager.getUserStates().remove(chatId);
        currentTestManager.removeCurrentTest(chatId);
        String msg = "Тест завершен. Ваш счет: " + score + " из " + totalQuestions + ".\nСпасибо за участие!";
        botUtils.sendMessageWithKeyboardEndTest(chatId, msg, this);
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
            Integer number = Integer.parseInt(messageText);
            List<Answer> currentAnswers = answerRepository.findByQuestion_QuestionId(questions.get(currentIndex).getQuestionId());
            currentAnswers.sort(Comparator.comparing(Answer::getNumber));
            Answer userAnswer = currentAnswers.get(number-1);
            Question currentQuestion = questions.get(currentIndex);
            String rightAnswer = "";

            if (userAnswer.getIsCorrect()) {
                /* TODO исправить count, вводить неявно. */
                currentTestManager.incrementScore(chatId);
                botUtils.sendMessage(chatId, "Ответ верный. Переходим к следующему вопросу.", this);
            }else {
                for (Answer answer : currentAnswers) {
                    if (answer.getIsCorrect()) {
                        rightAnswer += answer.getAnswerText() + " ";
                    }
                }
                String details = "Подробный ответ: \n";
                details += currentQuestion.getDetailed() + "\n";
                String msg ="Ответ не верный.\n"+"Правильный ответ: "+rightAnswer+"\n"+" Переходим к следующему вопросу.\n";
                /* TODO исправить count, вводить неявно. */
                botUtils.sendMessage(chatId, msg , this);
                botUtils.sendMessage(chatId, details , this);
            }
            // Здесь можно добавить логику для проверки ответа
            // Например, сравнить ответ пользователя с правильным ответом
            // Предположим, что ответы хранятся в отдельной таблице answers

            // Пример проверки:
            // Answer correctAnswer = answerRepository.findByQuestionId(currentQuestion.getQuestionId());
            // if (correctAnswer.getAnswerText().equalsIgnoreCase(messageText)) {
            //     // Правильный ответ
            // }

            // Для простоты, предположим, что любой ответ считается правильным

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
            botUtils.sendMessageWithKeyboard(chatId, "Вы успешно зарегистрированы! Добро пожаловать, " + firstName + ".",this);
        }
        catch (Exception e) {
            botUtils.sendMessageWithKeyboard(chatId, "Вы уже зарегистрированы. \n Для справки по возможностям бота используйте /help\n",this);
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Здравствуйте, " + name + " !" + "\n" +
                "Это тг бот для подготовки к собеседованиям.";
        botUtils.sendMessage(chatId, answer,this);
    }

}
