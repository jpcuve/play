package com.messio.gl.wavefront;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by jpc on 5/13/14.
 */
public class WavefrontParser {
    private final WavefrontHandler handler;

    public WavefrontParser(WavefrontHandler handler) {
        this.handler = handler;
    }

    public int[] split(String ss){
        int[] is = new int[3];
        int count = 0;
        for (String s: ss.split("/")){
            try {
                is[count] = Integer.parseInt(s);
            } catch (NumberFormatException x){
                is[count] = 0;
            }
            count++;
        }
        return is;
    }

    public void parse(InputStream is) throws IOException{
        final LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is, Charset.defaultCharset()));
        int vertexIndex = 1;
        int textureIndex = 1;
        int normalIndex = 1;
        String line;
        while ((line = lnr.readLine()) != null) if (line.length() > 0 && line.charAt(0) != '#' && handler != null) {
            final Scanner scanner = new Scanner(line);
            final String key = scanner.next();
            switch (key){
                case "o":
                    handler.object(scanner.nextLine());
                    break;
                case "g":
                    handler.group(scanner.nextLine());
                    break;
                case "v":
                    handler.vertex(vertexIndex++, scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble(), scanner.hasNextDouble() ? scanner.nextDouble() : 1.0);
                    break;
                case "vt":
                    handler.texture(textureIndex++, scanner.nextDouble(), scanner.nextDouble(), scanner.hasNextDouble() ? scanner.nextDouble() : 1.0);
                    break;
                case "vn":
                    handler.normal(normalIndex++, scanner.nextDouble(), scanner.nextDouble(), scanner.nextDouble());
                    break;
                case "f":
                    final String[] ss = scanner.nextLine().trim().split(" ");
                    for (int i = 0; i < ss.length - 2; i++){
                        handler.triangle(split(ss[0]), split(ss[i + 1]), split(ss[i + 2]));
                    }
                    break;

            }

        }
    }
}
