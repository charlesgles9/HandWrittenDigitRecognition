 uniform mat4 u_MVPMatrix;
 attribute vec4 a_position;
 attribute vec4 v_center;
 attribute vec4 a_color;
 varying vec4 v_color;
 varying vec4 circlePos;
 attribute vec2 a_TexCoordinate;
 varying vec2 v_TexCoordinate;
void main(){
  v_color=a_color;
  gl_Position=u_MVPMatrix*a_position;
  circlePos=v_center.xyzw;
  v_TexCoordinate=a_TexCoordinate;
 }