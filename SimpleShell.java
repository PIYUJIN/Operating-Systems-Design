import java.io.*;
import java.util.*;
import java.lang.*;

public class SimpleShell {
    public static void main(String[] args) throws IOException, InterruptedException {
        String commandLine;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        File file = new File(System.getProperty("user.dir"));      // get current directory
        File dir = new File(file.getAbsolutePath());     // absolute path of current directory
        File home_dir = new File(System.getProperty("user.home"));   // get home directory
//        System.out.println(dir);
        ArrayList<String> history = new ArrayList<>();     // ArrayList of history of the command

        // we break out with <control><C>
        while (true) {
            //read what the user entered
            System.out.print("jsh>");
            commandLine = console.readLine();     // commandLine = store user input by reading

            if (commandLine.equals("history")) {     // if input equals to 'history', show the history of command
                for (int i=0; i<=history.size()-1; i++)
                    System.out.println(i + " " + history.get(i));
                continue;
            }
            // if input equals to '!!'
            else if (commandLine.equals("!!")) {
                String prev = null;
                if (history.size()-1>=0) {   // if there are previous command, run
                    prev = history.get(history.size() - 1);
                    commandLine = prev;       //change commanLine to previous command to rerun that command
                }
                else {      // if there are no history, show the error message (no previous command)
                    System.out.println("There is no previous command in history");
                    continue;
                }
            }

            // if the input equals to !<number>
            String [] harray = commandLine.split("!");
            ArrayList list_history = new ArrayList<>(Arrays.asList(harray));
//            System.out.println(list_history);
            boolean TF = false;
            try {
                for (int i = 0; i < history.size(); i++) {
                    //                    System.out.println(Integer.parseInt(harray[1]));
                    try {
                        if (Integer.parseInt(harray[1]) > (history.size() - 1)) {   // if the number is greater than history index, show error message and rerun
                            TF = true;
                            System.out.println("Index out of bound");
                            break;
                        } else {
                            if (commandLine.contains("!") && list_history.get(1).equals(Integer.toString(i))) {
                                String prev = null;
                                prev = history.get(i);
                                commandLine = prev;     //change commanLine to previous certain index of command to rerun that command
                                break;
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break;
                    }
                }
            } catch(NumberFormatException e) {
                continue;
            }

            if(TF) {
//                System.out.println("continue");
                continue;
            }


            // split data with space and store in string array in #4
            String[] sarray = commandLine.split(" ");
//            System.out.println(sarray);


            // transform string array into list of strings
            ArrayList<String> list = new ArrayList<>(Arrays.asList(sarray));

            // if input is equals to 'history number'
            for (int i=0; i<history.size(); i++) {
                if (list.get(0).equals("history") && list.get(1).equals(Integer.toString(i))) {
                    System.out.println(i + " " + history.get(i));
                    break;
                }
                else {
                    continue;
                }
            }

            // if there are some command about history, just loop again
            if (list.get(0).equals("history"))
                continue;

            ProcessBuilder pb = new ProcessBuilder(list);
//            System.out.println(list);
//            System.out.println(list.getClass().getName());

            //record history in arraylist
            if (!list.get(0).equals("history"))
                history.add(commandLine);
//            System.out.println("history : " + history);

            //if the user entered a return, just loop again
            if (commandLine.equals(""))
                continue;

            //when the input equals to 'exit' or 'quit', finish the system
//            System.out.println(list.get(list.size()-1));
//            System.out.println(list.get(list.size()-1).getClass().getName());
            if (list.get(list.size()-1).equals("exit") || list.get(list.size()-1).equals("quit")) {
                System.out.println("Goodbye.");
                System.exit(0);
            }

            // directory is changed into home directory
            if (list.size()==1 && list.contains("cd")) {
                dir = home_dir;
                pb.directory(new File(String.valueOf(dir)));
                System.out.println(dir);
                continue;
            }
            // go to parent directory
            else if (list.get(0).equals("cd") && list.get(1).equals("..")) {
                if (dir.getParentFile() != null) {
                    dir = dir.getParentFile();
                    System.out.println(dir);
                    continue;
                } else {
                    continue;
                }
            }
            // find the certain directory or file
            else if (list.get(0).equals("cd")) {
                File c_file = new File(dir +"/"+list.get(1));

                if (c_file.exists()) {
                    dir = c_file;
                    continue;
                } else {       // there is no such file or directory
                    System.out.println("no such file or directory");
                    continue;
                }
            }
            else{
                try {
                    pb = new ProcessBuilder(list);
                    pb.directory(new File(String.valueOf(dir)));
                    //                System.out.println(dir);
                    Process process = pb.start();
                    //obtain the input stream
                    InputStream is = process.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);

                    //read the output of the process
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }

                    int exitCode = process.waitFor();
                    br.close();
                } catch (Exception e) {
                    history.remove(history.size()-1);
                    continue;
                }

            }
        }

    }



    /** THE STEPS
     * (1) parse the input to obtain the command and any parameters
     * (2) create a ProcessBuilder object
     * (3) start the process
     * (4) obtain the output stream
     * (5) output the contents returned by the command
     */

};
