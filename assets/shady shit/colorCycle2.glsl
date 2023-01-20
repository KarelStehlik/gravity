#type vertex
#version 330 core
layout (location=0) in vec3 pos;
layout (location=1) in vec4 color;
layout (location=2) in vec2 inTexCoords;

uniform mat4 projection;
uniform mat4 view;
uniform int time;

out vec4 fColor;
out vec2 texCoords;
out float glowOpacity;

void main(){
    float t = float(time) / (1<<17);
    glowOpacity = color.z;
    vec3 col = 0.5 + 0.5*cos(t+color.xyx+vec3(0,2,4));
    fColor = vec4(col,color[3]);
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
    color = fColor * glowOpacity * tex[3] + tex * (1-glowOpacity);
}
