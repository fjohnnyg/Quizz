import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Question {
    //Queue<String> questionList;
    Queue<List> questionList;

    public Question(){
        this.questionList = new ArrayDeque<>(10);
    }

    public static void main(String[] args) throws IOException {
        Question teste = new Question();

        teste.createListOfQuestion("ART");
    }

    /**
     * This method goes to the questions.txt and select the theme defined by the players
     * Puts the questions into the queue in a random order
     * @param gameTheme
     */
    public void createListOfQuestion(String gameTheme) throws IOException {

        List<String> temp = new ArrayList<>();
        List<List> temp2 = new ArrayList<>();

        //int counter = 0;

        BufferedReader readerQuestionTxt = new BufferedReader(new FileReader("resources/questions.txt"));
        String line = readerQuestionTxt.readLine();

        while(line != null) {

            if (line.contains(gameTheme)) {

                while(!line.isEmpty()) {
                    for (int i = 0; i < 5; i++) {
                        temp.add(line);
                        line = readerQuestionTxt.readLine();
                    }
                    //this.questionList.add(new ArrayList(temp));
                    temp2.add(new ArrayList<>(temp));
                    System.out.println(temp);
                    temp.clear();
                    //counter++;
                }
                break;
            }

            line = readerQuestionTxt.readLine();

            /*line.lines()
                    .forEach(sentence -> {
                        if (sentence.contains(gameTheme)) {
                            questionList.add(sentence);
                            //counter++;
                        }
                    });*/
        }
        Collections.shuffle(temp2);
        questionList.add(temp2);
        System.out.println(questionList.toString());

        /*while(line != null) {

            if (line.contains(gameTheme)) {

                while(!line.isEmpty()) {
                    for (int i = 0; i < 5; i++) {
                        this.questionList.add(line);
                        //System.out.println(line);
                        line = readerQuestionTxt.readLine();
                    }
                }
                break;
            }

            line = readerQuestionTxt.readLine();
        }
        System.out.println(questionList.toString());*/

    }
}
