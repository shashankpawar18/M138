import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Main {
    static char[][] strips = new char[100][26];
    static int stripNo = 0;

    static char[] plainTextArray = new char[48];
    static char[] cipherTextArray = new char[100];

    static List<Pair_PT_CT> preparedList = new ArrayList<Pair_PT_CT>();

    static List<Pair_PT_CT> round2PreparedList = new ArrayList<Pair_PT_CT>();

    static int maxStripsCount = 25;
    static int maxOffset = 25;

    static List<StripsForEachOffset> offsetList = new ArrayList<>();

    static int[] consideredStrips = new int[25];
    static int consideredStripsCount = 0;


    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();

        //int currentConsideredOffset = -1;
        List<StripsForEachOffset> consolidatedList = new ArrayList<>();
        List<StripsForEachOffset> filteredList = new ArrayList<>();
        List<StripsForEachOffset> validationFilteredList = new ArrayList<>();
        List<Integer> possibleOffsets = new ArrayList<>();

        populatePlainTextArray();
        populateCipherTextArray();
        populateStrips();

        printPlainTextArray();
        printCipherTextArray();
        printStrips();

        populatePreparedList();
        printPreparedList(preparedList);

        prepareConsolidatedList(consolidatedList);

        findPossibleOffsets(consolidatedList, possibleOffsets);
        printPossibleOffsets(possibleOffsets);

        prepareFilteredList(consolidatedList, filteredList, possibleOffsets);
        printFilteredList(filteredList);

        System.out.println();
        System.out.println();
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ROUND 2 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");


        for (int i = 25; i < plainTextArray.length; i++) {

            char currentPT = plainTextArray[i];
            char currentCT = cipherTextArray[i];


            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("currentPT: " + currentPT);
            System.out.println("currentCT: " + currentCT);

            char refPT = filteredList.get(i - 25).PT;
            char refCT = filteredList.get(i - 25).CT;

            System.out.println("refPT: " + refPT);
            System.out.println("refCT: " + refCT);

            Pair_PT_CT possibleObj = new Pair_PT_CT();
            possibleObj.PT = currentPT;
            possibleObj.CT = currentCT;

            for (Integer possibleOffsetObj : possibleOffsets) {

                int currentOffset = possibleOffsetObj.intValue();
                System.out.println("CurrentOffset: " + currentOffset);

                StripsForEachOffset currentObj = findStripsForEachOffset(filteredList, refPT, refCT, currentOffset);

                for (Integer stripNo : currentObj.stripNos) {

                    int currentStrip = stripNo.intValue();

                    for (int j = 0; j < 26; j++) {
                        if (strips[currentStrip][j] == currentPT) {
                            if (currentOffset + j < 26) {
                                if (strips[currentStrip][currentOffset + j] == currentCT) {
                                    StripOffset stripObj = new StripOffset();
                                    stripObj.stripNo = currentStrip;
                                    stripObj.offset = currentOffset;

                                    possibleObj.pairList.add(stripObj);
                                    break;
                                }
                            }
                        }
                    }

                    /*if (possibleObj.stripNos.size() > 0)
                        validationFilteredList.add(possibleObj);*/
                }
            }

            round2PreparedList.add(possibleObj);
        }

        printPreparedList(round2PreparedList);

        printFilteredList(validationFilteredList);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total Time Taken: " + totalTime);


    }

    private static void printFilteredList(List<StripsForEachOffset> filteredList) {
        for (StripsForEachOffset filteredListObj : filteredList) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("PT: " + filteredListObj.PT);
            System.out.println("CT: " + filteredListObj.CT);
            System.out.println("Offset: " + filteredListObj.offset);
            System.out.print("Strips: ");
            for (Integer stripObj : filteredListObj.stripNos) {
                System.out.print(stripObj.intValue() + ", ");
            }
            System.out.println();

        }
    }

    private static void prepareFilteredList(List<StripsForEachOffset> consolidatedList, List<StripsForEachOffset> filteredList, List<Integer> possibleOffsets) {
        for (Integer possibleOffsetObj : possibleOffsets) {

            int currentFilteredOffset = possibleOffsetObj.intValue();

            for (StripsForEachOffset consolidatedLisObj : consolidatedList) {
                if (consolidatedLisObj.offset == currentFilteredOffset) {
                    filteredList.add(consolidatedLisObj);
                }
            }
        }
    }

    private static void printPossibleOffsets(List<Integer> possibleOffsets) {
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Possible Offsets:");
        for (Integer possibleOffsetObj : possibleOffsets) {
            System.out.print(possibleOffsetObj.intValue() + ", ");
        }
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println();
    }

    private static void findPossibleOffsets(List<StripsForEachOffset> consolidatedList, List<Integer> possibleOffsets) {
        int currentConsolidatedOffset = -1;
        for (int i = 1; i <= 25; i++) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            currentConsolidatedOffset = i;
            List<StripsForEachOffset> placeHolderList = new ArrayList<>();

            for (StripsForEachOffset consolidatedListObj : consolidatedList) {
                if (consolidatedListObj.offset == currentConsolidatedOffset) {
                    placeHolderList.add(consolidatedListObj);
                }
            }


            System.out.println("Offset Value: " + currentConsolidatedOffset);
            System.out.println("PT CT Pair count:" + placeHolderList.size());

            if (placeHolderList.size() == 25) {
                possibleOffsets.add(currentConsolidatedOffset);

            }

            placeHolderList.clear();

        }
    }

    private static void prepareConsolidatedList(List<StripsForEachOffset> consolidatedList) {
        int currentConsideredOffset;
        for (int i = 1; i <= 25; i++) {

            currentConsideredOffset = i;
            consideredStripsCount = 0;

            for (Pair_PT_CT preparedListObj : preparedList) {


                List<StripOffset> stripListWithCurrentOffset = preparedListObj.containsOffset(preparedListObj.pairList, currentConsideredOffset);

                if (stripListWithCurrentOffset.size() > 0) {

                    StripsForEachOffset sfeObj = new StripsForEachOffset();

                    sfeObj.PT = preparedListObj.PT;
                    sfeObj.CT = preparedListObj.CT;
                    sfeObj.offset = currentConsideredOffset;

                    for (StripOffset stripListWithCurrentOffsetObj : stripListWithCurrentOffset) {
                        sfeObj.stripNos.add(stripListWithCurrentOffsetObj.stripNo);

                    }

                    consolidatedList.add(sfeObj);
                }
            }
        }
    }

    private static void populatePreparedList() {
        //Creating Objects for each PT,CT character pair
        for (int i = 0; i < 25; i++) {

            Pair_PT_CT pairObj = new Pair_PT_CT();

            pairObj.PT = plainTextArray[i];
            pairObj.CT = cipherTextArray[i];

            //StripOffset stripOffsetObj = new StripOffset();

            for (int j = 0; j < 100; j++) {//Strip Number
                for (int k = 0; k < 26; k++) {//Plain Text Character Index

                    if (pairObj.PT == strips[j][k]) {
                        int ptIndex = k;

                        for (int l = k + 1; l < 26; l++) {//Cipher Text character index

                            if (pairObj.CT == strips[j][l]) {
                                int ctIndex = l;

                                StripOffset stripOffsetObj = new StripOffset();
                                stripOffsetObj.offset = ctIndex - ptIndex;
                                stripOffsetObj.stripNo = j;

                                pairObj.pairList.add(stripOffsetObj);
                                break;
                            }
                        }
                        break;
                    }
                }
            }

            preparedList.add(pairObj);
        }

    }

    private static void populatePlainTextArray() throws IOException {
        String fileName = "/Users/casatech/Google Drive/CS_265/M138_Plain_Text.txt";
        String line = null;

        FileReader fileReader = new FileReader(fileName);

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while ((line = bufferedReader.readLine()) != null) {

            char[] tempArray = line.replaceAll(" ", "").toCharArray();

            for (int i = 0; i < tempArray.length; i++) {
                plainTextArray[i] = tempArray[i];

            }
        }
        bufferedReader.close();
    }

    private static void populateCipherTextArray() throws IOException {
        String fileName = "/Users/casatech/Google Drive/CS_265/M138_Cipher_Text.txt";
        String line = null;

        FileReader fileReader = new FileReader(fileName);

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while ((line = bufferedReader.readLine()) != null) {

            cipherTextArray = line.replaceAll(" ", "").toCharArray();
        }

        bufferedReader.close();
    }

    private static void populateStrips() throws IOException {
        String fileName = "/Users/casatech/Google Drive/CS_265/mtc3-schmeh-10-stripset.txt";
        String line = null;

        FileReader fileReader = new FileReader(fileName);

        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while ((line = bufferedReader.readLine()) != null) {
            //System.out.println(line);
            String[] split = line.split("\\s+");
            //System.out.println(split[1]);

            char[] charArray = split[1].toCharArray();

            for (int i = 0; i < charArray.length; i++) {
                strips[stripNo][i] = charArray[i];

            }
            stripNo++;
        }


        bufferedReader.close();
    }

    private static void printStrips() throws IOException {
        System.out.println("***************************************************");
        System.out.println("Strips");
        for (int i = 0; i < 100; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 26; j++) {
                System.out.print(strips[i][j]);
            }
            System.out.println();
        }
        System.out.println("***************************************************");
    }

    private static void printPlainTextArray() throws IOException {
        System.out.println("***************************************************");
        System.out.println("Plain Text");
        for (int i = 0; i < plainTextArray.length; i++) {
            System.out.print(plainTextArray[i]);
        }
        System.out.println();
        System.out.println("***************************************************");
    }

    private static void printCipherTextArray() throws IOException {
        System.out.println("***************************************************");
        System.out.println("Cipher Text");
        for (int i = 0; i < cipherTextArray.length; i++) {
            System.out.print(cipherTextArray[i]);
        }
        System.out.println();
        System.out.println("***************************************************");
    }

    private static void printPreparedList(List<Pair_PT_CT> param_list) {

        for (Pair_PT_CT listObj : param_list) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Plain Text: " + listObj.PT);
            System.out.println("Cipher Text: " + listObj.CT);
            System.out.println();
            for (StripOffset stObj : listObj.pairList) {
                System.out.println("#" + stObj.stripNo + " " + stObj.offset);
            }
        }

    }

    private static void printOffsetList() {

        for (StripsForEachOffset obj : offsetList) {
            System.out.println("#################################");
            System.out.println("Offset: " + obj.offset);
            System.out.println("Strip Count:" + obj.stripNos.size());
            System.out.print("Strips: ");
            Collections.sort(obj.stripNos);
            for (Integer i : obj.stripNos) {
                System.out.print(i + " ");
            }
            System.out.println();
        }

    }

    public static boolean useArraysBinarySearch(int[] arr, int targetValue) {
        int a = Arrays.binarySearch(arr, targetValue);
        if (a > 0)
            return true;
        else
            return false;
    }


    public static StripsForEachOffset findStripsForEachOffset(List<StripsForEachOffset> paramList, char paramPT, char paramCT, int paramOffset) {

        StripsForEachOffset returnObj = new StripsForEachOffset();

        for (StripsForEachOffset paramObj : paramList) {
            if (paramObj.PT == paramPT && paramObj.CT == paramCT && paramObj.offset == paramOffset) {

                returnObj = paramObj;
                break;
            }
        }

        return returnObj;

    }
}


