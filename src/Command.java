/**
 * Created by Evan on 8/22/2016.
 */
public abstract class Command {
    String name = "";

    Rank[] permission = {Rank.Admin};

    abstract String invoke();




}
