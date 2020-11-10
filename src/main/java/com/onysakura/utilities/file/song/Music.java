package com.onysakura.utilities.file.song;

import com.onysakura.utilities.db.sqlite.TableName;

@TableName("MUSIC")
public class Music {
    private String id;
    private String name;
    private String fullName;
    private String size;
    private FileType type;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public FileType getType() {
        return type;
    }

    public void setType(String type) {
        this.type = FileType.getType(type);
    }

    @Override
    public String toString() {
        return "Music{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", size='" + size + '\'' +
                ", type=" + type +
                '}';
    }
}
