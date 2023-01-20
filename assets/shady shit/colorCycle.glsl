#type vertex
#version 330 core
layout (location=0) in vec3 pos;
layout (location=1) in vec4 color;
layout (location=2) in vec2 inTexCoords;

uniform mat4 projection;
uniform mat4 view;
uniform int time;

out float glowOpacity;

out vec4 fColor;
out vec2 texCoords;

void main(){
    float t = float(time) / 1048576;
    float tf = (t - int(t));
    int ti = int(t);
    fColor = vec4(0,0,0,color[3]);
    for(int i=0;i<3;i++){
        fColor[i] = color[(i+ti+1)%3] * tf + color[(i+ti)%3] * (1-tf);
    }
    glowOpacity = (color[0] + color[1] +color[2])*.3333;
    texCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, pos.z, 1);
}


    #type fragment
    #version 330 core

uniform sampler2D sampler;

in vec4 fColor;
in vec2 texCoords;
in float glowOpacity;
out vec4 color;

void main(){
    vec4 tex = texture(sampler, texCoords);
    color = (fColor * glowOpacity * tex[3] + tex * (1-glowOpacity)) * fColor[3];
}