package com.kas.io;

import java.io.InputStream;

public class Resources {

    //根据配置文件的路径,将配置问价
    public static InputStream getResourceAsStream(String path){
        InputStream resourceAsStream = Resources.class.getClassLoader().getResourceAsStream(path);
        return resourceAsStream;
    }


}
