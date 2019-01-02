#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

uniform float stepX;
uniform float stepY;
uniform float outline;
uniform vec4 outlineColor;

void main()
{
    if(outline==0) {
        gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    } else {
        float alpha = 4 * texture2D( u_texture, v_texCoords ).a;
        alpha -= texture2D( u_texture, v_texCoords + vec2( stepX, 0.0f ) ).a;
        alpha -= texture2D( u_texture, v_texCoords + vec2( -stepX, 0.0f ) ).a;
        alpha -= texture2D( u_texture, v_texCoords + vec2( 0.0f, stepY ) ).a;
        alpha -= texture2D( u_texture, v_texCoords + vec2( 0.0f, -stepY ) ).a;

        gl_FragColor = vec4( outlineColor.x, outlineColor.y, outlineColor.z, alpha );
    }
}
