package com.messio.gl;

import org.lwjgl.opengl.*;

/**
 * Created by jpc on 24/04/14.
 */
public class GlContext {
    private final String version;
    private final int maxTextureSize;
    private final int[] textureUnits = {
            GL13.GL_TEXTURE0, GL13.GL_TEXTURE1, GL13.GL_TEXTURE2, GL13.GL_TEXTURE3, GL13.GL_TEXTURE4, GL13.GL_TEXTURE5, GL13.GL_TEXTURE6, GL13.GL_TEXTURE7, GL13.GL_TEXTURE8, GL13.GL_TEXTURE9,
            GL13.GL_TEXTURE10, GL13.GL_TEXTURE11, GL13.GL_TEXTURE12, GL13.GL_TEXTURE13, GL13.GL_TEXTURE14, GL13.GL_TEXTURE15, GL13.GL_TEXTURE16, GL13.GL_TEXTURE17, GL13.GL_TEXTURE18, GL13.GL_TEXTURE19,
            GL13.GL_TEXTURE20, GL13.GL_TEXTURE21, GL13.GL_TEXTURE22, GL13.GL_TEXTURE23, GL13.GL_TEXTURE24, GL13.GL_TEXTURE25, GL13.GL_TEXTURE26, GL13.GL_TEXTURE27, GL13.GL_TEXTURE28, GL13.GL_TEXTURE29,
            GL13.GL_TEXTURE30, GL13.GL_TEXTURE31
    };

    public GlContext() {
        this.version = GL11.glGetString(GL11.GL_VERSION);
        this.maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
    }

    public GlContext clear(Buffer... buffers){
        int i = 0;
        for (Buffer buffer: buffers) i |= buffer.getValue();
        GL11.glClear(i);
        return this;
    }

    public GlContext viewPort(int x, int y, int width, int height){
        GL11.glViewport(x, y, width, height);
        return this;
    }

    public GlContext enable(Capability... capabilities){
        for (final Capability capability: capabilities) GL11.glEnable(capability.getValue());
        return this;
    }

    public GlContext disable(Capability... capabilities){
        for (final Capability capability: capabilities) GL11.glDisable(capability.getValue());
        return this;
    }

    public GlContext activeTexture(int unit){
        if (unit < 0 || unit >= textureUnits.length) throw new IllegalArgumentException();
        GL13.glActiveTexture(textureUnits[unit]);
        return this;
    }

    public GlContext bindTexture(Texture texture){
        GL11.glBindTexture(texture.textureType, texture.handle);
        return this;
    }

    public GlContext setClearColor(double r, double g, double b, double a){
        GL11.glClearColor((float) r, (float) g, (float) b, (float) a);
        return this;
    }

    public GlContext depthRange(double near, double far){
        GL11.glDepthRange(near, far);
        return this;
    }

    public GlContext polygonMode(PolygonMode polygonMode){
        GL11.glPolygonMode(Face.FRONT_AND_BACK.getValue(), polygonMode.getValue());
        return this;
    }

    public GlContext blendFunc(BlendFactor sfactor, BlendFactor gfactor){
        GL11.glBlendFunc(sfactor.getValue(), gfactor.getValue());
        return this;
    }

    public GlContext blendColor(double r, double g, double b, double a){
        GL14.glBlendColor((float) r, (float) g, (float) b, (float) a);
        return this;
    }

    public GlContext drawArrays(final DrawMode drawMode, final VertexArrayObject vao){
        GL30.glBindVertexArray(vao.handle);
        vao.enableAllVertexAttribArrays();
        GL11.glDrawArrays(drawMode.getValue(), 0, vao.getElementCount());
        vao.disableAllVertexAttribArrays();
        GL30.glBindVertexArray(0);
        GlObject.checkError();
        return this;
    }

    public GlContext drawElements(final DrawMode drawMode, final VertexArrayObject vao, final VertexBufferObject vboIndices){
        GL30.glBindVertexArray(vao.handle);
        vao.enableAllVertexAttribArrays();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndices.handle);
        GL11.glDrawElements(drawMode.getValue(), vboIndices.getElementCount(), vboIndices.elementType, 0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        vao.disableAllVertexAttribArrays();
        GL30.glBindVertexArray(0);
        GlObject.checkError();
        return this;
    }

    public GlContext use(final Program program){
        GL20.glUseProgram(program.handle);
        return this;
    }


    public String getVersion() {
        return version;
    }

    public int getMaxTextureSize() {
        return maxTextureSize;
    }
}
