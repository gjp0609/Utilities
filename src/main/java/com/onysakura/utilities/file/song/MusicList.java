package com.onysakura.utilities.file.song;

import com.onysakura.utilities.db.sqlite.TableName;

@TableName("MUSIC_LIST")
public class MusicList {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
