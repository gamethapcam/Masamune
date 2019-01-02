#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

// outline attributes
uniform float stepX;
uniform float stepY;
uniform int mode;
uniform vec4 outlineColor;

// shader modes
const int MODE_DEFAULT = 0;
const int MODE_OUTLINE = 1;

void main()
{
    if(mode==MODE_OUTLINE) {

        float alpha = 4.0f * texture2D( u_texture, v_texCoords ).a;
        alpha -= texture2D( u_texture, v_texCoords + vec2( stepX, 0.0f ) ).a;
        alpha -= texture2D( u_texture, v_texCoords + vec2( -stepX, 0.0f ) ).a;
        alpha -= texture2D( u_texture, v_texCoords + vec2( 0.0f, stepY ) ).a;
        alpha -= texture2D( u_texture, v_texCoords + vec2( 0.0f, -stepY ) ).a;

        gl_FragColor = vec4( outlineColor.x, outlineColor.y, outlineColor.z, alpha );
    } else {
        gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    }
}
