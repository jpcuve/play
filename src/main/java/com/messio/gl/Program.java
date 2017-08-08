package com.messio.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.util.*;

/**
 * Created by jpc on 4/22/14.
 */
public class Program extends GlObject {
    public static class Builder {
        private final List<Shader> shaders = new ArrayList<>();
        private final Map<Integer, String> attribLocations = new HashMap<>();
        private final Map<String, Integer> uniformLocations = new HashMap<>();

        public Builder vertexShaderFromResource(String resourceName){
            return shaderFromResource(new VertexShader(), resourceName);
        }

        public Builder fragmentShaderFromResource(String resourceName){
            return shaderFromResource(new FragmentShader(), resourceName);
        }

        private Builder shaderFromResource(final Shader shader, String resourceName){
            shader.compileResource(resourceName);
            shader.checkStatus();
            shaders.add(shader);
            return this;
        }

        public Builder attribLocation(int location, String shaderInputVariableName){
            attribLocations.put(location, shaderInputVariableName);
            return this;
        }

        public Builder uniformLocation(String shaderUniformVariableName){
            uniformLocations.put(shaderUniformVariableName, -1);
            return this;
        }

        public Builder uniformLocations(String... shaderUniformVariableNames){
            for (final String shaderUniformVariableName: shaderUniformVariableNames) uniformLocation(shaderUniformVariableName);
            return this;
        }

        public Program build(){
            final Program program = new Program();
            for (final Shader shader: shaders) GL20.glAttachShader(program.handle, shader.handle);
            for (final Map.Entry<Integer, String> entry: attribLocations.entrySet()) GL20.glBindAttribLocation(program.handle, entry.getKey(), entry.getValue());
            GL20.glLinkProgram(program.handle);
            program.checkStatus();
            for (final String uniformVariableName: uniformLocations.keySet()){
                final int location = GL20.glGetUniformLocation(program.handle, uniformVariableName);
                uniformLocations.put(uniformVariableName, location);
            }
            program.uniforms.putAll(uniformLocations);
            return program;
        }
    }

    private Map<String, Integer> uniforms = new HashMap<>();

    public Program() {
        this.handle = GL20.glCreateProgram();
    }

    public Program uniformFloatingPointMatrix(String uniform, FloatingPointMatrix m){
        return uniformFloatingPointMatrix(uniform, false, m);
    }

    public Program uniform(String uniform, double... ds){
        final Integer handle = uniforms.get(uniform);
        if (handle != null && handle >= 0){
            if (ds != null) switch(ds.length){
                case 1:
                    GL20.glUniform1f(handle, (float) ds[0]);
                    break;
                case 2:
                    GL20.glUniform2f(handle, (float) ds[0], (float) ds[1]);
                    break;
                case 3:
                    GL20.glUniform3f(handle, (float) ds[0], (float) ds[1], (float) ds[2]);
                    break;
                case 4:
                    GL20.glUniform4f(handle, (float) ds[0], (float) ds[1], (float) ds[2], (float) ds[3]);
                    break;
                default:
                    throw new IllegalArgumentException("too many arguments (>4)");
            }
        } else{
            System.out.println("uniform not defined: " + uniform);
        }
        return this;
    }

    public Program uniform(int... is){
        return this;
    }

    public Program uniformFloatingPointMatrix(String uniform, boolean transpose, FloatingPointMatrix m){
        final Integer handle = uniforms.get(uniform);
        if (m.rowDim > 4 || (m.colDim != 1 && m.colDim != m.rowDim)) throw new IllegalArgumentException();
        if (handle != null && handle >= 0){
            if (m.colDim == 1){
                switch (m.rowDim){
                    case 2:
                        GL20.glUniform2(handle, m.getFloatBuffer());
                        break;
                    case 3:
                        GL20.glUniform3(handle, m.getFloatBuffer());
                        break;
                    case 4:
                        GL20.glUniform4(handle, m.getFloatBuffer());
                        break;
                }
            } else {
                switch (m.rowDim){
                    case 2:
                        GL20.glUniformMatrix2(handle, transpose, m.getFloatBuffer());
                        break;
                    case 3:
                        GL20.glUniformMatrix3(handle, transpose, m.getFloatBuffer());
                        break;
                    case 4:
                        GL20.glUniformMatrix4(handle, transpose, m.getFloatBuffer());
                        break;
                }
            }
        } else{
            System.out.println("uniform not defined: " + uniform);
        }
        return this;
    }

    protected void checkStatus(){
        GL20.glValidateProgram(this.handle);
        int status = GL20.glGetProgrami(handle, GL20.GL_LINK_STATUS);
        if (status == GL11.GL_FALSE){
            int infoLength = GL20.glGetProgrami(handle, GL20.GL_INFO_LOG_LENGTH);
            final String infoLog = GL20.glGetProgramInfoLog(handle, infoLength);
            throw new IllegalArgumentException("Program error: " + infoLog);
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed){
            GL20.glDeleteProgram(handle);
        }
        this.closed = true;
    }
}
