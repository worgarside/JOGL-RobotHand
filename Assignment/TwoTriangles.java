import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class TwoTriangles extends Mesh {

    private int[] textureId;
    private static final int LIGHT_COUNT = 3;

    public TwoTriangles(GL3 gl, int[] textureId) {
        super(gl);
        super.vertices = this.vertices;
        super.indices = this.indices;
        this.textureId = textureId;
        material.setAmbient(1f, 1f, 1f);

        material.setDiffusePoint(1f, 1f, 1f);
        material.setSpecularPoint(0.5f, 0.5f, 0.5f);

        material.setDiffuseSpot(1f, 1f, 1f);
        material.setSpecularSpot(0.5f, 0.5f, 0.5f);

        material.setShininess(32.0f);
        shader = new Shader(gl, "shaders/vs_tt_05.glsl", "shaders/fs_tt_05.glsl");
        fillBuffers(gl);
    }

    public void render(GL3 gl, Mat4 model) {
        Mat4 mvpMatrix = Mat4.multiply(perspective, Mat4.multiply(camera.getViewMatrix(), model));

        shader.use(gl);

        shader.setFloatArray(gl, "model", model.toFloatArrayForGLSL());
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        shader.setVec3(gl, "viewPos", camera.getPosition());

        shader.setVec3(gl, "light.position", light.getPosition(1));
        shader.setVec3(gl, "light.ambient", light.getMaterial().getAmbient());
        shader.setVec3(gl, "light.diffuse", light.getMaterial().getDiffusePoint());
        shader.setVec3(gl, "light.specular", light.getMaterial().getSpecularPoint());

        shader.setVec3(gl, "material.ambient", material.getAmbient());
        shader.setVec3(gl, "material.diffuse", material.getDiffusePoint());
        shader.setVec3(gl, "material.specular", material.getSpecularPoint());
        shader.setFloat(gl, "material.shininess", material.getShininess());

        shader.setInt(gl, "first_texture", 0);

        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId[0]);

        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
    }

    public void dispose(GL3 gl) {
        super.dispose(gl);
        gl.glDeleteBuffers(1, textureId, 0);
    }

    // ***************************************************
  /* THE DATA
   */
    // anticlockwise/counterclockwise ordering
    private float[] vertices = {      // position, colour, tex coords
            -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,  // top left
            -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
            0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,  // bottom right
            0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f   // top right
    };

    private int[] indices = {         // Note that we start from 0!
            0, 1, 2,
            0, 2, 3
    };

}