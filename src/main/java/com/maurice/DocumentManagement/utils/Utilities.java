package com.maurice.DocumentManagement.utils;

import io.jsonwebtoken.lang.Objects;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Utilities {

    public static String extractFolderFromKey(String key){
        String[] slices = key.split("/");
        String lastPiece = slices[slices.length - 1];
        String folder = key.substring(0, key.length() - lastPiece.length());
        return folder;
    }

    public static String extractNameFromFile(String file){
        return file.split("\\.")[0];
    }

    public static String extractFileTypeFromFile(String file){
        return file.split("\\.")[1];
    }

    public static String extractFileFromKey(String key){
        String[] slices = key.split("/");
        return slices[slices.length - 1];
    }

    public static void getFirstLevelObjectKeys(String prefix, List<String> list, Set<String> result) {
        list.stream().filter(item -> !item.equalsIgnoreCase(prefix)).map(
                item -> {
                    String keyWithoutPrefix = item.substring(prefix.length());
                    String firstChildDirectoryName = null;
                    if(keyWithoutPrefix.contains("/")){
                        firstChildDirectoryName = Objects.nullSafeToString(keyWithoutPrefix.split("/")[0]);
                        return prefix +firstChildDirectoryName+"/";
                    }
                    return item;
                }
        ).forEach(result::add);
    }

    public static Set<String> getOnlyFolderNamesFromKeys(Set<String> keys){
        return keys.stream().map(item -> {
            var fragments = item.split("/");
            return fragments[fragments.length - 1] + "/";
        }).collect(Collectors.toSet());

    }
}
