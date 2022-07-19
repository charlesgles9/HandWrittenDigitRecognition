 uniform mat4 MVPMatrix;
 attribute vec4 a_position;
 attribute vec4 a_transform;
 attribute vec3 a_scale;
 attribute vec4 a_color;
 attribute vec4 a_distanceFieldColor;
 attribute vec4 a_distanceFieldBounds;
 attribute vec4 a_center;
 attribute vec2 a_rounded_properties;
 attribute vec4 a_trim;
 attribute vec2 a_TexCoordinate;

 varying vec4 v_center;
 varying vec4 v_color;
 varying vec2 v_rounded_properties;
 varying vec4 v_trim;
 varying vec4 v_distanceFieldBounds;
 varying vec4 v_distanceFieldColor;
 varying vec2 pos;
 varying vec2 v_TexCoordinate;

void main(){
   //scaling matrix
  mat3 scaleMat=mat3(a_scale.x,0.0,0.0, 0.0,a_scale.y,0.0, 0.0,0.0,a_scale.z);
 // translation matrix
  mat3 transForwardMat= mat3(1.0,0.0,-a_center.x, 0.0,1.0,-a_center.y, 0.0,0.0,1.0);
  mat3 transBackwardMat=mat3(1.0,0.0, a_center.x, 0.0,1.0, a_center.y, 0.0,0.0,1.0);
  // rotation matrices
  mat3 rotZ=mat3(cos(a_transform.z),sin(a_transform.z),0.0, -sin(a_transform.z),cos(a_transform.z),0.0, 0.0,0.0,1.0);
  mat3 rotX=mat3(1.0,0.0,0.0, 0.0,cos(a_transform.x),-sin(a_transform.x), 0.0,sin(a_transform.x),cos(a_transform.x));
  mat3 rotY=mat3(cos(a_transform.y),0.0,sin(a_transform.y), 0.0,1.0,0.0, -sin(a_transform.y),0.0,cos(a_transform.y));
  vec4 a_pos=a_position;
  //push this point back to the origin
  a_pos.xyz=vec3(a_pos.xy,1.0)*transForwardMat;
  //first scale the vertex
  a_pos.xyz=a_pos.xyz*scaleMat;
  //apply rotations
  a_pos.xyz=a_pos.xyz*rotZ;
  a_pos.xyz=a_pos.xyz*rotY;
  a_pos.xyz=a_pos.xyz*rotX;
  // after rotations push it back to it's original position
  a_pos.xyz=vec3(a_pos.xy,1.0)*transBackwardMat;
  a_pos.z=a_position.z;
  gl_Position=MVPMatrix*a_position;
  v_color=a_color;
  v_center=a_center;
  v_trim=a_trim;
  v_rounded_properties=a_rounded_properties;
  v_TexCoordinate=a_TexCoordinate;
  v_distanceFieldColor=a_distanceFieldColor;
  v_distanceFieldBounds=a_distanceFieldBounds;
 }