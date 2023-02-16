import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Question {
    //Queue<String> questionList;
    //Queue<List> questionList;
    List<List> questionList;
    String[] answersLetter = {"", "A. ", "B. ", "C. "};
    String correctAnswer;

    public Question(){
        this.questionList = new ArrayList<>();
    }

    /*public static void main(String[] args) throws IOException {
        Question teste = new Question();

        teste.createListOfQuestion("ART");
    }*/

    /**
     * This method goes to the questions.txt and select the theme defined by the players
     * Puts the questions into the queue in a random order
     * @param gameTheme
     */
    public void createListOfQuestion(String gameTheme) throws IOException {

        List<String> temp = new ArrayList<>();

        BufferedReader readerQuestionTxt = new BufferedReader(new FileReader("resources/questions.txt"));
        String line = readerQuestionTxt.readLine();


        //PARA COLOCAR TUDO NO MESMO SÍTIO
        while(line != null) {

            if (line.contains(gameTheme)) {

                while(!line.isEmpty()) {
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
        randomQuestions();
    }

    public void randomQuestions(){
        Collections.shuffle(questionList);
        System.out.println(questionList.toString());

        this.correctAnswer = questionList.get(0).get(1).toString().substring(3);
        System.out.println(this.correctAnswer);

        Collections.shuffle(questionList.get(0).subList(1, 4));
    }

    public String questionToString(){
    //public void questionToString(){

        String fullQuestionToServer = questionList.get(0).get(0).toString() + "\n";

        for (int i = 1; i < questionList.get(0).size(); i++) {
            fullQuestionToServer += answersLetter[i] + questionList.get(0).get(i).toString().substring(3) + "\n";
        }

        this.correctAnswer = String.valueOf(fullQuestionToServer.charAt(fullQuestionToServer.indexOf(this.correctAnswer)-3));

        /*getQuestion();
        getCorrectAnswer();*/
        removeQuestionFromList();

        return fullQuestionToServer;
    }

    public void removeQuestionFromList(){
        questionList.remove(0);
        System.out.println(questionList);
    }

    public String getCorrectAnswer(){
        //System.out.println(correctAnswer);
        return correctAnswer;
    }
    public String getQuestion(){
        return questionToString();
    }
}
