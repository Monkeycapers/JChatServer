import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Evan on 8/22/2016.
 */
public class Promote {
    Rank[] permission;
    String name;
    Boolean secret = true;
    ArrayList<Rank> rankOrder;
    public Promote() {
        permission = new Rank[] {Rank.Op, Rank.Admin};
        name = "/promote";
        rankOrder = new ArrayList<Rank>();
        rankOrder.add(Rank.Guest); rankOrder.add(Rank.User); rankOrder.add(Rank.Op); rankOrder.add(Rank.Admin);
        for (Rank r: rankOrder) {
            System.out.println(r.name());
        }
    }

    public String invoke(User invoker, User target) {

        if (!Arrays.asList(permission).contains(invoker.rank)) {
            return "You do not have the permision to use this command";
        }

        if (rankOrder.indexOf(invoker.rank) <= rankOrder.indexOf(target.rank)) {
            return "You are a lower or equal rank to your target";
        }

        target.rank = rankOrder.get(rankOrder.indexOf(target.rank));

        Authenticate.update(target);

        return "Promoted user " + target.username + " to rank: " + target.rank.name();
    }


}
