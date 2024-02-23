package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Task {

    public record File(
            int id,
            String name,
            List<String> categories,
            int parent,
            int size) {
    }

    /**
     * Task 1
     */
    public static List<String> leafFiles(List<File> files) {
        return leafFilesList(files).stream()
        .map(f -> f.name())
        .collect(Collectors.toList());
    }

    /**
     * Task 2
     */
    public static List<String> kLargestCategories(List<File> files, int k) {
        
        Map<String, Integer> categoryCount = new HashMap<>();
        for (File file : files) {
            for (String category : file.categories()) {
                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            }
        }

        // If less categories than k, then all categories are in the set k largest
        if (k > categoryCount.size()) {
            return categoryCount.entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        }

        // Sorts count in descending order, then alphabetical if equal in count
        return categoryCount.entrySet().stream()
            .sorted((e1, e2) -> {
                int cmp1 = e2.getValue().compareTo(e1.getValue());
                if (cmp1 != 0) {
                    return cmp1;
                }

                return e1.getKey().compareTo(e2.getKey());
            })
            .limit(k)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    /**
     * Task 3
     */
    public static int largestFileSize(List<File> files) {
        if (files.size() == 0) {
            return 0;
        }
        List<Integer> parentSizes = new ArrayList<>();
        List<Integer> parents = files.stream()
            .filter(file -> file.parent() == -1)
            .map(f -> f.id())
            .collect(Collectors.toList());

        for (int parentId : parents) {
            parentSizes.add(totalSizeOfFile(files, parentId));
        }
        
        return parentSizes.stream().max((n1, n2) -> n1.compareTo(n2)).get();
    }

    // Finds any files listed as a parent and removes them from the list
    private static List<File> leafFilesList(List<File> files) {
        if (files.size() == 0) {
            return new ArrayList<>();
        }
        
        List<Integer> parentIds = files.stream()
            .filter(file -> file.parent() != -1)
            .map(f -> f.parent())
            .collect(Collectors.toList());

        return files.stream().filter(f -> !parentIds.contains(f.id())).collect(Collectors.toList());    
    }

    // Given an Id, recursively returns the size of the corresponding file and all its children
    private static int totalSizeOfFile(List<File> files, int parentId) {
        // Always a single valid parentId from previous recursion, so get() can be called
        File parent = files.stream().filter(file -> file.id() == parentId).findFirst().get();
        
        int totalSize = parent.size();
        List<File> childrenList = files.stream()
            .filter(file -> file.parent() == parentId)
            .collect(Collectors.toList());
        
        
        for (File child : childrenList) {
            totalSize += totalSizeOfFile(files, child.id());
        }
        
        return totalSize;
    }

    

    public static void main(String[] args) {
        List<File> testFiles = List.of(
                new File(1, "Document.txt", List.of("Documents"), 3, 1024),
                new File(2, "Image.jpg", List.of("Media", "Photos"), 34, 2048),
                new File(3, "Folder", List.of("Folder"), -1, 0),
                new File(5, "Spreadsheet.xlsx", List.of("Documents", "Excel"), 3, 4096),
                new File(8, "Backup.zip", List.of("Backup"), 233, 8192),
                new File(13, "Presentation.pptx", List.of("Documents", "Presentation"), 3, 3072),
                new File(21, "Video.mp4", List.of("Media", "Videos"), 34, 6144),
                new File(34, "Folder2", List.of("Folder"), 3, 0),
                new File(55, "Code.py", List.of("Programming"), -1, 1536),
                new File(89, "Audio.mp3", List.of("Media", "Audio"), 34, 2560),
                new File(144, "Spreadsheet2.xlsx", List.of("Documents", "Excel"), 3, 2048),
                new File(233, "Folder3", List.of("Folder"), -1, 4096));
       
        List<String> leafFiles = leafFiles(testFiles);
        leafFiles.sort(null);
        assert leafFiles.equals(List.of(
                "Audio.mp3",
                "Backup.zip",
                "Code.py",
                "Document.txt",
                "Image.jpg",
                "Presentation.pptx",
                "Spreadsheet.xlsx",
                "Spreadsheet2.xlsx",
                "Video.mp4"));

        assert kLargestCategories(testFiles, 3).equals(List.of(
                "Documents", "Folder", "Media"));
        System.out.println(kLargestCategories(testFiles, 3));
        assert largestFileSize(testFiles) == 20992;
        System.out.println(largestFileSize(testFiles));
    }
}