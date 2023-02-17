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
    private int maxQuestions = 10;

    public Question(){
        this.questionList = new ArrayList<>();
    }

    public static void main(String[] args) throws IOException {
        Question teste = new Question();

        teste.createListOfQuestion("ALL THEMES");
    }

    /**
     * This method goes to the questions.txt and select the theme defined by the players
     * Puts the questions into the list in a random order
     * @param gameTheme is the Theme chosen
     */
    public void createListOfQuestion(String gameTheme) throws IOException {

        List<String> temp = new ArrayList<>();

        BufferedReader readerQuestionTxt = new BufferedReader(new FileReader("resources/questions.txt"));

        if (!gameTheme.equals("ALL THEMES"))
            createListOneTheme(gameTheme, temp, readerQuestionTxt);
        else
            createListAllThemes(temp);

        System.out.println(questionList.toString());
        ////
        randomQuestions();
    }

    public void createListOneTheme(String gameTheme, List<String> temp, BufferedReader readerQuestionTxt) throws IOException {

        String line = readerQuestionTxt.readLine();

        while (line != null) {

            if (line.contains(gameTheme)) {

                while (!line.isEmpty()) {
                    for (int i = 0; i < 4; i++) {
                        temp.add(line.replace(gameTheme + ": ", ""));
                        line = readerQuestionTxt.readLine();
                    }
                    this.questionList.add(new ArrayList(temp));
                    System.out.println(temp);
                    temp.clear();
                }
                break;
            }
            line = readerQuestionTxt.readLine();
        }
    }

    public void createListAllThemes(List<String> temp) throws IOException {

        createCleanFile();

        Path finalFilePath = Paths.get("resources/tempQuestions.txt");
        BufferedReader questionReader = new BufferedReader(new FileReader(finalFilePath.toFile()));
        String presentLine = "";
        int randomQuestion;

        List<Integer> jumpLines = new ArrayList<>();
        for (int i = 0; i < Files.lines(finalFilePath).count()-1; i += 4) {
            jumpLines.add(i);
        }

        while(questionList.size() < maxQuestions) {

            randomQuestion = (int) (Math.random() * jumpLines.size());
            System.out.println("valor do random: " + randomQuestion);
            System.out.println(jumpLines);
            jumpLines.remove(randomQuestion);

            presentLine = questionReader.readLine();
            temp.add(presentLine.substring(presentLine.indexOf(": ")+2, presentLine.length()));

            for (int i = 0; i < 3; i++) {
                presentLine = questionReader.readLine();
                temp.add(presentLine.substring(3, presentLine.length()));
            }
            this.questionList.add(new ArrayList(temp));
            System.out.println(temp);
            temp.clear();
        }
    }

    private void createCleanFile() throws IOException {
        Path originalFilePath = Paths.get("resources/questions.txt");
        Path finalFilePath = Paths.get("resources/tempQuestions.txt");
        System.out.println(Files.readAllLines(finalFilePath).get(0));

        Scanner txtFile = new Scanner(originalFilePath);
        PrintWriter writer = new PrintWriter("resources/tempQuestions.txt");

        while (txtFile.hasNext()) {
            String line = txtFile.nextLine();
            if (!line.isEmpty()) {
                writer.write(line);
                writer.write("\n");
            }
        }

        txtFile.close();
        writer.close();
    }

    /**
     * Does the random of the questions and answers
     * Updates the value of the correct answer
     */

    public void randomQuestions(){
        Collections.shuffle(questionList);
        System.out.println(questionList.toString());

        for (int i = 0; i < questionList.size(); i++) {
            this.correctAnswer = questionList.get(i).get(1).toString().substring(3);
            correctAnswersList.add(this.correctAnswer);
            //System.out.println(this.correctAnswer);

            Collections.shuffle(questionList.get(i).subList(1, 4));
            System.out.println(questionList.get(i).get(1));
        }
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
            fullQuestionToServer += answersLetter[i] + questionList.get(0).get(i).toString().substring(3) + "\n";
        }

        //this.correctAnswer = String.valueOf(fullQuestionToServer.charAt(fullQuestionToServer.indexOf(this.correctAnswer)-3));
        this.correctAnswer = String.valueOf(fullQuestionToServer.charAt(fullQuestionToServer.indexOf(correctAnswersList.get(0))-3));

        /*getQuestion();
        getCorrectAnswer();*/
        removeQuestionFromList();

        return fullQuestionToServer;
    }

    public void removeQuestionFromList(){
        questionList.remove(0);
        correctAnswersList.remove(0);
        System.out.println(questionList);
        System.out.println(correctAnswersList);
    }

    public String getCorrectAnswer(){
        //System.out.println(correctAnswer);
        return correctAnswer;
    }
    public String getQuestion(){
        return questionToString();
    }
}
