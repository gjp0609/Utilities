package com.onysakura.utilities.file.song;

import com.onysakura.utilities.db.sqlite.BaseRepository;
import com.onysakura.utilities.utils.CustomLogger;
import com.onysakura.utilities.utils.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

public class FindDuplicateFiles {
    private static final String FINAL_PATH = "/Files/MusicF";
    private static final String DEL_PATH = "/Files/MusicDel";
    private static final String[] DIR_PATH = new String[]{
            "/Files/Music",
//            "/Files/Music2",
    };
    private static final CustomLogger.Log LOG = CustomLogger.getLogger(FindDuplicateFiles.class);

    private static final BaseRepository<Music> MUSIC_REPOSITORY = new BaseRepository<>(Music.class);

    static {
        MUSIC_REPOSITORY.createTable();
    }

    public static void main(String[] args) throws Exception {
        for (String dirPath : DIR_PATH) {
            File dir = new File(dirPath);
            if (!dir.exists() || !dir.isDirectory()) {
                LOG.warn("path is not directory");
            }
            File[] files = dir.listFiles();
            if (files == null) {
                LOG.warn("no file in " + dirPath);
                continue;
            }
            for (File file : files) {
                String fileName = file.getName();
                String length = String.valueOf(file.length());
                int lastIndexOf = fileName.lastIndexOf(".");
                String musicName = fileName.substring(0, lastIndexOf);
                String fileSuffix = fileName.substring(lastIndexOf);
                List<Music> musicList = MUSIC_REPOSITORY.selectAll();
                boolean hasSameSong = false;
                for (Music music : musicList) {
                    if (StringUtils.levenshtein(music.getName(), musicName) > 80) {
                        LOG.info("has -> {},    {}", music.getFullName(), fileName);
                        LOG.info("input d to delete, r to replace: ");
                        Scanner scanner = new Scanner(System.in);
                        String input = scanner.nextLine();
                        switch (input) {
                            case "a":
                            case "A":
                                break;
                            case "r":
                            case "R":
                                break;
                            case "d":
                            case "D":
                            default:
                                hasSameSong = true;
                                break;
                        }
                    }
                }
                if (!hasSameSong) {
                    Files.move(file.toPath(), new File(FINAL_PATH + "/" + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Music music = new Music();
                    music.setName(musicName);
                    music.setFullName(fileName);
                    music.setSize(length);
                    music.setType(fileSuffix);
                    MUSIC_REPOSITORY.insert(music);
                } else {
                    Files.move(file.toPath(), new File(FINAL_PATH + "/" + fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
