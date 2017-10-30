import java.util.ArrayList;
import java.util.List;

public class StripsForEachOffset {
    protected int offset = -1;
    protected char PT;
    protected char CT;
    protected List<Integer> stripNos = new ArrayList<>();

    /*public StripsForEachOffset(StripsForEachOffset obj){
        this.CT = obj.CT;
        this.PT = obj.PT;
        this.offset = obj.offset;
        this.stripNos = obj.stripNos;
    }

    public StripsForEachOffset(){

    }*/

    protected int stripCountWithCurrentOffset(StripsForEachOffset obj, int currentOffset){

        List<Integer> intList = new ArrayList<>();

        for (Integer strip:obj.stripNos) {
            if (strip.intValue() == currentOffset){
                intList.add(strip);
            }
        }

        return intList.size();

    }




}
