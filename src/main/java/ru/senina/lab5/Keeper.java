package ru.senina.lab5;

import ru.senina.lab5.commands.*;
import ru.senina.lab5.labwork.Coordinates;
import ru.senina.lab5.labwork.Difficulty;
import ru.senina.lab5.labwork.Discipline;
import ru.senina.lab5.labwork.LabWork;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Keeper {
    private String filename = "resources/data.json";
    private CollectionKeeper collectionKeeper;

    public Keeper(String filename) {
        this.filename = filename;
    }

    public void start() throws IOException {
        Parser parser = new Parser();
        try {
            collectionKeeper = parser.fromJsonToCollectionKeeper(parser.fromFileToString(filename));
        } catch (InvalidArgumentsException | IOException e) {
            System.out.println(e.getMessage() + " Collection will be empty.");
            collectionKeeper = new CollectionKeeper(new LinkedList<LabWork>());
        }

        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put("help", new HelpCommand());
        commandMap.put("info", new InfoCommand(collectionKeeper));
        commandMap.put("show", new ShowCommand(collectionKeeper, parser));
        commandMap.put("add", new AddCommand(collectionKeeper));
        commandMap.put("update", new UpdateCommand(collectionKeeper));
        commandMap.put("remove_by_id", new RemoveByIDCommand(collectionKeeper));
        commandMap.put("clear", new ClearCommand(collectionKeeper));
        commandMap.put("save", new SaveCommand(collectionKeeper, parser, filename));
        commandMap.put("remove_at", new RemoveAtCommand(collectionKeeper));
        commandMap.put("remove_greater", new RemoveGreaterCommand(collectionKeeper));
        commandMap.put("sort", new SortCommand(collectionKeeper));
        commandMap.put("min_by_difficulty", new MinByDifficultyCommand(collectionKeeper, parser));
        commandMap.put("filter_by_description", new FilterByDescriptionCommand(collectionKeeper, parser));
        commandMap.put("print_descending", new PrintDescendingCommand(collectionKeeper, parser));
        terminal(parser, commandMap, "no file", 0);

    }

    public void terminal(Parser parser, Map<String, Command> commandMap, String filename, int level) throws IOException {
        if(level > 10 ){
            System.out.println("You can't execute file recursively more then 10 times! The programme will be finished!");
            System.exit(1);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        File f = new File(filename);
        if(f.exists() && !f.isDirectory() && Files.isReadable(f.toPath())) {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        }

        while (true) {
            try {
                String[] line = br.readLine().split(" ");
                if (commandMap.containsKey(line[0])) {
                    boolean validCommand = true;
                    Command command = commandMap.get(line[0]);
                    command.setArgs(line);
                    boolean commandIsReady = true;
                    // TODO: переписать чтобы было без commandIsReady
                    if (command instanceof ElementNeed) {
                        commandIsReady = false;
                        boolean exit = false;
                        while (!exit) {
                            try {
                                //TODO: rewrite without code duplication
                                LabWork element = new LabWork();
                                System.out.println("You run a command, which needs LabWork element to be entered.");

                                System.out.println("Enter element's name.");
                                element.setName(br.readLine());

                                System.out.println("Enter coordinates. In first line x <= 74. In second y >= -47.");
                                element.setCoordinates(new Coordinates(Integer.parseInt(br.readLine()), Long.parseLong(br.readLine())));

                                System.out.println("Enter minimal point.");
                                element.setMinimalPoint(Float.parseFloat(br.readLine()));

                                System.out.println("Enter element description.");
                                element.setDescription(br.readLine());

                                System.out.println("Enter average point.");
                                element.setAveragePoint(Integer.parseInt(br.readLine()));

                                System.out.println("Enter one difficulty of following list:");
                                Difficulty[] difficulties = Difficulty.values();
                                for (Difficulty difficulty : difficulties) {
                                    System.out.print(difficulty.toString() + "; ");
                                }

                                element.setDifficulty(br.readLine());

                                System.out.println("Enter discipline parametrs:");
                                Discipline discipline = new Discipline();
                                System.out.println("Enter discipline name.");
                                discipline.setName(br.readLine());
                                System.out.println("Enter discipline lectureHours.");
                                discipline.setLectureHours(Long.parseLong(br.readLine()));
                                System.out.println("Enter discipline practiceHours.");
                                discipline.setPracticeHours(Integer.parseInt(br.readLine()));
                                System.out.println("Enter discipline selfStudyHours.");
                                discipline.setSelfStudyHours(Integer.parseInt(br.readLine()));
                                element.setDiscipline(discipline);
                                ((ElementNeed) command).setLabWorkElement(element);
                                commandIsReady = true;
                                exit = true;
                            } catch (InvalidArgumentsException e) {
                                System.out.println("You have entered invalidate value." + e.getMessage() + "\nDo you want to exit from command? (yes/no)");
                                if (br.readLine().equals("yes")) {
                                    exit = true;
                                    commandIsReady = false;
                                    System.out.println("You have exit from previous command.");
                                } else {
                                    System.out.println("Try again.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("You have entered wrong type value." + "\nDo you want to exit from command? (yes/no)");
                                if (br.readLine().equals("yes")) {
                                    exit = true;
                                    commandIsReady = false;
                                    System.out.println("You have exit from previous command.");
                                } else {
                                    System.out.println("Try again.");
                                }
                            }
                        }
                    }
                    if(commandIsReady){
                        System.out.println(command.run());
                    }
                }else{
                if (line[0].equals("exit")) {
                    br.close();
                    System.exit(0);
                }else if(line[0].equals("execute_script")) {
                    if(line.length != 2){
                        System.out.println("execute_script have the only argument - filename.");
                    }else {
                        terminal(parser, commandMap, line[1], level + 1);
                    }
                }else{
                    System.out.println("There is no such command.");}
                }
            } catch (InvalidArgumentsException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
