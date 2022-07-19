precision mediump float;
varying vec4 v_color;
uniform vec2 srcRes;
varying vec4 circlePos;
uniform int sampleId;
uniform sampler2D u_texture;
varying vec2 v_TexCoordinate;
void main(){
  vec2 uv,src,pos;
  float aspect=srcRes.x/srcRes.y;
  // the z value here represents the radius
  float radius=circlePos.z;
  float thickness=circlePos.w;
  // modify  coordinates to match screen space coordinates
  src.x=gl_FragCoord.x;
  src.y=srcRes.y-gl_FragCoord.y;
  pos=circlePos.xy;
  uv=src-pos;
  float d=sqrt(dot(uv,uv));
  //outer radius
  float boolean_radius= step(d,radius);
  gl_FragColor=v_color*boolean_radius;
  // inner radius using the thickness value
  float boolean_thickness=1.0-step(d,radius-thickness);
  gl_FragColor*=boolean_thickness;

}