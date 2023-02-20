package server;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Question {
    //Queue<String> questionList;
    //Queue<List> questionList;
    private List<List> questionList;
    private String[] answersLetter = {"", "A. ", "B. ", "C. "};
    private List<String> correctAnswersList = new ArrayList<>();
    private String correctAnswer;
    private String correctAnswerValue;
    private int maxQuestions = 10;

    public Question(){
        this.questionList = new ArrayList<>();
    }

    /**
     * This method receives the theme chosen by the player and create the list of questions from questions.txt
     * Puts the questions into the list in a random order
     * @param gameTheme is the Theme chosen
     */
    public void createListOfQuestion(String gameTheme) throws IOException {

        List<String> tempQuestionsList = new ArrayList<>();

        BufferedReader readerQuestionTxt = new BufferedReader(new FileReader("resources/questions.txt"));

        if (!gameTheme.equals("ALL THEMES"))
            createListOneTheme(gameTheme, tempQuestionsList, readerQuestionTxt);
        else
            createListAllThemes(tempQuestionsList);

        //System.out.println(questionList.toString());
        ////
        randomQuestions();
    }

    /**
     * this method creates the List of the chosen theme
     * @param gameTheme
     * @param temp
     * @param readerQuestionTxt
     * @throws IOException
     */

    public void createListOneTheme(String gameTheme, List<String> temp, BufferedReader readerQuestionTxt) throws IOException {

        String line = readerQuestionTxt.readLine();

        while (line != null) {

            if (line.contains(gameTheme)) {

                while (!line.isEmpty()) {
                        temp.add(line.substring(line.indexOf(": ")+2, line.length()));
                    for (int i = 0; i < 3; i++) {
                        line = readerQuestionTxt.readLine();
                        temp.add(line.substring(3, line.length()));
                    }
                    line = readerQuestionTxt.readLine();
                    this.questionList.add(new ArrayList(temp));
                    //System.out.println(temp);
                    temp.clear();
                }
                break;
            }
            line = readerQuestionTxt.readLine();
        }
    }

    /**
     * this method creates the List with questions of every themes
     * the choice is made randomly
     * @param temp
     * @throws IOException
     */

    public void createListAllThemes(List<String> temp) throws IOException {

        createCleanFile();

        Path finalFilePath = Paths.get("resources/tempQuestions.txt");
        BufferedReader questionReader = new BufferedReader(new FileReader(finalFilePath.toFile()));
        String line = questionReader.readLine();
        int randomQuestion;

        while(line != null) {

            temp.add(line.substring(line.indexOf(": ")+2, line.length()));

            for (int i = 0; i < 3; i++) {
                line = questionReader.readLine();
                temp.add(line.substring(3, line.length()));
            }

            line = questionReader.readLine();

            this.questionList.add(new ArrayList(temp));
            System.out.println(temp);
            temp.clear();
        }
    }

    private void createCleanFile() {

        try {
            Path originalFilePath = Paths.get("resources/questions.txt");
            Scanner txtFile = new Scanner(originalFilePath);
            PrintWriter writer = new PrintWriter("resources/tempQuestions.txt");

            while (txtFile.hasNext()) {
                String line = txtFile.nextLine();
                if (!line.isEmpty() && !line.contains("--FIM--")) {
                    writer.write(line + "\n");
                }
            }
            txtFile.close();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Does the random of the questions and answers
     * Puts the value of the correct answers into an array
     */

    public void randomQuestions(){
        Collections.shuffle(questionList);

        for (int i = 0; i < questionList.size(); i++) {
            correctAnswersList.add(questionList.get(i).get(1).toString());

            Collections.shuffle(questionList.get(i).subList(1, 4));
        }
        //System.out.println(questionList);
        //System.out.println(correctAnswersList.toString());
    }

    /**
     * Compiles the position zero of the List into a String to send to the server
     * After having the value in the string, removes position 0 of the List
     * @return
     */

    public String questionToString(){
        //public void questionToString(){

        String fullQuestionToServer = questionList.get(0).get(0).toString() + "\n";

        for (int i = 1; i < questionList.get(0).size(); i++) {
            //fullQuestionToServer += answersLetter[i] + questionList.get(0).get(i).toString().substring(3) + "\n";
            fullQuestionToServer += answersLetter[i] + questionList.get(0).get(i).toString() + "\n";
        }

        System.out.println(fullQuestionToServer);

        correctAnswer = String.valueOf(fullQuestionToServer.charAt(fullQuestionToServer.indexOf(correctAnswersList.get(0))-3));
        System.out.println(correctAnswer);

        correctAnswerValue = correctAnswersList.get(0);
        System.out.println(correctAnswerValue);

        removeQuestionFromList();

        return fullQuestionToServer;
    }

    public void removeQuestionFromList(){
        questionList.remove(0);
        correctAnswersList.remove(0);
    }

    public String getCorrectAnswerValue() {
        return correctAnswerValue;
    }

    public String getCorrectAnswer(){
        return correctAnswer;
    }
    public String getQuestion(){
        return questionToString();
    }
}

