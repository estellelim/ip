package muffin;

public class Parser {
    public String[] parseInput(String input) {
        // Find the index of the first space
        int firstSpaceIndex = input.indexOf(" ");

        // If a space is found, split the first word
        String firstWord = input;
        String remainingString = "";
        if (firstSpaceIndex != -1) {
            firstWord = input.substring(0, firstSpaceIndex);
            remainingString = input.substring(firstSpaceIndex + 1);
        }

        String[] parts = remainingString.split("/");
        for (int i = 1; i < parts.length; i++) {
            // Trim to remove any leading/trailing spaces
            parts[i] = parts[i].trim();

            // Remove the first word in each split part
            int spaceIndex = parts[i].indexOf(" ");
            if (spaceIndex != -1) {
                parts[i] = parts[i].substring(spaceIndex + 1).trim();
            }
        }

        String[] allParts = new String[parts.length + 1];
        allParts[0] = firstWord;
        System.arraycopy(parts, 0, allParts, 1, parts.length);
        return allParts;
    }
}
