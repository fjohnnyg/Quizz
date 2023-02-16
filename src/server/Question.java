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
    private String correctAnswer;
    private int maxQuestions = 5;

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
        String line = readerQuestionTxt.readLine();

        //PARA COLOCAR TUDO NO MESMO SÍTIO
        if(!gameTheme.equals("ALL THEMES")) {
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
        ///


        ////If the player choose ALL THEMES

        if(gameTheme.equals("ALL THEMES")){

            System.out.println("todos juntos");

            List<Integer> jumpLines = new ArrayList<>();
            PrintWriter newFile = new PrintWriter("resources/tempQuestions.txt");

            while(line != null){

                if(!line.isEmpty()){
                    newFile.write(line);
                    newFile.write("\n");
                }
                line = readerQuestionTxt.readLine();
            }

            System.out.println("1o while já está");

            newFile.close();

            //////

            Path finalFilePath = Paths.get("resources/tempQuestions.txt");
            BufferedReader finalFile = new BufferedReader(new FileReader(finalFilePath.toFile()));
            String lineFile = finalFile.readLine();

            for (int i = 0; i < Files.lines(finalFilePath).count()-1; i += 4) {
                jumpLines.add(i);
            }
            System.out.println(jumpLines);


            while(jumpLines.size() > 0) {

                int randomQuestion = (int) (Math.random() * jumpLines.size());
                System.out.println("valor do random: " + randomQuestion);
                //System.out.println("random value");

                System.out.println(finalFile.read(CharBuffer.allocate(randomQuestion)));

                String presentLine = Files.readAllLines(finalFilePath).get(randomQuestion);

                temp.add(presentLine.substring(presentLine.indexOf(": ")+2, presentLine.length()));
                lineFile = presentLine;

                 for (int i = 0; i < 3; i++) {
                     temp.add(lineFile.substring(3, lineFile.length()));
                     lineFile = finalFile.readLine();
                 }
                 this.questionList.add(new ArrayList(temp));
                 System.out.println(temp);
                 temp.clear();

                 lineFile = finalFile.readLine();

                 jumpLines.remove(randomQuestion);
            }



        }

        System.out.println(questionList.toString());

        ////

        randomQuestions();
    }

    /**
     * Does the random of the questions and answers
     * Updates the value of the correct answer
     */

    public void randomQuestions(){
        Collections.shuffle(questionList);
        System.out.println(questionList.toString());

        this.correctAnswer = questionList.get(0).get(1).toString().substring(3);
        System.out.println(this.correctAnswer);

        Collections.shuffle(questionList.get(0).subList(1, 4));
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

