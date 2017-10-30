import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Pair_PT_CT extends StripOffset {
    protected char PT;
    protected char CT;

    protected List<StripOffset> pairList = new ArrayList<>();

    public static List<StripOffset> containsOffset(List<StripOffset> stripOffsetPairList, int offsetValue) {

        List<StripOffset> returnList = new ArrayList<>();

        for (StripOffset obj : stripOffsetPairList) {
            if (obj.offset == offsetValue) {

                if (returnList.contains(obj)) {
                    //Do nothing
                } else {
                    returnList.add(obj);
                }
            }
        }
        return returnList;
    }

}
