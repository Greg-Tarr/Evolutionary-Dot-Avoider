class Dot {
  float w = 2;
  float posX = random(width);
  float posY = random(height);
  float Dcolor = 255;
  float Dcolor1 = 255;
  float Dcolor2 = 255;
  Dot(float Tcolor, float Tcolor1, float Tcolor2){
    Dcolor = Tcolor;
    Dcolor1 = Tcolor1;
    Dcolor2 = Tcolor2;
  }
  void draw() {
      stroke(255);
      fill(Dcolor, Dcolor1, Dcolor2);
      ellipse(posX, posY, w, w);
  }
  void kill(){
    w = 0;
  }
  void r() {
    w = 2;
  }
}
