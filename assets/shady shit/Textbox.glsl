#type vertex
#version 330 core
layout (location=0) in vec3 pos;
layout (location=1) in vec4 color;
layout (location=2) in vec2 inTexCoords;

uniform mat4 projection;
uniform mat4 view;

out vec4 fColor;
out vec2 texCoords;
out vec2 coords;

void main(){
    fColor = color;
    texCoords = inTexCoords;
    gl_Position = projection * view * vec4(pos.x, pos.y, pos.z, 1);
    coords = pos.xy;
}

    #type fragment
    #version 330 core

uniform sampler2D sampler;

in vec4 fColor;
in vec2 texCoords;
in vec2 coords;

out vec4 color;

void main(){
    float avg = 0;
    float off = fColor.x - fColor.y;
    color = (fColor * avg + texture(sampler, texCoords) * (1-avg)) * fColor[3];
}
