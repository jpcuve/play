package com.messio.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by jpc on 4/22/14.
 */
public class Shader extends GlObject {

    public Shader(int shaderType) {
        this.handle = GL20.glCreateShader(shaderType);
    }

    public void compileResource(String resourceName){
        try (final Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream(resourceName)).useDelimiter("\\A")){
            final String source = scanner.hasNext() ? scanner.next() : "";
            GL20.glShaderSource(handle, source);
            GL20.glCompileShader(handle);
            checkStatus();
        }
    }

    protected void checkStatus(){
        int status = GL20.glGetShaderi(handle, GL20.GL_COMPILE_STATUS);
        if (status == GL11.GL_FALSE){
            int infoLength = GL20.glGetShaderi(handle, GL20.GL_INFO_LOG_LENGTH);
            final String infoLog = GL20.glGetShaderInfoLog(handle, infoLength);
            throw new IllegalArgumentException("Shader compilation error: " + infoLog);
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed){
            GL20.glDeleteShader(handle);
        }
        closed = true;
    }
}
