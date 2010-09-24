package org.bestgrid.grisu.client.gricli;

import org.bestgrid.grisu.client.gricli.command.GricliCommandFactory;
import org.bestgrid.grisu.client.gricli.command.GricliCommand;
import java.io.File;
import java.io.IOException;

import org.vpac.grisu.settings.Environment;
import jline.ConsoleReader;
import org.bestgrid.grisu.client.gricli.util.CommandlineTokenizer;

public class Gricli {

    static final String CONFIG_FILE_PATH = Environment.getGrisuClientDirectory().getPath() +
            File.pathSeparator + "gricli.config";
    
    private GricliEnvironment env;
    private GricliCommand command ;



    public static void main(String[] args) throws IOException {
        
        GricliEnvironment env = new GricliEnvironment(CONFIG_FILE_PATH);
        ConsoleReader reader = new ConsoleReader();
        GricliCommandFactory f = new GricliCommandFactory();
        reader.addCompletor(f.createCompletor());
        while (true){
            try {
                String line = reader.readLine("gricli> ");
                String[] arguments = CommandlineTokenizer.tokenize(line);
                GricliCommand command = f.create(arguments);
                env = command.execute(env);
            }
            catch (SyntaxException ex){
                System.out.println(ex.getMessage());
            }
            catch (GricliException ex){
                ex.printStackTrace();
            }

        }
    }

    private static  void printUsage(){
        System.err.println("we are supposed to print usage here");
    }

    public void run(){
        try {
            command.execute(env);
        } catch (GricliException ex){
            ex.printStackTrace();
        }
    }

}
