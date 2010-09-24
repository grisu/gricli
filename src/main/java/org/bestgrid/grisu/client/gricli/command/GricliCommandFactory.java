package org.bestgrid.grisu.client.gricli.command;

import java.util.ArrayList;
import java.util.HashMap;
import org.bestgrid.grisu.client.gricli.InvalidCommandException;
import org.bestgrid.grisu.client.gricli.SyntaxException;
import org.bestgrid.grisu.client.gricli.UnknownCommandException;

import jline.ArgumentCompletor;
import jline.SimpleCompletor;
import jline.Completor;
import jline.NullCompletor;

public class GricliCommandFactory {

    private HashMap<String,CommandCreator> creatorMap = new HashMap<String,CommandCreator>();

    /*
     * simple auto-completion based on keywords
    */
    public Completor createCompletor() {
        boolean hasChainedCreators = true;
        HashMap<String, CommandCreator> tempMap = creatorMap;
        ArrayList<ChainedCreator> creators = new ArrayList<ChainedCreator>();
        ArrayList<ChainedCreator> creators2 = new ArrayList<ChainedCreator>();
        ArrayList<Completor> completors = new ArrayList<Completor>();
        ArrayList<String> keywords = new ArrayList<String>();

        for (String keyword : tempMap.keySet()) {
            keywords.add(keyword);
            CommandCreator cr = tempMap.get(keyword);
            if (cr.getClass() == ChainedCreator.class) {
                creators.add((ChainedCreator) cr);
            }

        }
        completors.add(new SimpleCompletor(keywords.toArray(new String[]{})));

        for (String k : keywords) {
            System.out.println(k);
        }
        System.out.println("----------------");



        creators2 = (ArrayList<ChainedCreator>) creators.clone();
        while (creators2.size() > 0) {
            keywords = new ArrayList<String>();
            creators = new ArrayList<ChainedCreator>();
            for (ChainedCreator cch : creators2) {
                tempMap = cch.getCreatorMap();
                for (String ckey : tempMap.keySet()) {
                    keywords.add(ckey);
                    CommandCreator cr = tempMap.get(ckey);
                    if (cr.getClass() == ChainedCreator.class) {
                        creators.add((ChainedCreator) cr);
                    }
                }
                for (String k : keywords) {
                    System.out.println(k);
                }
                System.out.println("----------------");
                completors.add(new SimpleCompletor(keywords.toArray(new String[]{})));
            }
            creators2 = (ArrayList<ChainedCreator>) creators.clone();
        }

        completors.add(new NullCompletor());
        return new ArgumentCompletor(completors.toArray(new Completor[]{}));
    }

    public GricliCommandFactory(){
        creatorMap.put("login", new LoginCreator());
        ChainedCreator printCreator = new ChainedCreator("print");
        printCreator.add("jobs", new PrintJobsCreator());
        printCreator.add("job", new PrintJobCreator());
        printCreator.add("queues", new PrintQueuesCreator());
        printCreator.add("hosts", new PrintHostsCreator());
        printCreator.add("globals", new PrintGlobalsCreator());
        creatorMap.put("print", printCreator);

        ChainedCreator setCreator = new ChainedCreator("set");
        setCreator.add("global", new SetGlobalCreator());
        creatorMap.put("set", setCreator);

        ChainedCreator submitCreator = new ChainedCreator("submit");
        submitCreator.add("cmd", new SubmitCmdCreator());
        creatorMap.put("submit", submitCreator);

        ChainedCreator killCreator = new ChainedCreator("kill");
        killCreator.add("job", new KillJobCreator(false));
        creatorMap.put("kill", killCreator);

       ChainedCreator destroyCreator = new ChainedCreator("destroy");
        destroyCreator.add("job", new KillJobCreator(true));
        creatorMap.put("destroy", destroyCreator);

        creatorMap.put("gls", new GridLsCreator());

    }

    public GricliCommand create(String[] args) throws SyntaxException{

        if (args.length == 0 ){
            throw new InvalidCommandException("empty command");
        }
        String command = args[0];
        String[] arguments = new String[args.length - 1];
        System.arraycopy(args,1,arguments,0,arguments.length);

        CommandCreator creator = creatorMap.get(command);
        if (creator != null){
            return creator.create(arguments);
        }
        else {
            throw new UnknownCommandException(command);
        }
    }

    private abstract class CommandCreator {
        public CommandCreator() {};
        public abstract GricliCommand create(String[] args) throws SyntaxException;
    }

    /*
     * login <serviceInterfaceUrl>
     */

    private class LoginCreator extends CommandCreator {

        @Override
        public GricliCommand create(String[] args) throws InvalidCommandException{
            if (args.length == 0){
                return new LocalLoginCommand(null);
            }
            else{
                return new LocalLoginCommand(args[0]);
            }

        }

    }

    /*
     * allows to implement complex commands like
     * "set global" and "set job"
     */
    private class ChainedCreator extends CommandCreator {

        private HashMap<String, CommandCreator> creatorMap =
                new HashMap<String, CommandCreator>();
        private final String commandName;

        public ChainedCreator(String commandName){
            this.commandName = commandName;
        }

        public void add(String command, CommandCreator creator){
            creatorMap.put(command, creator);
        }

        public HashMap<String,CommandCreator> getCreatorMap(){
            return creatorMap;
        }

        @Override
        public GricliCommand create(String[] args) throws SyntaxException {
            if (args.length < 1){
                throw new InvalidCommandException( this.commandName + " command incomplete ");
            } 
            CommandCreator sub = creatorMap.get(args[0]);
            if (sub == null){
                throw new UnknownCommandException(this.commandName + " " + args[0]);
            }

            String[] subargs = new String[args.length - 1];
            System.arraycopy(args,1,subargs,0,subargs.length);
            return sub.create(subargs);

        }

    }

    /**
     * print jobs
     */
    private class PrintJobsCreator extends CommandCreator{

        @Override
        public GricliCommand create(String[] args) throws SyntaxException {
            return new PrintJobsCommand();
        }
        
    }

    /**
     * print hosts
     */

    private class PrintHostsCreator extends CommandCreator {

        @Override
        public GricliCommand create(String[] args) throws SyntaxException {
            return new PrintHostsCommand();
        }

    }

    /**
     * print queues <queue>
     */
    private class PrintQueuesCreator extends CommandCreator {

        @Override
        public GricliCommand create(String[] args) throws SyntaxException {
            String fqan = (args.length > 0 ) ? args[0] : null;
            return new PrintQueuesCommand(fqan);
        }

    }

    /**
     * print job <jobname>
     */
    private class PrintJobCreator extends CommandCreator {

        @Override
        public GricliCommand create(String[] args) throws SyntaxException {
            if (args.length == 0){
                throw new InvalidCommandException("job name not specified");
            }
            return new PrintJobCommand(args[0]);
        }

    }

    /*
     * set global <var> <value>
     */

    private class SetGlobalCreator extends CommandCreator{

        @Override
        public GricliCommand create(String[] args) throws SyntaxException {
            if (args.length != 2){
                throw new InvalidCommandException("usage: set global <global> <value>");
            } else {
                return new SetGlobalCommand(args[0],args[1]);
            }
        }

    }
    /*
     * submit cmd <cmd>
     */
    private class SubmitCmdCreator extends CommandCreator {

        @Override
        public GricliCommand create(String[] args) throws SyntaxException {
            if (args.length != 1){
                throw new InvalidCommandException("usage: submit cmd <cmd>");
            }
            return new SubmitCmdCommand(args[0]);
        }

    }

    /*
     * print globals
     */
    private class PrintGlobalsCreator extends CommandCreator {

        @Override
        public GricliCommand create(String[] args) throws SyntaxException {
            return new PrintGlobalsCommand();
        }

    }

    /*
     * gls
     */

    private class GridLsCreator extends CommandCreator {

        @Override
        public GricliCommand create(String[] args) throws SyntaxException {
            return new GridLsCommand();
        }

    }

    /*
     * kill job <jobname>
     * or
     * destroy job <jobname>
     */
    private class KillJobCreator extends CommandCreator {
        private final boolean clean;
        public KillJobCreator(boolean clean){
            this.clean = clean;
        }

        @Override
        public GricliCommand create(String[] args) throws SyntaxException {
            if (args.length != 1){
                throw new InvalidCommandException("kill command needs job name");
            }
            return new KillJobCommand(args[0],this.clean);
        }
    }


}
